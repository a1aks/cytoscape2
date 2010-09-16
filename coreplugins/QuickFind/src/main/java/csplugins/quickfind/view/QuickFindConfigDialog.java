
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

package csplugins.quickfind.view;

import csplugins.quickfind.util.*;

import csplugins.widgets.autocomplete.index.GenericIndex;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

import cytoscape.task.util.TaskManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


/**
 * Quick Find Config Dialog Box.
 *
 * @author Ethan Cerami.
 */
public class QuickFindConfigDialog extends JDialog {
	/**
	 * Attribute ComboBox
	 */
	private JComboBox attributeComboBox;

	/**
	 * Table of Sample Attribute Values
	 */
	private JTable sampleAttributeValuesTable;

	/**
	 * Attribute description text area.
	 */
	private JTextArea attributeDescriptionBox;

	/**
	 * Current Network
	 */
	private CyNetwork currentNetwork;

	/**
	 * Current Index
	 */
	private GenericIndex currentIndex;

	/**
	 * Index Type.
	 */
	private int indexType;

	/**
	 * Apply Text.
	 */
	private static final String BUTTON_INDEX_TEXT = "Apply";

	/**
	 * Reindex Text.
	 */
	private static final String BUTTON_REINDEX_TEXT = "Apply";

	/**
	 * Apply Button.
	 */
	private JButton applyButton;

	/**
	 * Flag to indicate that we are currently adding new attributes.
	 */
	private boolean addingNewAttributeList = false;

	/**
	 * Constructor.
	 */
	public QuickFindConfigDialog() {
		//  Initialize, based on currently selected network
		currentNetwork = Cytoscape.getCurrentNetwork();

		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		currentIndex = quickFind.getIndex(currentNetwork);
		indexType = currentIndex.getIndexType();

		Container container = getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setTitle("Configure Search Options for:  " + currentNetwork.getTitle());

		//  If we are working on Linux, set always on top to true.
		//  This is a hack to deal with numerous z-ordering bugs on Linux.
		String os = System.getProperty("os.name");

		if (os != null) {
			if (os.toLowerCase().startsWith("linux")) {
				this.setAlwaysOnTop(true);
			}
		}

		//  Create Master Panel
		JPanel masterPanel = new JPanel();
		masterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

		//  Add Node/Edge Selection Panel
		JPanel nodeEdgePanel = createNodeEdgePanel();
		masterPanel.add(nodeEdgePanel);

		//  Add Attribute ComboBox Panel
		JPanel attributePanel = createAttributeSelectionPanel();
		masterPanel.add(attributePanel);

		//  Add Attribute Description Panel
		JPanel attributeDescriptionPanel = createAttributeDescriptionPanel();
		masterPanel.add(attributeDescriptionPanel);

		//  Add Sample Attribute Values Panel
		JPanel attributeValuePanel = createAttributeValuePanel();
		masterPanel.add(attributeValuePanel);

		//  Add Button Panel
		masterPanel.add(Box.createVerticalGlue());

		JPanel buttonPanel = createButtonPanel();
		masterPanel.add(buttonPanel);
		container.add(masterPanel);

		//  Pack, set modality, and center on screen
		pack();
		setModal(true);
		setLocationRelativeTo(Cytoscape.getDesktop());
		setVisible(true);
	}

	/**
	 * Gets Index Type.
	 *
	 * @return QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
	 */
	int getIndexType() {
		return this.indexType;
	}

	/**
	 * Enable / Disable Apply Button.
	 *
	 * @param enable Enable flag;
	 */
	void enableApplyButton(boolean enable) {
		if (applyButton != null) {
			applyButton.setEnabled(enable);
		}
	}

