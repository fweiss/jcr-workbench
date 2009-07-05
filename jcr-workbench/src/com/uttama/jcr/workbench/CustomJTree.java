package com.uttama.jcr.workbench;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * The CustomJTree fixes a controversial defect refarding right click selection behavior.
 *
 */
public class CustomJTree
extends JTree {
	public CustomJTree(TreeModel model) {
		super(model);
		/* We might want this a bit different. Currently, the right click
		 * highlights the node in the tree, but also sends a model change
		 * event to the property panel. The latter may not be desirable. On 
		 * Windows explorer, the behavior appears to be that selects and views
		 * the object, while right click selects and opens a menu.
		 */
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				int button = me.getButton();
				if (button == 3) {
					//int selRow = tree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = getPathForLocation(me.getX(), me.getY());	
					if (selPath != null) {
						// Bug 4196497 right click non-select, unlike Windows Explorer
						// TODO: check current selection, if the right click is on a multi,
						// don't change the selection.
						setSelectionPath(selPath);
					}
				}
			}
		});
	}
}
