/*
 * GeneOntologyPanel2.java
 *
 * Created on April 4, 2006, 1:13 PM
 */

package cytoscape.data.servers.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.BioDataServerUtil;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/**
 * 
 * @author kono
 */
public class AnotationPanel extends javax.swing.JPanel {

	private final String LS = System.getProperty("line.separator");

	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";
	private static final String BIOLOGICAL_PROCESS = "P";
	private static final String MOLECULAR_FUNCTION = "F";
	private static final String CELLULAR_COMPONENT = "C";
	
	// For Gene Association preview table
	// String[] columnNames = {"DB", "DB_Object_ID", "DB_Object_Symbol",
	// "Qualifier", "GO ID", "DB:Reference",
	// "Evidence", "With (or) From", "Aspect",
	// "DB_Object_Name", "DB_Object_Synonym", "DB_Object_Type",
	// "taxon", "Date", "Assigned_by"};

	// String[] columnNames = { "DB_Object_Symbol", "DB_Object_Synonym" };
	String[] columnNames = { "GO Symbol", "GO Term", "GO Aspect", "GO Synonym", "GO Taxon ID" };
	private File oboFile = null;
	private HashMap gaFiles = null;

	private CyAttributes nodeAttributes;

	private BioDataServerUtil bdsu;
	private HashMap taxonMap = null;
	private HashMap nodeSpeciesMap;
	private HashMap synoMap;
	
	private boolean isSpecies;
	private HashMap originalSpecies;

