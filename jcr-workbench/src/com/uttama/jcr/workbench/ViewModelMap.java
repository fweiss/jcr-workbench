package com.uttama.jcr.workbench;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import com.uttama.jcr.workbench.model.NodeModel;

public class ViewModelMap {
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
	public void switchView(NodeModel nodeModel) {
		String nodeTypeName = nodeModel.getPrimaryNodeType();
		Link link = map.get(nodeTypeName);
		if (link == null)
			link = defaultLink;
		Component view = link.view;
		String viewName = view.getName();
		ModelChangeEvent mce = new ModelChangeEvent(nodeModel);
		link.modelChangeListener.modelChanged(mce);
		cardLayout.show(parent, viewName);
	}
}
