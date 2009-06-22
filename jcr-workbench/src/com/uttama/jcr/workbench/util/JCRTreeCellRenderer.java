package com.uttama.jcr.workbench.util;

import java.awt.Component;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.Logger;
/**
 * A TreeCellRender customized for JCR.
 * 
 * In addition to the three view states (closed, open, leaf), icons
 * also have session state (saved, changed, added, deleted).
 * 
 * @author frankw
 *
 */
public class JCRTreeCellRenderer
extends DefaultTreeCellRenderer {
	private static final Logger log = Logger.getLogger(JCRTreeCellRenderer.class);
	Node value;
	Icon nodeIcon;
	Icon closedChangedNodeIcon;
	public JCRTreeCellRenderer() {
		super();
        setLeafIcon(new ImageIcon("d:/workspace/jcr-workbench/src/leaf.gif"));
        loadIcons();
        setClosedIcon(nodeIcon);
	}
	protected void loadIcons() {
		String dir = "d:/workspace/jcr-workbench/images/";
        nodeIcon = new ImageIcon(dir + "node.gif");
        closedChangedNodeIcon = new ImageIcon(dir + "node-changed.gif");
	}/*
	@Override
	public Icon getClosedIcon() {
		if (value.isModified())
			return this.closedChangedNodeIcon;
		else
			return this.closedIcon;
	}
	@Override
	public Icon getLeafIcon() {
		return this.leafIcon;
	}
	@Override
	public Icon getOpenIcon() {
		return this.openIcon;
	}*/
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
				log.error("getText: " + e.toString());
				name = "(delete)";
			}
		}
		return name;
	}
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		this.value = (Node) value;
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}

}
