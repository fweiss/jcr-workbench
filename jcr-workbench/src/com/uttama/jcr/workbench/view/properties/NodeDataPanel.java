package com.uttama.jcr.workbench.view.properties;

import java.awt.BorderLayout;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.dialogs.NodePropertyDialog;
import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.model.NodePropertiesModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;
import com.uttama.jcr.workbench.view.swing.PropertyTable;

public class NodeDataPanel
extends PropertyPanel
implements NodeChangedListener, ActionListener, FocusListener, ModelChangeListener {
	private static final TableCellRenderer defaultTableCellRenderer = null;
	static Logger log = Logger.getLogger(NodeDataPanel.class);
	private NodeModel nodeModel;
	JTextField name;
	JTextField primaryType;
	JTextField uuid;
	JList references;
	PropertyTable properties;
	
	public NodePropertyDialog nodePropertyDialog;
	
	JButton saveButton;
	Action panelAddPropertyAction;
	Action panelDeletePropertyAction;
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
	// FIXME: remove model from constructor
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
		JPanel tableContainer = new JPanel(new BorderLayout());
		tableContainer.add(properties.getTableHeader(), BorderLayout.PAGE_START);
		tableContainer.add(properties, BorderLayout.CENTER);
		BevelBorder border = new BevelBorder(BevelBorder.LOWERED);
		border.getBorderInsets(tableContainer, new Insets(2, 2, 2, 2));
		//tableContainer.setBorder(border);
		group.addNLabeledComponent("properties", tableContainer);
		
		addForm(group);

		createActions();
		saveButton = new JButton("Save");
		addButton(saveButton);
		createButtons();
		
		name.addActionListener(this);
		name.setActionCommand("foo");
		name.addFocusListener(this);
	}
	private PropertyTable createPropertiesTable() {
		TableColumnModel tableColumnModel = NodePropertiesModel.getTableColumnModel();
		PropertyTable table = new PropertyTable(new DefaultTableModel(), tableColumnModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(160);
		table.getColumnModel().getColumn(2).setPreferredWidth(550);
		return table;
	}
	public void setModel(NodeModel nodeModel) {
		this.nodeModel = nodeModel;
		this.properties.setModel(nodeModel.getNodePropertiesModel());
		nodeModel.getNodePropertiesModel().addTableModelListener(this.properties);
		//properties.getColumnModel().getColumn(0).setPreferredWidth(160);
		//properties.getColumnModel().getColumn(2).setPreferredWidth(550);
		updateFields();
	}
	public void setSaveButtonAction(Action action) {
		saveButton.setAction(action);
	}
	private void createActions() {
		panelAddPropertyAction = new AbstractAction("Add Property") {
			public void actionPerformed(ActionEvent ae) {
				if (nodePropertyDialog != null)
				nodePropertyDialog.setVisible(true);
			}
		}; 
	}
	private void createButtons() {
		addButton(new JButton(panelAddPropertyAction));
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
		if ( ! nodeModel.isDeleted())
			references.setListData(nodeModel.getReferencePaths());
	}
}
