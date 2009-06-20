package com.uttama.jcr.workbench.model;

import java.util.Vector;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;

public class NodeModel {
	private Node node;
	private Vector<NodeChangedListener> listeners = new Vector<NodeChangedListener>();
	private NodePropertiesModel nodePropertiesModel;
	public NodeModel() {
		nodePropertiesModel = new NodePropertiesModel();
	}
	public void setNode(Node node) {
		this.node = node;
		NodeChangedEvent nce = new NodeChangedEvent(this);
		fireNodeChangedEvent(nce);
		nodePropertiesModel.setNode(node);
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