	/**
	 * Creates Button Panel.
	 *
	 * @return JPanel Object.
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// Cancel Button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					QuickFindConfigDialog.this.setVisible(false);
					QuickFindConfigDialog.this.dispose();
				}
			});

		//  Apply Button
		applyButton = new JButton(BUTTON_REINDEX_TEXT);
		applyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					QuickFindConfigDialog.this.setVisible(false);
					QuickFindConfigDialog.this.dispose();

					String newAttribute = (String) attributeComboBox.getSelectedItem();
					ReindexQuickFind task = new ReindexQuickFind(currentNetwork, indexType,
					                                             newAttribute);
					JTaskConfig config = new JTaskConfig();
					config.setAutoDispose(true);
					config.displayStatus(true);
					config.displayTimeElapsed(false);
					config.displayCloseButton(true);
					config.setOwner(Cytoscape.getDesktop());
					config.setModal(true);

					//  Execute Task via TaskManager
					//  This automatically pops-open a JTask Dialog Box.
					//  This method will block until the JTask Dialog Box
					//  is disposed.
					TaskManager.executeTask(task, config);
				}
			});
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancelButton);
		buttonPanel.add(applyButton);

		return buttonPanel;
	}

	/**
	 * Creates a Panel to show the currently selected attribute description.
	 *
	 * @return JPanel Object.
	 */
	private JPanel createAttributeDescriptionPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Attribute Description:"));
		panel.setLayout(new BorderLayout());
		attributeDescriptionBox = new JTextArea(5, 40);
		attributeDescriptionBox.setEditable(false);
		attributeDescriptionBox.setLineWrap(true);
		attributeDescriptionBox.setWrapStyleWord(true);

		JScrollPane scrollPane = new JScrollPane(attributeDescriptionBox);
		panel.add(scrollPane, BorderLayout.CENTER);
		setAttributeDescription();

