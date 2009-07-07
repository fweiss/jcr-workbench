package com.uttama.jcr.workbench.view.properties;

import java.util.Properties;

import javax.jcr.Repository;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.RepositoryModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class RepositoryPanel
extends PropertyPanel
implements ModelChangeListener {
	private RepositoryModel repositoryModel;
	private JTextField configuration;
	private JTextField repository;
	private JTextField username;
	private JTextField password;
	public JButton openButton;
	
	public RepositoryPanel(String name) {
		super(name);
		//main.setLayout(new FlowLayout(FlowLayout.LEFT));

		LabeledGrid group = new LabeledGrid(getLabels());
		group.addNLabeledComponent("configuration", configuration = new JTextField(40));
		group.addNLabeledComponent("repository", repository = new JTextField(40));
		group.addNLabeledComponent("username", username = new JTextField(30));
		group.addNLabeledComponent("password", password = new JTextField(30));
		addForm(group);
		//main.add(Box.createVerticalStrut(20));
		
		openButton = new JButton("Open");
		addButton(openButton);
	}
	protected Properties getLabels() {
		Properties labels = new Properties();
		labels.put("configuration", "Configuration");
		labels.put("repository", "Repository");
		labels.put("username", "User Name");
		labels.put("password", "Password");
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
}