	/** Creates new form GeneOntologyPanel2 */
	public AnotationPanel() {
		initComponents();
		originalSpecies = new HashMap();
		nodeAttributes = Cytoscape.getNodeAttributes();
		String[] attrNames = nodeAttributes.getAttributeNames();
		isSpecies = false;
		for(int i=0; i<attrNames.length; i++) {
			if(attrNames[i].equals(Semantics.SPECIES)){
				isSpecies = true;
				break;
			}
		}
		
		if(isSpecies) {
			Iterator it = Cytoscape.getRootGraph().nodesIterator();
			while(it.hasNext()) {
				CyNode node = (CyNode) it.next();
				String nodeId = node.getIdentifier();
				originalSpecies.put(nodeId, nodeAttributes.getStringAttribute(nodeId, Semantics.SPECIES));
			}
			
		}
		
		nodeSpeciesMap = new HashMap();
		synoMap = new HashMap();
		gaFiles = new HashMap();
		bdsu = new BioDataServerUtil();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		jSplitPane1 = new javax.swing.JSplitPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		nodeNameList = new javax.swing.JList();
		jPanel2 = new javax.swing.JPanel();
		previewScrollPane = new javax.swing.JScrollPane();

		// This if for sync. species name with GO annotation.
		speciesCheckBox = new JCheckBox();
		speciesCheckBox.setFont(new java.awt.Font("Dialog", 1, 10));
		speciesCheckBox.setText("Transfer GO Species Name to Nodes");
		speciesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		speciesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		speciesCheckBox.setEnabled(false);
		speciesCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				speciesCheckBoxActionPerformed(evt);
			}
		});

		previewTable = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		headerScrollPane = new javax.swing.JScrollPane();
		headerEditorPane = new javax.swing.JEditorPane();
		oboTextField = new javax.swing.JTextField();
		oboButton = new javax.swing.JButton();
		jScrollPane3 = new javax.swing.JScrollPane();

		gaList = new javax.swing.JList();
		gaListModel = new DefaultListModel();

		gaButton = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();

		flipCheckBox = new javax.swing.JCheckBox();
		flipCheckBox.setEnabled(false);

		speciesComboBox = new javax.swing.JComboBox();

		setPreferredSize(new java.awt.Dimension(350, 300));
		jLabel1.setFont(new java.awt.Font("Serif", 1, 14));
		jLabel1.setText("Select Annotation Files");

		jSplitPane1.setDividerLocation(130);
		jSplitPane1.setDividerSize(5);
		jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Node IDs and Species",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Serif", 1, 10)));

		gaList.setModel(gaListModel);
		gaList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		gaList.setToolTipText("Click file name for preview (First 50 entries)");
		gaList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				gaListMouseClicked(evt);
			}
		});

		jScrollPane2.setViewportView(nodeNameList);

		jSplitPane1.setLeftComponent(jScrollPane2);

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				"Preview of Gene Association File",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Serif", 1, 10)));

		previewTable.getTableHeader().setReorderingAllowed(false);
		previewTable.setRowSelectionAllowed(false);
		previewTable.setColumnSelectionAllowed(false);
		previewTable.setCellSelectionEnabled(false);
		previewScrollPane.setViewportView(previewTable);

		headerScrollPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Header Preview"));
		headerScrollPane.setViewportView(headerEditorPane);
		headerEditorPane.setContentType("text/html");

		headerEditorPane
				.setText("<html><body>"
						+ "<blink><u><strong>Important Note</strong></u></blink><br>Each node ID will be tested against each GO Symbol and GO Synonym for a match. <br><br>"
						+ "If Transfer checkbox is unchecked, each node ID <strong><font color=\"red\">must</font></strong> "
						+ "have defined a Species attribute that matches the GO Taxon ID."
						+ "</body></html>");

		// Layout information
		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING, previewScrollPane,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405,
				Short.MAX_VALUE).add(headerScrollPane,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				jPanel2Layout.createSequentialGroup().add(headerScrollPane,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74,
						Short.MAX_VALUE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						previewScrollPane,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
		jSplitPane1.setRightComponent(jPanel2);

		jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Gene Association Files",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Serif", 1, 10)));

		nodeNameList.setModel(new NodeListModel(getNodeList()));
		nodeNameList.setEnabled(true);
		nodeNameList
				.setToolTipText("These node names will be used for mapping.");

		jScrollPane3.setViewportView(gaList);

		gaButton.setText("Add");
		gaButton.setActionCommand("addGa");
		gaButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

		flipCheckBox.setFont(new java.awt.Font("Dialog", 1, 10));
		flipCheckBox.setText("Flip GO's Canonical Name and First Synonym");
		flipCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		flipCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		flipCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				flipCheckBoxActionPerformed(evt);
			}
		});

		upperPanel = new javax.swing.JPanel();

		org.jdesktop.layout.GroupLayout upperPanelLayout = new org.jdesktop.layout.GroupLayout(
				upperPanel);
		upperPanel.setLayout(upperPanelLayout);
		upperPanelLayout
				.setHorizontalGroup(upperPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								upperPanelLayout
										.createSequentialGroup()
										.add(
												jScrollPane3,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												263,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												upperPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																gaButton,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																56,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(speciesCheckBox))));
		upperPanelLayout
				.setVerticalGroup(upperPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								upperPanelLayout
										.createSequentialGroup()
										.add(
												upperPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																upperPanelLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.add(
																				gaButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				speciesCheckBox))
														.add(
																jScrollPane3,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																67,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								jSplitPane1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								485, Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								jSeparator1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								485, Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								jLabel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								485, Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								upperPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(jLabel1)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								jSeparator1,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								upperPanel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								jSplitPane1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								229, Short.MAX_VALUE).addContainerGap()));
	}

	// Actions
	protected void gaButtonMouseClicked() {
		// TODO add your handling code here:
		File gaFile = FileUtil.getFile("Import Annotation File", FileUtil.LOAD,
				new CyFileFilter[] {});

		if (gaFile != null) {
			gaFiles.put(gaFile.getName(), gaFile);
			gaListModel.addElement(gaFile.getName());
			gaList.setSelectedValue(gaFile.getName(), true);
			try {
				showPreview(gaFile.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		flipCheckBox.setEnabled(true);
		speciesCheckBox.setEnabled(true);
	}

	protected void oboButtonMouseClicked() {
		// TODO add your handling code here:
		CyFileFilter oboFilter = new CyFileFilter();
		oboFilter.addExtension("obo");
		oboFilter.setDescription("OBO files");

		// Get the file name
		oboFile = FileUtil.getFile("Import OBO File", FileUtil.LOAD,
				new CyFileFilter[] { oboFilter });

		if (oboFile != null) {
			this.oboTextField.setText(oboFile.getAbsolutePath());
			this.oboTextField.setToolTipText(oboFile.getAbsolutePath());
		}

	}

	private void flipCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		if (flipCheckBox.isSelected() == true) {
			previewTable.setDefaultRenderer(Object.class,
					new PreviewTableCellRenderer(1));
			previewTable.repaint();
		} else {
			previewTable.setDefaultRenderer(Object.class,
					new PreviewTableCellRenderer(0));
			previewTable.repaint();
		}
	}

	private void speciesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		if (speciesCheckBox.isSelected() == true) {
			//System.out.println("Sync!");
			syncSpecies(true);
		} else {
			syncSpecies(false);
		}
	}

	private void gaListMouseClicked(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		try {
			showPreview(gaList.getSelectedValue().toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	private void restoreSpecies() {
		
			ArrayList nodeNames = new ArrayList();
			nodeAttributes.deleteAttribute(Semantics.SPECIES);
			Iterator it = Cytoscape.getRootGraph().nodesIterator();
			
			String nodeName = null;
			String entry = null;
			CyNode node = null;
			while (it.hasNext()) {
				node = (CyNode) it.next();
				nodeName = node.getIdentifier();
				if(isSpecies == false) {
					entry = nodeName + " = " + "No species data";
				} else {
					entry = nodeName + " = " + originalSpecies.get(nodeName).toString();
				}
				nodeNames.add(entry);
			}
			
			nodeNameList.setModel(new NodeListModel(nodeNames));
			nodeNameList.repaint();
	
		
	}
	

	private void syncSpecies(boolean syncFlag) {

		// Sync is Unchecked
		if(syncFlag == false) {
			restoreSpecies();
			return;
		}
		
		if (nodeSpeciesMap == null) {
			return;
		} else if (nodeSpeciesMap.size() == 0) {
			return;
		}

		ArrayList nodeNames = new ArrayList();
		Iterator it = Cytoscape.getRootGraph().nodesIterator();

		String nodeName = null;
		String entry = null;
		String speciesName = null;
		boolean synoFlag = false;

		CyNode node = null;
		while (it.hasNext()) {
			node = (CyNode) it.next();
			nodeName = node.getIdentifier();
			speciesName = (String) nodeSpeciesMap.get(nodeName);
			// Search synonym map
			if (speciesName == null) {
				String alias = (String) synoMap.get(nodeName);
				if (alias != null) {
					speciesName = (String) nodeSpeciesMap.get(alias);
					if (speciesName != null) {
						synoFlag = true;
					}

				}
			}

			// Check Species
			if (speciesName == null) {
				entry = nodeName + " = " + "No species data";
			} else if (synoFlag == true) {
				entry = nodeName + " = " + speciesName + " (Mapped by synonym)";
				nodeAttributes.setAttribute(nodeName, Semantics.SPECIES,
						speciesName);
			} else {
				entry = nodeName + " = " + speciesName;
				nodeAttributes.setAttribute(nodeName, Semantics.SPECIES,
						speciesName);
			}
			nodeNames.add(entry);
			synoFlag = false;
		}

		// Create new node list
		nodeNameList.setModel(new NodeListModel(nodeNames));
		nodeNameList.repaint();
	}

	private HashMap buildSpeciesHash() throws IOException {
		HashMap speceisHash = new HashMap();
		Set keys = gaFiles.keySet();
		Iterator fileIt = keys.iterator();
		while (fileIt.hasNext()) {
			String line = null;
			File annotationFile = (File) fileIt.next();
			BufferedReader br = new BufferedReader(new FileReader(
					annotationFile));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("!")) {
					continue;
				} else {

				}
			}

		}

		return speceisHash;
	}

	private void showPreview(String fileName) throws IOException {
		File previewTarget = (File) gaFiles.get(fileName);

		BufferedReader br = new BufferedReader(new FileReader(previewTarget));
		this.headerEditorPane.setContentType("text/plain");
		this.headerEditorPane.setText("Loading annotation file...");
		this.headerEditorPane.setText(getHeader(br));
		buildPreviewTable(br);
		br.close();

	}

	private String getHeader(BufferedReader br) throws IOException {
		String line;
		String header = "";

		while ((line = br.readLine()) != null) {
			if (line.startsWith("!")) {
				header = header + line + LS;
			} else {
				break;
			}
		}

		return header.trim();
	}

	/*
	 * Create a preview table for the selected Gene Association File.
	 * 
	 */
	private void buildPreviewTable(BufferedReader br) throws IOException {

		String line;
		
		String[] rowString;
		Vector data = new Vector();
		Vector cn = new Vector();
		BufferedReader spListReader = null;

		try {

			URL taxURL = getClass().getResource(TAXON_RESOURCE_FILE);
			spListReader = new BufferedReader(new InputStreamReader(taxURL
					.openStream()));

			System.out.println("Taxonomy table found in jar file...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap taxonMap = bdsu.getTaxonMap(spListReader);
		String[] taxonID = null;

		boolean aspectErrorFlag = false;
		boolean taxonErrorFlag = false;
		
		while ((line = br.readLine()) != null) {
			rowString = line.split("\t");
			Vector row = new Vector();
			String taxonName = null;
			for (int i = 0; i < rowString.length; i++) {
				if (i == 2 && rowString[i] != null) {
					row.add(rowString[i]);
				} else if(i==4 && rowString[i] != null) {
					row.add(rowString[i]);
				} else if(i==8) {
					if(rowString[i].equalsIgnoreCase(BIOLOGICAL_PROCESS)) {
						row.add("Biological Process (" + BIOLOGICAL_PROCESS + ")");
					} else if(rowString[i].equalsIgnoreCase(CELLULAR_COMPONENT)) {
						row.add("Cellular Component (" + CELLULAR_COMPONENT + ")");
					} else if(rowString[i].equalsIgnoreCase(MOLECULAR_FUNCTION)) {
						row.add("Molecular Function (" + MOLECULAR_FUNCTION + ")");
					} else {
						row.add("Error!: Invalid Aspect");
						aspectErrorFlag = true;
					}
				} else if (i == 10 && rowString[i] != null) {
					row.add(rowString[i]);

					// System.out.println("!!!!!!! Making SynoMap = " +
					// row.get(0)
					// + " = " + rowString[i]);
					// This is the Synonym field.
					String[] syno = rowString[i].split("\\|");
					for (int j = 0; j < syno.length; j++) {
						synoMap.put(syno[j].trim(), row.get(0));
					}

				} else if (i == 12 && rowString[i] != null) {
					// This is a taxon ID.
					// We need to convert this into species name
					taxonID = rowString[i].split(":");
					// System.out.println("!!!!!!! taxon ID = " + taxonID[0] +
					// ", " + taxonID[1]);
					if(taxonID.length != 2) {
						row.add("ERROR!: Invalid Taxon ID " + rowString[i]);
						taxonErrorFlag = true;
					} else if (taxonID.length == 2){
						taxonName = (String) taxonMap.get(taxonID[1]);
						row.add(taxonName + " (" + taxonID[1] + ")");
					} else {
						row.add("ERROR!: Invalid Taxon ID " + rowString[i]);
						taxonErrorFlag = true;
					}
				}
			}

			nodeSpeciesMap.put(row.get(0), taxonName);
			data.add(row);
		}

		for (int i = 0; i < columnNames.length; i++) {
			cn.add(columnNames[i]);
		}
		previewTableModel = new DefaultTableModel(data, cn);
		previewTable.setModel(previewTableModel);

		previewTable.setDefaultRenderer(Object.class,
				new PreviewTableCellRenderer(0));
		
		if(taxonErrorFlag == true || aspectErrorFlag == true) {
			gaFileErrorHandler(taxonErrorFlag, aspectErrorFlag);
		}
	}
	
	private void gaFileErrorHandler(boolean taxonFlag, boolean aspectFlag) {
		if(taxonFlag == true) {
			JOptionPane.showMessageDialog(
					this , "Some Taxonomy IDs are invalid." , "Warning" ,
					JOptionPane.INFORMATION_MESSAGE
				);
		}
		
		if(aspectFlag == true) {
			JOptionPane.showMessageDialog(
					this , "This file contains invalid GO Aspects." , "Error!" ,
					JOptionPane.INFORMATION_MESSAGE
				);
		}
	}

	private List getNodeList() {
		ArrayList nodes = (ArrayList) Cytoscape.getCyNodesList();
		ArrayList nodeNames = new ArrayList();
		Iterator it = nodes.iterator();
		

		String nodeName = null;
		String entry = null;
		CyNode node = null;
		while (it.hasNext()) {
			node = (CyNode) it.next();
			nodeName = node.getIdentifier();

			// Check Species
			if (isSpecies == false) {
				entry = nodeName + " = " + "No Species attr.";
			} else {
				entry = nodeName
						+ " = "
						+ nodeAttributes.getStringAttribute(nodeName,
								Semantics.SPECIES);
			}

			nodeNames.add(entry);
		}
		return nodeNames;

	}

	protected void setSpeciesList(final BufferedReader rd) throws IOException {
		String curLine = null;
		String name1 = null;
		// String name2 = null;

		// remove the first line, which is a title
		curLine = rd.readLine();

		while ((curLine = rd.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(curLine, "|");
			st.nextToken();
			name1 = st.nextToken().trim();
			speciesComboBox.addItem(name1);

		}
	}

	public String getOverwiteComboBox() {
		return (String) speciesComboBox.getSelectedItem();
	}

	public boolean getFlipCheckBoxStatus() {
		return flipCheckBox.isSelected();
	}

	public boolean isFilesSelected() {
		if (this.gaFiles.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public File getOBOFile() {
		return this.oboFile;
	}

	public Map getGAFiles() {
		return this.gaFiles;
	}

	public void addButtonActionListener(ActionListener l) {
		gaButton.addActionListener(l);
	}

	// Variables declaration - do not modify
	private javax.swing.JScrollPane headerScrollPane;
	private javax.swing.JButton oboButton;
	private javax.swing.JButton gaButton;
	private javax.swing.JCheckBox flipCheckBox;
	private javax.swing.JComboBox speciesComboBox;
	private javax.swing.JEditorPane headerEditorPane;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JList nodeNameList;
	private javax.swing.JList gaList;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel upperPanel;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JTable previewTable;
	private javax.swing.JTextField oboTextField;
	private javax.swing.JScrollPane previewScrollPane;

	private javax.swing.JCheckBox speciesCheckBox;

	private DefaultListModel gaListModel;
	private DefaultTableModel previewTableModel;
	// End of variables declaration

	// Inner class
	class NodeListModel extends javax.swing.AbstractListModel {

		List list;

		NodeListModel(List list) {
			this.list = list;
		}

		public int getSize() {
			return list.size();
		}

		public Object getElementAt(int i) {
			return list.get(i);
		}
	}
}

/*
 * Cell renderer for preview table
 * 
 */
class PreviewTableCellRenderer extends JLabel implements TableCellRenderer {
	private Font selectedFont = new Font("Sans-serif", Font.BOLD, 12);
	private int col = 0;

	public PreviewTableCellRenderer(int col) {
		super();
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		this.col = col;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			// setForeground(table.getForeground());

			if (column == col) {
				setForeground(Color.RED);
				setFont(selectedFont);

				// super.setBackground(Color.RED);
			} else {

				setForeground(Color.BLACK);
				super.setBackground(table.getBackground());
				setFont(table.getFont());
			}
		}

		setHorizontalAlignment(LEFT);

		if (column == 1) {
			String newValue = "";
			String[] synonym = (value.toString()).split("\\|");
			for (int i = 0; i < synonym.length; i++) {
				newValue = newValue + synonym[i];
				if (i != synonym.length - 1) {
					newValue = newValue + ",  ";
				}
			}
			setText((value == null) ? "" : newValue.toString());
			

		} else {
			setText((value == null) ? "" : value.toString());
			
		}
		return this;
	}

}
