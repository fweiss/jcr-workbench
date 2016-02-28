package com.uttama.jcr.workbench.model.nodetype;

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

import com.uttama.jcr.workbench.model.node.NodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;

public class NodeTypeModel
implements TreeModel, ModelChangeListener {
    private final static Logger log = LoggerFactory.getLogger(NodeTypeModel.class);
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
    /**
     * Find all the nodetypes that are derived (subclassed) from the given nodetype.
     * A derived nodetype has a jcr:supertype property whose value matches the given nodetype's
     * name. This relationship is executed as a XPath-type query. It is assumed the given node
     * is actually a nodetype node.
     * // TODO: why?
     *
     * @param nodeModel the parent node
     * @return list for matching jcr:nodeType nodes, if any
     * @throws RepositoryException
     */
    private List<Node> getDerived(NodeModel nodeModel)
    throws RepositoryException {
        Node node = nodeModel.getNode();
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
    /**
     * TreeModel must return the given node's child at the given index.
     * See also getChildCount.
     */
    @Override
    public Object getChild(Object nodeModel, int index) {
        try {
            List<Node> derived = getDerived((NodeModel) nodeModel);
            //return derived.get(index);
            return new NodeModel(derived.get(index));
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getChildCount(Object nodeModel) {
        try {
            //if (node.getName().equals("nt:base")) {
                return getDerived((NodeModel) nodeModel).size();
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
            return new NodeModel(ntBase);
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
    public boolean isLeaf(Object nodeModel) {
        // TODO Auto-generated method stub
        return getChildCount(nodeModel) == 0;
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
