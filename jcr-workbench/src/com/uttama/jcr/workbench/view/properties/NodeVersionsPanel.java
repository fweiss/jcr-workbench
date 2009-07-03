package com.uttama.jcr.workbench.view.properties;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JList;

import com.uttama.jcr.workbench.ModelChangeEvent;
import com.uttama.jcr.workbench.ModelChangeListener;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class NodeVersionsPanel
extends PropertyPanel
implements ModelChangeListener {
	private JCheckBox isVersionable = new JCheckBox();
	private JList versionLabels = new JList();
	public static Properties getLabels() {
		Properties labels = new Properties();
		labels.put("isVersionable", "Versionable");
		labels.put("versionLabels", "Version Labels");
		return labels;
	}
	public NodeVersionsPanel(String name) {
		super(name);
		
		LabeledGrid fields = new LabeledGrid(getLabels());
		fields.addNLabeledComponent("isVersionable", isVersionable);
		fields.addNLabeledComponent("versionLabels", versionLabels);
		addForm(fields);
	}
	private void updateFields(NodeModel nodeModel) {
		isVersionable.setSelected(nodeModel.isVersionable());
		if (nodeModel.isVersionable()) {
			versionLabels.setListData(nodeModel.getAllVersionLabels());
		} else {
			versionLabels.setListData(new String[]{ });
		}
	}
	@Override
	public void modelChanged(ModelChangeEvent mce) {
		NodeModel nodeModel = (NodeModel) mce.getSource();
		updateFields(nodeModel);
	}
}
