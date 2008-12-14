/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.data.annotation;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.servers.BioDataServer;

import cytoscape.util.OpenBrowser;

import cytoscape.view.CyNetworkView;

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/*
 * OntologyMapperDialog.java
 *
 * Created on 2006/04/11, 9:57
 *
 * This code was created by Netbeans 5 w/Swing-Layout + Eclipse 3.1.
 * Swing-Layout class will be standard in Java Standard Edition 6.
 *
 * @author kono
 */

/**
 *
 */
public class OntologyMapperDialog extends JDialog {
	/**
	 *
	 */
	public static final String GO_MOLECULAR_FUNCTION = "GO Molecular Function";

	/**
	 *
	 */
	public static final String GO_BIOLOGICAL_PROCESS = "GO Biological Process";

	/**
	 *
	 */
	public static final String GO_CELLULAR_COMPONENT = "GO Cellular Component";

	// URL of AmiGO. This can be changed.
	private static final String AMIGO_URL = "http://www.godatabase.org/cgi-bin/amigo/go.cgi?view=details&search_constraint=terms&depth=0&query=";
	protected BioDataServer dataServer;
	protected String defaultSpecies;
	private AnnotationDescription[] annotationDescriptions;
	private TreePath annotationPath;
	private String currentAnnotationCategory;
	private CyNetworkView networkView;
	private CyNetwork network;
	private CyAttributes nodeAttributes;

	/**
	 * Creates a new OntologyMapperDialog object.
	 */
	public OntologyMapperDialog() {
		initDataStructures();
		initComponents();
	}

	/**
	 * Creates new form OntologyMapperDialog.<br>
	 *
	 * @param parent
	 * @param modal
	 */
	public OntologyMapperDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initDataStructures();

