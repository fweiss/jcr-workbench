package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.ExportNodeParameters;
import com.uttama.jcr.workbench.model.NewNodeParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;

public class ExportDialog
extends JDialog
implements ModelChangeListener {
	private final Frame owner;
	private ExportNodeParameters parameters;
	private JTextField nodePath = new JTextField(40);
	private JCheckBox includeSubtree = new JCheckBox();
	private JCheckBox includeBinary = new JCheckBox();
	private JTextField filePath = new JTextField(40);
	private JFileChooser fileChooser = new JFileChooser();
	Action okAction;
	Action localOkAction;
	Action cancelAction;
	Action browseAction;
	final static String title = "Export Tree";
	final static Dialog.ModalityType modal = Dialog.ModalityType.APPLICATION_MODAL;
	protected static Properties getLabels() {
		Properties labels = new Properties();
		labels.put("nodePath", "Node Path");
		labels.put("includeSubtree", "Include Subtree");
		labels.put("includeBinary", "Include Binary");
		labels.put("filePath", "FilePath");
		return labels;
	}
	public ExportDialog(Frame owner) {
		super(owner, title, modal);
		this.owner = owner;
		this.parameters = null;
		addFields();
		addButtons();
		setSize(owner);
	}
	protected void addFields() {
		LabeledGrid grid = new LabeledGrid(getLabels());
		grid.addNLabeledComponent("nodePath", nodePath);
		grid.addNLabeledComponent("includeSubtree", includeSubtree);
		grid.addNLabeledComponent("includeBinary", includeBinary);
		grid.addNLabeledComponent("filePath", filePath);
		this.getContentPane().add(grid, BorderLayout.CENTER);
	}
	private void addButtons() {
		Box box = Box.createHorizontalBox();
		localOkAction = new OKAction("OK");
		cancelAction = new CancelAction("Cancel");
		browseAction = new BrowseAction("Browse");
		JButton okButton = new JButton(localOkAction);
		box.add(okButton);
		JButton cancelButton = new JButton(cancelAction);
		box.add(cancelButton);
		box.add(new JButton(browseAction));
		this.getContentPane().add(box, BorderLayout.SOUTH);
	}
	// TODO: refactor to base class
	private void setSize(Frame frame) {
		setSize(500, 250);
		Dimension dd = getSize();
		Dimension fd = frame.getSize();
		Dimension sd = getToolkit().getScreenSize();
		Point l = frame.getLocation();
		l.translate((fd.width-dd.width)/2, (fd.height-dd.height)/2);
		l.x = Math.max(0, Math.min(l.x, sd.width-dd.width));
		l.y = Math.max(0, Math.min(l.y, sd.height-dd.height));
		setLocation(l.x, l.y);
	}
	public void show(Action okAction, ExportNodeParameters parameters) {
		this.okAction = okAction;
		this.parameters = parameters;
		updateFields();
		setSize(owner);
		super.setVisible(true);
	}
	protected void updateFields() {
		nodePath.setText(parameters.nodePath);
		includeSubtree.setSelected(parameters.includeSubtree);
		includeBinary.setSelected(parameters.includeBinary);
		filePath.setText(parameters.file.getPath());
	}
	protected void saveFields() {
		parameters.nodePath = nodePath.getText();
		parameters.includeSubtree = includeSubtree.isSelected();
		parameters.includeBinary = includeBinary.isSelected();
		parameters.file = fileChooser.getSelectedFile();
	}
	class BrowseAction
	extends AbstractAction {
		public BrowseAction(String label) {
			super(label);
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			int selected = fileChooser.showSaveDialog(getContentPane());
			if (selected == JFileChooser.APPROVE_OPTION) {
				// FIXME: use button listeners/actions?
				saveFields();
				parameters.file = fileChooser.getSelectedFile();
				updateFields();
			}
		}
	}
	class OKAction
	extends AbstractAction {
		public OKAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			saveFields();
			okAction.actionPerformed(ae);
		}
	}
	class CancelAction
	extends AbstractAction {
		public CancelAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			setVisible(false);
		}
	}
	@Override
	public void modelChanged(ModelChangeEvent mce) {
		NewNodeParameters p = (NewNodeParameters) mce.getSource();
		//parent.setText(p.parent);
		//name.setText(p.name);
		//primaryNodeTypeName.setText(p.primaryNodeTypeName);
	}
}