		return panel;
	}

	/**
	 * Creates a Panel of Sample Attribute Values.
	 *
	 * @return JPanel Object.
	 */
	private JPanel createAttributeValuePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Sample Attribute Values:"));
		panel.setLayout(new GridLayout(1, 0));

		//  Table Cells are not editable
		sampleAttributeValuesTable = new JTable() {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		addTableModel(sampleAttributeValuesTable);
		this.setVisibleRowCount(sampleAttributeValuesTable, 5);
		panel.add(sampleAttributeValuesTable);

		return panel;
	}

	/**
	 * Sets Text for Attribute Description Box.
	 */
	private void setAttributeDescription() {
		Object selectedAttribute = attributeComboBox.getSelectedItem();
		CyAttributes attributes = getCyAttributes();
		String attributeKey;

		if (selectedAttribute != null) {
			attributeKey = selectedAttribute.toString();
		} else {
			attributeKey = currentIndex.getControllingAttribute();
		}

		String description;

		if (attributeKey.equals(QuickFind.UNIQUE_IDENTIFIER)) {
			description = "Each node and edge in Cytoscape is assigned a "
			              + "unique identifier.  This is an alphanumeric value.";
		} else if (attributeKey.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
			description = "Index all attributes.  Use this option for the "
			              + "widest search scope possible.  Note that indexing "
			              + "all attributes on very large networks may take a few " + "seconds.";
		} else {
			description = attributes.getAttributeDescription(attributeKey);
		}

		if (description == null) {
			description = "No description available.";
		}

		attributeDescriptionBox.setText(description);
		attributeDescriptionBox.setCaretPosition(0);
	}

	/**
	 * Creates TableModel consisting of Distinct Attribute Values.
	 */
	private void addTableModel(JTable table) {
		Object selectedAttribute = attributeComboBox.getSelectedItem();

		//  Determine current attribute key
		String attributeKey;

		if (selectedAttribute != null) {
			attributeKey = selectedAttribute.toString();
		} else {
			attributeKey = currentIndex.getControllingAttribute();
		}

		//  Create column names
		Vector columnNames = new Vector();
		columnNames.add(attributeKey);

		TableModel model = new DefaultTableModel(columnNames, 5);

		DetermineDistinctValuesTask task = new DetermineDistinctValuesTask(model, attributeKey, this);

		JTaskConfig config = new JTaskConfig();
		config.setAutoDispose(true);
		config.displayStatus(true);
		config.displayTimeElapsed(false);
		config.displayCloseButton(true);
		config.setOwner(Cytoscape.getDesktop());
		config.setModal(true);

		//  Execute Task via TaskManager
		//  This automatically pops-open a JTask Dialog Box.
		//  This method will block until the JTask Dialog Box
		//  is disposed.
		table.setModel(model);
		TaskManager.executeTask(task, config);
	}

	private JPanel createNodeEdgePanel() {
		JPanel nodeEdgePanel = new JPanel();
		nodeEdgePanel.setBorder(new TitledBorder("Search:"));
		nodeEdgePanel.setLayout(new BoxLayout(nodeEdgePanel, BoxLayout.X_AXIS));

		JRadioButton nodeButton = new JRadioButton("Nodes");
		nodeButton.setActionCommand(Integer.toString(QuickFind.INDEX_NODES));

		JRadioButton edgeButton = new JRadioButton("Edges");
		edgeButton.setActionCommand(Integer.toString(QuickFind.INDEX_EDGES));

		if (indexType == QuickFind.INDEX_NODES) {
			nodeButton.setSelected(true);
		} else {
			edgeButton.setSelected(true);
		}

		ButtonGroup group = new ButtonGroup();
		group.add(nodeButton);
		group.add(edgeButton);
		nodeEdgePanel.add(nodeButton);
		nodeEdgePanel.add(edgeButton);
		nodeEdgePanel.add(Box.createHorizontalGlue());

		//  User has switched index type.
		ActionListener indexTypeListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				String actionCommand = actionEvent.getActionCommand();
				int type = Integer.parseInt(actionCommand);

				if (type != indexType) {
					indexType = type;
					addingNewAttributeList = true;

					Vector attributeList = createAttributeList();
					attributeComboBox.removeAllItems();

					for (int i = 0; i < attributeList.size(); i++) {
						attributeComboBox.addItem(attributeList.get(i));
					}

					addingNewAttributeList = false;

					//  Simulate attribute combo box selection.
					//  Invoke via SwingUtilities, so that radio button
					//  selection is not delayed.
					if (attributeList.size() > 0) {
						SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									attributeComboBox.setSelectedIndex(0);
								}
							});
					}
				}
			}
		};

		nodeButton.addActionListener(indexTypeListener);
		edgeButton.addActionListener(indexTypeListener);

		return nodeEdgePanel;
	}

	/**
	 * Creates the Attribute Selection Panel.
	 *
	 * @return JPanel Object.
	 */
	private JPanel createAttributeSelectionPanel() {
		JPanel attributePanel = new JPanel();

		attributePanel.setBorder(new TitledBorder("Select Attribute:"));
		attributePanel.setLayout(new BoxLayout(attributePanel, BoxLayout.X_AXIS));

		//  Create ComboBox
		Vector attributeList = createAttributeList();
		attributeComboBox = new JComboBox(attributeList);

		String currentAttribute = currentIndex.getControllingAttribute();

		if (currentAttribute != null) {
			attributeComboBox.setSelectedItem(currentAttribute);
		}

		attributePanel.add(attributeComboBox);
		attributePanel.add(Box.createHorizontalGlue());

		//  Add Action Listener
		attributeComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//  If we are adding new attributes to combox box, ignore the event
					if (addingNewAttributeList) {
						return;
					}

					//  First, set text of apply button
					String currentAttribute = currentIndex.getControllingAttribute();
					String newAttribute = (String) attributeComboBox.getSelectedItem();

					if (currentAttribute.equalsIgnoreCase(newAttribute)) {
						applyButton.setText(BUTTON_REINDEX_TEXT);
					} else {
						applyButton.setText(BUTTON_INDEX_TEXT);
					}

					addTableModel(sampleAttributeValuesTable);
					setAttributeDescription();
				}
			});

		return attributePanel;
	}

	private Vector createAttributeList() {
		Vector attributeList = new Vector();
		CyAttributes attributes = getCyAttributes();
		String[] attributeNames = attributes.getAttributeNames();

		if (attributeNames != null) {
			//  Show all attributes, except those of TYPE_COMPLEX
			for (int i = 0; i < attributeNames.length; i++) {
				int type = attributes.getType(attributeNames[i]);

				//  only show user visible attributes
				if (attributes.getUserVisible(attributeNames[i])) {
					if (type != CyAttributes.TYPE_COMPLEX) {
						attributeList.add(attributeNames[i]);
					}
				}
			}

			//  Alphabetical sort
			Collections.sort(attributeList);

			//  Add default:  Unique Identifier
			attributeList.insertElementAt(QuickFind.UNIQUE_IDENTIFIER, 0);

			//  Add option to index by all attributes
			//  Not yet sure if I want to add this yet.  Keep code below.
			//  if (attributeList.size() > 1) {
			//    attributeList.add(QuickFind.INDEX_ALL_ATTRIBUTES);
			//  }
		}

		return attributeList;
	}

	CyAttributes getCyAttributes() {
		CyAttributes attributes;

		if (indexType == QuickFind.INDEX_NODES) {
			attributes = Cytoscape.getNodeAttributes();
		} else {
			attributes = Cytoscape.getEdgeAttributes();
		}

		return attributes;
	}

	/**
	 * Sets the Visible Row Count.
	 *
	 * @param table JTable Object.
	 * @param rows  Number of Visible Rows.
	 */
	private void setVisibleRowCount(JTable table, int rows) {
		int height = 0;

		for (int row = 0; row < rows; row++) {
			height += table.getRowHeight(row);
		}

		table.setPreferredScrollableViewportSize(new Dimension(table
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    .getPreferredScrollableViewportSize().width,
		                                                       height));
	}

	/**
	 * Main method:  used for local debugging purposes only.
	 *
	 * @param args No command line arguments expected.
	 */
	public static void main(String[] args) {
		new QuickFindConfigDialog();
	}
}


