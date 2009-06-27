package com.uttama.jcr.workbench.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
	private boolean isDeleted;
	private boolean sortChildNodes = true;
	
	private NodePropertiesModel nodePropertiesModel;

	private Vector<NodeChangedListener> listeners = new Vector<NodeChangedListener>();
	
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
	public void setDeleted() {
		this.isDeleted = true;
	}
	public boolean isDeleted() {
		return this.isDeleted;
	}
	public String getPrimaryNodeType() {
		String primaryType = "";
		if ( ! isDeleted) {
			try {
				primaryType = this.getNode().getPrimaryNodeType().getName();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return primaryType;
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
			if (sortChildNodes) {
				List<Node> sortedNodes = sorted(node.getNodes());
				return new NodeModel(sortedNodes.get(index));
			} else {
				NodeIterator iterator = node.getNodes();
				iterator.skip(index);
				return new NodeModel((Node) iterator.next());
			}
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
	private List<Node> sorted(NodeIterator nodeIterator) {
		List<Node> nodeList = new LinkedList<Node>();
		while (nodeIterator.hasNext())
			nodeList.add(nodeIterator.nextNode());
		Collections.sort(nodeList, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				try {
					String name1 = n1.getName();
					String name2 = n2.getName();
					return name1.compareToIgnoreCase(name2);
				} catch (RepositoryException e) {
					throw new RuntimeException("NodeModel.sorted: " + e.toString());
				}
			}
		});
		return nodeList;
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
