package com.uttama.jcr.workbench.view.node;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.sun.deploy.util.StringUtils;
import com.uttama.jcr.workbench.view.ViewModelChangeEvent;
import com.uttama.jcr.workbench.view.ViewModelChangeListener;
import com.uttama.jcr.workbench.model.node.NewNodeParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class NewNodeDialog
extends CustomJDialog
implements ViewModelChangeListener {
    private NewNodeParameters parameters;

    private JLabel parentPath;
    private JTextField name;
    private JTextField primaryNodeTypeName;
    private JTextField mixinNodeTypes;

    Action okAction;
    Action localOkAction;
    Action cancelAction;
    final static String title = "New Node Parameters";
    final static Dialog.ModalityType modal = Dialog.ModalityType.APPLICATION_MODAL;

    public NewNodeDialog(Frame owner, NewNodeParameters parameters) {
        super(owner, title, modal);
        this.parameters = parameters;
    }
    // FIXME: use addNLabeledComponent
    @Override
    protected void addFields() {
        parentPath = new JLabel();
        name = new JTextField(30);
        primaryNodeTypeName = new JTextField(30);
        mixinNodeTypes = new JTextField(30);

        LabeledGrid grid = new LabeledGrid();
        grid.addLabeledComponent("Parent", parentPath);
        grid.addLabeledComponent("Name", name);
        grid.addLabeledComponent("Primary node type", primaryNodeTypeName);
        grid.addLabeledComponent("Mixin node types", mixinNodeTypes);
        this.getContentPane().add(grid, BorderLayout.CENTER);
    }
    public void show(Action okAction) {
        this.okAction = okAction;
        super.setVisible(true);
    }
    @Override
    public void modelChanged(ViewModelChangeEvent mce) {
        NewNodeParameters p = (NewNodeParameters) mce.getSource();
        parentPath.setText(p.parent);
        name.setText(p.name);
        primaryNodeTypeName.setText(p.primaryNodeTypeName);
        mixinNodeTypes.setText(StringUtils.join(Arrays.asList(p.mixinNodeTypes), ","));
    }
    @Override
    protected void okAction(ActionEvent ae) {
        parameters.name = name.getText();
        parameters.primaryNodeTypeName = primaryNodeTypeName.getText();
        parameters.mixinNodeTypes = new String[] { mixinNodeTypes.getText() };
        okAction.actionPerformed(ae);
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(390, 200);
    }
}