		initComponents();
		appendCurrentAnnotaions();
	}

	private void initDataStructures() {
		nodeAttributes = Cytoscape.getNodeAttributes();

		networkView = Cytoscape.getCurrentNetworkView();
		network = networkView.getNetwork();
		dataServer = Cytoscape.getBioDataServer();

		annotationDescriptions = dataServer.getAnnotationDescriptions();

		//
		Semantics.applyNamingServices(network);

		defaultSpecies = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
	}

	private void appendCurrentAnnotaions() {
		String[] attributeNames = nodeAttributes.getAttributeNames();

		for (int idx = 0; idx < attributeNames.length; idx++) {
			Set allTerms = new TreeSet();

			if (attributeNames[idx].startsWith(GO_MOLECULAR_FUNCTION)
			    || attributeNames[idx].startsWith(GO_BIOLOGICAL_PROCESS)
			    || attributeNames[idx].startsWith(GO_CELLULAR_COMPONENT)) {
				// Need to pick all attributes to get unique values...
				Iterator it = Cytoscape.getRootGraph().nodesIterator();

				while (it.hasNext()) {
					CyNode node = (CyNode) it.next();
					String nodeID = node.getIdentifier();
					List listAttr = nodeAttributes.getListAttribute(nodeID, attributeNames[idx]);

					if ((listAttr != null) && (listAttr.size() != 0)) {
						allTerms.addAll(listAttr);
					}
				}
			}

			// Now append the list
			if (allTerms.size() != 0) {
				String species = defaultSpecies;
				String[] parts = attributeNames[idx].split(" ");
				String curator = parts[0].trim();
				String annotationType = parts[1] + " " + parts[2];
				AnnotationDescription aDesc = new AnnotationDescription(species, curator,
				                                                        annotationType);
				appendToSelectionTree(attributeNames[idx], allTerms, aDesc);
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		goServerScrollPane = new javax.swing.JScrollPane();
		goServerTree = new javax.swing.JTree();
		goAttributeScrollPane = new javax.swing.JScrollPane();
		goAttributeTree = new javax.swing.JTree();
		buttonPanel = new javax.swing.JPanel();
		applyButton = new javax.swing.JButton();
		removeButton = new javax.swing.JButton();
		applyAllButton = new javax.swing.JButton();
		removeAllButton = new javax.swing.JButton();
		layoutButton = new javax.swing.JButton();
		addEdgesButton = new javax.swing.JButton();
		deleteCreatedButton = new javax.swing.JButton();
		okButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Ontology Mapper");
		goServerScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                          "Ontologies in Local GO Server",
		                                                                          javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                          javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                          new java.awt.Font("Serif",
		                                                                                            1,
		                                                                                            12)));

		goServerTree = this.createAvailableAnnotationsTree();
		goServerTree.addTreeSelectionListener(new AddAnnotationTreeSelectionListener());

		goServerScrollPane.setViewportView(goServerTree);

		goAttributeTree = this.createNodeSelectionTree();
		goAttributeTree.addTreeSelectionListener(new SelectNodesTreeSelectionListener());

		goAttributeTree.addMouseListener(new PopupMenuListener());
		goAttributeTree.setToolTipText("Click to select nodes, or right click to open context menu.");

		goAttributeScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                             "GO Data as Attributes",
		                                                                             javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                             javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                             new java.awt.Font("Serif",
		                                                                                               1,
		                                                                                               12)));
		goAttributeScrollPane.setViewportView(goAttributeTree);

		applyButton.setFont(new java.awt.Font("Serif", 1, 12));
		applyButton.setText(">>");
		applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					applyButtonActionPerformed(evt);
				}
			});

		removeButton.setFont(new java.awt.Font("Serif", 1, 12));
		removeButton.setText("<<");
		removeButton.setEnabled(false);
		removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					removeButtonActionPerformed(evt);
				}
			});

		applyAllButton.setText("Apply All");
		applyAllButton.setToolTipText("Apply all annotations in the selected category.");
		applyButton.setEnabled(false);
		applyAllButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					applyAllButtonActionPerformed(evt);
				}
			});

		removeAllButton.setText("Remove All");

		layoutButton.setText("Layout");

		addEdgesButton.setText("Add Edges");

		deleteCreatedButton.setText("Delete Created");

		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					okButtonActionPerformed(evt);
				}
			});

		amigoLink = new JMenuItem("Search this GO term in AmiGO...");
		amigoLink.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					OpenBrowser.openURL(getURL());
				}
			});
		contextMenu = new JPopupMenu();
		contextMenu.add(amigoLink);

		//
		// Layout
		//
		org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
		buttonPanel.setLayout(buttonPanelLayout);
		buttonPanelLayout.setHorizontalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(buttonPanelLayout.createSequentialGroup()
		                                                                            .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                                                                  .add(buttonPanelLayout.createSequentialGroup()
		                                                                                                                        .addContainerGap()
		                                                                                                                        .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                                                                                                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                                                                                                                   applyButton,
		                                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                                                   120,
		                                                                                                                                                   Short.MAX_VALUE)
		                                                                                                                                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                                                                                                                   removeButton,
		                                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                                                   120,
		                                                                                                                                                   Short.MAX_VALUE)
		                                                                                                                                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                                                                                                                   applyAllButton,
		                                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                                                   120,
		                                                                                                                                                   Short.MAX_VALUE)))
		                                                                                                  .add(buttonPanelLayout.createSequentialGroup()
		                                                                                                                        .add(45,
		                                                                                                                             45,
		                                                                                                                             45)
		                                                                                                                        .add(okButton)))
		                                                                            .addContainerGap()));

		buttonPanelLayout.setVerticalGroup(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                         buttonPanelLayout.createSequentialGroup()
		                                                                          .addContainerGap(103,
		                                                                                           Short.MAX_VALUE)
		                                                                          .add(applyButton)
		                                                                          .add(23, 23, 23)
		                                                                          .add(removeButton)
		                                                                          .add(23, 23, 23)
		                                                                          .add(applyAllButton)
		                                                                          .add(123, 123, 123)
		                                                                          .add(okButton)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                     layout.createSequentialGroup().addContainerGap()
		                                           .add(goServerScrollPane,
		                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                350, Short.MAX_VALUE)
		                                           .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                           .add(buttonPanel,
		                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                           .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                           .add(goAttributeScrollPane,
		                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                350, Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(buttonPanel,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         Short.MAX_VALUE)
		                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                         goAttributeScrollPane,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         400, Short.MAX_VALUE)
		                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                         goServerScrollPane,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         400, Short.MAX_VALUE))
		                                         .addContainerGap()));
		pack();
	}

	private String getURL() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) goAttributeTree
		                                                                                                                                                                                                                                                                                                                                                                                       .getLastSelectedPathComponent();
		String nodeLabel = node.getUserObject().toString();
		String[] parts = nodeLabel.split("=");

		return AMIGO_URL + parts[0].trim();
	}

	private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		DefaultTreeModel model = (DefaultTreeModel) goAttributeTree.getModel();

		TreePath[] selectedPaths = goAttributeTree.getSelectionPaths();

		if ((selectedPaths == null) || (selectedPaths.length == 0)) {
			return;
		}

		for (int idx = 0; idx < selectedPaths.length; idx++) {
			String annotationLevelName = selectedPaths[idx].getPathComponent(1).toString();

			DefaultMutableTreeNode lastPath = (DefaultMutableTreeNode) selectedPaths[idx]
			                                                                                                                                                                                                                                                                                                                                                                                                       .getPathComponent(1);
			lastPath.removeFromParent();
			model.reload();

			// Remove from CyAttribute
			if (annotationLevelName != null) {
				nodeAttributes.deleteAttribute(annotationLevelName);
			}
		}
	}

	/*
	 * Apply all annotation in the category (Celluar Component, Biological
	 * Process, or Molecular Function)
	 *
	 * This means, annotation in all levels will be mapped onto node attributes.
	 */
	private void applyAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int rootNodeCount = goServerTree.getModel().getChildCount(goServerTree.getModel().getRoot());

		for (int i = 0; i < rootNodeCount; i++) {
			// first, create a Set object of all annotations.
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) goServerTree.getModel()
			                                                                    .getChild(goServerTree.getModel()
			                                                                                          .getRoot(),
			                                                                              i);
			AnnotationDescription aDesc = (AnnotationDescription) node1.getUserObject();

			if (aDesc == null)
				return;

			Set uniqueAnnotationValues = new HashSet();
			currentAnnotationCategory = aDesc.getCurator() + " " + aDesc.getType();

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) goAttributeTree.getModel()
			                                                                      .getRoot();
			DefaultTreeModel model = (DefaultTreeModel) goAttributeTree.getModel();

			// Extract levels
			boolean skipFlag = false;

			for (int idx = 0; idx < model.getChildCount(root); idx++) {
				String childrenName = model.getChild(root, idx).toString();

				if (childrenName.equals(currentAnnotationCategory)) {
					skipFlag = true;
				}
			}

			if (skipFlag == false) {
				uniqueAnnotationValues = addAllAnnotationToNodes(aDesc, currentAnnotationCategory);

				if (uniqueAnnotationValues.size() == 0) {
					showNoMatchErrorDialog();
				}

				if ((uniqueAnnotationValues != null) && (uniqueAnnotationValues.size() > 0)) {
					appendToSelectionTree(currentAnnotationCategory, uniqueAnnotationValues, aDesc);
				}
			}
		}
	}

	private String formatGOID(Integer id) {
		String formattedID = "GO:";
		String idString = id.toString();

		if (idString.length() != 7) {
			int length = idString.length();

			for (int idx = 0; idx < (7 - length); idx++) {
				formattedID = formattedID + "0";
			}
		}

		formattedID = formattedID + idString;

		return formattedID;
	}

	/*
	 * This method will be called by Apply Button ( >> ).
	 */
	private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
		boolean rootFlag = false;
		Set uniqueAnnotationValues = null;
		AnnotationDescription aDesc = null;

		if (annotationPath == null) {
			return;
		} else if (annotationPath.getPathComponent(1) == annotationPath.getLastPathComponent()) {
			rootFlag = true;

			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) annotationPath.getPathComponent(1);
			aDesc = (AnnotationDescription) node1.getUserObject();

			if (aDesc == null)
				return;

			uniqueAnnotationValues = new HashSet();
			currentAnnotationCategory = aDesc.getCurator() + " " + aDesc.getType();
			uniqueAnnotationValues = addAllAnnotationToNodes(aDesc, currentAnnotationCategory);
		} else {
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) annotationPath.getPathComponent(1);
			aDesc = (AnnotationDescription) node1.getUserObject();

			DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) annotationPath.getPathComponent(2);

			int level = ((Integer) node2.getUserObject()).intValue();

			if (aDesc == null)
				return;

			String baseAnnotationName = aDesc.getCurator() + " " + aDesc.getType();
			currentAnnotationCategory = baseAnnotationName + " (Level " + level + ")";

			uniqueAnnotationValues = addAnnotationToNodes(aDesc, level, currentAnnotationCategory);
		}

		/*
		 * Error checking: - If the GO term in the level is already in the right
		 * tree, do not append.
		 */
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) goAttributeTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) goAttributeTree.getModel();

		// Extract levels
		for (int idx = 0; idx < model.getChildCount(root); idx++) {
			String childrenName = model.getChild(root, idx).toString();

			if (childrenName.equals(currentAnnotationCategory)) {
				JOptionPane.showMessageDialog(this, "The annotation is already imported.",
				                              "Error!", JOptionPane.INFORMATION_MESSAGE);

				return;
			}
		}

		CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
		Iterator it = Cytoscape.getRootGraph().nodesIterator();
		HashSet values = new HashSet();

		Annotation anno = dataServer.getAnnotation(aDesc);
		Ontology onto = anno.getOntology();
		HashMap terms = onto.getTerms();

		// Build Reverse Hash
		HashMap reverse = new HashMap();
		Set termIDs = terms.keySet();

		Iterator termIt = termIDs.iterator();

		while (termIt.hasNext()) {
			Object id = termIt.next();
			OntologyTerm term = (OntologyTerm) terms.get(id);
			// CyLogger.getLogger().info("ID = " + formatGOID((Integer) id) + ", term =
			// " + term.getName());
			reverse.put(term.getName(), id);
		}

		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
			byte type = nodeAtts.getType(currentAnnotationCategory);

			if (type == CyAttributes.TYPE_STRING) {
				String strVal = nodeAtts.getStringAttribute(node.getIdentifier(),
				                                            currentAnnotationCategory);

				if (strVal != null)
					values.add(strVal);
			} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
				List vals = nodeAtts.getListAttribute(node.getIdentifier(),
				                                      currentAnnotationCategory);

				if (vals.size() > 0) {
					Object val = vals.get(0);

					if (val instanceof String) {
						Iterator listIt = vals.iterator();

						while (listIt.hasNext()) {
							String termName = (String) listIt.next();
							String treeNode = formatGOID((Integer) reverse.get(termName)) + " = "
							                  + termName;

							values.add(treeNode);
						}
					}
				}
			}
		} // while it.hasNext

		if (uniqueAnnotationValues.size() == 0) {
			showNoMatchErrorDialog();
		} // Mod by kono end (10/20/2005)

		if ((uniqueAnnotationValues != null) && (uniqueAnnotationValues.size() > 0)) {
			appendToSelectionTree(currentAnnotationCategory, uniqueAnnotationValues, aDesc);
		}
	}

	private void showNoMatchErrorDialog() {
		JOptionPane.showMessageDialog(null,
		                              "There is no match between the selected annotation \n"
		                              + "and current nodes in the network.\n"
		                              + "\nMake sure that your network data file \n"
		                              + "and Gene Association files use same naming scheme.\n\n"
		                              + "Please compare the 3rd column of Gene Association\n"
		                              + "file (DB_Object_Symbol) and node names in your network.",
		                              "No match in Gene Ontology Database",
		                              JOptionPane.ERROR_MESSAGE);
	}

	protected void appendToSelectionTree(String currentAnnotationCategory,
	                                     Set uniqueAnnotationValues, AnnotationDescription aDesc) {
		if (dataServer == null) {
			return;
		}

		Annotation anno = dataServer.getAnnotation(aDesc);

		if (anno == null) {
			return;
		}

		Ontology onto = anno.getOntology();
		HashMap terms = onto.getTerms();

		// Build Reverse Hash
		HashMap reverse = new HashMap();
		Set termIDs = terms.keySet();

		Iterator termIt = termIDs.iterator();

		while (termIt.hasNext()) {
			Object id = termIt.next();
			OntologyTerm term = (OntologyTerm) terms.get(id);
			// CyLogger.getLogger().info("ID = " + formatGOID((Integer) id) + ", term =
			// " + term.getName());
			reverse.put(term.getName(), id);
		}

		DefaultMutableTreeNode branch = new DefaultMutableTreeNode(currentAnnotationCategory);

		Iterator it = uniqueAnnotationValues.iterator();

		while (it.hasNext()) {
			String termName = (String) it.next();
			String treeNode = formatGOID((Integer) reverse.get(termName)) + " = " + termName;
			branch.add(new DefaultMutableTreeNode(treeNode));
		}

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) goAttributeTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) goAttributeTree.getModel();
		model.insertNodeInto(branch, root, root.getChildCount());
		goAttributeTree.scrollPathToVisible(new TreePath(branch.getPath()));
		model.reload();
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.setVisible(false);
	}

	// Variables declaration - do not modify
	private javax.swing.JButton addEdgesButton;
	private javax.swing.JButton applyAllButton;
	private javax.swing.JButton applyButton;
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JButton deleteCreatedButton;
	private javax.swing.JScrollPane goAttributeScrollPane;
	private javax.swing.JTree goAttributeTree;
	private javax.swing.JScrollPane goServerScrollPane;
	private javax.swing.JTree goServerTree;
	private javax.swing.JButton layoutButton;
	private javax.swing.JButton okButton;
	private javax.swing.JButton removeAllButton;
	private javax.swing.JButton removeButton;
	private JPopupMenu contextMenu;
	private JMenuItem amigoLink;

	// End of variables declaration

	/*
	 * Build GO Server Tree (On the LEFT)
	 *
	 * This tree is based on data in BioDataServer Object.
	 *
	 */
	protected JTree createAvailableAnnotationsTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Available Annotations");
		createTreeNodes(root, annotationDescriptions);

		JTree tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);

		// expandAll (tree, new TreePath (root), true);
		return tree;
	}

	/*
	 * Create nodes for the GO Server Tree.
	 */
	protected void createTreeNodes(DefaultMutableTreeNode root, AnnotationDescription[] descriptions) // for each of the descriptions, and only if the description is of a
	                                                                                                  // species in the
	                                                                                                  // current graph: create a 'topLevelName' which will be the branch of
	                                                                                                  // the JTree,
	                                                                                                  // and a set of leaves for each logical level in that description's
	                                                                                                  // ontology
	 {
		if ((descriptions == null) || (descriptions.length == 0))
			return;

		// Extract species name in the network.
		Set speciesInGraph = Semantics.getSpeciesInNetwork(network);

		DefaultMutableTreeNode branch = null;
		Vector topLevelNamesList = new Vector();

		for (int i = 0; i < descriptions.length; i++) {
			String species = descriptions[i].getSpecies();

			if (!speciesInGraph.contains(species)) {
				continue;
			}

			topLevelNamesList.add(descriptions[i].getCurator() + ", " + descriptions[i].getType()
			                      + ", " + descriptions[i].getSpecies());
			branch = new DefaultMutableTreeNode(descriptions[i]);

			// Extract annotation from
			Annotation annotation = dataServer.getAnnotation(descriptions[i]);

			if (annotation == null) {
				continue;
			}

			int maxDepth = annotation.maxDepth();

			for (int level = 0; level < maxDepth; level++)
				branch.add(new DefaultMutableTreeNode(new Integer(level + 1)));

			root.add(branch);
		}
	} // createTreeNodes

	/*
	 * Incomplete. This will be used to view GO terms as tree structure.
	 */
	protected void buildOntologyTree() {
		Annotation anno = dataServer.getAnnotation(annotationDescriptions[0]);
		Ontology onto = anno.getOntology();
		Set keys = onto.getTerms().keySet();
		Iterator it = keys.iterator();

		CyLogger.getLogger().info("Ontology for: " + anno.getType() + " ::: " + anno.getOntologyType());

		// Traverse tree

		// depthFirst();
		DefaultMutableTreeNode node;

		while (it.hasNext()) {
			Object key = it.next();
			OntologyTerm goTerm = onto.getTerm(((Integer) key).intValue());
			node = new DefaultMutableTreeNode(goTerm.getName());
		}
	}

	private void depthFirst(int[] children) {
	}

	protected JTree createNodeSelectionTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Annotations Categories");
		JTree tree = new JTree(root);
		// tree.getSelectionModel().setSelectionMode
		// (TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		expandAll(tree, new TreePath(root), true);

		return tree;
	} // createNodeSelectionTree

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();

		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			} // for
		} // if

		if (expand)
			tree.expandPath(parent);
		else
			tree.collapsePath(parent);
	} // expandAll

	/*
	 * Methods called by actions
	 *
	 */

	/**
	 *  DOCUMENT ME!
	 *
	 * @param aDesc DOCUMENT ME!
	 * @param level DOCUMENT ME!
	 * @param annotationNameAtLevel DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set addAnnotationToNodes(AnnotationDescription aDesc, int level,
	                                String annotationNameAtLevel) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		// something like "GO biological process" or "KEGG metabolic pathway"

		// make a fresh start
		nodeAttributes.deleteAttribute(annotationNameAtLevel);

		Iterator it = Cytoscape.getRootGraph().nodesIterator();
		TreeSet allTerms = new TreeSet();

		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
			String label = node.getIdentifier();

			String[][] fullAnnotations = dataServer.getAllAnnotations(aDesc, label);

			if (fullAnnotations.length != 0) {
				String[] uniqueAnnotationsAtLevel = collapseToUniqueAnnotationsAtLevel(fullAnnotations,
				                                                                       level);

				// List to save values in the current level
				List annotsList = new ArrayList();

				if (uniqueAnnotationsAtLevel.length == 0) {
					// No attribute available for this node
					nodeAttributes.setAttribute(label, annotationNameAtLevel, "");
				} else {
					// Extract all values in the current level
					for (int j = 0; j < uniqueAnnotationsAtLevel.length; j++) {
						annotsList.add(uniqueAnnotationsAtLevel[j]);
					}

					if (annotsList.size() != 0) {
						nodeAttributes.setListAttribute(label, annotationNameAtLevel, annotsList);
						allTerms.addAll(annotsList);
					}
				}

				// int[] annotationIDs = dataServer.getClassifications(aDesc,
				// nodeLabelArray[i]);
				// Integer[] integerArray = new Integer[annotationIDs.length];
				// for (int j = 0; j < annotationIDs.length; j++)
				// integerArray[j] = new Integer(annotationIDs[j]);
			} // else: this node is annotated
		} // for i

		return allTerms;
	} // addAnnotationToNodes

	/**
	 *  DOCUMENT ME!
	 *
	 * @param aDesc DOCUMENT ME!
	 * @param annotationName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set addAllAnnotationToNodes(AnnotationDescription aDesc, String annotationName) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		Iterator it = Cytoscape.getRootGraph().nodesIterator();

		// This is for all terms in the category.
		TreeSet allTerms = new TreeSet();

		TreeSet uniqueTerms;

		while (it.hasNext()) {
			uniqueTerms = new TreeSet();

			CyNode node = (CyNode) it.next();
			String label = node.getIdentifier();
			String[][] fullAnnotations = dataServer.getAllAnnotations(aDesc, label);

			if (fullAnnotations.length != 0) {
				ArrayList termList = new ArrayList();

				for (int i = 0; i < fullAnnotations.length; i++) {
					for (int j = 0; j < fullAnnotations[i].length; j++) {
						termList.add(fullAnnotations[i][j]);
					}
				}

				uniqueTerms.addAll(termList);
			}

			ArrayList convertedList = new ArrayList();
			convertedList.addAll(uniqueTerms);

			if (convertedList.size() != 0) {
				nodeAttributes.setListAttribute(label, annotationName, convertedList);
			}

			allTerms.addAll(uniqueTerms);
		}

		return allTerms;
	}

	/**
	 * return only the unique categories (typically representing gene
	 * annotations) which exist at the specified level. genes (for example) may
	 * be annotated as belonging to two or more different categories, but at a
	 * higher logical level, these different categories may merge. for example,
	 * the halobacterium gene VNG0623G is classified by KEGG, in its 3-level
	 * ontology as belonging to two categories at the most specific (third)
	 * level
	 * <ol>
	 * <li> Valine, leucine and isoleucine degradation
	 * <li> Propanoate metabolism
	 * <ol>
	 * At level two, the annotation is:
	 * <ol>
	 * <li> Amino Acid Metabolism
	 * <li> Carbohydrate Metabolism
	 * <ol>
	 * At level one, the annotation is simply
	 * <ol>
	 * <li>Metabolism
	 * <ol>
	 * This method calculates and returns the unique annotations for a
	 * biological entity at the specified level.
	 */
	private String[] collapseToUniqueAnnotationsAtLevel(String[][] fullAnnotations, int level) {
		Vector collector = new Vector();

		for (int i = 0; i < fullAnnotations.length; i++) {
			int indexOfClosestAvailableLevel = level - 1;

			if (indexOfClosestAvailableLevel > (fullAnnotations[i].length - 1))
				indexOfClosestAvailableLevel = fullAnnotations[i].length - 1;

			String annotationAtLevel = fullAnnotations[i][indexOfClosestAvailableLevel];

			if (!collector.contains(annotationAtLevel))
				collector.add(annotationAtLevel);
		} // for i

		return (String[]) collector.toArray(new String[0]);
	} // collapseToUniqueAnnotationsAtLevel

	class AddAnnotationTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) goServerTree
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         .getLastSelectedPathComponent();

			if (node == null) {
				applyButton.setEnabled(false);

				return;
			}

			if (!node.isLeaf()) {
				applyButton.setEnabled(true);
				annotationPath = goServerTree.getSelectionPaths()[0];

				return;
			}

			applyButton.setEnabled(true);
			annotationPath = goServerTree.getSelectionPaths()[0];
		}
	} // 

	class PopupMenuListener implements MouseListener {
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) goAttributeTree
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 .getLastSelectedPathComponent();

			if (node == null)
				return;

			/*
			 * These functions are removed from 2.3 Maybe fixed in later
			 * version...
			 */

			// layoutByAnnotationButton.setEnabled(!node.isLeaf());
			// addSharedAnnotationEdgesButton.setEnabled(!node.isLeaf());
			if (!node.isLeaf())
				return;

			if (javax.swing.SwingUtilities.isRightMouseButton(arg0)) {
				contextMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			} else if (javax.swing.SwingUtilities.isMiddleMouseButton(arg0)) {
			} else if (javax.swing.SwingUtilities.isLeftMouseButton(arg0)) {
			}
		}

		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}
	}

	/*
	 * Used for selecting nodes from GO Annotation tree.
	 */
	class SelectNodesTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) goAttributeTree
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              .getLastSelectedPathComponent();

			if (node == null) {
				removeButton.setEnabled(false);

				return;
			}

			/*
			 * These functions are removed from 2.3 Maybe fixed in later
			 * version...
			 */

			// layoutByAnnotationButton.setEnabled(!node.isLeaf());
			// addSharedAnnotationEdgesButton.setEnabled(!node.isLeaf());
			if (!node.isLeaf()) {
				removeButton.setEnabled(true);

				return;
			}

			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

			// unselect every node in the graph
			for (Iterator nvi = networkView.getNodeViewsIterator(); nvi.hasNext();) {
				((NodeView) nvi.next()).setSelected(false);
			}

			TreePath[] selectedPaths = goAttributeTree.getSelectionPaths();
			HashMap selectionHash = extractAnnotationsFromSelection(selectedPaths);

			for (Iterator nvi = networkView.getNodeViewsIterator(); nvi.hasNext();) {
				// get the particular node view
				NodeView nv = (NodeView) nvi.next();

				String nodeLabel = nv.getNode().getIdentifier();

				if (nodeLabel == null) {
					continue;
				}

				// iterate over all attributes in the selectionHash
				for (Iterator mi = selectionHash.keySet().iterator(); mi.hasNext();) {
					String name = (String) mi.next();

					Vector categoryList = (Vector) selectionHash.get(name);
					byte type = nodeAttributes.getType(name);

					if (type == CyAttributes.TYPE_STRING) {
						String attributeValue = nodeAttributes.getStringAttribute(nodeLabel, name);

						if ((attributeValue != null) && categoryList.contains(attributeValue))
							nv.setSelected(true);

						break; // no point in checking other attributes
					} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
						boolean hit = false;
						List attributeList = nodeAttributes.getListAttribute(nodeLabel, name);

						for (Iterator ali = attributeList.iterator(); ali.hasNext();) {
							if (categoryList.contains(ali.next())) {
								nv.setSelected(true);
								hit = true;

								break; // no point in checking the rest of
								       // the list
							}
						} // ali iterator

						if (hit) {
							break;
						} // no point in checking other attributes
					} // if list
				} // mi iterator
			} // nvi iterator

			removeButton.setEnabled(false);
			networkView.redrawGraph(false, false);
		} // valueChanged

		/**
		 * create a hashmap, <String, String []>:
		 *
		 * "KEGG Metabolic Pathway (level 1)" -> (Genetic Information
		 * Processing) "KEGG Metabolic Pathway (level 2)" -> (Amino Acid
		 * Metabolism, Nucleotide Metabolism)
		 *
		 * this method is brittle, and will fail if the structure of the tree
		 * changes: it expects level 0: root level 1: a standard annotation name
		 * (see above) level 2: standard annotation category name (also see
		 * above)
		 */
		protected HashMap extractAnnotationsFromSelection(TreePath[] paths) {
			HashMap hash = new HashMap();

			for (int i = 0; i < paths.length; i++) {
				String annotationName = paths[i].getPathComponent(1).toString();
				String annotationValue = paths[i].getPathComponent(2).toString();

				String[] parts = annotationValue.split("=");
				annotationValue = parts[1].trim();

				Vector list;

				if (!hash.containsKey(annotationName)) {
					list = new Vector();
					hash.put(annotationName, list);
				}

				list = (Vector) hash.get(annotationName);
				list.add(annotationValue);
			} // for i

			return hash;
		} // extractAnnotationsFromSelection
	} // inner class SelectNodesTreeSelectionListener
}
