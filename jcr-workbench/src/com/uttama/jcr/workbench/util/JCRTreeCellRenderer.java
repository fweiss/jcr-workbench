package com.uttama.jcr.workbench.util;

import java.awt.Component;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.Logger;

public class JCRTreeCellRenderer
extends DefaultTreeCellRenderer {
	private static final Logger log = Logger.getLogger(JCRTreeCellRenderer.class);
	Object value;
	public JCRTreeCellRenderer() {
		super();
        setLeafIcon(new ImageIcon("d:/workspace/jcr-workbench/src/leaf.gif"));
	}
	@Override
	public void setText(String text) {
		Node node = (Node) value;
		try {
			super.setText(node == null ? "flea" : node.getName());
		} catch (RepositoryException e) {
			log.error("can't get node name:" + e.toString());
		}
	}
	@Override
	public String getText() {
		String name = super.getText();
		if (value != null) {
			try {
				Node node = (Node) value;
				boolean isRoot = node.getPrimaryNodeType().getName().equals("rep:root");
				if (isRoot)
					name = "jcr:root";
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return name;
	}
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		this.value = value;
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}

}
