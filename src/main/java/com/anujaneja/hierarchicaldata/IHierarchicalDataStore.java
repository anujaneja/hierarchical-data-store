package com.anujaneja.hierarchicaldata;

import java.util.List;

public interface IHierarchicalDataStore {
    void create(String path, String data);
    void update(String path, String data);
    boolean delete(String path);
    String  get(String path);
    List<Node> list(String path);
    Node getRoot();
    void addListener(String path, Listener listener);
}
