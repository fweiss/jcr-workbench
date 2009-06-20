package com.uttama.jcr.workbench.view.properties;

import javax.jcr.Repository;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class RepositoryPanel
extends PropertyPanel {
	private JTextField configuration;
	private JTextField repository;
	private JTextField username;
	private JTextField password;
	public JButton openButton;
	
	
	public RepositoryPanel() {
		super();
		//main.setLayout(new FlowLayout(FlowLayout.LEFT));

		LabeledGrid group = new LabeledGrid();
		group.addLabeledComponent("Configuration:", configuration = new JTextField(30));
		group.addLabeledComponent("Repository:", repository = new JTextField(30));
		group.addLabeledComponent("Username:", username = new JTextField(30));
		group.addLabeledComponent("Password:", password = new JTextField(30));
		addForm(group);
		//main.add(Box.createVerticalStrut(20));
		
		openButton = new JButton("Open");
		addButton(openButton);
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
	public String getConfiguration() {
		return configuration.getText();
	}
	public void setConfiguration(String configuration) {
		this.configuration.setText(configuration);
	}
	public String getRepository() {
		return repository.getText();
	}
	public void setRepository(String repository) {
		this.repository.setText(repository);
	}
	public String getUsername() {
		return username.getText();
	}
	public void setUsername(String username) {
		this.username.setText(username);
	}
	public String getPassword() {
		return password.getText();
	}
	public void setPassword(String password) {
		this.password.setText(password);
	}
}
