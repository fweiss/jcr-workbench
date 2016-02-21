package com.uttama.jcr.workbench;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.ModelChangeListener;
import com.uttama.jcr.workbench.model.NodeModel;
/**
 * This is a registry of views corresponding to various node types displayed in the repository
 * navigator. For example, the root node in the repository navigator is of type jcr:root and should 
 * display the Repository panel. There is also a default mapping.
 * 
 * The view's ModelChangeListener is sent a modelChanged notification with the selected node's model.
 * 
 * The node's primary node type is used for the map.
 * 
 * The configuration of the map is carried out by the main class (JCRWorkbench).
 * 
 * @author frankw
 *
 */
public class ViewModelMap {
	private final static Logger log = Logger.getLogger(ViewModelMap.class);
	static class Link {
		public Object model;
		public Component view;
		public ModelChangeListener modelChangeListener;
	}
	private CardLayout cardLayout;
	private Map<String,Link> map;
	private Container parent;
	private Link defaultLink;
	public ViewModelMap(Container parent, CardLayout cardLayout) {
		this.parent = parent;
		this.cardLayout = cardLayout;
		map = new HashMap<String,Link>();
	}
	public void putDefault(Object model, Component view, ModelChangeListener modelChangeListener) {
		defaultLink = new Link();
		defaultLink.model = model;
		defaultLink.view = view;
		defaultLink.modelChangeListener = modelChangeListener;
	}
	public void put(String nodeTypeName, Object model, Component view, ModelChangeListener modelChangeListener) {
		Link link = new Link();
		link.view = view;
		link.model = model;
		link.modelChangeListener = modelChangeListener;
		map.put(nodeTypeName, link);
	}
	/**
	 * Shows the view in the property panel corresponding to the given node's type.
	 * @param nodeModel the selected node
	 */
	public void switchView(NodeModel nodeModel) {
		String nodeTypeName = nodeModel.getPrimaryNodeType();
		log.trace("switch: " + nodeTypeName);
		Link link = map.get(nodeTypeName);
		if (link == null)
			link = defaultLink;
		Component view = link.view;
		String viewName = view.getName();
		// FIXME: awkward, rep:root is repositoryModel, others are the selection's nodeModel
		ModelChangeEvent mce;
		if (nodeTypeName.equals("rep:root")) {
			mce = new ModelChangeEvent(link.model);
		} else {
			mce = new ModelChangeEvent(nodeModel);
		}
		cardLayout.show(parent, viewName);
		link.modelChangeListener.modelChanged(mce);
	}
}
