package com.uttama.jcr.workbench.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.version.VersionException;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;

/**
 * NodeModel wraps a javax.jcr.Node by adding several aspects:
 * 
 * 1. MVC event model
 * 2. Deleted node info
 * 
 *
 */
public class NodeModel {
	private static final Logger log = Logger.getLogger(NodeModel.class);
	private Node node;
	private boolean isDeleted;
	private boolean sortChildNodes = true;
	
	private NodePropertiesModel nodePropertiesModel;
	DeletedNodeModel deletedNode = new DeletedNodeModel();

	private Set<NodeChangedListener> listeners = new HashSet<NodeChangedListener>();
	
	public NodeModel() {
		nodePropertiesModel = new NodePropertiesModel();
	}
	/**
	 * Constructor for wrapping a javax.jcr.Node with a NodeModel.
	 * @param node
	 */
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
		deletedNode.name = getName();
		this.isDeleted = true;
	}
	public boolean isDeleted() {
		return this.isDeleted;
	}
	public boolean isModified() {
		return node.isModified();
	}
	public boolean isNew() {
		return node.isNew();
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
	public void setProperty(String name, String value, int type) {
		try {
			node.setProperty(name, value, type);
			NodeChangedEvent nce = new NodeChangedEvent(this);
			fireNodeChangedEvent(nce);
			//TableModelEvent tme = new TableModelEvent(nodePropertiesModel);
			nodePropertiesModel.fireTableDataChanged();
			
		} catch (ValueFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getNodePath() {
		try {
			return node.getPath();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Get the name of the node, depending on its type.
	 * @return
	 */
	public String getName() {
		final String wildcard = "*";
		if (isDeleted) {
			return deletedNode.name;
		}
		try {
			String nodeTypeName = node.getPrimaryNodeType().getName();
			if (nodeTypeName.equals("nt:propertyDefinition")) {
				if (node.hasProperty("jcr:name"))
					return node.getProperty("jcr:name").getValue().getString();
				else
					return wildcard;
			}
			return node.getName();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "???";
	}
	public Vector<String> getReferencePaths() {
		Vector<String> paths = new Vector<String>();
		try {
			PropertyIterator iterator = node.getReferences();
			while (iterator.hasNext()) {
				Property property = iterator.nextProperty();
				String path = property.getPath();
				paths.add(path);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return paths;
	}
	public boolean isVersionable() {
		try {
			return node.isNodeType("mix:versionable");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public String[] getAllVersionLabels() {
		try {
			if (isVersionable()) {
				return node.getVersionHistory().getVersionLabels();
			} else {
				return null;
			}
		} catch (UnsupportedRepositoryOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public boolean isLeaf() {
		if (isDeleted) {
			return true;
		} else {
			try {
				return node.getNodes().getSize() == 0;
			} catch (RepositoryException e) {
				log.error("isLeaf: " + e.toString());
				return true;
			}
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
	public NodeDefinition getDefinition() {
		try {
			return node.getDefinition();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	class DeletedNodeModel {
		public String name;
		public boolean isLeaf;
	}
}
