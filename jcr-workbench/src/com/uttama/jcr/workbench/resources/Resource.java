package com.uttama.jcr.workbench.resources;

import java.net.URL;

import javax.swing.ImageIcon;

public abstract class Resource {
	public static ImageIcon createImageIcon(String path, String description) {
		URL url = Resource.class.getResource(path);
		return new ImageIcon(url, description);
	}
}
