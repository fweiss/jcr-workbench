package com.uttama.jcr.workbench.view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.model.NewNodeParameters;

public class NewNodeDialog
extends JDialog {
	private final Frame owner;
	private NewNodeParameters parameters;
	private JTextField name = new JTextField(30);
	private JTextField primaryNodeTypeName = new JTextField(30);
	Action okAction;
	Action localOkAction;
	Action cancelAction;
	final static String title = "New Node Parameters";
	final static Dialog.ModalityType modal = Dialog.ModalityType.APPLICATION_MODAL;
	public NewNodeDialog(Frame owner, NewNodeParameters parameters) {
		super(owner, title, modal);
		this.owner = owner;
		this.parameters = parameters;
		addFields();
		addButtons();
		setSize(owner);
	}
	private void addButtons() {
		Box box = Box.createHorizontalBox();
		localOkAction = new OKAction("OK");
		cancelAction = new CancelAction("Cancel");
		JButton okButton = new JButton(localOkAction);
		box.add(okButton);
		JButton cancelButton = new JButton(cancelAction);
		box.add(cancelButton);
		this.getContentPane().add(box, BorderLayout.SOUTH);
	}
	private void addFields() {
		LabeledGrid grid = new LabeledGrid();
		grid.addLabeledComponent("Name", name);
		grid.addLabeledComponent("Primary node type", primaryNodeTypeName);
		this.getContentPane().add(grid, BorderLayout.CENTER);
	}
	private void setSize(Frame frame) {
		setSize(500, 150);
		Dimension dd = getSize();
		Dimension fd = frame.getSize();
		Dimension sd = getToolkit().getScreenSize();
		Point l = frame.getLocation();
		l.translate((fd.width-dd.width)/2, (fd.height-dd.height)/2);
		l.x = Math.max(0, Math.min(l.x, sd.width-dd.width));
		l.y = Math.max(0, Math.min(l.y, sd.height-dd.height));
		setLocation(l.x, l.y);
	}
	public void show(Action okAction) {
		this.okAction = okAction;
		setSize(owner);
		super.show();
	}
	class OKAction
	extends AbstractAction {
		public OKAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			parameters.name = name.getText();
			parameters.primaryNodeTypeName = primaryNodeTypeName.getText();
			okAction.actionPerformed(ae);
		}
	}
	class CancelAction
	extends AbstractAction {
		public CancelAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			hide();
		}
	}
}
