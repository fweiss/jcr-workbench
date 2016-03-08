package com.uttama.jcr.workbench.view.node;

import javax.swing.JTabbedPane;

import com.uttama.jcr.workbench.view.ViewModelChangeEvent;
import com.uttama.jcr.workbench.view.ViewModelChangeListener;
import com.uttama.jcr.workbench.model.node.version.NodeVersionModelEvent;
import com.uttama.jcr.workbench.model.node.version.NodeVersionModelListener;

/**
 * The NodePanel composes the node's data into tabbed panels to reduce UI clutter.
 */
public class NodePanel
extends JTabbedPane
implements ViewModelChangeListener, NodeVersionModelListener {
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
    public NodeVersionsPanel getNodeVersionPanel() {
        return nodeVersionsPanel;
    }
    public NodeDataPanel getNodeDataPanel() {
        return nodeDataPanel;
    }

    /* NodeVersionModelListener */

    @Override // delegate
    public void versionsChanged(NodeVersionModelEvent nvme) {
        nodeVersionsPanel.versionsChanged(nvme);
    }

    // ModelChangeListener

    @Override
    public void modelChanged(ViewModelChangeEvent mce) {
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
