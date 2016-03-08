package com.uttama.jcr.workbench.view.node;

import java.util.Properties;

import javax.swing.*;

import com.uttama.jcr.workbench.view.ViewModelChangeEvent;
import com.uttama.jcr.workbench.view.ViewModelChangeListener;
import com.uttama.jcr.workbench.model.node.NodeModel;
import com.uttama.jcr.workbench.model.node.version.NodeVersionModel;
import com.uttama.jcr.workbench.model.node.version.NodeVersionModelEvent;
import com.uttama.jcr.workbench.model.node.version.NodeVersionModelListener;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeVersionsPanel
extends PropertyPanel
implements ViewModelChangeListener, NodeVersionModelListener {
    static Logger log = LoggerFactory.getLogger(NodeVersionsPanel.class);

    private JCheckBox isVersionable = new JCheckBox();
    private JList versionLabels = new JList();
    private JTable versionTable = new JTable();

    private NodeModel nodeModel;
    private NodeVersionModel nodeVersionModel;

    public NodeVersionsPanel(String name) {
        super(name);

        LabeledGrid fields = new LabeledGrid(getLabels());
        fields.addNLabeledComponent("isVersionable", isVersionable);
        fields.addNLabeledComponent("versionLabels", versionLabels);
        fields.addNLabeledComponent("versions", versionTable);
        addForm(fields);
    }
    // FIXME move to properties file or i18n
    public static Properties getLabels() {
        Properties labels = new Properties();
        labels.put("isVersionable", "Versionable");
        labels.put("versionLabels", "Version Labels");
        labels.put("versions", "Versions");
        return labels;
    }
    public void setModel(NodeModel nodeModel) {
        this.nodeModel = nodeModel;
        this.nodeVersionModel = nodeModel.getNodeVersionModel();
        versionTable.setModel(this.nodeVersionModel);
        updateFields(nodeModel);
    }
    private void updateFields(NodeModel nodeModel) {
        isVersionable.setSelected(nodeModel.isVersionable());
        if (nodeModel.isVersionable()) {
            versionLabels.setListData(nodeModel.getAllVersionLabels());
        } else {
            versionLabels.setListData(new String[]{ });
        }
    }

    // ModelChangeListener

    @Override
    public void modelChanged(ViewModelChangeEvent mce) {
        NodeModel nodeModel = ((NodeModel) mce.getSource());
        versionTable.setModel(nodeModel.getNodeVersionModel());
        updateFields(nodeModel);
    }

    // NodeVersionModelListener

    @Override
    public void versionsChanged(NodeVersionModelEvent nvme) {

    }
}
