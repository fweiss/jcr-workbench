package com.uttama.jcr.workbench.view;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

public class LabeledGrid
extends JPanel {
    private static final long serialVersionUID = -5801936789132066876L;
    GridBagLayout gridbag;
    GridBagConstraints c0;
    GridBagConstraints c1;
    Properties labels;

    public LabeledGrid() {
        gridbag = new GridBagLayout();
        c0 = new GridBagConstraints();
        c1 = new GridBagConstraints();
        setLayout(gridbag);

        c0.anchor = GridBagConstraints.NORTHWEST;
        c0.ipadx = 5;
        c1.gridwidth = GridBagConstraints.REMAINDER;
        c1.anchor = GridBagConstraints.WEST;
        c1.ipadx = 5;
        c1.insets = new Insets(0,5,8,0);
    }
    public LabeledGrid(Properties labels) {
        this();
        setLabels(labels);
    }
    public void addLabeledComponent(String s, Component comp) {
        JLabel label = new JLabel(s);
        gridbag.setConstraints(label, c0);
        add(label);
        gridbag.setConstraints(comp, c1);
        add(comp);
    }
    public void xaddNLabeledComponent(String name, Component comp) {
        String labelString = labels.getProperty(name);
        addLabeledComponent(labelString, comp);
    }
    public void addNLabeledComponent(String name, Component comp) {
        String labelString = labels.getProperty(name);
        JLabel label = new JLabel(labelString);
        gridbag.setConstraints(label, c0);
        add(label);
        comp.setName(name);
        gridbag.setConstraints(comp, c1);
        add(comp);
    }
    public void setLabels(Properties labels) {
        this.labels = labels;
    }
}
