package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.model.NodePropertyParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class NodePropertyDialog
extends CustomJDialog {
	NodePropertyParameters nodePropertyParameters;
	private JTextField name;
	private JComboBox type;
	private JTextField value;
	public Action okAction;
	private static Properties getLabels() {
		Properties labels = new Properties();
		labels.put("name", "Name");
		labels.put("type", "Type");
		labels.put("value", "Value");
		return labels;
	}
	public NodePropertyDialog(Frame owner, NodePropertyParameters nodePropertyParameters) {
		super(owner, "Node Property");
		this.nodePropertyParameters = nodePropertyParameters;
	}
	protected void addFields() {
		name = new JTextField(30);
		type = new JComboBox(getPropertyTypes());
		value = new JTextField(30);
		LabeledGrid grid = new LabeledGrid(getLabels());
		grid.addNLabeledComponent("name", name);
		grid.addNLabeledComponent("type", type);
		grid.addNLabeledComponent("value", value);
		this.getContentPane().add(grid, BorderLayout.CENTER);
	}
	protected void okAction(ActionEvent ae) {
		saveFields();
		okAction.actionPerformed(ae);
	}
	private void saveFields() {
		nodePropertyParameters.name = name.getText();
		nodePropertyParameters.value = value.getText();
	}
	public String[] getPropertyTypes() {
		return new String[] {
			"BINARY",
			"BOOLEAN",
			"DATE",
			"DOUBLE",
			"LONG",
			"NAME",
			"PATH",
			"REFERENCE",
			"STRING"
		};
	}
}
