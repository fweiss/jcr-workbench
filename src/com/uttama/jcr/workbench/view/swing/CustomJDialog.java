package com.uttama.jcr.workbench.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

/**
 * Customize JDialog with features common to the application:
 * 
 * borders, backgrounds, styling
 * a button pane
 * common OK and Cancel button actions
 * 
 * Subclasses should override the addFields() method to add the dialog-specific
 * labelled fields.
 * 
 * Override actionPerformed() to handle the OK click event.
 * 
 */
public abstract class CustomJDialog
extends JDialog {
	final static Dialog.ModalityType modal = Dialog.ModalityType.APPLICATION_MODAL;
	protected JPanel buttonPanel;
	private Action dialogOkAction;
	private Action dialogCancelAction;
	public CustomJDialog(Frame owner, String title, ModalityType modal) {
		super(owner, title, modal);
		
		JPanel contentPanel = new JPanel();
		BorderLayout layout = new BorderLayout();
		layout.setVgap(8);
		contentPanel.setLayout(layout);
		Color background = UIManager.getColor("Panel.background");
		contentPanel.setBorder(new MatteBorder(8, 8, 8, 8, background));
		setContentPane(contentPanel);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		addFields();
		addButtons();
		setSize(owner);
	}
	public CustomJDialog(Frame owner, String title) {
		this(owner, title, modal);
	}
	protected abstract void addFields();
	protected void setSize(Frame frame) {
		//setSize(500, 150);
		setSize(getPreferredSize());
		Dimension dd = getSize();
		Dimension fd = frame.getSize();
		Dimension sd = getToolkit().getScreenSize();
		Point l = frame.getLocation();
		l.translate((fd.width-dd.width)/2, (fd.height-dd.height)/2);
		l.x = Math.max(0, Math.min(l.x, sd.width-dd.width));
		l.y = Math.max(0, Math.min(l.y, sd.height-dd.height));
		setLocation(l.x, l.y);
	}
	protected void addButtons() {
		JButton defaultButton;
		dialogOkAction = new DialogOkAction("OK");
		dialogCancelAction = new DialogCancelAction("Cancel");
		buttonPanel.add(defaultButton = new JButton(dialogOkAction));
		buttonPanel.add(new JButton(dialogCancelAction));
		getRootPane().setDefaultButton(defaultButton);
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 150);
	}
	protected abstract void okAction(ActionEvent ae);
	class DialogOkAction
	extends AbstractAction {
		public DialogOkAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			okAction(ae);
		}
	}
	class DialogCancelAction
	extends AbstractAction {
		public DialogCancelAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			setVisible(false);
		}
	}
}
