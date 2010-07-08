/*
 File: CytoscapeSessionWriter.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.writers;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.data.Semantics;
import cytoscape.generated.Child;
import cytoscape.generated.Cysession;
import cytoscape.generated.Cytopanel;
import cytoscape.generated.Cytopanels;
import cytoscape.generated.Desktop;
import cytoscape.generated.DesktopSize;
import cytoscape.generated.HiddenEdges;
import cytoscape.generated.HiddenNodes;
import cytoscape.generated.Network;
import cytoscape.generated.NetworkFrame;
import cytoscape.generated.NetworkFrames;
import cytoscape.generated.NetworkTree;
import cytoscape.generated.Node;
import cytoscape.generated.ObjectFactory;
import cytoscape.generated.Ontology;
import cytoscape.generated.OntologyServer;
import cytoscape.generated.Panel;
import cytoscape.generated.Panels;
import cytoscape.generated.Parent;
import cytoscape.generated.Plugins;
import cytoscape.generated.SelectedEdges;
import cytoscape.generated.SelectedNodes;
import cytoscape.generated.Server;
import cytoscape.generated.SessionState;
import cytoscape.util.BookmarksUtil;
import cytoscape.util.swing.JTreeTable;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.NetworkPanel;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.CalculatorIO;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.customgraphic.CustomGraphicsManager;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.ImageUtil;
import cytoscape.visual.customgraphic.NullCustomGraphics;
import cytoscape.visual.customgraphic.impl.bitmap.URLImageCustomGraphics;


/**
 * Write session states into files.<br>
 * Basic functions of this class are:<br>
 * <ul>
 * <li> 1. Create network files</li>
 * <li> 2. Create session state file</li>
 * <li> 3. Get properties file locations</li>
 * <li> 4. Zip them into one session file "*.cys" </li>
 * </ul>
 *
 * @version 1.0
 * @since 2.3
 * @see cytoscape.data.readers.XGMMLReader
 * @author kono
 *
 */
public class CytoscapeSessionWriter {
	
	// These values will be replaced.
	private static final char[] INVALID_CHAR = {'[', '\\', '/', ':', '*', '?', '"', '<', '>', '|', ']'};
	
	// cysession.xml document version
	private static final String cysessionVersion = "0.9";

	// Enumerate types (node & edge)
	/**
	 *
	 */
	public static final int NODE = 1;

	/**
	 *
	 */
	public static final int EDGE = 2;
	private static final String DEFAULT_VS_NAME = "default";

	// Number of Cytopanels. Currently, we have 3 panels.
	private static final int CYTOPANEL_COUNT = 3;

	// Name of CySession file.
	private static final String CYSESSION_FILE_NAME = "cysession.xml";
	private static final String VIZMAP_FILE = "session_vizmap.props";
	private static final String CYPROP_FILE = "session_cytoscape.props";
	private static final String BOOKMARKS_FILE = "session_bookmarks.xml";

	// Extension for the xgmml file
	private static final String XGMML_EXT = ".xgmml";

	// Package name generated by JAXB.
	// This file was created from "cysession.schema"
	private final String packageName = "cytoscape.generated";

	// Property files
	Properties prop;

	// Root of the network tree
	private static final String TREE_ROOT = "root";

	// File name for the session
	private String sessionFileName = null;
	private Bookmarks bookmarks;
	private Set<CyNetwork> networks;
	private HashMap networkMap;
	private String sessionNote = "You can add note for this session here.";

	//
	// The following JAXB-generated objects are for CySession.xml file.
	//
	private ObjectFactory factory;
	private Cysession session;
	private NetworkTree tree;
	private SessionState sState;
	private List netList;
	private Cytopanels cps;
	private Plugins plugins;
	private String sessionDirName;
	private String sessionDir;
	private Map viewMap = Cytoscape.getNetworkViewMap();
	private ZipOutputStream zos; 

	/**
	 * Constructor.
	 *
	 * @param sessionName
	 *            Filename of the session.
	 */
	public CytoscapeSessionWriter(String sessionName) {
		this.sessionFileName = sessionName;

		// For now, session ID is time and date
		final DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm");
		sessionDirName = "CytoscapeSession-" + df.format(new Date()); 
		sessionDir = sessionDirName + "/"; 

		// Get all networks in the session
		networks = Cytoscape.getNetworkSet();
		networkMap = new HashMap();
	}

