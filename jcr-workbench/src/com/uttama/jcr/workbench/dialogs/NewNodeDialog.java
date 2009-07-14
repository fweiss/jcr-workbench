package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.NewNodeParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class NewNodeDialog
extends CustomJDialog
implements ModelChangeListener {
	private final Frame owner;
	private NewNodeParameters parameters;
	private JLabel parent;
	private JTextField name;
	private JTextField primaryNodeTypeName;
	Action okAction;
	Action localOkAction;
	Action cancelAction;
	final static String title = "New Node Parameters";
	final static Dialog.ModalityType modal = Dialog.ModalityType.APPLICATION_MODAL;
	public NewNodeDialog(Frame owner, NewNodeParameters parameters) {
		super(owner, title, modal);
		this.owner = owner;
		this.parameters = parameters;
		addButtons();
	}
	// FIXME: use addNLabeledComponent
	protected void addFields() {
		parent = new JLabel();
		name = new JTextField(30);
		primaryNodeTypeName = new JTextField(30);
		LabeledGrid grid = new LabeledGrid();
		grid.addLabeledComponent("Parent", parent);
		grid.addLabeledComponent("Name", name);
		grid.addLabeledComponent("Primary node type", primaryNodeTypeName);
		this.getContentPane().add(grid, BorderLayout.CENTER);
	}
	public void show(Action okAction) {
		this.okAction = okAction;
		setSize(owner);
		super.setVisible(true);
	}
	@Override
	public void modelChanged(ModelChangeEvent mce) {
		NewNodeParameters p = (NewNodeParameters) mce.getSource();
		parent.setText(p.parent);
		name.setText(p.name);
		primaryNodeTypeName.setText(p.primaryNodeTypeName);
	}
	protected void okAction(ActionEvent ae) {
		parameters.name = name.getText();
		parameters.primaryNodeTypeName = primaryNodeTypeName.getText();
		okAction.actionPerformed(ae);
	}
}
