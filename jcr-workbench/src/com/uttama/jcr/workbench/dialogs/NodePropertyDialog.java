package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jcr.PropertyType;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.model.NodePropertyParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class NodePropertyDialog
extends CustomJDialog
implements ActionListener {
	static final Dimension preferredSize = new Dimension(800, 450);
	NodePropertyParameters nodePropertyParameters;
	private JTextField name;
	private JCheckBox isMulti;
	private JList type;
	private JTextField value;
	private JTextField errorValueFormat;
	public Action okAction;
	ButtonGroup group;
	private static Properties getLabels() {
		Properties labels = new Properties();
		labels.put("name", "Name");
		labels.put("multi", "Multi");
		labels.put("type", "Type");
		labels.put("value", "Value");
		labels.put("error", "Error");
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
		createPropertyTypeList();
		value = new JTextField(30);
		errorValueFormat = new JTextField(60);
		
		LabeledGrid grid1 = new LabeledGrid(getLabels());
		grid1.addNLabeledComponent("name", name);
		grid1.addNLabeledComponent("type", new JScrollPane(type));
		//grid1.addNLabeledComponent("type", createPropertyTypePanel());
		grid1.addNLabeledComponent("value", value);
		grid1.addNLabeledComponent("multi", isMulti);
		getContentPane().add(grid1, BorderLayout.NORTH);
		
		LabeledGrid grid2 = new LabeledGrid(getLabels());
		grid2.addNLabeledComponent("error", errorValueFormat);
		getContentPane().add(grid2, BorderLayout.CENTER);

		//getContentPane().add(new JScrollPane(type), BorderLayout.WEST);
	}
	/**
	 * Create a radio button-type list with a compact layout.
	 */
	private void createPropertyTypeList() {
		type = new JList(getPropertyTypes());
		type.setPreferredSize(new Dimension(260, 40));
		type.setVisibleRowCount(0);
		type.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		type.setSelectedIndex(0);
	}
	/**
	 * Create a radio button property type selector.
	 * @return
	 */
	private JPanel createPropertyTypePanel() {
		JPanel panel = new JPanel(new FlowLayout());
		group = new ButtonGroup();
		for (String label : getPropertyTypes()) {
			JRadioButton button = new JRadioButton(label);
			panel.add(button);
			group.add(button);
			button.addActionListener(this);
		}
		return panel;
	}
	protected void okAction(ActionEvent ae) {
		saveFields();
		okAction.actionPerformed(ae);
	}
	private void saveFields() {
		nodePropertyParameters.name = name.getText();
		nodePropertyParameters.value = value.getText();
		nodePropertyParameters.propertyType = PropertyType.valueFromName((String) type.getSelectedValue());
//		Object rbs[] = group.getSelection().getSelectedObjects();
//		if (rbs != null && rbs.length > 0) {
//			JRadioButton rb = (JRadioButton) rbs[0];
//			nodePropertyParameters.propertyType = PropertyType.valueFromName(rb.getText());
//		}
	}
	private void updateFields() {
		errorValueFormat.setText(nodePropertyParameters.errorMessage);
	}
	@Override
	public void setVisible(boolean visible) {
		updateFields();
		super.setVisible(visible);
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JRadioButton) {
			JRadioButton button = (JRadioButton) e.getSource();
			nodePropertyParameters.propertyType = PropertyType.valueFromName((String) button.getText());
		}
	}
}
