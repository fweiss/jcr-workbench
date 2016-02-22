package com.uttama.jcr.workbench.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Properties;
import java.util.Vector;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.model.node.SearchNodeParameters;
import com.uttama.jcr.workbench.view.LabeledGrid;
import com.uttama.jcr.workbench.view.swing.CustomJDialog;

public class SearchNodeDialog
extends CustomJDialog {
	static final Logger log = Logger.getLogger(SearchNodeDialog.class);
	private SearchNodeParameters searchNodeParameters;
	private JTextField contextNode;
	private JTextField xpathQuery;
	private JList results;
	private Action searchAction;
	public SearchNodeDialog(Frame owner, SearchNodeParameters searchNodeParameters) {
		super(owner, "Search Node");
		this.searchNodeParameters = searchNodeParameters;
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 300);
	}
	private Properties getLabels() {
		Properties labels = new Properties();
		labels.put("context", "Context path");
		labels.put("xpathQuery", "XPath query");
		labels.put("results", "Results");
		return labels;
	}
	@Override
	protected void addFields() {
		contextNode = new JTextField(50);
		xpathQuery = new JTextField(50);
		results = new JList();
		
		LabeledGrid grid = new LabeledGrid(getLabels());
		grid.addNLabeledComponent("context", contextNode);
		grid.addNLabeledComponent("xpathQuery", xpathQuery);
		grid.addNLabeledComponent("results", results);
		getContentPane().add(grid, BorderLayout.CENTER);
	}
	@Override
	public void addButtons() {
		super.addButtons();
		searchAction = new SearchAction("Search");
		buttonPanel.add(new JButton(searchAction));
	}
	@Override
	public void setVisible(boolean visible) {
		if (visible)
			updateFields();
		super.setVisible(visible);
	}
	@Override
	protected void okAction(ActionEvent ae) {
		// TODO Auto-generated method stub
		
	}
	private void updateFields() {
		contextNode.setText(searchNodeParameters.contextNodeModel.getNodePath());
	}
	private void saveFields() {
		searchNodeParameters.xpathQuery = xpathQuery.getText();
	}
	class SearchAction
	extends AbstractAction {
		public SearchAction(String name) {
			super(name);
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			saveFields();
			Node context = searchNodeParameters.contextNodeModel.getNode();
			Vector<Node> nodes;
			log.trace("searching: " + searchNodeParameters.xpathQuery);
			try {
				nodes = getResults(context, searchNodeParameters.xpathQuery);
				log.trace("results count: " + nodes.size());
				results.setListData(nodes);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private Vector<Node> getResults(Node node, String xpathQuery)
	throws RepositoryException {
		Vector<Node> list = new Vector<Node>();
		QueryManager queryManager = node.getSession().getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(xpathQuery, Query.XPATH);
		QueryResult queryResult = query.execute();
		NodeIterator nodes = queryResult.getNodes();
		while (nodes.hasNext()) {
            list.add(nodes.nextNode());
        }
		return list;
	}

}
