package com.anujaneja.hierarchicaldata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private String                      name;
    private String                      data;
    private String                      path;
    private Map<String, Node>           childs          = new HashMap<>();
    private Map<String, List<Listener>> listenerMapping = new HashMap<>();

    public Node() {
    }

    public Node(String name, String data,String path) {
        this.data = data;
        this.name = name;
        this.path = path;
    }

    public Map<String, Node> getChilds() {
        return childs;
    }

    public void setChilds(Map<String, Node> childs) {
        this.childs = childs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<Listener>> getListenerMapping() {
        return listenerMapping;
    }

    public void setListenerMapping(Map<String, List<Listener>> listenerMapping) {
        this.listenerMapping = listenerMapping;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override public String toString() {
        return "Node{" + "name='" + name + '\'' + ", data='" + data + '\'' + ", path='" + path + '\'' + '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
