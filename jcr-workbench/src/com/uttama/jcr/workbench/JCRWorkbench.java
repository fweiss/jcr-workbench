package com.uttama.jcr.workbench;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.log4j.Logger;

import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;
import com.uttama.jcr.workbench.model.NewNodeParameters;
import com.uttama.jcr.workbench.model.NodeTypeModel;
import com.uttama.jcr.workbench.model.RepositoryModel;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.util.JCRTreeCellRenderer;
import com.uttama.jcr.workbench.view.NewNodeDialog;
import com.uttama.jcr.workbench.view.NodeTabbedPanel;
import com.uttama.jcr.workbench.view.NodeTypePanel;
import com.uttama.jcr.workbench.view.properties.NodeDataPanel;
import com.uttama.jcr.workbench.view.properties.RepositoryPanel;

public class JCRWorkbench
extends JApplet
implements ActionListener, TreeSelectionListener, NodeChangedListener {
	private static final Logger log = Logger.getLogger(JCRWorkbench.class);
	private static final long serialVersionUID = 9004058156389836075L;
	private Dimension defaultAppletSize = new Dimension(1200, 500);
	
	private JTree tree;
    private JSplitPane splitPane;
	private CardLayout propertyCardLayout;
	private JPanel propertyPanel;
	private NodeDataPanel nodePanel;
	private NodeDataPanel newNodePanel;
	private RepositoryPanel repositoryPanel;
	private NodeTypePanel nodeTypePanel;
	private NodeTabbedPanel nodeTabbedPanel;
	
	private NewNodeDialog newNodeDialog;
	
	private NodeModel nodeModel;
	private RepositoryModel repositoryModel = null;
	private NewNodeParameters newNodeParameters;
	private NodeTypeModel nodeTypeModel;
	
	private ViewModelMap viewModelMap;
	
	protected Action removeNodeAction;

	public void init() {
		this.setSize(defaultAppletSize);
		this.setName("JCR Workbench");
	    try {
	        SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	            	try {
		            	setLookAndFeel();
		            	createModels();
		        	    createTopViews();
		        	    createModelListeners();
		        	    createListeners();
		        	    defaultConfiguration();
	            	} catch (RuntimeException e) {
	            		e.printStackTrace();
	            	}
	            }
	        });
	    } catch (Exception e) {
	        log.error("init: " + e.toString());
	    }
	}
	public void destroy() {
		if (repositoryModel != null) {
			repositoryModel.closeSession();
		}
	}
	protected void setLookAndFeel() {
		//String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		//String motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			//UIManager.setLookAndFeel(windows);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			log.error(e.toString());
		}
	}
	private Frame findParentFrame() {
		Container parent = this;
		while (parent != null && ! (parent instanceof Frame))
			parent = parent.getParent();
		return (Frame) parent;
	}
	private void createModels() {
		nodeModel = new NodeModel();
		repositoryModel = new RepositoryModel();
		newNodeParameters = new NewNodeParameters();
		nodeTypeModel = new NodeTypeModel();
	}
	/**
	 * Create the view for the application.
	 * The top-level view is a left/right split pane. The split is adjustable and each
	 * pane is scrollable.
	 * 
	 * The left pane contains a tree for navigating the remopsitory.
	 * 
	 * The right pane is a set of subpanes which are layed out to display one at a time.
	 */
	private void createTopViews() {
		tree = createTreePane(repositoryModel);
		//propertyCardPanel = createPropertyPanel();
		propertyPanel = createPropertyPanel();
    	splitPane = createSplitPane(tree, propertyPanel);
    	getContentPane().add(splitPane, BorderLayout.CENTER);
    	
    	newNodeDialog = new NewNodeDialog(findParentFrame(), newNodeParameters);
	}
	private JTree createTreePane(TreeModel model) {
		tree = new JTree(model);
		tree.setShowsRootHandles(true);
		//tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(true);
        
        //DefaultTreeCellRenderer r = (DefaultTreeCellRenderer) tree.getCellRenderer();
        //r.setLeafIcon(new ImageIcon("d:/workspace/jcr-workbench/src/leaf.gif"));
        tree.setCellRenderer(new JCRTreeCellRenderer());
		return tree;
	}
	public JPanel createPropertyPanel() {
		JPanel propertyPanel = new JPanel();
		propertyCardLayout = new CardLayout();
		propertyPanel.setLayout(propertyCardLayout);
		nodeTypePanel = new NodeTypePanel("nodeType");
		nodeTypePanel.setModel(nodeTypeModel);
		
		repositoryPanel = new RepositoryPanel();
		nodePanel = new NodeDataPanel(nodeModel);
		newNodePanel = new NodeDataPanel(nodeModel);
    	nodeTabbedPanel = new NodeTabbedPanel("nodeTabbedPanel");
		
		propertyPanel.add(repositoryPanel, "repository");
		propertyPanel.add(nodePanel, "node");
		propertyPanel.add(newNodePanel, "newNode");
		propertyPanel.add(nodeTypePanel, "nodeType");
		propertyPanel.add(nodeTabbedPanel, "nodeTabbedPanel");
		return propertyPanel;
	}
	protected void showPropertyPanel(String key) {
		propertyCardLayout.show(propertyPanel, key);
	}
	public JSplitPane createSplitPane(Component leftPane, Component rightPane) {
        int orientation = JSplitPane.HORIZONTAL_SPLIT;
        boolean continuousLayout = true;
        Component left = new JScrollPane(leftPane);
        Component right = new JScrollPane(rightPane);
        splitPane = new JSplitPane(orientation, continuousLayout, left, right);
    	splitPane.setDividerLocation(260);
		return splitPane;
	}
	private void createModelListeners() {
		tree.addTreeSelectionListener(this);
		nodeModel.addNodeChangedListener(this);
		
		viewModelMap = new ViewModelMap(propertyPanel, propertyCardLayout);
		viewModelMap.put("rep:root", repositoryModel, repositoryPanel, repositoryPanel);
		viewModelMap.put("nt:unstructured", nodeModel, nodePanel, nodePanel);
		// should be separate tree
		viewModelMap.put("rep:nodeTypes", nodeTypeModel, nodeTypePanel, nodeTypePanel);
		//viewModelMap.putDefault(nodeModel, nodePanel, nodePanel);
		viewModelMap.putDefault(nodeModel, nodeTabbedPanel, nodeTabbedPanel);
	}
	private void createListeners() {
		repositoryPanel.openButton.addActionListener(this);
		SaveNodeAction saveNodeAction = new SaveNodeAction("Save Node", null);
		//saveNodeAction.setEnabled(false);
		nodePanel.setSaveButtonAction(saveNodeAction);
		newNodePanel.setSaveButtonAction(saveNodeAction);
		
		removeNodeAction = new RemoveNodeAction("Delete", null);
		
		final JPopupMenu popup = new JPopupMenu();
	    //JMenuItem menuItem = new JMenuItem("Delete");
	    //menuItem.addActionListener(this);
	    //popup.add(menuItem);
		popup.add(removeNodeAction);
	    NewNodeAction newNodeAction = new NewNodeAction("New Node", null);
	    popup.add(newNodeAction);
	    
	    JMenuItem menuItem = new JMenuItem("Save");
	    menuItem.addActionListener(this);
	    popup.add(menuItem);
	    
		MouseListener ml = new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int button = me.getButton();
				if (button == 3) {
				//int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(me.getX(), me.getY());	
				if (selPath != null) {
					// Bug 4196497 right click non-select, unlike Windows Explorer
					// TODO: check current selection, if the right click is on a multi,
					// don't change the selection.
					tree.setSelectionPath(selPath);
					log.trace("clicked: " + selPath.toString());
					
					popup.show(me.getComponent(), me.getX(), me.getY());
				}
			}}
		};
		tree.addMouseListener(ml);
	}
	class NewNodeAction
	extends AbstractAction {
		public NewNodeAction(String label, Icon icon) {
			super(label, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			log.trace("new node action: " + ae.getSource());
			if (ae.getSource().getClass().getName().startsWith("javax.swing.JPopupMenu")) {
				//newNodeParameters.parent = tree.getSelectionPath();
				ModelChangeEvent mce = new ModelChangeEvent(newNodeParameters);
				newNodeDialog.modelChanged(mce);
				newNodeDialog.show(this);
			} else {
				//newNodeDialog.hide();
				newNodeDialog.setVisible(false);
				xactionPerformed(ae);
			}
		}
		public void xactionPerformed(ActionEvent ae) {
			TreePath treePath = tree.getSelectionPath();
			String nodeName = newNodeParameters.name;
			String primaryNodeTypeName = newNodeParameters.primaryNodeTypeName;
			try {
				Node node = repositoryModel.addNode(treePath, nodeName, primaryNodeTypeName);
				tree.setSelectionPath(treePath.pathByAddingChild(new NodeModel(node)));
			}
			catch (RepositoryModelException e) {
				log.error("NewNodeAction: " + e.toString());
			}
		}
	}
	class SaveNodeAction
	extends AbstractAction {
		public SaveNodeAction(String label, Icon icon) {
			super(label, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			//TreePath selPath = tree.getPathForLocation(ae.getX(), ae.getY());
			try {
				repositoryModel.save();
			} catch (RepositoryModelException e) {
				log.error("SaveNodeAction: " + e.toString());;
			}
			log.trace("saved!");		}
	}
	class RemoveNodeAction
	extends AbstractAction {
		public RemoveNodeAction(String label, Icon icon) {
			super(label, icon);
		}
		public void actionPerformed(ActionEvent ae) {
			TreePath treePath = tree.getSelectionPath();
			try {
				repositoryModel.removeNode(treePath);
				//tree.setSelectionPath(treePath.pathByAddingChild(node));
				//nodeModel.setNode(node);
			}
			catch (RepositoryModelException e) {
				log.error("RemoveNodeAction: " + e.toString());
			}
			
		}
	}
	protected void defaultConfiguration() {
		String configurationPath = "d:/workspace/jackrabbit-app/repository.xml";
		String repositoryPath = "d:/workspace/jackrabbit-app/repository";
		String username = "username";
		String password = "password";
		repositoryPanel.setConfiguration(configurationPath);
		repositoryPanel.setRepository(repositoryPath);
		repositoryPanel.setUsername(username);
		repositoryPanel.setPassword(password);
	}
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getActionCommand().equals("Open")) {
			String configurationPath = "d:/workspace/jackrabbit-app/repository.xml";
			String repositoryPath = "d:/workspace/jackrabbit-app/repository";
			String username = "username";
			String password = "password";
			try {
				Repository repository = new TransientRepository(configurationPath, repositoryPath);
				Credentials credentials = new SimpleCredentials(username, password.toCharArray());
				repositoryModel.openSession(repository, credentials);
				repositoryPanel.setDescriptors(repository);
				nodeTypeModel.setRootNode(repositoryModel.getRootNode());
			}
			catch (IOException ex) {
				log.error("error with repository(): " + ex.toString());
			}
		}
	}
	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		NodeModel nodeModel = (NodeModel) tse.getPath().getLastPathComponent();
		viewModelMap.switchView(nodeModel);
	}
	public void xvalueChanged(TreeSelectionEvent tse) {
		log.trace("valueChanged");
		NodeModel nodeModel = (NodeModel) tse.getPath().getLastPathComponent();
			String nt = nodeModel.getPrimaryNodeType();
			//log.debug(nodeModel.getNode().getPrimaryNodeType().getName());
			if (nt.equals("rep:root")) {
				propertyCardLayout.show(propertyPanel, "repository");
			} else {
				//nodeModel.setNode(node);
				nodePanel.setModel(nodeModel);
				propertyCardLayout.show(propertyPanel, "node");
			}
	}
	@Override
	public void valueChanged(NodeChangedEvent nce) {
		if (nce.isNameChanged()) {
			try {
				Node node = nce.getNodeModel().getNode();
				log.trace("node name changed: " + node.getPath() + ":" + nce.getNodeModel().getNode().getName());
				TreePath treePath = repositoryModel.getTreePath(node.getPath());
				repositoryModel.valueForPathChanged(treePath, node.getName());
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
