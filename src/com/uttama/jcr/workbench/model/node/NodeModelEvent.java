package com.uttama.jcr.workbench.model.node;

public class NodeModelEvent {
    private final NodeModel nodeModel;
    private boolean nameChanged;
    public NodeModelEvent(NodeModel nodeModel) {
        this.nodeModel = nodeModel;
        this.nameChanged = false;
    }
    public NodeModel getNodeModel() {
        return this.nodeModel;
    }
    public void setNameChanged(boolean changed) {
        this.nameChanged = changed;
    }
    public boolean isNameChanged() {
        return this.nameChanged;
    }
}