/**
 * Long-term task to Reindex QuickFind.
 *
 * @author Ethan Cerami.
 */
class ReindexQuickFind implements Task {
	private String newAttributeKey;
	private CyNetwork cyNetwork;
	private int indexType;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 *
	 * @param indexType       Index Type.
	 * @param newAttributeKey New Attribute Key for Indexing.
	 */
	ReindexQuickFind(CyNetwork cyNetwork, int indexType, String newAttributeKey) {
		this.cyNetwork = cyNetwork;
		this.indexType = indexType;
		this.newAttributeKey = newAttributeKey;
	}

	/**
	 * Executes Task:  Reindex.
	 */
	public void run() {
		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.reindexNetwork(cyNetwork, indexType, newAttributeKey, taskMonitor);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void halt() {
		// No-op
	}

	/**
	 * Sets the TaskMonitor.
	 *
	 * @param taskMonitor TaskMonitor Object.
	 * @throws IllegalThreadStateException Illegal Thread State.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets Title of Task.
	 *
	 * @return Title of Task.
	 */
	public String getTitle() {
		return "ReIndexing";
	}
}


/**
 * Long-term task to determine distinct attribute values.
 *
 * @author Ethan Cerami.
 */
class DetermineDistinctValuesTask implements Task {
	private TableModel tableModel;
	private String attributeKey;
	private QuickFindConfigDialog parentDialog;
	private TaskMonitor taskMonitor;

	/**
	 * Creates a new DetermineDistinctValuesTask object.
	 *
	 * @param tableModel  DOCUMENT ME!
	 * @param attributeKey  DOCUMENT ME!
	 * @param parentDialog  DOCUMENT ME!
	 */
	public DetermineDistinctValuesTask(TableModel tableModel, String attributeKey,
	                                   QuickFindConfigDialog parentDialog) {
		this.tableModel = tableModel;

		if (attributeKey.equals(QuickFind.INDEX_ALL_ATTRIBUTES)) {
			attributeKey = QuickFind.UNIQUE_IDENTIFIER;
		}

		this.attributeKey = attributeKey;

		//  Disable apply button, while task is in progress.
		parentDialog.enableApplyButton(false);
		this.parentDialog = parentDialog;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void run() {
		taskMonitor.setPercentCompleted(-1);

		//  Obtain distinct attribute values
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes attributes = parentDialog.getCyAttributes();

		Iterator iterator;

		if (parentDialog.getIndexType() == QuickFind.INDEX_NODES) {
			iterator = network.nodesIterator();
		} else {
			iterator = network.edgesIterator();
		}

		String[] values = CyAttributesUtil.getDistinctAttributeValues(iterator, attributes,
		                                                              attributeKey, 5);

		if ((values != null) && (values.length > 0)) {
			for (int i = 0; i < values.length; i++) {
				tableModel.setValueAt(values[i], i, 0);
			}

			parentDialog.enableApplyButton(true);
		} else {
			tableModel.setValueAt("No values found in network:  " + network.getTitle()
			                      + ".  Cannot create index.", 0, 0);
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void halt() {
		//  No-op
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param taskMonitor DOCUMENT ME!
	 *
	 * @throws IllegalThreadStateException DOCUMENT ME!
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getTitle() {
		return "Accessing sample attribute data";
	}
}
