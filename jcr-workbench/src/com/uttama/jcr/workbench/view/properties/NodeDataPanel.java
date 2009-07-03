package com.uttama.jcr.workbench.view.properties;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.ModelChangeEvent;
import com.uttama.jcr.workbench.ModelChangeListener;
import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class NodeDataPanel
extends PropertyPanel
implements NodeChangedListener, ActionListener, FocusListener, ModelChangeListener {
	static Logger log = Logger.getLogger(NodeDataPanel.class);
	private NodeModel nodeModel;
	JTextField name;
	JTextField primaryType;
	JTextField uuid;
	JList references;
	JTable properties;
	JButton saveButton;
	private static Properties getLabels() {
		Properties labels = new Properties();
		labels.put("name", "Name:");
		labels.put("primaryType", "Primary Type:");
		labels.put("uuid", "UUID");
		labels.put("references", "References");
		labels.put("properties", "Properties:");
		return labels;
	}
	public NodeDataPanel(String name) {
		this((NodeModel) null);
		setName(name);
	}
	public NodeDataPanel(NodeModel nodeModel) {
		setName("node");
		this.nodeModel = nodeModel;
		//JPanel main = this;
		//main.setLayout(new FlowLayout(FlowLayout.LEFT));
		//main.setLayout(new BorderLayout());
		LabeledGrid group = new LabeledGrid();
		group.setLabels(getLabels());
		group.addNLabeledComponent("name", name = new JTextField(30));
		group.addNLabeledComponent("primaryType", primaryType = new JTextField(30));
		group.addNLabeledComponent("uuid", uuid = new JTextField(30));
		group.addNLabeledComponent("references", references = new JList());

		properties = createPropertiesTable();
		group.addNLabeledComponent("properties", properties);
		
		addForm(group);
		
		saveButton = new JButton("Save");
		addButton(saveButton);
		
		name.addActionListener(this);
		name.setActionCommand("foo");
		name.addFocusListener(this);
	}
	private JTable createPropertiesTable() {
		TableColumnModel tableColumnModel = new DefaultTableColumnModel();
		tableColumnModel.addColumn(new TableColumn(0));
		tableColumnModel.addColumn(new TableColumn(1));
		tableColumnModel.addColumn(new TableColumn(2));
		JTable table = new JTable(new DefaultTableModel(), tableColumnModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(160);
		table.getColumnModel().getColumn(2).setPreferredWidth(550);
		
		BevelBorder border = new BevelBorder(BevelBorder.LOWERED);
		border.getBorderInsets(table, new Insets(2, 2, 2, 2));
		table.setBorder(border);

		return table;
	}
	public void setModel(NodeModel nodeModel) {
		this.nodeModel = nodeModel;
		this.properties.setModel(nodeModel.getNodePropertiesModel());
		properties.getColumnModel().getColumn(0).setPreferredWidth(160);
		properties.getColumnModel().getColumn(2).setPreferredWidth(550);
		updateFields();
	}
	public void setSaveButtonAction(Action action) {
		saveButton.setAction(action);
	}
	public void setNode(Node node) {
		try {
			String nt = node.getPrimaryNodeType().getName();
			primaryType.setText(nt);
			name.setText(node.getName());
			uuid.setText(node.isNodeType("mix:referenceable") ? node.getUUID() : "");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void updateFields() {
		if (nodeModel.isDeleted()) {
			setEnabled(false);
		} else {
			setEnabled(true);
			Node node = nodeModel.getNode();
			try {
				String nt = node.getPrimaryNodeType().getName();
				primaryType.setText(nt);
				name.setText(node.getName());
				uuid.setText(node.isNodeType("mix:referenceable") ? node.getUUID() : "");
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String[] getRequiredNodeTypeNames() {
		List<String> names = new LinkedList<String>();
		NodeDefinition nodeDefinition;
		try {
			nodeDefinition = nodeModel.getNode().getDefinition();
			NodeType nodes[] = nodeDefinition.getRequiredPrimaryTypes();
			for (NodeType node : nodes)
				names.add(node.getName());
			return names.toArray(new String[]{ });
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void valueChanged(NodeChangedEvent nce) {
		Node node = nce.getNodeModel().getNode();
		try {
			String nt = node.getPrimaryNodeType().getName();
			primaryType.setText(nt);
			name.setText(node.getName());
			uuid.setText(node.isNodeType("mix:referenceable") ? node.getUUID() : "");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.trace("actionevent: " + ae.toString());
	}
	@Override
	public void focusGained(FocusEvent fe) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent fe) {
		log.trace("focus lost: " + fe.getComponent().getName());
		nodeModel.setName(name.getText());
	}
	public void modelChanged(ModelChangeEvent mce) {
		NodeModel nodeModel = (NodeModel) mce.getSource();
		setModel(nodeModel);
	}
}
