package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.jcr.PropertyType;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.model.NodePropertyParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class NodePropertyDialog
extends CustomJDialog {
	static final Dimension preferredSize = new Dimension(500, 250);
	NodePropertyParameters nodePropertyParameters;
	private JTextField name;
	private JCheckBox isMulti;
	private JList type;
	private JTextField value;
	public Action okAction;
	private static Properties getLabels() {
		Properties labels = new Properties();
		labels.put("name", "Name");
		labels.put("multi", "Multi");
		labels.put("type", "Type");
		labels.put("value", "Value");
		return labels;
	}
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}
	public NodePropertyDialog(Frame owner, NodePropertyParameters nodePropertyParameters) {
		super(owner, "Node Property");
		this.nodePropertyParameters = nodePropertyParameters;
	}
	protected void addFields() {
		name = new JTextField(30);
		isMulti = new JCheckBox();
		type = new JList(getPropertyTypes());
		type.setSelectedIndex(0);
		value = new JTextField(30);
		
		LabeledGrid grid1 = new LabeledGrid(getLabels());
		grid1.addNLabeledComponent("name", name);
		grid1.addNLabeledComponent("multi", isMulti);
		getContentPane().add(grid1, BorderLayout.NORTH);
		
		LabeledGrid grid2 = new LabeledGrid(getLabels());
		grid2.addNLabeledComponent("value", value);
		getContentPane().add(grid2, BorderLayout.CENTER);

		getContentPane().add(new JScrollPane(type), BorderLayout.WEST);
	}
	protected void okAction(ActionEvent ae) {
		saveFields();
		okAction.actionPerformed(ae);
	}
	private void saveFields() {
		nodePropertyParameters.name = name.getText();
		nodePropertyParameters.value = value.getText();
		nodePropertyParameters.propertyType = PropertyType.valueFromName((String) type.getSelectedValue());
	}
	public String[] getPropertyTypes() {
		return new String[] {
			"String",
			"Double",
			"Long",
			"Boolean",
			"Date",
			"Name",
			"Binary",
			"Path",
			"Reference"
		};
	}
}
