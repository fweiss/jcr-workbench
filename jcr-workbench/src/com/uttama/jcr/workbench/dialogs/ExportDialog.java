package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.model.ExportNodeParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class ExportDialog
extends CustomJDialog {
	private ExportNodeParameters parameters;
	private JTextField nodePath;
	private JCheckBox includeSubtree;
	private JCheckBox includeBinary;
	private JTextField filePath;
	private JFileChooser fileChooser;
	Action okAction;
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
		this.parameters = null;
	}
	protected void addFields() {
		nodePath = new JTextField(40);
		includeSubtree = new JCheckBox();
		includeBinary = new JCheckBox();
		filePath = new JTextField(40);
		fileChooser = new JFileChooser();
		LabeledGrid grid = new LabeledGrid(getLabels());
		grid.addNLabeledComponent("nodePath", nodePath);
		grid.addNLabeledComponent("includeSubtree", includeSubtree);
		grid.addNLabeledComponent("includeBinary", includeBinary);
		grid.addNLabeledComponent("filePath", filePath);
		this.getContentPane().add(grid, BorderLayout.CENTER);
	}
	public void addButtons() {
		super.addButtons();
		browseAction = new BrowseAction("Browse");
		buttonPanel.add(new JButton(browseAction));
	}
	public Dimension getPreferredSize() {
		return new Dimension(500, 250);
	}
	public void show(Action okAction, ExportNodeParameters parameters) {
		this.okAction = okAction;
		this.parameters = parameters;
		updateFields();
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
	protected void okAction(ActionEvent ae) {
		saveFields();
		okAction.actionPerformed(ae);
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
}
