package com.uttama.jcr.workbench.view.swing;

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

public abstract class CustomJDialog
extends JDialog {
	final static Dialog.ModalityType modal = Dialog.ModalityType.APPLICATION_MODAL;
	private Action dialogOkAction;
	private Action dialogCancelAction;
	protected Box buttonBox;
	public CustomJDialog(Frame owner, String title, ModalityType modal) {
		super(owner, title, modal);
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
		buttonBox = Box.createHorizontalBox();
		dialogOkAction = new DialogOkAction("OK");
		dialogCancelAction = new DialogCancelAction("Cancel");
		buttonBox.add(new JButton(dialogOkAction));
		buttonBox.add(new JButton(dialogCancelAction));
		this.getContentPane().add(buttonBox, BorderLayout.SOUTH);
	}
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
