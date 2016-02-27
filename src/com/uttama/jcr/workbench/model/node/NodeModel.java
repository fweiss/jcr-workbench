package com.uttama.jcr.workbench.model.node;

import java.io.InputStream;
import java.util.*;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.version.VersionException;

import com.uttama.jcr.workbench.model.node.properties.NodePropertiesModel;
import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.RepositoryModelException;

/**
 * NodeModel wraps a javax.jcr.Node by adding several aspects:
 * 
 * 1. MVC event model
 * 2. Deleted node info
 * 3. Sorting by name
 * 
 * TODO Should this implement node or extend AbstractNode?
 */
public class NodeModel {
    private static final Logger log = Logger.getLogger(NodeModel.class);
    private Node node;
    private boolean isDeleted;
    // N.B. sorting causes issue with insertion
    private boolean sortChildNodes = false;

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
    public void setProperty(ValueFactory valueFactory, String name, int propertyType, Object object) throws RepositoryModelException {
        try {
            Value value = null;
            switch (propertyType) {
                case PropertyType.BINARY:
                    value = valueFactory.createValue((InputStream) object);
                    break;
                case PropertyType.BOOLEAN:
                    value = valueFactory.createValue((Boolean) object);
                    break;
                case PropertyType.DATE:
                    value = valueFactory.createValue((Calendar) object);
                    break;
                case PropertyType.DOUBLE:
                    value = valueFactory.createValue((Double) object);
                    break;
                case PropertyType.LONG:
                    value = valueFactory.createValue((Long) object);
                    break;
                case PropertyType.NAME:
                    value = valueFactory.createValue((String) object, PropertyType.NAME);
                    break;
                case PropertyType.PATH:
                    value = valueFactory.createValue((String) object, PropertyType.PATH);
                    break;
                case PropertyType.REFERENCE:
                    value = valueFactory.createValue((String) object, PropertyType.REFERENCE);
                    break;
                case PropertyType.STRING:
                    value = valueFactory.createValue((String) object);
                    break;
            }
            node.setProperty(name, value, propertyType);
            NodeChangedEvent nce = new NodeChangedEvent(this);
            fireNodeChangedEvent(nce);
            nodePropertiesModel.fireTableDataChanged();
        } catch (RepositoryException e) {
            throw new RepositoryModelException("error setting property " + name + ":", e);
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
    @Override
    public String toString() {
        String result = "";
        try {
            result = this.node.getName();
        }
        catch (RepositoryException e) {
            log.warn("toString: cannot access name: " + e.toString());
        }
        return result;
    }
}
