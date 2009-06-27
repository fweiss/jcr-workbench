package com.uttama.jcr.workbench.view.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.ModelChangeEvent;
import com.uttama.jcr.workbench.ModelChangeListener;
import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class NodePanel
extends PropertyPanel
implements NodeChangedListener, ActionListener, FocusListener, ModelChangeListener {
	static Logger log = Logger.getLogger(NodePanel.class);
	private NodeModel nodeModel;
	JTextField primaryType;
	JTextField name;
	JTextField mixins;
	JTextField uuid;
	JTable properties;
	JButton saveButton;
	public NodePanel(NodeModel nodeModel) {
		setName("node");
		this.nodeModel = nodeModel;
		//JPanel main = this;
		//main.setLayout(new FlowLayout(FlowLayout.LEFT));
		//main.setLayout(new BorderLayout());
		LabeledGrid group = new LabeledGrid();
		group.setLabels(getLabels());
		group.addNLabeledComponent("primaryType", primaryType = new JTextField(30));
		group.addNLabeledComponent("name", name = new JTextField(30));
		group.addNLabeledComponent("mixins", mixins = new JTextField(10));
		group.addNLabeledComponent("uuid", uuid = new JTextField(30));
		
		properties = new JTable();
		properties.setModel(nodeModel.getNodePropertiesModel());
		properties.getColumnModel().getColumn(0).setPreferredWidth(160);
		properties.getColumnModel().getColumn(2).setPreferredWidth(550);
		group.addNLabeledComponent("properties", properties);
		
		addForm(group);
		//main.add(Box.createVerticalStrut(20));
		nodeModel.addNodeChangedListener(this);
		//this.setBackground(Color.GREEN);
		saveButton = new JButton("Save");
		addButton(saveButton);
		
		name.addActionListener(this);
		name.setActionCommand("foo");
		name.addFocusListener(this);
	}
	public void setModel(NodeModel nodeModel) {
		this.nodeModel = nodeModel;
		this.properties.setModel(nodeModel.getNodePropertiesModel());
		properties.getColumnModel().getColumn(0).setPreferredWidth(160);
		properties.getColumnModel().getColumn(2).setPreferredWidth(550);
		updateFields();
	}
	private Properties getLabels() {
		Properties labels = new Properties();
		labels.put("primaryType", "Primary Type:");
		labels.put("name", "Name:");
		labels.put("mixins", "Mixins:");
		labels.put("uuid", "UUID");
		labels.put("properties", "Properties:");
		return labels;
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
