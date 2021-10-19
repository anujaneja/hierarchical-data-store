package com.anujaneja.hierarchicaldata;

import com.anujaneja.hierarchicaldata.events.Event;
import com.anujaneja.hierarchicaldata.impl.HierarchicalDataStoreImpl;
import java.util.LinkedList;
import java.util.Queue;

public class DriverMain {
    private  static IHierarchicalDataStore hierarchicalDataStore = new HierarchicalDataStoreImpl();
    public static void main(String[] args) {

        hierarchicalDataStore.create("/root/","nothing");
        hierarchicalDataStore.addListener("/root/", new Listener() {
            @Override
            public void notifyEvent(Event event) {
                System.out.println("Event type:  "+event.getType()+ " old Node: "+event.getOldNode()+" New Node: "+event.getNewNode());
            }
        });

        hierarchicalDataStore.create("/root/child1/","childdata 1");
        hierarchicalDataStore.create("/root/child2/","childdata 2");
        System.out.println(hierarchicalDataStore.list("/root/"));
        hierarchicalDataStore.create("/root/child1/subchild1","subchild 1");
        Node root = hierarchicalDataStore.getRoot();

        printAllNodesData();

        System.out.println("List of all child nodes of root: ");

        System.out.println(hierarchicalDataStore.list(root.getPath()));

        System.out.println("Deleting a child2");

        hierarchicalDataStore.delete("/root/child2");

        printAllNodesData();
    }

    private static void printAllNodesData() {
        //GET and print all node data...
        Node root = hierarchicalDataStore.getRoot();

        Queue<Node> priorityQueue = new LinkedList<>();
        priorityQueue.offer(root);

        while(!priorityQueue.isEmpty()) {
            Node node = priorityQueue.poll();
            System.out.println(hierarchicalDataStore.get(node.getPath()));

            for(Node child: node.getChilds().values()) {
                priorityQueue.offer(child);
            }
        }
    }
}
