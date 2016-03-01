package com.uttama.jcr.workbench.model.node.version;

public class NodeVersionModelEvent {
    private NodeVersionModel model;
    public NodeVersionModelEvent(NodeVersionModel model) {
        this.model = model;
    }
    public NodeVersionModel getMode() {
        return model;
    }

}
