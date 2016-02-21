package com.uttama.jcr.workbench;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.swing.*;
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
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NodeChangedEvent;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NodeChangedListener;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.ExportNodeParameters;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NewNodeParameters;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NodePropertyParameters;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NodeTypeModel;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository.RepositoryModel;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NodeModel;
import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.SearchNodeParameters;
import com.uttama.jcr.workbench.view.NodePanel;
import com.uttama.jcr.workbench.view.NodeTypeHierarchyPanel;
import com.uttama.jcr.workbench.view.properties.NodeDataPanel;
import com.uttama.jcr.workbench.view.properties.RepositoryPanel;
import com.uttama.jcr.workbench.view.swing.CustomJTree;
import com.uttama.jcr.workbench.view.swing.JCRTreeCellRenderer;

public class JCRWorkbench
extends JApplet
implements ActionListener, TreeSelectionListener, NodeChangedListener {
	private static final Logger log = Logger.getLogger(JCRWorkbench.class);
	private static final long serialVersionUID = 9004058156389836075L;
	private static Dimension defaultAppletSize = new Dimension(1200, 500);
	
	private JTree tree;
    private JSplitPane splitPane;
	private CardLayout propertyCardLayout;
	private JPanel propertyPanel;
	private NodeDataPanel nodePanel;
	private NodeDataPanel newNodePanel;
	private RepositoryPanel repositoryPanel;
	private NodeTypeHierarchyPanel nodeTypePanel;
	private NodePanel nodeTabbedPanel;
	
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame("JCR Workbench");
                frame.setSize(defaultAppletSize);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JCRWorkbench applet = new JCRWorkbench();
                frame.getContentPane().add(applet);
                applet.buildGui();
                frame.setVisible(true);
            }
        });
    }

    /**
	 * Init the JApplet. Note that the widnow size reaslly needs to be set in the launcher,
	 * not in init(), otherwise, the window size changes as the applet is launched. Of course
	 * perhaps it should be written as an application instead. Not sure why I chose JApplet, probable
	 * becaaue it was a handy way to launch in it Eclipse.
	 */
	@Override
	public void init() {
		this.setSize(defaultAppletSize);
		this.setName("JCR Workbench");
		findParentFrame().setTitle("JCR Workbench");
	    try {
	        SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
                    buildGui();
	            }
	        });
	    } catch (Exception e) {
	        log.error("init: " + e.toString());
	    }
	}
	@Override
	public void destroy() {
		if (repositoryModel != null) {
			repositoryModel.closeSession();
		}
	}
    public void buildGui() {
        try {
            setLookAndFeel();
            createMenus();
            createModels();
            createDialogs();
            createTopViews();
            createViewListeners();
            createModelListeners();
            createActions();
            createNodeContextMenu();
            createListeners();
            defaultConfiguration();
        } catch (RuntimeException e) {
            e.printStackTrace();
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
	private void createMenus() {
		JMenu helpMenu = new JMenu("Help");
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
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
	/**
	 * Create the stack of property panels and teir associated models.
	 * @return a JPanel with the stack of property panels.
	 */
	public JPanel createPropertyPanel() {
		JPanel propertyPanel = new JPanel();
		propertyCardLayout = new CardLayout();
		propertyPanel.setLayout(propertyCardLayout);
		nodeTypePanel = new NodeTypeHierarchyPanel("nodeType");
		nodeTypePanel.setModel(nodeTypeModel);
		
		repositoryPanel = new RepositoryPanel("repository");
		
		// FIXME: these are all in nodetabbedpanel
		nodePanel = new NodeDataPanel(nodeModel);
		newNodePanel = new NodeDataPanel(nodeModel);
		
    	nodeTabbedPanel = new NodePanel("nodeTabbedPanel");
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
	/**
	 * Currently, this controller only listens for view events from the navigator tree.
	 * It uses the ViewModelMap to delegate the dispatching of these events to open the
	 * corresponding view in the data panel.
	 */
	private void createViewListeners() {
		tree.addTreeSelectionListener(this);
		viewModelMap = new ViewModelMap(propertyPanel, propertyCardLayout);
		viewModelMap.put("rep:root", repositoryModel, repositoryPanel, repositoryPanel);
		viewModelMap.put("nt:unstructured", nodeModel, nodeTabbedPanel, nodeTabbedPanel);
		// should be separate tree
		viewModelMap.put("rep:nodeTypes", nodeTypeModel, nodeTypePanel, nodeTypePanel);
		//viewModelMap.putDefault(nodeModel, nodePanel, nodePanel);
		viewModelMap.putDefault(nodeModel, nodeTabbedPanel, nodeTabbedPanel);
	}
	/**
	 * Register the model events that this controller will listen for.
	 */
	private void createModelListeners() {
        repositoryModel.addRepositoryModelListener(repositoryPanel);
		nodeModel.addNodeChangedListener(this);
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
			@Override
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
	// menu to implicitly show a dialog instead of having the Action do both that
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
				log.error("SaveNodeAction: " + e.toString());
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
	/**
	 * Action performed for the NodePropertyDialog.
	 * Validates the input and sets the property.
	 */
	class SetNodePropertyAction
	extends AbstractAction {
		public SetNodePropertyAction(String label) {
			super(label);
		}
		public void actionPerformed(ActionEvent ae) {
			TreePath treePath = tree.getSelectionPath();
			NodeModel nodeModel = (NodeModel) treePath.getLastPathComponent();
			String name = nodePropertyParameters.name;
			//Object value = nodePropertyParameters.value;
			int propertyType = nodePropertyParameters.propertyType;
			
			try {
				ValueFactory valueFactory = repositoryModel.getValueFactory();
				Value value = null;
				switch (nodePropertyParameters.propertyType) {
					case PropertyType.BINARY:
						value = valueFactory.createValue((InputStream) nodePropertyParameters.value);
						break;
					case PropertyType.BOOLEAN:
						value = valueFactory.createValue((Boolean) nodePropertyParameters.value);
						break;
					case PropertyType.DATE:
						value = valueFactory.createValue((Calendar) nodePropertyParameters.value);
						break;
					case PropertyType.DOUBLE:
						value = valueFactory.createValue((Double) nodePropertyParameters.value);
						break;
					case PropertyType.LONG:
						value = valueFactory.createValue((Long) nodePropertyParameters.value);
						break;
					case PropertyType.NAME:
						value = valueFactory.createValue((String) nodePropertyParameters.value, PropertyType.NAME);
						break;
					case PropertyType.PATH:
						value = valueFactory.createValue((String) nodePropertyParameters.value, PropertyType.PATH);
						break;
					case PropertyType.REFERENCE:
						value = valueFactory.createValue((String) nodePropertyParameters.value, PropertyType.REFERENCE);
						break;
					case PropertyType.STRING:
						value = valueFactory.createValue((String) nodePropertyParameters.value);
						break;
					
				}
				nodeModel.setProperty(name, value, propertyType);
				nodePropertyDialog.setVisible(false);
			} catch (Exception e) {
				Throwable t = e;
				nodePropertyParameters.errorMessage = "";
				while (t != null) {
					nodePropertyParameters.errorMessage += t.getClass().getName() + ": ";
					nodePropertyParameters.errorMessage += t.getMessage() + "\n";
					t = t.getCause();
				}
				//nodePropertyParameters.errorMessage = e.toString();
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
			final String configurationPath = repositoryModel.getConfigurationPath();
			final String repositoryPath = repositoryModel.getRepositoryPath();
			final String username = repositoryModel.getUsername();
			final String password = repositoryModel.getPassword();
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
			final JDialog modalDialog = new JDialog(findParentFrame(), "Opening Repository Session", ModalityType.DOCUMENT_MODAL);
			modalDialog.setSize(300, 75);
			modalDialog.setLocationRelativeTo(findParentFrame());
			modalDialog.add(BorderLayout.CENTER, new JLabel("This may take several seconds.\n\nPlease wait."));
			
			SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

				@Override
				protected Void doInBackground() throws Exception {
					try {
						Repository repository = new TransientRepository(configurationPath, repositoryPath);
						Credentials credentials = new SimpleCredentials(username, password.toCharArray());
						repositoryModel.openSession(repository, credentials);
						//repositoryPanel.setDescriptors(repository);
						nodeTypeModel.setRootNode(repositoryModel.getRootNode());
						setEnabled(false);
					} catch (IOException ex) {
						log.error("error with repository(): " + ex.toString());
					} catch (RepositoryModelException e) {
						log.error("error with repository(): " + e.toString());
						String message = "Open session failed.\n\n" + e.getMessage();
						JOptionPane.showMessageDialog(getContentPane(), message);
						return null;
					}
					finally {
						modalDialog.dispose();
					}
					return null;
				}
			};
			worker.execute();
			modalDialog.setVisible(true);
		}
	}
	protected void defaultConfiguration() {
		log.info("user.dir: " + System.getProperty("user.dir"));
		File defaultRepositoryDir = new File(System.getProperty("user.dir"));
		File configurationFile = new File(defaultRepositoryDir, "repository/repository.xml");
		File repositoryDir = new File(defaultRepositoryDir, "repository");
		String configurationPath = configurationFile.getAbsolutePath();
		String repositoryPath = repositoryDir.getAbsolutePath();
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
