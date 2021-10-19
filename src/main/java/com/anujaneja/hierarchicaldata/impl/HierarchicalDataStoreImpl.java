package com.anujaneja.hierarchicaldata.impl;

import com.anujaneja.hierarchicaldata.IHierarchicalDataStore;
import com.anujaneja.hierarchicaldata.Listener;
import com.anujaneja.hierarchicaldata.Node;
import com.anujaneja.hierarchicaldata.events.Event;
import com.anujaneja.hierarchicaldata.events.EventType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HierarchicalDataStoreImpl implements IHierarchicalDataStore {

    private Node root = new Node();

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    String 	leastCommonAncestor(Node root, String path) {
        //For now...return path as it is...
        return path;
    }


    @Override
    public void create(String path, String data) {
        if(path==null || "".equals(path)) {
            return;
        }

        String[] nodes  = path.substring(1).split("/");

        if(nodes.length>0 && root.getName()!=null &&  !root.getName().equals(nodes[0])) {
            throw new RuntimeException("Can not have multiple roots");
        }

        String leastCommonAncestorPath =  leastCommonAncestor(root,path);
        List<Listener> listeners = new ArrayList<>();

        synchronized (leastCommonAncestorPath) {
            if(root.getName()==null && nodes.length==1) {
                root.setName(nodes[0]);
                root.setData(data);
                root.setPath(path);
                return;
            }

            Node currLevelNode = root;
            String currentPath = "/"+root.getName()+"/";
            for(int i=1;i<nodes.length-1;i++) {
                String nodeName = nodes[i];
                Map<String,Node> childs =  currLevelNode.getChilds();

                if(childs.get(nodeName)==null) {
                    currentPath = currentPath+nodeName+"/";
                   Node node = new Node(nodes[i],null,path);
                   currLevelNode.getChilds().put(nodeName,node);
                   currLevelNode=node;
                } else {
                    currentPath = currentPath+nodeName+"/";
                    currLevelNode = childs.get(nodeName);
                }

                if(currLevelNode.getListenerMapping().size()>0) {
                    listeners.addAll(currLevelNode.getListenerMapping().get(currLevelNode.getName()));
                }


            }
            String nodeName= nodes[nodes.length-1];
            currentPath=currentPath+nodeName+"/";
            Node newNode = new Node(nodeName,data,currentPath);
            currLevelNode.getChilds().put(nodeName,newNode);

            for (Listener listener : listeners) {
                Event event = new Event(EventType.CREATED.name(),newNode,newNode);
                listener.notifyEvent(event);
            }
        }

    }

    @Override
    public void update(String path, String data) {
        if(path==null || "".equals(path)) {
            return;
        }

        String[] nodes  = path.substring(1).split("/");

        String leastCommonAncestorPath =  leastCommonAncestor(root,path);

        synchronized (leastCommonAncestorPath) {
            if(root.getName()!=null && nodes.length==1) {
                root.setData(data);
                return;
            }
            int index = 0;
            update(root,nodes,data,index);

        }

    }

    private void update(Node node, String[] nodes, String data, int level) {
        if(node==null || level>=nodes.length) {
            return;
        }

        String nodeName = nodes[level];
        if(node.getName().equals(nodeName) && nodes.length-1==level) {
            //Updating the data...
            node.setData(data);
        } else if(node.getName().equals(nodeName)) {
            Map<String,Node> childs =  node.getChilds();
            //Found the path...
            String nextNodeName = nodes[++level];
            if(childs.get(nextNodeName)!=null) {
                Node child = childs.get(nextNodeName);
                update(child,nodes,data,level);
            }
        }
    }

    @Override public boolean delete(String path) {
        if(path==null || "".equals(path)) {
            return false;
        }

        String[] nodes  = path.substring(1).split("/");

        String leastCommonAncestorPath =  leastCommonAncestor(root,path);
        List<Listener> listeners = new ArrayList<>();
        synchronized (leastCommonAncestorPath) {
            if(nodes.length==1 && root.getName().equals(nodes[0])) {
                if(root.getListenerMapping().size()>0) {
                    listeners.addAll(root.getListenerMapping().get(root.getName()));
                }
                root=null;
                return true;
            } else {
                int level=1;

                boolean isSuccess =  delete(root,nodes,level,listeners);
                if(isSuccess) {
                    //Notify the watchers...
                    for(Listener listener:listeners) {
                        Event event = new Event(EventType.DELETED.name(), null,new Node(nodes[nodes.length-1], null,path));
                        listener.notifyEvent(event);
                    }
                }

                return isSuccess;

            }
        }
    }

    private boolean delete(Node node, String[] nodes, int level,List<Listener> listeners) {
        String nodeName = nodes[level-1];

        if(node==null || level>=nodes.length || !node.getName().equals(nodeName)) {
            return false;
        }

        String childNodeName = nodes[level];

        //found the node to be deleted...
        if(node.getChilds().get(childNodeName)!=null && level==nodes.length-1) {
            if(node.getListenerMapping().size()>0) {
                listeners.addAll(node.getListenerMapping().get(node.getName()));
            }
            node.getChilds().remove(childNodeName);
            return true;
        } else if(node.getChilds().get(childNodeName)!=null) {
            Node child = node.getChilds().get(childNodeName);
            if(node.getListenerMapping().size()>0) {
                listeners.addAll(node.getListenerMapping().get(node.getName()));
            }
            return delete(child,nodes,++level,listeners);
        }

        return false;
    }

    @Override public String get(String path) {
        if(path==null || "".equals(path)) {
            return null;
        }

        String[] nodes  = path.substring(1).split("/");
        String data = null;

        if(nodes.length==1) {
            return root.getData();
        }

        Node currLevelNode = root;

        for(int i=1;i<nodes.length;i++) {
            String nodeName = nodes[i];
            Map<String,Node> childs =  currLevelNode.getChilds();

            if(childs.get(nodeName)!=null && i==nodes.length-1) {
                //last node...
                data = childs.get(nodeName).getData();
            } else if(childs.get(nodeName)!=null) {
                currLevelNode=childs.get(nodeName);
            } else {
                break;
            }
        }

        return data;
    }

    @Override public List<Node> list(String path) {
        if(path==null || "".equals(path)) {
            return null;
        }
        String[] nodes  = path.substring(1).split("/");
        if(nodes.length==1) {
            return new ArrayList<>(root.getChilds().values());
        }

        Node currNode = root;
        List<Node> childNodes = new ArrayList<>();

        for(int i=1;i<nodes.length;i++) {
            String nodeName = nodes[i];
            if(currNode !=null && currNode.getName().equals(nodeName) && nodes.length-1==i) {
                childNodes = new ArrayList<>(currNode.getChilds().values());
            } else if(currNode !=null && currNode.getName().equals(nodeName)) {
                String childNodeName = nodes[i+1];
                currNode = currNode.getChilds().get(childNodeName);
            } else {
                break;
            }
        }

        return childNodes;
    }

    public void addListener(String path, Listener listener) {
        if(path==null || "".equals(path)) {
            return;
        }

        String[] nodes  = path.substring(1).split("/");
        Node currNode = root;

        for(String nodeName:nodes) {
            if(currNode.getName().equals(nodeName)) {
                currNode.getListenerMapping().putIfAbsent(nodeName,new ArrayList<>());
                currNode.getListenerMapping().get(nodeName).add(listener);
            } else if(currNode.getChilds().get(nodeName)!=null) {
                currNode = currNode.getChilds().get(nodeName);
                currNode.getListenerMapping().putIfAbsent(nodeName,new ArrayList<>());
                currNode.getListenerMapping().get(nodeName).add(listener);
            }
        }
    }
}
