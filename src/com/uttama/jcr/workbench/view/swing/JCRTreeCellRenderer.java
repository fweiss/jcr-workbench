package com.uttama.jcr.workbench.view.swing;

import java.awt.Component;

import javax.jcr.RepositoryException;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.model.node.NodeModel;
import com.uttama.jcr.workbench.resources.Resource;
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
    NodeModel value;
    NodeTypeIcons nodeTypeIcons;
    Icon nodeIcon;
    Icon closedChangedNodeIcon;
    public JCRTreeCellRenderer() {
        super();
        nodeTypeIcons = new NodeTypeIcons();
        loadIcons();
        //setClosedIcon(nodeIcon);
        //setLeafIcon(nodeIcon);
        //setOpenIcon(nodeIcon);
    }
    protected void loadIcons() {
        nodeTypeIcons.addedIcon = Resource.createImageIcon("node-new.gif", "Node has been added");
        nodeTypeIcons.defaultIcon = Resource.createImageIcon("node.gif", "Node");
        nodeTypeIcons.changedIcon = Resource.createImageIcon("node-changed.gif", "Node has been changed");
        nodeTypeIcons.deletedIcon = Resource.createImageIcon("node-removed.gif", "Node has been removed");
        closedChangedNodeIcon = Resource.createImageIcon("node-changed.gif", "Node");
    }
    @Override
    public Icon getClosedIcon() {
        //if (value.isModified())
        //	return this.closedChangedNodeIcon;
        //else
            return nodeTypeIcons.getClosedIcon(value);
    }
    @Override
    public Icon getLeafIcon() {
        return nodeTypeIcons.getLeafIcon(value);
    }
    @Override
    public Icon getOpenIcon() {
        return nodeTypeIcons.getOpenIcon(value);
    }
//	@Override
//	public void setText(String text) {
//		NodeModel node = value;
//		try {
//			super.setText(node == null ? "flea" : node.getNode().getName());
//		} catch (RepositoryException e) {
//			log.error("can't get node name:" + e.toString());
//		}
//	}
    @Override
    public String getText() {
        String name = "???-???";
        if (value != null) {
            if (value.isDeleted()) {
                name = value.getName();
            } else {
                try {
                    NodeModel node = value;
                    boolean isRoot = node.getNode().getPrimaryNodeType().getName().equals("rep:root");
                    name = isRoot ? "jcr:root" : value.getName();
                } catch (RepositoryException e) {
                    log.error("getText: " + e.toString());
                    name = "(delete)";
                }
            }
        }
        return name;
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.value = (NodeModel) value;
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
    static class NodeTypeIcons {
        Icon defaultIcon;
        Icon addedIcon;
        Icon changedIcon;
        Icon deletedIcon;
        Icon getLeafIcon(NodeModel nodeModel) {
            return getIcon(nodeModel);
        }
        Icon getClosedIcon(NodeModel nodeModel) {
            return getIcon(nodeModel);
        }
        /**
         * N.B. precedence
         * @param nodeModel
         * @return
         */
        Icon getIcon(NodeModel nodeModel) {
            if (nodeModel.isDeleted())
                return deletedIcon;
            if (nodeModel.isModified())
                return changedIcon;
            if (nodeModel.isNew())
                return addedIcon;
            return defaultIcon;
        }
        Icon getOpenIcon(NodeModel nodeModel) {
            return getIcon(nodeModel);
        }
    }
}
