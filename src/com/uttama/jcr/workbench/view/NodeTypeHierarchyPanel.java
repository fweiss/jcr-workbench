package com.uttama.jcr.workbench.view;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.model.NodeTypeModel;
import com.uttama.jcr.workbench.view.swing.CustomJTree;
import com.uttama.jcr.workbench.view.swing.JCRTreeCellRenderer;

public class NodeTypeHierarchyPanel
extends JPanel
implements ModelChangeListener {
	private JTree typeTree;
	NodeTypeModel nodeTypeModel;
	public NodeTypeHierarchyPanel(String name) {
		super();
		setName(name);
		createViews();
	}
	public void setModel(NodeTypeModel nodeTypeModel) {
		this.nodeTypeModel = nodeTypeModel;
	}
	private void createViews() {
		//typeTree = new JTree();
		
		typeTree = new CustomJTree(null);
		typeTree.setShowsRootHandles(true);
		typeTree.putClientProperty("JTree.lineStyle", "Angled");
		typeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		typeTree.setRootVisible(true);
		typeTree.setCellRenderer(new JCRTreeCellRenderer());

        
		add(typeTree);
	}
	@Override
	public void modelChanged(ModelChangeEvent mce) {
		NodeModel nodeModel = (NodeModel) mce.getSource();
		try {
			nodeTypeModel.setRootNode(nodeModel.getNode().getSession().getRootNode());
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		typeTree.setModel(nodeTypeModel);
	}

}
