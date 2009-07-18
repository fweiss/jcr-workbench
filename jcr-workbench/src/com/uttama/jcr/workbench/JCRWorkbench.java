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
import java.io.File;
import java.io.IOException;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
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

import com.uttama.jcr.workbench.dialogs.ExportDialog;
import com.uttama.jcr.workbench.dialogs.NewNodeDialog;
import com.uttama.jcr.workbench.dialogs.NodePropertyDialog;
import com.uttama.jcr.workbench.dialogs.SearchNodeDialog;
import com.uttama.jcr.workbench.events.ModelChangeEvent;
import com.uttama.jcr.workbench.events.NodeChangedEvent;
import com.uttama.jcr.workbench.events.NodeChangedListener;
import com.uttama.jcr.workbench.model.ExportNodeParameters;
import com.uttama.jcr.workbench.model.NewNodeParameters;
import com.uttama.jcr.workbench.model.NodePropertyParameters;
import com.uttama.jcr.workbench.model.NodeTypeModel;
import com.uttama.jcr.workbench.model.RepositoryModel;
import com.uttama.jcr.workbench.model.NodeModel;
import com.uttama.jcr.workbench.model.SearchNodeParameters;
import com.uttama.jcr.workbench.view.NodeTabbedPanel;
import com.uttama.jcr.workbench.view.NodeTypePanel;
import com.uttama.jcr.workbench.view.properties.NodeDataPanel;
import com.uttama.jcr.workbench.view.properties.RepositoryPanel;
import com.uttama.jcr.workbench.view.swing.CustomJTree;
import com.uttama.jcr.workbench.view.swing.JCRTreeCellRenderer;

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
	private ExportDialog exportDialog;
	private NodePropertyDialog nodePropertyDialog;
	private SearchNodeDialog searchNodeDialog;
	
	private NodeModel nodeModel;
	private RepositoryModel repositoryModel = null;
	private NodeTypeModel nodeTypeModel;
	
	private NewNodeParameters newNodeParameters;
	private ExportNodeParameters exportNodeParameters;
	private NodePropertyParameters nodePropertyParameters;
	private SearchNodeParameters searchNodeParameters;
	
	private ViewModelMap viewModelMap;
	
	protected Action removeNodeAction;
	protected Action exportNodeAction;
	protected Action importNodeAction;
	protected Action saveNodeAction;
	protected Action newNodeAction;
	protected Action searchNodeAction;
	protected Action openSessionAction;
	protected Action closeSessionAction;
	
	public void init() {
		this.setSize(defaultAppletSize);
		this.setName("JCR Workbench");
	    try {
	        SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	            	try {
		            	setLookAndFeel();
		            	createModels();
		        	    createDialogs();
		        	    createTopViews();
		        	    createModelListeners();
		        	    createActions();
		        	    createNodeContextMenu();
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
		
		exportNodeParameters = new ExportNodeParameters();
		nodePropertyParameters = new NodePropertyParameters();
		searchNodeParameters = new SearchNodeParameters();
	}
	protected void createDialogs() {
		Frame owner = findParentFrame();
    	newNodeDialog = new NewNodeDialog(owner, newNodeParameters);
    	exportDialog = new ExportDialog(owner);
    	searchNodeDialog = new SearchNodeDialog(owner, searchNodeParameters);
    	
    	Action setNodePropertyAction = new SetNodePropertyAction("Set Property");
    	nodePropertyDialog = new NodePropertyDialog(owner, nodePropertyParameters);
    	nodePropertyDialog.okAction = setNodePropertyAction;
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
	}
	private JTree createTreePane(TreeModel model) {
		tree = new CustomJTree(model);
		tree.setShowsRootHandles(true);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(true);
        tree.setCellRenderer(new JCRTreeCellRenderer());
		return tree;
	}
	public JPanel createPropertyPanel() {
		JPanel propertyPanel = new JPanel();
		propertyCardLayout = new CardLayout();
		propertyPanel.setLayout(propertyCardLayout);
		nodeTypePanel = new NodeTypePanel("nodeType");
		nodeTypePanel.setModel(nodeTypeModel);
		
		repositoryPanel = new RepositoryPanel("repository");
		
		// FIXME: these are all in nodetabbedpanel
		nodePanel = new NodeDataPanel(nodeModel);
		newNodePanel = new NodeDataPanel(nodeModel);
		
    	nodeTabbedPanel = new NodeTabbedPanel("nodeTabbedPanel");
    	nodeTabbedPanel.nodeDataPanel.nodePropertyDialog = nodePropertyDialog;
		
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
		viewModelMap.put("nt:unstructured", nodeModel, nodeTabbedPanel, nodeTabbedPanel);
		// should be separate tree
		viewModelMap.put("rep:nodeTypes", nodeTypeModel, nodeTypePanel, nodeTypePanel);
		//viewModelMap.putDefault(nodeModel, nodePanel, nodePanel);
		viewModelMap.putDefault(nodeModel, nodeTabbedPanel, nodeTabbedPanel);
	}
	private void createActions() {
		saveNodeAction = new SaveNodeAction("Save Node");
		removeNodeAction = new RemoveNodeAction("Delete Node");
	    newNodeAction = new NewNodeAction("New Node");
	    exportNodeAction = new ExportNodeAction("Export Node");
	    importNodeAction = new ImportNodeAction("Import Node");
	    searchNodeAction = new SearchNodeAction("Search Node");
	    openSessionAction = new OpenSessionAction("Open");
	}
	private void createNodeContextMenu() {
		final JPopupMenu popup = new JPopupMenu();
		popup.add(removeNodeAction);
	    popup.add(newNodeAction);
	    popup.add(saveNodeAction);
	    popup.add(exportNodeAction);
	    popup.add(importNodeAction);
	    popup.add(searchNodeAction);
	    
		tree.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()) {
					popup.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});
	}
	private void createListeners() {
		//repositoryPanel.openButton.addActionListener(this);
		repositoryPanel.openButton.setAction(openSessionAction);
		//saveNodeAction.setEnabled(false);
		nodePanel.setSaveButtonAction(saveNodeAction);
		newNodePanel.setSaveButtonAction(saveNodeAction);
	}
	// FIXME: pull up the popup menu launching or refactor the popup
	// menu to implicitly show a dialg instead of having the Action do both that
	// and actually performing the action.
	class SearchNodeAction
	extends AbstractAction {
		public SearchNodeAction(String name) {
			super(name);
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().getClass().getName().startsWith("javax.swing.JPopupMenu")) {
				TreePath treePath = tree.getSelectionPath();
				NodeModel nodeModel = (NodeModel) treePath.getLastPathComponent();
				searchNodeParameters.contextNodeModel = nodeModel;
				searchNodeDialog.setVisible(true);
			} else {
				searchNodeDialog.setVisible(false);
			}
		}
	}
	class NewNodeAction
	extends AbstractAction {
		public NewNodeAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			log.trace("new node action: " + ae.getSource());
			if (ae.getSource().getClass().getName().startsWith("javax.swing.JPopupMenu")) {
				try {
					newNodeParameters.parent = "/" + RepositoryModel.getRelPath(tree.getSelectionPath());
				} catch (RepositoryModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newNodeParameters.primaryNodeTypeName = "nt:unstructured";
				ModelChangeEvent mce = new ModelChangeEvent(newNodeParameters);
				newNodeDialog.modelChanged(mce);
				newNodeDialog.show(this);
			} else {
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
		public SaveNodeAction(String label) {
			super(label);
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
		public RemoveNodeAction(String label) {
			super(label);
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
	class SetNodePropertyAction
	extends AbstractAction {
		public SetNodePropertyAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			TreePath treePath = tree.getSelectionPath();
			NodeModel nodeModel = (NodeModel) treePath.getLastPathComponent();
			String name = nodePropertyParameters.name;
			String value = nodePropertyParameters.value;
			int propertyType = nodePropertyParameters.propertyType;
			try {
				nodeModel.setProperty(name, value, propertyType);
				nodePropertyDialog.setVisible(false);
			} catch (Exception e) {
				nodePropertyParameters.errorMessage = e.toString();
				nodePropertyDialog.setVisible(true);
			}
		}
	}
	class ExportNodeAction
	extends AbstractAction {
		public ExportNodeAction(String name) {
			super(name);
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().getClass().getName().startsWith("javax.swing.JPopupMenu")) {
				TreePath treePath = tree.getSelectionPath();
				try {
					exportNodeParameters.nodePath = "/" + RepositoryModel.getRelPath(treePath);
				} catch (RepositoryModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				exportNodeParameters.file = new File(".");
				exportDialog.show(this, exportNodeParameters);
			} else {
				exportDialog.setVisible(false);
				repositoryModel.exportNodes(exportNodeParameters);
			}
		}
	}
	class ImportNodeAction
	extends AbstractAction {
		public ImportNodeAction(String name) {
			super(name);
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().getClass().getName().startsWith("javax.swing.JPopupMenu")) {
				TreePath treePath = tree.getSelectionPath();
				try {
					exportNodeParameters.nodePath = "/" + RepositoryModel.getRelPath(treePath);
				} catch (RepositoryModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				exportNodeParameters.file = new File(".");
				exportDialog.show(this, exportNodeParameters);
			} else {
				exportDialog.setVisible(false);
				repositoryModel.importNodes(exportNodeParameters);
			}
		}
	}
	class OpenSessionAction
	extends AbstractAction {
		public OpenSessionAction(String name) {
			super(name);
		}
		@Override
		public void actionPerformed(ActionEvent ae) {
			repositoryPanel.saveFields(repositoryModel);
			String configurationPath = repositoryModel.getConfigurationPath();
			String repositoryPath = repositoryModel.getRepositoryPath();
			String username = repositoryModel.getUsername();
			String password = repositoryModel.getPassword();
			File configurationFile = new File(configurationPath);
			if ( ! configurationFile.exists()) {
				String confirmText = "A repository configuration file was not found at the given location.\n\nClick OK to initialize a new Jackrabbit repository at the given location.";
				String confirmTitle = "No Repository Found";
				int optionType = JOptionPane.OK_CANCEL_OPTION;
				int messageType = JOptionPane.QUESTION_MESSAGE;
				int selectedValue = JOptionPane.showConfirmDialog(getContentPane(), confirmText, confirmTitle, optionType, messageType);
				if (selectedValue != JOptionPane.OK_OPTION)
					return;
			}
			try {
				Repository repository = new TransientRepository(configurationPath, repositoryPath);
				Credentials credentials = new SimpleCredentials(username, password.toCharArray());
				repositoryModel.openSession(repository, credentials);
				//repositoryPanel.setDescriptors(repository);
				nodeTypeModel.setRootNode(repositoryModel.getRootNode());
			} catch (IOException ex) {
				log.error("error with repository(): " + ex.toString());
			} catch (RepositoryModelException e) {
				log.error("error with repository(): " + e.toString());
				String message = "Open session failed.\n\n" + e.getMessage();
				JOptionPane.showMessageDialog(getContentPane(), message);
				return;
			}
		}
	}
	protected void defaultConfiguration() {
		log.info(System.getProperty("user.dir"));
		File defaultRepositoryDir = new File(System.getProperty("user.dir"));
		File configurationFile = new File(defaultRepositoryDir.getParentFile(), "repository/repository.xml");
		File repositoryDir = new File(defaultRepositoryDir.getParentFile(), "repository");
		String configurationPath = configurationFile.getAbsolutePath();
		String repositoryPath = repositoryDir.getAbsolutePath();
		//String configurationPath = "d:/workspace/jackrabbit-app/repository.xml";
		//String repositoryPath = "d:/workspace/jackrabbit-app/repository";
		String username = "username";
		String password = "password";
		repositoryModel.setConfigurationPath(configurationPath);
		repositoryModel.setRepositoryPath(repositoryPath);
		repositoryModel.setUsername(username);
		repositoryModel.setPassword(password);
		
		ModelChangeEvent mce = new ModelChangeEvent(repositoryModel);
		repositoryPanel.modelChanged(mce);
	}
	// FIXME: refactor for actions
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getActionCommand().equals("xOpen")) {
			repositoryPanel.saveFields(repositoryModel);
			String configurationPath = repositoryModel.getConfigurationPath();
			String repositoryPath = repositoryModel.getRepositoryPath();
			String username = repositoryModel.getUsername();
			String password = repositoryModel.getPassword();
			File configurationFile = new File(configurationPath);
			if ( ! configurationFile.exists()) {
				String confirmText = "A repository configuration file was not found at the given location.\n\nClick OK to initialize a new Jackrabbit repository at the given location.";
				String confirmTitle = "No Repository Found";
				int optionType = JOptionPane.OK_CANCEL_OPTION;
				int messageType = JOptionPane.QUESTION_MESSAGE;
				int selectedValue = JOptionPane.showConfirmDialog(this.getContentPane(), confirmText, confirmTitle, optionType, messageType);
				if (selectedValue != JOptionPane.OK_OPTION)
					return;
			}
			try {
				Repository repository = new TransientRepository(configurationPath, repositoryPath);
				Credentials credentials = new SimpleCredentials(username, password.toCharArray());
				repositoryModel.openSession(repository, credentials);
				//repositoryPanel.setDescriptors(repository);
				nodeTypeModel.setRootNode(repositoryModel.getRootNode());
			} catch (IOException ex) {
				log.error("error with repository(): " + ex.toString());
			} catch (RepositoryModelException e) {
				log.error("error with repository(): " + e.toString());
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