	/**
	 * Write current session to a local .cys file.
	 *
	 * @throws Exception
	 *
	 */
	public void writeSessionToDisk() throws Exception {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(sessionFileName);
			try {
				zos = new ZipOutputStream(fos);

				for (CyNetwork network : networks)
					zipNetwork(network);
				zipCySession();
				zipVizmapProps();
				zipCytoscapeProps();
				zipBookmarks();
				zipFileListMap();
				zipCustomGraphics();
			}
			finally {
				if (zos != null) {
					zos.close();
				}
			}
		}
		finally {
			if (fos != null) {
				fos.close();
			}
		}

		Cytoscape.firePropertyChange(Cytoscape.SESSION_SAVED, null, null);
	}


	/**
	 * Utility to replace invalid chars in the XGMML file name.<br>
	 *
	 * @param fileName
	 *            Original file name directly taken from the title.
	 * @return Modified file name without invalid chars.
	 *
	 */
	private String getValidFileName(String fileName) {
		
		String newFileName = fileName;
		Integer i = 0;
		for(Character ch : INVALID_CHAR) 
			newFileName = newFileName.replace(ch.toString(), (i++).toString());
		
		return newFileName;
	}

	/**
	 * Initialize objects for the marshaller.
	 *
	 * @throws JAXBException
	 */
	private void initObjectsForDataBinding() throws JAXBException {
		factory = new ObjectFactory();

		session = factory.createCysession();
		session.setSessionNote(sessionNote);

		tree = factory.createNetworkTree();
		sState = factory.createSessionState();
		setDesktopStates();
		session.setSessionState(sState);
		cps = getCytoPanelStates();
		netList = tree.getNetwork();
		sState.setPlugins(plugins);
		sState.setCytopanels(cps);
		sState.setServer(getServerState());
	}

	private void setDesktopStates() throws JAXBException {
		DesktopSize dSize = factory.createDesktopSize();
		NetworkFrames frames = factory.createNetworkFrames();
		Component[] networkFrames = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane()
		                                     .getComponents();

		for (int i = 0; i < networkFrames.length; i++) {
			if(networkFrames[i] instanceof JInternalFrame) {
				JInternalFrame networkFrame = (JInternalFrame) networkFrames[i];
				NetworkFrame frame = factory.createNetworkFrame();
				frame.setFrameID(networkFrame.getTitle());
				frame.setWidth(BigInteger.valueOf(networkFrame.getWidth()));
				frame.setHeight(BigInteger.valueOf(networkFrame.getHeight()));
				frame.setX(BigInteger.valueOf(networkFrame.getX()));
				frame.setY(BigInteger.valueOf(networkFrame.getY()));
				frames.getNetworkFrame().add(frame);
			}
		}

		dSize.setHeight(BigInteger.valueOf(Cytoscape.getDesktop().getSize().height));
		dSize.setWidth(BigInteger.valueOf(Cytoscape.getDesktop().getSize().width));

		Desktop desktop = factory.createDesktop();
		desktop.setDesktopSize(dSize);
		desktop.setNetworkFrames(frames);
		sState.setDesktop(desktop);
	}

	/**
	 * Writes the vizmap.props file to the session zip.
	 */
	private void zipVizmapProps() throws IOException {

		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = vizmapper.getCalculatorCatalog();

		zos.putNextEntry(new ZipEntry(sessionDir + VIZMAP_FILE) );

		Writer writer = new OutputStreamWriter( zos );
		CalculatorIO.storeCatalog(catalog, writer);
	}

	/**
	 * Writes the cytoscape.props file to the session zip.
	 */
	private void zipCytoscapeProps() throws IOException {
	
		zos.putNextEntry(new ZipEntry(sessionDir + CYPROP_FILE) );

		CytoscapeInit.getProperties().store(zos, "Cytoscape Property File");
	}

	/**
	 * Writes the bookmarks.xml file to the session zip.
	 */
	private void zipBookmarks() throws IOException, JAXBException {

		zos.putNextEntry(new ZipEntry(sessionDir + BOOKMARKS_FILE) );

		bookmarks = Cytoscape.getBookmarks();
		BookmarksUtil.saveBookmark(bookmarks, zos);
	}
	
	
	private void zipCustomGraphics() throws IOException, InterruptedException {
		final CustomGraphicsManager pool = Cytoscape.getVisualMappingManager().getCustomGraphicsPool();
		// Collect all custom graphics
		final Collection<CyCustomGraphics> customGraphics = pool.getAll();
		
		// Add metadata file to the session file
		zos.putNextEntry(new ZipEntry(sessionDir + "images/" + CustomGraphicsManager.METADATA_FILE));
		pool.getMetadata().store(zos, "Image Metadata");
		
		for(CyCustomGraphics cg: customGraphics) {
			final Image img = cg.getRenderedImage();
			// For now, save URLImage only.
			// TODO: how can we handle Dynamic Images?
			if(img != null && cg instanceof NullCustomGraphics == false && cg instanceof URLImageCustomGraphics) {
				final Long id= cg.getIdentifier();
				zos.putNextEntry(new ZipEntry(sessionDir + "images/" + id + ".png"));
				ImageIO.write(ImageUtil.toBufferedImage(img), "PNG", zos);
			}
		}
	}

	/**
	 * Writes a network file to the session zip. 
	 *
	 * @throws URISyntaxException
	 * @throws JAXBException
	 * @throws XMLStreamException
	 */
	private void zipNetwork(final CyNetwork network)
	    throws IOException, JAXBException, URISyntaxException {

		String xgmmlFile = getValidFileName( network.getTitle() + XGMML_EXT );
		CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());

		zos.putNextEntry(new ZipEntry(sessionDir + xgmmlFile));

		Writer writer = new OutputStreamWriter(zos, "UTF-8");
		// Write the XGMML file *without* our graphics attributes
		// We'll let the Vizmapper handle those
		XGMMLWriter xgmmlWriter = new XGMMLWriter(network, view, true);
		xgmmlWriter.write(writer);
	}

	/**
	 * Create cysession.xml file.
	 *
	 * @throws Exception
	 */
	private void zipCySession() throws Exception {
		final JAXBContext jc = JAXBContext.newInstance(packageName, this.getClass().getClassLoader());

		initObjectsForDataBinding();
		session.setId(sessionDirName);

		// Document version. Maybe used in the future.
		session.setDocumentVersion(cysessionVersion);

		getNetworkTree();
		session.setNetworkTree(tree);

		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
		              new NamespacePrefixMapperForCysession());

		zos.putNextEntry(new ZipEntry(sessionDir + CYSESSION_FILE_NAME) );

		m.marshal(session, zos);
		session = null;
	}

	/**
	 * Writes any files from plugins to the session file.
	 *
	 * @throws IOException
	 */
	private void zipFileListMap() throws IOException {

		// fire an event to tell plugins we're ready to save!
		Map<String, List<File>> pluginFileMap = new HashMap<String, List<File>>();
		Cytoscape.firePropertyChange(Cytoscape.SAVE_PLUGIN_STATE, pluginFileMap, null);

		// now write any files to the zip files
		if ((pluginFileMap != null) && (pluginFileMap.size() > 0)) {
			byte[] buf = new byte[5000];
			Set<String> pluginSet = pluginFileMap.keySet();
		
			for (String pluginName : pluginSet) {
				List<File> theFileList = (List<File>) pluginFileMap.get(pluginName);
		
				if ((theFileList == null) || (theFileList.size() == 0))
					continue;
	
				for (File theFile : theFileList) {
					if ((theFile == null) || (!theFile.exists()))
						continue;
	
					zos.putNextEntry(new ZipEntry( sessionDir + "plugins/" + pluginName + 
					                               "/" + theFile.getName() ) );

					// copy the file contents to the zip output stream
					FileInputStream fileIS = null;
					try {
						fileIS = new FileInputStream(theFile);
						int numRead = 0;
						while ((numRead = fileIS.read(buf)) > -1) {
							zos.write(buf, 0, numRead);
						}
					}
					finally {
						if (fileIS != null) {
							fileIS.close();
						}
					}
				}
			}
		}
	}

	/**
	 * Get information about the current session status.
	 *
	 * This includes the following: 1. List of networks opened/created by the
	 * user. 2. Status of the network. 3. relationship between
	 * network-attributes Build GML file into xml
	 *
	 * Extract current NetworkPanel state for saving.
	 */
	private void getNetworkTree() throws Exception {
		// Tree table storeed in the Network Panel
		JTreeTable treeTable;

		// Get network panel
		CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
		NetworkPanel netPanel = cyDesktop.getNetworkPanel();

		// Get list of networks
		treeTable = netPanel.getTreeTable();

		Iterator itr = networks.iterator();

		// Visit each node in the tree
		while (itr.hasNext()) {
			CyNetwork network = (CyNetwork) itr.next();
			String networkID = network.getIdentifier();
			String networkName = network.getTitle();

			networkMap.put(networkName, networkID);
		}

		if (treeTable != null) {
			// Extract root node in the tree
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) netPanel.getNetworkNode(TREE_ROOT);

			walkTree(root);
		}
	}

	/**
	 * Visit all tree node and save the status in the model.
	 *
	 * @param node
	 * @throws JAXBException
	 */
	private void walkTree(DefaultMutableTreeNode node) throws JAXBException {
		// Check number of children for this node.
		int childCount = node.getChildCount();

		// Create Network object for this node.
		String fileName = node.getUserObject().toString() + XGMML_EXT;
		Network curNode = factory.createNetwork();
		curNode.setFilename(getValidFileName(fileName));
		curNode.setId(node.getUserObject().toString());

		CyNetwork curNet = Cytoscape.getNetwork((String) networkMap.get(node.getUserObject()
		                                                                    .toString()));
		CyNetworkView curView = (CyNetworkView) viewMap.get(curNet.getIdentifier());

		if (!node.getUserObject().toString().equals("Network Root")) {
			String visualStyleName = null;

			if (curView != null) {
				VisualStyle curVS = curView.getVisualStyle();

				if (curVS != null) {
					visualStyleName = curVS.getName();
				}
			}

			if (visualStyleName == null) {
				visualStyleName = DEFAULT_VS_NAME;
			}

			curNode.setVisualStyle(visualStyleName);
		} else {
			curNode.setVisualStyle(DEFAULT_VS_NAME);
		}

		if (Cytoscape.getNetworkView((String) networkMap.get(node.getUserObject().toString())) == Cytoscape
		                                                                                          .getNullNetworkView()) {
			curNode.setViewAvailable(false);
		} else {
			curNode.setViewAvailable(true);
		}

		Parent parent = null;
		parent = factory.createParent();

		if (node.getParent() == null) {
			parent.setId("NULL");
			curNode.setParent(parent);
		} else {
			// Set current network as the parent of child networks.
			DefaultMutableTreeNode curParent = (DefaultMutableTreeNode) node.getParent();
			parent.setId(curParent.getUserObject().toString());
			curNode.setParent(parent);
		}

		List children = curNode.getChild();

		for (int i = 0; i < childCount; i++) {
			// Exctract a network from the Network Panel.
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);

			// Create Child object
			Child childNetwork = factory.createChild();
			childNetwork.setId(child.getUserObject().toString());
			children.add(childNetwork);

			if (child.isLeaf()) {
				// Reached to the leaf of network tree.
				// Need to create leaf node here.
				Network leaf = factory.createNetwork();
				String childFileName = child.getUserObject().toString() + XGMML_EXT;
				leaf.setFilename(getValidFileName(childFileName));
				leaf.setId(child.getUserObject().toString());

				CyNetworkView leafView = Cytoscape.getNetworkView((String) networkMap.get(child.getUserObject()
				                                                                               .toString()));

				String leafVisualStyleName = null;

				if (leafView != Cytoscape.getNullNetworkView()) {
					VisualStyle leafVS = leafView.getVisualStyle();

					if (leafVS != null) {
						leafVisualStyleName = leafVS.getName();
					}
				}

				if (leafVisualStyleName == null) {
					leafVisualStyleName = DEFAULT_VS_NAME;
				}

				leaf.setVisualStyle(leafVisualStyleName);

				String targetID = (String) networkMap.get(child.getUserObject().toString());

				Parent tempParent = factory.createParent();
				tempParent.setId(curNode.getId());
				leaf.setParent(tempParent);

				CyNetwork targetNetwork = Cytoscape.getNetwork(targetID);
				CyNetworkView curNetworkView = Cytoscape.getNetworkView(targetID);

				if (curNetworkView == Cytoscape.getNullNetworkView()) {
					leaf.setViewAvailable(false);
				} else {
					leaf.setViewAvailable(true);
				}

				/*
				 * This is for Metanode. Will be used in the future...
				 *
				 * Iterator it = targetNetwork.nodesIterator(); ViewableNodes vn =
				 * factory.createViewableNodes(); while (it.hasNext()) { String
				 * viewableID = ((CyNode) it.next()) .getIdentifier(); Node
				 * viewableNode = factory.createNode();
				 * viewableNode.setId(viewableID);
				 * vn.getNode().add(viewableNode); } leaf.setViewableNodes(vn);
				 */

				/*
				 * Add selected & hidden nodes/edges foe leaf nodes.
				 */
				SelectedNodes sn = (SelectedNodes) getSelectedObjects(NODE, targetNetwork);

				if (sn != null) {
					leaf.setSelectedNodes(sn);
				}

				SelectedEdges se = (SelectedEdges) getSelectedObjects(EDGE, targetNetwork);

				if (se != null) {
					leaf.setSelectedEdges(se);
				}

				HiddenNodes hn = (HiddenNodes) getHiddenObjects(NODE, curNetworkView);
				HiddenEdges he = (HiddenEdges) getHiddenObjects(EDGE, curNetworkView);

				if (hn != null) {
					leaf.setHiddenNodes(hn);
				}

				if (he != null) {
					leaf.setHiddenEdges(he);
				}

				netList.add(leaf);
			} else {
				walkTree(child);
			}
		}

		//
		// Add hidden/selected nodes and edges
		//
		String targetID = (String) networkMap.get(node.getUserObject().toString());
		CyNetwork targetNetwork = Cytoscape.getNetwork(targetID);

		/*
		 * This is for metanode. will be used in the future.
		 *
		 * if (curNode.getId() != "Network Root") { Iterator it =
		 * targetNetwork.nodesIterator(); ViewableNodes vn =
		 * factory.createViewableNodes(); while (it.hasNext()) {
		 *
		 * String viewableID = ((CyNode) it.next()).getIdentifier(); Node
		 * viewableNode = factory.createNode(); viewableNode.setId(viewableID);
		 * vn.getNode().add(viewableNode); } curNode.setViewableNodes(vn); }
		 */
		SelectedNodes sn = (SelectedNodes) getSelectedObjects(NODE, targetNetwork);

		if (sn != null) {
			curNode.setSelectedNodes(sn);
		}

		SelectedEdges se = (SelectedEdges) getSelectedObjects(EDGE, targetNetwork);

		if (se != null) {
			curNode.setSelectedEdges(se);
		}

		// Extract hidden nodes and edges
		CyNetworkView curNetworkView = Cytoscape.getNetworkView(targetID);

		if (curNetworkView != Cytoscape.getNullNetworkView()) {
			HiddenNodes hn = (HiddenNodes) getHiddenObjects(NODE, curNetworkView);
			HiddenEdges he = (HiddenEdges) getHiddenObjects(EDGE, curNetworkView);

			if (hn != null) {
				curNode.setHiddenNodes(hn);
			}

			if (he != null) {
				curNode.setHiddenEdges(he);
			}
		}

		// Add current network to the list.
		netList.add(curNode);
	}

	/**
	 *
	 * @param type
	 *            Type of the object (node or edge)
	 * @param view
	 *            Current network view.
	 * @return JAXB object (HiddenNodes or HiddenEdges)
	 * @throws JAXBException
	 */
	private Object getHiddenObjects(int type, CyNetworkView view) throws JAXBException {
		// List-up all hidden nodes
		if (type == NODE) {
			HiddenNodes hn = factory.createHiddenNodes();
			List hNodeList = hn.getNode();

			CyNode targetNode = null;
			String curNodeName = null;

			for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
				NodeView nview = (NodeView) i.next();

				// Check if the node is hidden or not.
				// If it's hidden, store in the session file.
				if (view.showGraphObject(nview)) {
					targetNode = (CyNode) nview.getNode();
					curNodeName = targetNode.getIdentifier();

					Node tempNode = factory.createNode();
					tempNode.setId(curNodeName);

					hNodeList.add(tempNode);

					// Keep them hidden...
					view.hideGraphObject(nview);
				}
			}

			if (hn.getNode().size() != 0) {
				return hn;
			} else {
				return null;
			}
		} else if (type == EDGE) {
			HiddenEdges he = factory.createHiddenEdges();
			List hEdgeList = he.getEdge();

			CyEdge targetEdge = null;
			String curEdgeName = null;

			for (Iterator i = view.getEdgeViewsIterator(); i.hasNext();) {
				EdgeView eview = (EdgeView) i.next();

				// Check if the edge is hidden or not.
				// If it's hidden, store in the session file.
				if (view.showGraphObject(eview)) {
					targetEdge = (CyEdge) eview.getEdge();
					curEdgeName = targetEdge.getIdentifier();

					cytoscape.generated.Edge tempEdge = factory.createEdge();
					tempEdge.setId(curEdgeName);
					tempEdge.setSource(targetEdge.getSource().getIdentifier());
					tempEdge.setTarget(targetEdge.getTarget().getIdentifier());
					tempEdge.setInteraction(Cytoscape.getEdgeAttributes()
					                                 .getStringAttribute(targetEdge.getIdentifier(),
					                                                     Semantics.INTERACTION));
					hEdgeList.add(tempEdge);
					// Keep them hidden...
					view.hideGraphObject(eview);
				}
			}

			if (he.getEdge().size() != 0) {
				return he;
			} else {
				return null;
			}
		}

		return null;
	}

	/**
	 * List all selected nodes and edges in the session file.
	 *
	 * @param type
	 *            Tyoe if object (node or edge)
	 * @param curNet
	 *            Current network
	 * @return
	 * @throws JAXBException
	 */
	private Object getSelectedObjects(int type, CyNetwork curNet) throws JAXBException {
		if (type == NODE) {
			SelectedNodes sn = factory.createSelectedNodes();
			List sNodeList = sn.getNode();

			Set selectedNodes = curNet.getSelectedNodes();

			if (selectedNodes.size() != 0) {
				Iterator iterator = selectedNodes.iterator();
				CyNode targetNode = null;

				while (iterator.hasNext()) {
					targetNode = (CyNode) iterator.next();

					String curNodeName = targetNode.getIdentifier();
					Node tempNode = factory.createNode();
					tempNode.setId(curNodeName);

					sNodeList.add(tempNode);
				}

				return sn;
			} else {
				return null;
			}
		} else if (type == EDGE) {
			SelectedEdges se = factory.createSelectedEdges();
			List sEdgeList = se.getEdge();

			Set selectedEdges = curNet.getSelectedEdges();

			if (selectedEdges.size() != 0) {
				Iterator iterator = selectedEdges.iterator();
				CyEdge targetEdge = null;

				while (iterator.hasNext()) {
					targetEdge = (CyEdge) iterator.next();

					String curEdgeName = targetEdge.getIdentifier();
					cytoscape.generated.Edge tempEdge = factory.createEdge();
					tempEdge.setId(curEdgeName);
					tempEdge.setSource(targetEdge.getSource().getIdentifier());
					tempEdge.setTarget(targetEdge.getTarget().getIdentifier());
					tempEdge.setInteraction(Cytoscape.getEdgeAttributes()
					                                 .getStringAttribute(targetEdge.getIdentifier(),
					                                                     Semantics.INTERACTION));
					sEdgeList.add(tempEdge);
				}

				return se;
			} else {
				return null;
			}
		}

		return null;
	}

	/**
	 * Extract states of the 3 Cytopanels.
	 *
	 * @return
	 * @throws JAXBException
	 *
	 * Note: We will store the states of plugins near future. The location of
	 * those states will be stored here.
	 */
	private Cytopanels getCytoPanelStates() throws JAXBException {
		Cytopanels cps = factory.createCytopanels();
		List cytoPanelList = cps.getCytopanel();

		String[] cytopanelStates = new String[CYTOPANEL_COUNT + 1];
		int[] selectedPanels = new int[CYTOPANEL_COUNT + 1];

		// Extract states of 3 panels.
		cytopanelStates[1] = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).getState()
		                              .toString();
		selectedPanels[1] = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
		                             .getSelectedIndex();

		cytopanelStates[2] = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).getState()
		                              .toString();
		selectedPanels[2] = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		                             .getSelectedIndex();

		cytopanelStates[3] = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).getState()
		                              .toString();
		selectedPanels[3] = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
		                             .getSelectedIndex();

		for (int i = 1; i < (CYTOPANEL_COUNT + 1); i++) {
			Panels internalPanels = factory.createPanels();
			List iPanelList = internalPanels.getPanel();
			Panel iPanel = factory.createPanel();
			iPanel.setId("test");

			iPanelList.add(iPanel);

			Cytopanel curCp = factory.createCytopanel();
			curCp.setId("CytoPanel" + i);
			curCp.setPanelState(cytopanelStates[i]);
			curCp.setSelectedPanel(Integer.toString(selectedPanels[i]));
			curCp.setPanels(internalPanels);
			cytoPanelList.add(curCp);
		}

		return cps;
	}

	/**
	 * Set session note.<br>
	 * Session note can be anything, it is just like a memo pad for the session.
	 * NOTE: session note should be set before calling writeSessionToDisk().
	 *
	 * @param note
	 *            Session note string.
	 * @uml.property name="sessionNote"
	 */
	public void setSessionNote(String note) {
		this.sessionNote = note;
	}

	/**
	 * Check loaded ontologies and save those states in cysession.xml.
	 *
	 * @return Server object
	 */
	private Server getServerState() {
		Server server = factory.createServer();
		OntologyServer os = factory.createOntologyServer();

		Set<String> ontoNames = Cytoscape.getOntologyServer().getOntologyNames();
		Map<String, URL> sources = Cytoscape.getOntologyServer().getOntologySources();

		for (String name : ontoNames) {
			Ontology onto = factory.createOntology();
			onto.setName(name);
			onto.setHref(sources.get(name).toString());
			os.getOntology().add(onto);
		}

		server.setOntologyServer(os);

		return server;
	}
}


