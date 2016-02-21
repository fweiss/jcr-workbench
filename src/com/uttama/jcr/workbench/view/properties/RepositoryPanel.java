package com.uttama.jcr.workbench.view.properties;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.jcr.Repository;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository.RepositoryModelEvent;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository.RepositoryModelListener;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository.RepositoryModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class RepositoryPanel
extends PropertyPanel
implements ModelChangeListener, RepositoryModelListener {
	private RepositoryModel repositoryModel;
	private JTextField configuration;
	private JTextField repository;
	private JTextField username;
	private JTextField password;
	private JTable namespaces;
	
	public JButton openButton;
	
	public RepositoryPanel(String name) {
		super(name);

		LabeledGrid group = new LabeledGrid(getLabels());
		group.addNLabeledComponent("configuration", configuration = new JTextField(40));
		group.addNLabeledComponent("repository", repository = new JTextField(40));
		group.addNLabeledComponent("username", username = new JTextField(30));
		group.addNLabeledComponent("password", password = new JTextField(30));
		group.addNLabeledComponent("namespaces", createTableWrapper(namespaces = createNamespacesTable()));

		addForm(group);

		openButton = new JButton("Open");
		addButton(openButton);
	}
	protected JTable createNamespacesTable() {
		// FIXME: refactor to MVC, combine with NodePropertiesModel.getTableCoulmnModel()
		TableColumnModel tableColumnModel = new DefaultTableColumnModel();
		String columnNames[] = { "Prefix", "URI" };
		for (int i=0; i<columnNames.length; i++) {
			TableColumn column = new TableColumn(i);
			column.setHeaderValue(columnNames[i]);
			tableColumnModel.addColumn(column);
		}
		JTable table = new JTable(new DefaultTableModel(), tableColumnModel);
		return table;
	}
	/**
	 * Wrap the JTable with a JPanel with column headers.
	 * FIXME: refactor to utility/factory class
	 * @param table the table to be wrapped
	 * @return the wrapped table with column headers
	 */
	static JPanel createTableWrapper(JTable table) {
		JPanel tableContainer = new JPanel(new BorderLayout());
		tableContainer.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tableContainer.add(table, BorderLayout.CENTER);
		return tableContainer;
	}
	protected Properties getLabels() {
		Properties labels = new Properties();
		labels.put("configuration", "Configuration");
		labels.put("repository", "Repository");
		labels.put("username", "User Name");
		labels.put("password", "Password");
		labels.put("namespaces", "Namespaces");
		return labels;
	}
	public void setDescriptors(Repository repository) {
		LabeledGrid dg = new LabeledGrid();
		for (String key : repository.getDescriptorKeys()) {
			String value = repository.getDescriptor(key);
			dg.addLabeledComponent(key + ":", new JLabel(value));
		}
		add(dg);
		this.doLayout();
	}
	protected void updateFields() {
		configuration.setText(repositoryModel.getConfigurationPath());
		repository.setText(repositoryModel.getRepositoryPath());
		username.setText(repositoryModel.getUsername());
		password.setText(repositoryModel.getPassword());
		updateNamespacesField(repositoryModel);
	}
	private void updateNamespacesField(RepositoryModel model) {
		namespaces.setModel(model.getNamespaceTableModel());
		namespaces.getColumnModel().getColumn(1).setPreferredWidth(480);
	}
	public void saveFields(RepositoryModel repositoryModel) {
		repositoryModel.setConfigurationPath(configuration.getText());
		repositoryModel.setRepositoryPath(repository.getText());
		repositoryModel.setUsername(username.getText());
		repositoryModel.setPassword(password.getText());
	}
	public void modelChanged(ModelChangeEvent mce) {
		this.repositoryModel = (RepositoryModel) mce.getSource();
		updateFields();
	}

    // RepositoryModelListener

    @Override
    public void namespacesChanged(RepositoryModelEvent rme) {
        updateNamespacesField(rme.getModel());
    }
    @Override
    public void nodeStatusChanged(RepositoryModelEvent rpe) {
        /* ignored */
    }
}
