package com.uttama.jcr.workbench.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.RepositoryModelException;
/**
 * The RepositoryModel serves mainly as a wrapper for javax.jcr.Session.
 * It provides a Swing model facade providing services and notifications to
 * controllers, views and controls
 * 
 * @author frankw
 *
 */
public class RepositoryModel
implements TreeModel {
	private final static Logger log = Logger.getLogger(RepositoryModel.class);
    private Session jcrSession;
	private Repository repository;
	private Vector<TreeModelListener> treeModelListeners;
	Node root;
	public RepositoryModel() {
		treeModelListeners = new  Vector<TreeModelListener>();
	}
	public void openSession(Repository repository, Credentials credentials) {
		try {			
			this.repository = repository;
			// TODO: workspace
		    jcrSession = this.repository.login(credentials);
		    root = jcrSession.getRootNode();
		    fireTreeStructureChanged();
		}
		catch (Throwable ex) {
			System.out.println(ex.toString());
			//throw new Exception("cannot init: " + ex.toString());
		}
	}
	public void closeSession() {
		if (jcrSession != null)
			jcrSession.logout();
	}
	public static String getRelPath(TreePath treePath)
	throws RepositoryModelException {
		StringBuffer sb = new StringBuffer();
		int pathCount = treePath.getPathCount();
		for (int i=1; i<pathCount; i++) {
			Node node = (Node) treePath.getPathComponent(i);
			try {
				sb.append((i>1 ? "/" : "") + node.getName());
			} catch (RepositoryException e) {
				throw new RepositoryModelException("getNode: name of node: " + e.toString());
			}
		}
		return sb.toString();
	}
	public TreePath getTreePath(String path)
	throws RepositoryModelException {
		String parts[] = path.split("/");
		Node node = root;
		List<Node> list = new LinkedList<Node>();
		list.add(node);
		try {
			for (int i=1; i<parts.length; i++) {
				node = node.getNode(parts[i]);
				list.add(node);
			}
			TreePath treePath = new TreePath(list.toArray());
			return treePath;
		} catch (RepositoryException e) {
			throw new RepositoryModelException("getTreePath: " + e.toString());
		}
	}
	private Node getNode(TreePath treePath)
	throws RepositoryModelException {
		Node node;
		String relPath = getRelPath(treePath);
		try {
			node = root.getNode(relPath);
		} catch (RepositoryException e) {
			throw new RepositoryModelException("getNode: " + relPath + ": " + e.toString());
		}
		return node;
	}
	public Node addNode(TreePath treePath, String name)
	throws RepositoryModelException {
		log.trace("newNode: " + name);
		Node parentNode = getNode(treePath);
		try {
			Node newNode = parentNode.addNode(name);
			fireTreeNodesInserted(treePath);
			return newNode;
		} catch (RepositoryException e) {
			throw new RepositoryModelException("addNode: " + e.toString());
		}
	}
	public void save()
	throws RepositoryModelException {
		try {
			jcrSession.save();
		} catch (RepositoryException e) {
			throw new RepositoryModelException("save: " + e.toString());
		}
	}
	public Node getRootNode() {
		return root;
	}
	@Override
	public Object getChild(Object n, int index) {
		Node node = (Node) n;
		try {
			NodeIterator iterator = node.getNodes();
			iterator.skip(index);
			return iterator.next();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public int getChildCount(Object n) {
		Node node = (Node) n;
		try {
			//TODO: check long/int
			return (int) node.getNodes().getSize();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		//return node instanceof String;
		return false;
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListeners.add(listener);
	}
	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueForPathChanged(TreePath treePath, Object newValue) {
		// TODO: should check if actual change
		TreeModelEvent tme = new TreeModelEvent(this, treePath);
		fireTreeNodesChanged(tme);
	}
	private void fireTreeNodesChanged(TreeModelEvent tme) {
		for (TreeModelListener listener : treeModelListeners)
			listener.treeNodesChanged(new TreeModelEvent(this, new TreePath(root)));
	}
	private void fireTreeStructureChanged() {
		for (TreeModelListener listener : treeModelListeners)
			listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(root)));
	}
	private void fireTreeNodesInserted(TreePath treePath) {
		for (TreeModelListener listener : treeModelListeners)
			listener.treeNodesInserted(new TreeModelEvent(this, treePath));
	}
//	private void fireTreeNodesRemoved(TreePath treePath) {
//		for (TreeModelListener listener : treeModelListeners)
//			listener.treeNodesRemoved(new TreeModelEvent(this, treePath));
//	}
}
