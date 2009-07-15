package com.uttama.jcr.workbench.model;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;

public class NodeTypeModel
implements TreeModel, ModelChangeListener {
	private final static Logger log = Logger.getLogger(NodeTypeModel.class);
	private Node jcrNodeTypes;
	public NodeTypeModel() {
	}
	public void setRootNode(Node rootNode) {
		log.debug("root node: " + rootNode.toString());
		try {
			this.jcrNodeTypes = rootNode.getNode("jcr:system/jcr:nodeTypes");
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private List<Node> getDerived(Node node)
	throws RepositoryException {
		List<Node> list = new LinkedList<Node>();
		//if (node.getName().equals("nt:base")) {
			String nodeType = node.getName();
			//Node ntBase = jcrNodeTypes.getNode("nt:base");
			QueryManager queryManager = node.getSession().getWorkspace().getQueryManager();
			String statement = "jcr:system/jcr:nodeTypes/*[jcr:supertypes='" + nodeType + "']";
			Query query = queryManager.createQuery(statement, Query.XPATH);
			log.debug("query: " + query.getStatement());
			QueryResult queryResult = query.execute();
			NodeIterator nodes = queryResult.getNodes();
			log.debug("query result:" + nodes.getSize());
			while (nodes.hasNext())
				list.add(nodes.nextNode());
		//}
		return list;
	}
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getChild(Object obj, int index) {
		Node node = (Node) obj;
		try {
			List<Node> derived = getDerived(node);
			return derived.get(index);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getChildCount(Object obj) {
		Node node = (Node) obj;
		try {
			//if (node.getName().equals("nt:base")) {
				return getDerived(node).size();
			//}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getRoot() {
		try {
			Node ntBase = jcrNodeTypes.getNode("nt:base");
			return ntBase;
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void modelChanged(ModelChangeEvent mce) {
		NodeModel nodeModel = (NodeModel) mce.getSource();
		setRootNode(nodeModel.getNode());
		
	}

}
