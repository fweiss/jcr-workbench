package com.uttama.jcr.workbench.view.properties;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.OnParentVersionAction;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTextField;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.node.NodeModel;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.PropertyPanel;

public class NodeDefinitionPanel
extends PropertyPanel
implements ModelChangeListener {
    private JTextField onParentVersion = new JTextField(8);
    private JCheckBox isAutoCreated = new JCheckBox();
    private JCheckBox isMandatory = new JCheckBox();
    private JCheckBox isProtected = new JCheckBox();
    private JCheckBox allowSameNameSiblings = new JCheckBox();
    private JTextField defaultPrimaryType = new JTextField(30);
    private JList requiredPrimaryTypes = new JList();
    private JTextField requiredPropertyType = new JTextField(10);
    public static Properties getLabels() {
        Properties labels = new Properties();
        labels.put("defaultPrimaryType", "Default Primary Node Type");
        labels.put("requiredPrimaryTypes", "Required Primary Node Types");
        labels.put("onParentVersion", "On Parent Version");
        labels.put("isAutoCreated", "Autocreated");
        labels.put("isMandatory", "Mandatory:");
        labels.put("isProtected", "Protected");
        labels.put("allowSameNameSiblings", "Allow Same Name Siblings");
        labels.put("requiredPropertyType", "Required Property Type");
        return labels;
    }
    public NodeDefinitionPanel(String name) {
        super(name);

        LabeledGrid fields = new LabeledGrid(getLabels());
        fields.addNLabeledComponent("defaultPrimaryType", defaultPrimaryType);
        fields.addNLabeledComponent("requiredPrimaryTypes", requiredPrimaryTypes);
        fields.addNLabeledComponent("onParentVersion", onParentVersion);
        fields.addNLabeledComponent("isAutoCreated", isAutoCreated);
        fields.addNLabeledComponent("isMandatory", isMandatory);
        fields.addNLabeledComponent("isProtected", isProtected);
        fields.addNLabeledComponent("allowSameNameSiblings", allowSameNameSiblings);
        fields.addNLabeledComponent("requiredPropertyType", requiredPropertyType);
        addForm(fields);
    }
    private void updateFields(NodeDefinition nd) {
        onParentVersion.setText(OnParentVersionAction.nameFromValue(nd.getOnParentVersion()));
        isAutoCreated.setSelected(nd.isAutoCreated());
        isMandatory.setSelected(nd.isMandatory());
        isProtected.setSelected(nd.isProtected());
        allowSameNameSiblings.setSelected(nd.allowsSameNameSiblings());
        NodeType dpt = nd.getDefaultPrimaryType();
        defaultPrimaryType.setText(dpt == null ? "" : dpt.getName());
        requiredPrimaryTypes.setListData(toNodeTypeNames(nd.getRequiredPrimaryTypes()));
        //requiredPropertType.setText(nd.getRequiredType() + "");
    }
    private String[] toNodeTypeNames(NodeType[] nodeTypes) {
        List<String> names = new LinkedList<String>();
        for (NodeType nodeType : nodeTypes)
            names.add(nodeType.getName());
        return names.toArray(new String[]{ });
    }
    @Override
    public void modelChanged(ModelChangeEvent mce) {
        NodeModel nm = (NodeModel) mce.getSource();
        updateFields(nm.getDefinition());
    }
}
