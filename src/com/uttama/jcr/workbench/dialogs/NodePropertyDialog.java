package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.jcr.PropertyType;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.JCRWorkbench;
import com.uttama.jcr.workbench.model.NodePropertyParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

/**
 * This dialog provides a means for entering and editing the value of a node property.
 * There are basically three fields: name, type and value. There is also an error panel
 * and a means for changing to and from multiple values.
 * 
 * The name is a simple text field, but there is a restriction on the characters. It is also
 * supposed to be namespace aware?
 * 
 * The type field is a closed enumeration. To save space and clicks, this is implemented as a
 * two-line select list. A drop-down list would require an additional click, a vertical select
 * list would use more space, and a radio button set would be fussy.
 * 
 * The value field adjusts depending on the type that is selected. For the boolean type, it
 * is a checkbox. The different value fields are displayed via CardLayout.
 *
 */
public class NodePropertyDialog
extends CustomJDialog
implements ActionListener, ListSelectionListener {
	private static final Logger log = Logger.getLogger(NodePropertyDialog.class);
	static final Dimension preferredSize = new Dimension(800, 450);
	NodePropertyParameters nodePropertyParameters;
	private JTextField name;
	private JCheckBox isMulti;
	private JList type;
	private JTextField value;
	private JTextArea errorValueFormat;
	public Action okAction;
	ButtonGroup group;
	ValueCardPanel valueCardPanel;
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
	@Override
	protected void addFields() {
		name = new JTextField(30);
		isMulti = new JCheckBox();
		createPropertyTypeList();
		type.addListSelectionListener(this);
		value = new JTextField(30);
		errorValueFormat = new JTextArea(); //JTextField(60);
		
		valueCardPanel = new ValueCardPanel();
		
		LabeledGrid grid1 = new LabeledGrid(getLabels());
		grid1.addNLabeledComponent("name", name);
		grid1.addNLabeledComponent("type", new JScrollPane(type));
		//grid1.addNLabeledComponent("type", createPropertyTypePanel());
		//grid1.addNLabeledComponent("value", value);
		grid1.addNLabeledComponent("value", valueCardPanel);
		grid1.addNLabeledComponent("multi", isMulti);
		//getContentPane().add(grid1, BorderLayout.NORTH);
		
		//LabeledGrid grid2 = new LabeledGrid(getLabels());
		//grid2.addNLabeledComponent("error", errorValueFormat);
		//getContentPane().add(grid2, BorderLayout.CENTER);

		//getContentPane().add(new JScrollPane(type), BorderLayout.WEST);
		JPanel leftJustify = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftJustify.add(grid1);
		JSplitPane splitPane = createSplitPane(leftJustify, errorValueFormat);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		
	}
	private JSplitPane createSplitPane(Component leftPane, Component rightPane) {
		JSplitPane splitPane;
        int orientation = JSplitPane.VERTICAL_SPLIT;
        boolean continuousLayout = true;
        Component left = new JScrollPane(leftPane);
        Component right = new JScrollPane(rightPane);
        splitPane = new JSplitPane(orientation, continuousLayout, left, right);
    	splitPane.setDividerLocation(260);
		return splitPane;
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
	@Override
	protected void okAction(ActionEvent ae) {
		try {
			saveFields();
			okAction.actionPerformed(ae);
		} catch (Exception e) {
			errorValueFormat.setText(e.toString());
		}
	}
	private void saveFields()
	throws Exception {
		nodePropertyParameters.name = name.getText();
		nodePropertyParameters.propertyType = PropertyType.valueFromName((String) type.getSelectedValue());
		nodePropertyParameters.value = valueCardPanel.getValueFromField(nodePropertyParameters.propertyType);
//		Object rbs[] = group.getSelection().getSelectedObjects();
//		if (rbs != null && rbs.length > 0) {
//			JRadioButton rb = (JRadioButton) rbs[0];
//			nodePropertyParameters.propertyType = PropertyType.valueFromName(rb.getText());
//		}
	}
	private void updateFields() {
		name.setText(nodePropertyParameters.name);
		String propertyTypeName = PropertyType.nameFromValue(nodePropertyParameters.propertyType);
		List<String> typeNames = Arrays.asList(getPropertyTypes());
		int index = typeNames.indexOf(propertyTypeName);
		log.trace("property type: " + propertyTypeName + " index: " + index);
		type.setSelectedIndex(index);
		valueCardPanel.show(propertyTypeName);
		errorValueFormat.setText(nodePropertyParameters.errorMessage);
	}
	@Override
	public void setVisible(boolean visible) {
		updateFields();
		super.setVisible(visible);
	}
	public void clearErrors() {
		nodePropertyParameters.errorMessage = null;
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
			String typeName = button.getText();
			nodePropertyParameters.propertyType = PropertyType.valueFromName(typeName);
			valueCardPanel.show(typeName);
		}
	}
	class ValueCardPanel
	extends JPanel {
		private CardLayout cardLayout = new CardLayout();
		private JTextArea stringValue = new JTextArea();
		private JTextField longValue = new JTextField(40);
		private JTextField doubleValue = new JTextField(40);
		private JCheckBox booleanValue = new JCheckBox();;
		private JTextField dateValue = new JTextField(40);
		private JTextField nameValue = new JTextField(40);
		private JTextField binaryValue = new JTextField(40);
		private JTextField pathValue = new JTextField(40);
		private JTextField referenceValue = new JTextField(40);
		public ValueCardPanel() {
			super();
			setLayout(cardLayout);
			add(stringValue, "String");
			add(doubleValue, "Double");
			add(longValue, "Long");
			add(booleanValue, "Boolean");
			add(dateValue, "Date");
			add(nameValue, "Name");
			add(binaryValue, "Binary");
			add(pathValue, "Path");
			add(referenceValue, "Reference");
		}
		public void show(String name) {
			cardLayout.show(this, name);
		}
		private Object getValueFromField(int propertyType)
		throws ParseException {
			switch (nodePropertyParameters.propertyType) {
			case PropertyType.BINARY:
				return null;
			case PropertyType.BOOLEAN:
				return booleanValue.isSelected();
			case PropertyType.DATE:
				SimpleDateFormat dateFormat = new SimpleDateFormat();
				dateFormat.setLenient(true);
				dateFormat.parse(dateValue.getText());
				return dateFormat.getCalendar();
			case PropertyType.DOUBLE:
				return Double.parseDouble(doubleValue.getText());
			case PropertyType.LONG:
				return Long.parseLong(longValue.getText());
			case PropertyType.NAME:
				return nameValue.getText();
			case PropertyType.PATH:
				return pathValue.getText();
			case PropertyType.REFERENCE:
				return referenceValue.getText();
			case PropertyType.STRING:
				return stringValue.getText();
			default:
				return null;
			}
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() instanceof JList) {
			JList list = (JList) e.getSource();
			String typeName = (String) list.getSelectedValue();
			nodePropertyParameters.propertyType = PropertyType.valueFromName(typeName);
			valueCardPanel.show(typeName);
		}
	}
	public void valueChanged(NodePropertyParameters parameters) {
		nodePropertyParameters = parameters;
		updateFields();
	}
}
