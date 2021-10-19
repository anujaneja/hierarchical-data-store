package com.anujaneja.hierarchicaldata.events;

import com.anujaneja.hierarchicaldata.Node;

public class Event {
    private String  type;
    private Node    newNode;
    private Node    oldNode;

    public Event() {
    }

    public Event(String type, Node newNode, Node oldNode) {
        this.type = type;
        this.newNode = newNode;
        this.oldNode = oldNode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Node getNewNode() {
        return newNode;
    }

    public void setNewNode(Node newNode) {
        this.newNode = newNode;
    }

    public Node getOldNode() {
        return oldNode;
    }

    public void setOldNode(Node oldNode) {
        this.oldNode = oldNode;
    }
}
