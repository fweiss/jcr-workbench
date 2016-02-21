package com.uttama.jcr.workbench.events;

import com.uttama.jcr.workbench.model.NodeModel;

public class NodeChangedEvent {
	private final NodeModel nodeModel;
	private boolean nameChanged;
	public NodeChangedEvent(NodeModel nodeModel) {
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
