package com.uttama.jcr.workbench.view;

import javax.swing.JTabbedPane;

import com.uttama.jcr.workbench.ModelChangeEvent;
import com.uttama.jcr.workbench.ModelChangeListener;
import com.uttama.jcr.workbench.view.properties.NodeDataPanel;
import com.uttama.jcr.workbench.view.properties.NodeDefinitionPanel;
import com.uttama.jcr.workbench.view.properties.NodeVersionsPanel;

public class NodeTabbedPanel
extends JTabbedPane
implements ModelChangeListener {
	private NodeDataPanel nodeDataPanel;
	private NodeDefinitionPanel nodeDefinitionPanel;
	private NodeVersionsPanel nodeVersionsPanel;
	public NodeTabbedPanel(String name) {
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
		nodeDataPanel.modelChanged(mce);
		nodeDefinitionPanel.modelChanged(mce);
		nodeVersionsPanel.modelChanged(mce);
	}

}
