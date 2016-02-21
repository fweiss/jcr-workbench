package com.uttama.jcr.workbench.view;

import javax.swing.JTabbedPane;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.view.properties.NodeDataPanel;
import com.uttama.jcr.workbench.view.properties.NodeDefinitionPanel;
import com.uttama.jcr.workbench.view.properties.NodeVersionsPanel;

public class NodePanel
extends JTabbedPane
implements ModelChangeListener {
	public NodeDataPanel nodeDataPanel;
	private NodeDefinitionPanel nodeDefinitionPanel;
	private NodeVersionsPanel nodeVersionsPanel;
	public NodePanel(String name) {
		super();
		setName(name);
		nodeDataPanel = new NodeDataPanel("nodeDataPanel");
		nodeDefinitionPanel = new NodeDefinitionPanel("nodeDefinitionPanel");
		nodeVersionsPanel = new NodeVersionsPanel("nodeVersionsPanel");
		// TODO: locale
		add("Data", nodeDataPanel);
		add("Definition", nodeDefinitionPanel);
		add("Versions", nodeVersionsPanel);
	}

	@Override
	public void modelChanged(ModelChangeEvent mce) {
		stopEditing();
		nodeDataPanel.modelChanged(mce);
		nodeDefinitionPanel.modelChanged(mce);
		nodeVersionsPanel.modelChanged(mce);
	}
	protected void stopEditing() {
		// TODO: fallback if editing cannot be stopped
		nodeDataPanel.stopEditing();
	}
}
