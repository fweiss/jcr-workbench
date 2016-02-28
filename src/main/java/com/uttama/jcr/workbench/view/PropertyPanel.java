package com.uttama.jcr.workbench.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class PropertyPanel
extends JPanel {
    private JPanel buttonPane;
    private JPanel formPane;
    public PropertyPanel() {
        this("");
    }
    public PropertyPanel(String name) {
        super();
        setName(name);
        setLayout(new BorderLayout());

        buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(buttonPane, BorderLayout.SOUTH);

        formPane = new JPanel();
        formPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(formPane, BorderLayout.CENTER);
    }
    protected void addForm(JComponent component) {
        formPane.add(component);
    }
    protected void addButton(JComponent component) {
        buttonPane.add(component);
    }
}
