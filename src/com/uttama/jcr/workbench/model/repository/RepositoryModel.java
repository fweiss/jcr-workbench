package com.uttama.jcr.workbench.model.repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.jcr.Credentials;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFactory;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.uttama.jcr.workbench.model.node.ExportNodeParameters;
import com.uttama.jcr.workbench.model.node.NodeModel;
import com.uttama.jcr.workbench.model.node.NodeChangedEvent;
import com.uttama.jcr.workbench.model.node.NodeChangedListener;
import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.RepositoryModelException;

/**
 * The RepositoryModel serves mainly as a wrapper for javax.jcr.Session.
 * It provides a Swing TreeModel for JTree an a facade providing services and notifications to
 * controllers, views and controls
 * 
 * @author frankw
 *
 */
public class RepositoryModel
implements TreeModel, NodeChangedListener {
    private final static Logger log = Logger.getLogger(RepositoryModel.class);

    private String configurationPath;
    private String repositoryPath;
    private String username;
    private String password;
    private Session jcrSession;
    private Repository repository;
    private Vector<TreeModelListener> treeModelListeners;
    Node root;
    public RepositoryModel() {
        treeModelListeners = new  Vector<TreeModelListener>();
    }
    public void openSession(Repository repository)
    throws RepositoryModelException {
        try {
            this.repository = repository;
            Credentials credentials = new SimpleCredentials(username, password.toCharArray());
            // TODO: workspace
            jcrSession = this.repository.login(credentials);
            root = jcrSession.getRootNode();
            fireTreeStructureChanged();
            fireNamespacesChanged();
        }
        catch (RepositoryException ex) {
            throw new RepositoryModelException("open session: " + ex.toString());
        }
    }
    public void closeSession() {
        if (jcrSession != null)
            jcrSession.logout();
    }
    public void exportNodes(ExportNodeParameters parameters) {
        String nodePath = parameters.nodePath;
        boolean skipBinary = ! parameters.includeBinary;
        boolean noRecurse = ! parameters.includeSubtree;
        try {
            FileOutputStream os = new FileOutputStream(parameters.file);
            jcrSession.exportSystemView(nodePath, os, skipBinary, noRecurse);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Import the subtree in the given XML file into the given node.
     * This uses the Session import. There's also a Workspace import method.
     * @param parameters
     */
    public void importNodes(ExportNodeParameters parameters) {
        String nodePath = parameters.nodePath;
        int uuidBehavior = ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW;
        try {
            FileInputStream is = new FileInputStream(parameters.file);
            jcrSession.importXML(nodePath, is, uuidBehavior);
            is.close();
            fireTreeStructureChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getRelPath(TreePath treePath)
    throws RepositoryModelException {
        StringBuffer sb = new StringBuffer();
        int pathCount = treePath.getPathCount();
        for (int i=1; i<pathCount; i++) {
            NodeModel nodeModel = (NodeModel) treePath.getPathComponent(i);
            try {
                sb.append((i>1 ? "/" : "") + nodeModel.getNode().getName());
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
        List<NodeModel> list = new LinkedList<NodeModel>();
        list.add(new NodeModel(node));
        try {
            for (int i=1; i<parts.length; i++) {
                node = node.getNode(parts[i]);
                list.add(new NodeModel(node));
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
            node = relPath.isEmpty() ? root : root.getNode(relPath);
        } catch (RepositoryException e) {
            throw new RepositoryModelException("getNode: " + relPath + ": " + e.toString());
        }
        return node;
    }
    public Node addNode(TreePath treePath, String name, String primaryNodeTypeName)
    throws RepositoryModelException {
        log.trace("newNode: " + name);
        Node parentNode = getNode(treePath);
        try {
            Node newNode = parentNode.addNode(name, primaryNodeTypeName);
            int childIndices[] = { (int) parentNode.getNodes().getSize() - 1 };
            Node children[] = { newNode };
            TreeModelEvent tme = new TreeModelEvent(this, treePath, childIndices, children);
            log.trace("treePath: " + treePath.toString() + " index: " + childIndices[0] + " new: " + children[0].getName());
            fireTreeNodesInserted(tme);
            return newNode;
        } catch (RepositoryException e) {
            throw new RepositoryModelException("addNode: " + e.toString());
        }
    }
    // FIXME: refactor to NodeModel
    public void removeNode(TreePath treePath)
    throws RepositoryModelException {
        NodeModel nodeModel = (NodeModel) treePath.getLastPathComponent();
        try {
            nodeModel.setDeleted();
            nodeModel.getNode().remove();
            TreeModelEvent tme = new TreeModelEvent(this, treePath.getParentPath());
            fireTreeNodesChanged(tme);
        } catch (RepositoryException e) {
            throw new RepositoryModelException("removeNode: " + e.toString());
        }
    }
    public ValueFactory getValueFactory()
    throws RepositoryModelException{
        try {
            return jcrSession.getValueFactory();
        } catch (RepositoryException e) {
            throw new RepositoryModelException("getValueFactory: " + e.toString());
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

    /* TreeModel-ness */

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        treeModelListeners.add(listener);
    }
    @Override
    public Object getChild(Object n, int index) {
        NodeModel nodeModel = (NodeModel) n;
        NodeModel child = nodeModel.getChild(nodeModel, index);
        child.addNodeChangedListener(this);
        log.trace("getChild: " + child.getName());
        return child;
    }
    @Override
    public int getChildCount(Object n) {
        NodeModel nodeModel = (NodeModel) n;
        return nodeModel.getChildCount(nodeModel);
    }
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null)
            return -1;
        NodeModel nodeModel = (NodeModel) parent;
        try {
            NodeIterator iterator = nodeModel.getNode().getNodes();
            int i = 0;
            while (iterator.hasNext()) {
                if (parent.equals(iterator.nextNode()))
                    return i;
                i++;
            }
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public Object getRoot() {
        return root == null ? null : new NodeModel(root);
    }
    @Override
    public boolean isLeaf(Object n) {
        NodeModel nodeModel = (NodeModel) n;
        return nodeModel.isLeaf();
    }
    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        treeModelListeners.remove(listener);
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
    private void fireTreeNodesInserted(TreeModelEvent tme) {
        for (TreeModelListener listener : treeModelListeners)
            listener.treeNodesInserted(tme);
    }
//	private void fireTreeNodesRemoved(TreePath treePath) {
//		for (TreeModelListener listener : treeModelListeners)
//			listener.treeNodesRemoved(new TreeModelEvent(this, treePath));
//	}

    // RepositoryModelEvent

    List<RepositoryModelListener> repositoryModelListeners = new LinkedList<RepositoryModelListener>();
    public void addRepositoryModelListener(RepositoryModelListener rml) {
        repositoryModelListeners.add(rml);
    }
    private void fireNamespacesChanged() {
        for (RepositoryModelListener listener : repositoryModelListeners) {
            listener.namespacesChanged(new RepositoryModelEvent(this));
        }
    }
    public void fireConfigurationChanged() {
        for (RepositoryModelListener listener : repositoryModelListeners) {
            listener.configurationChanged(new RepositoryModelEvent(this));
        }
    }
    // TODO remove listener

    @Override
    public void valueChanged(NodeChangedEvent nce) {
        NodeModel nodeModel = nce.getNodeModel();
        try {
            TreePath treePath = getTreePath(nodeModel.getNodePath());
            TreeModelEvent tme = new TreeModelEvent(this, treePath);
            fireTreeNodesChanged(tme);
        } catch (RepositoryModelException e) {
            e.printStackTrace();
        }
    }
    public String getConfigurationPath() {
        return configurationPath;
    }
    public void setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
    }
    public String getRepositoryPath() {
        return repositoryPath;
    }
    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public TableModel getNamespaceTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnCount(2);
        if (jcrSession == null)
            return model;
        try {
            String prefixes[] = jcrSession.getNamespacePrefixes();
            for (String prefix : prefixes) {
                String uri = jcrSession.getNamespaceURI(prefix);
                model.addRow(new Object[] { prefix, uri.isEmpty() ? "(default namespace)" : uri });
            }
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return model;
    }
}