class NamespacePrefixMapperForCysession extends NamespacePrefixMapper {
	/**
	 * Returns a preferred prefix for the given namespace URI.
	 *
	 * This method is intended to be overrided by a derived class.
	 *
	 * @param namespaceUri
	 *            The namespace URI for which the prefix needs to be found.
	 *            Never be null. "" is used to denote the default namespace.
	 * @param suggestion
	 *            When the content tree has a suggestion for the prefix to the
	 *            given namespaceUri, that suggestion is passed as a parameter.
	 *            Typicall this value comes from the QName.getPrefix to show the
	 *            preference of the content tree. This parameter may be null,
	 *            and this parameter may represent an already occupied prefix.
	 * @param requirePrefix
	 *            If this method is expected to return non-empty prefix. When
	 *            this flag is true, it means that the given namespace URI
	 *            cannot be set as the default namespace.
	 *
	 * @return null if there's no prefered prefix for the namespace URI. In this
	 *         case, the system will generate a prefix for you.
	 *
	 * Otherwise the system will try to use the returned prefix, but generally
	 * there's no guarantee if the prefix will be actually used or not.
	 *
	 * return "" to map this namespace URI to the default namespace. Again,
	 * there's no guarantee that this preference will be honored.
	 *
	 * If this method returns "" when requirePrefix=true, the return value will
	 * be ignored and the system will generate one.
	 */
	public String getPreferredPrefix(final String namespaceUri, final String suggestion,
	                                 boolean requirePrefix) {
		// I want this namespace to be mapped to "xsi"
		if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri))
			return "xsi";

		// Xlink
		if ("http://www.w3.org/1999/xlink".equals(namespaceUri)) {
			return "xlink";
		}

		// otherwise I don't care. Just use the default suggestion, whatever it
		// may be.
		return suggestion;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String[] getPreDeclaredNamespaceUris() {
		return new String[] {
		           "http://www.w3.org/2001/XMLSchema-instance", "http://www.w3.org/1999/xlink",
		       };
	}
}
