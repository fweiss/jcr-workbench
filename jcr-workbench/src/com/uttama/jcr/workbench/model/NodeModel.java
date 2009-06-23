package com.uttama.jcr.workbench.model;

import java.util.Vector;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;

public class NodeModel {
	private static final Logger log = Logger.getLogger(NodeModel.class);
	private Node node;
	private Vector<NodeChangedListener> listeners = new Vector<NodeChangedListener>();
	private NodePropertiesModel nodePropertiesModel;
	public NodeModel() {
		nodePropertiesModel = new NodePropertiesModel();
	}
	public NodeModel(Node node) {
		this.node = node;
		this.nodePropertiesModel = new NodePropertiesModel();
		this.nodePropertiesModel.setNode(node);
	}
	public void setNode(Node node) {
		this.node = node;
		NodeChangedEvent nce = new NodeChangedEvent(this);
		fireNodeChangedEvent(nce);
		nodePropertiesModel.setNode(node);
	}
	public boolean isLeaf() {
		try {
			return node.getNodes().getSize() == 0;
		} catch (RepositoryException e) {
			log.error("isLeaf: " + e.toString());
			return true;
		}
	}
	public NodeModel getChild(NodeModel nodeModel, int index) {
		try {
			NodeIterator iterator = node.getNodes();
			iterator.skip(index);
			return new NodeModel((Node) iterator.next());
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}
	public int getChildCount(NodeModel nodeModel) {
		try {
			//TODO: check long/int
			return (int) node.getNodes().getSize();
		} catch (RepositoryException e) {
			log.error("getChildCount: " + e.toString());
			return 0;
		}
	}
	public NodePropertiesModel getNodePropertiesModel() {
		return this.nodePropertiesModel;
	}
	public Node getNode() {
		return this.node;
	}
	public void setName(String newName) {
		try {
			if (this.node.getName().equals(newName))
				return;
			node.getSession().move(node.getPath(), node.getParent().getPath() + "/" + newName);
			NodeChangedEvent nce = new NodeChangedEvent(this);
			nce.setNameChanged(true);
			fireNodeChangedEvent(nce);
		} catch (ItemExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PathNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void fireNodeChangedEvent(final NodeChangedEvent nce) {
		for (NodeChangedListener listener : listeners) {
			listener.valueChanged(nce);
		}
	}
	public void addNodeChangedListener(NodeChangedListener listener) {
		listeners.add(listener);
	}
	public void removeNodeChangedListener(NodeChangedListener listener) {
		listeners.remove(listener);
	}
}
