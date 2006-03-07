/*
 File: XGMMLReader.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute of Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
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

package cytoscape.data.readers;

import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.generated2.Att;
import cytoscape.generated2.Graph;
import cytoscape.generated2.Graphics;
import cytoscape.generated2.RdfRDF;
import cytoscape.generated2.impl.AttImpl;
import cytoscape.visual.LineType;

/**
 * XGMMLReader. This version is Metanode-compatible.
 * 
 * @author kono
 * 
 */
public class XGMMLReader implements GraphReader {

	Random rdn = new Random();

	// Graph Tags
	protected static String GRAPH = "graph";
	protected static String NODE = "node";
	protected static String EDGE = "edge";
	protected static String GRAPHICS = "graphics";
	protected static String LABEL = "label";
	protected static String SOURCE = "source";
	protected static String TARGET = "target";

	// The following elements are in "graphics" section of GML
	protected static String X = "x";
	protected static String Y = "y";
	protected static String H = "h";
	protected static String W = "w";
	protected static String TYPE = "type";
	protected static String ID = "id";
	protected static String ROOT_INDEX = "root_index";

	// Shapes used in Cytoscape (not GML standard)
	// In GML, they are called "type"
	protected static String RECTANGLE = "rectangle";
	protected static String ELLIPSE = "ellipse";
	protected static String LINE = "Line"; // This is the Polyline object.
	protected static String POINT = "point";
	protected static String DIAMOND = "diamond";
	protected static String HEXAGON = "hexagon";
	protected static String OCTAGON = "octagon";
	protected static String PARALELLOGRAM = "parallelogram";
	protected static String TRIANGLE = "triangle";

	// Other GML "graphics" attributes
	protected static String FILL = "fill";
	protected static String WIDTH = "width";
	protected static String STRAIGHT_LINES = "line";
	protected static String CURVED_LINES = "curved";
	protected static String SOURCE_ARROW = "source_arrow";
	protected static String TARGET_ARROW = "target_arrow";

	// States of the ends of arrows
	protected static String ARROW = "arrow";
	protected static String ARROW_NONE = "none";
	protected static String ARROW_FIRST = "first";
	protected static String ARROW_LAST = "last";
	protected static String ARROW_BOTH = "both";
	protected static String OUTLINE = "outline";
	protected static String OUTLINE_WIDTH = "outline_width";
	protected static String DEFAULT_EDGE_INTERACTION = "pp";

	protected static final String FLOAT_TYPE = "float";
	protected static final String INT_TYPE = "int";
	protected static final String STRING_TYPE = "string";
	protected static final String BOOLEAN_TYPE = "boolean";
	protected static final String LIST_TYPE = "list";
	protected static final String MAP_TYPE = "map";

	// XGMML file name to be loaded.
	private String fileName;
	private String networkName;

	// Package name generated by JAXB based on XGMML schema file
	private final String XGMML_PACKAGE = "cytoscape.generated2";

	private List nodes;
	private List edges;

	private RdfRDF metadata;
	private String backgroundColor;

	InputStream networkStream;

	OpenIntIntHashMap nodeIDMap;
	IntArrayList giny_nodes, giny_edges;

	ArrayList nodeIndex, edgeIndex;

	Graph network;

	ArrayList rootNodes;

	HashMap metanodeMap;

	CyAttributes nodeAttributes;
	CyAttributes edgeAttributes;

	HashMap nodeGraphicsMap;
	HashMap edgeGraphicsMap;
	
	public XGMMLReader(String fileName) {

		this.fileName = fileName;
		try {
			// System.out.println("Opening stream for " + fileName);
			networkStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.networkName = null;

		this.metanodeMap = new HashMap();
		this.nodeGraphicsMap = new HashMap();
		this.edgeGraphicsMap = new HashMap();
	}

	public XGMMLReader(InputStream is) {

		this.networkName = null;
		this.networkStream = is;

		this.metanodeMap = new HashMap();
		this.nodeGraphicsMap = new HashMap();
		this.edgeGraphicsMap = new HashMap();
	}

	// This constructor will be used when metanode is included in the
	// network.
	public XGMMLReader(InputStream is, List view) {

		this.networkName = null;
		this.networkStream = is;

		this.metanodeMap = new HashMap();

		this.nodeGraphicsMap = new HashMap();
		this.edgeGraphicsMap = new HashMap();
	}

	public void readIndex() throws JAXBException, FileNotFoundException {
		JAXBContext jc = JAXBContext.newInstance(XGMML_PACKAGE);
		// Unmarshall the XGMML file
		Unmarshaller u = jc.createUnmarshaller();

		Graph network = (Graph) u.unmarshal(new FileInputStream(fileName));

		List nodesAndEdges = network.getNodeOrEdge();
		Iterator it = nodesAndEdges.iterator();
		while (it.hasNext()) {
			Object curObj = it.next();
			if (curObj.getClass() == cytoscape.generated2.impl.NodeImpl.class) {
				nodes.add(curObj);
			} else {
				edges.add(curObj);
			}
		}

	}

	public void read() throws IOException {
		try {
			this.readXGMML();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Actual method to unmarshall XGMML documents.
	 * 
	 * @throws JAXBException
	 * @throws IOException
	 */
	private void readXGMML() throws JAXBException, IOException {

		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();

		// Use JAXB-generated methods to create data structure
		JAXBContext jc = JAXBContext.newInstance(XGMML_PACKAGE);
		// Unmarshall the XGMML file
		Unmarshaller u = jc.createUnmarshaller();

		// Read the file and map the entire XML document into
		// data structure.
		network = (Graph) u.unmarshal(networkStream);
		networkName = network.getLabel();

		rootNodes = new ArrayList();

		// Extract Network Attributes
		// Currently, supported attribute data type is RDF metadata only.
		List networkAttributes = network.getAtt();

		for (int i = 0; i < networkAttributes.size(); i++) {
			Att curAtt = (Att) networkAttributes.get(i);

			// System.out.println("Network Attribute found: " +
			// curAtt.getName());
			if (curAtt.getName().equals("networkMetadata")) {
				metadata = (RdfRDF) (curAtt.getContent().get(0));

			} else if (curAtt.getName().equals("backgroundColor")) {
				// System.out.println("Background color is " +
				// curAtt.getValue());
				backgroundColor = curAtt.getValue();
			}
		}

		// Get all nodes and edges as one List object.
		List nodesAndEdges = network.getNodeOrEdge();

		// Split the list into two: node and edge list
		nodes = new ArrayList();
		edges = new ArrayList();
		Iterator it = nodesAndEdges.iterator();
		while (it.hasNext()) {
			Object curObj = it.next();
			if (curObj.getClass() == cytoscape.generated2.impl.NodeImpl.class) {
				nodes.add(curObj);
			} else {
				edges.add(curObj);
			}
		}

		// Build the network
		createGraph(network);
	}

	/**
	 * Create graph directly from JAXB objects
	 * 
	 * @param network
	 */
	protected void createGraph(Graph network) {

		ArrayList metanodes = new ArrayList();

		// Check capacity
		Cytoscape.ensureCapacity(nodes.size(), edges.size());

		// Extract nodes
		nodeIDMap = new OpenIntIntHashMap(nodes.size());
		giny_nodes = new IntArrayList(nodes.size());
		//OpenIntIntHashMap gml_id2order = new OpenIntIntHashMap(nodes.size());
		
		HashMap gml_id2order = new HashMap(nodes.size());
		
		
		Set nodeNameSet = new HashSet(nodes.size());

		HashMap nodeMap = new HashMap(nodes.size());

		// Add All Nodes to Network
		for (int idx = 0; idx < nodes.size(); idx++) {

			// Get a node object (NOT a giny node. XGMML node!)
			cytoscape.generated2.Node curNode = (cytoscape.generated2.Node) nodes
					.get(idx);

			String nodeType = curNode.getName();

			String label = (String) curNode.getLabel();

			readAttributes(label, curNode.getAtt(), NODE);

			// System.out.println("Node Name: " + label + ", ID is "
			// + Cytoscape.getCyNode(label, true).getRootGraphIndex());

			nodeMap.put(curNode.getId(), label);

			// nodeMap.put(curNode.getId(), label);
			if (nodeType != null) {
				if (nodeType.equals("metaNode")) {

					// List of child nodes under this parent node.
					List children = null;

					Iterator it = curNode.getAtt().iterator();
					while (it.hasNext()) {
						Att curAttr = (Att) it.next();

						if (curAttr.getName().equals("metanodeChildren")) {
							Graph subgraph = (Graph) curAttr.getContent()
									.get(0);
							children = subgraph.getNodeOrEdge();
							metanodeMap.put(label, children);
						}
					}
				}

			}
			if (nodeNameSet.add(curNode.getId())) {
				Node node = (Node) Cytoscape.getCyNode(label, true);

				giny_nodes.add(node.getRootGraphIndex());
				nodeIDMap.put(idx, node.getRootGraphIndex());
				
				
				//gml_id2order.put(Integer.parseInt(curNode.getId()), idx);
				
				
				gml_id2order.put(curNode.getId(), Integer.toString(idx));
				
				
				
				
				// ((KeyValue) node_root_index_pairs.get(idx)).value = (new
				// Integer(
				// node.getRootGraphIndex()));
			} else {
				throw new GMLException("GML id " + nodes.get(idx)
						+ " has a duplicated label: " + label);
				// ((KeyValue)node_root_index_pairs.get(idx)).value = null;
			}
		}
		nodeNameSet = null;

		// Extract edges
		giny_edges = new IntArrayList(edges.size());
		Set edgeNameSet = new HashSet(edges.size());

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// Add All Edges to Network
		for (int idx = 0; idx < edges.size(); idx++) {

			cytoscape.generated2.Edge curEdge = (cytoscape.generated2.Edge) edges
					.get(idx);

//			if (gml_id2order.containsKey(Integer.parseInt(curEdge.getSource()))
//					&& gml_id2order.containsKey(Integer.parseInt(curEdge
//							.getTarget()))) {
			
			if (gml_id2order.containsKey(curEdge.getSource())
					&& gml_id2order.containsKey(curEdge.getTarget())) {
			
			
				// String label = curEdge.getLabel();

				String edgeName = curEdge.getLabel();

				if (edgeName == null) {
					edgeName = "N/A";
				}

				int duplicate_count = 1;
				while (!edgeNameSet.add(edgeName)) {
					edgeName = edgeName + "_" + duplicate_count;
					duplicate_count += 1;
				}

				Edge edge = Cytoscape.getRootGraph().getEdge(edgeName);

				if (edge == null) {

					String sourceName = (String) nodeMap.get(curEdge
							.getSource());
					String targetName = (String) nodeMap.get(curEdge
							.getTarget());

					Node node_1 = Cytoscape.getRootGraph().getNode(sourceName);
					Node node_2 = Cytoscape.getRootGraph().getNode(targetName);

					// edge = Cytoscape.getCyEdge(node_1, node_2,
					// Semantics.INTERACTION, edgeName, true);

					Iterator it = curEdge.getAtt().iterator();
					Att interaction = null;
					String itrValue = "pp";
					while (it.hasNext()) {
						interaction = (Att) it.next();
						if (interaction.getName().equals("interaction")) {
							itrValue = interaction.getValue();
							break;
						}
					}
					// System.out.println("!!!!!Edge Data: " + itrValue);
					edge = Cytoscape.getCyEdge(node_1, node_2,
							Semantics.INTERACTION, itrValue, true);
				}

				// Set correct ID, canonical name and interaction name
				edge.setIdentifier(edgeName);
				// System.out.println("Edge Data: " + edge.getIdentifier());

				readAttributes(edgeName, curEdge.getAtt(), EDGE);

				giny_edges.add(edge.getRootGraphIndex());
				// ((KeyValue) edge_root_index_pairs.get(idx)).value = (new
				// Integer(
				// edge.getRootGraphIndex()));
			} else {
				throw new GMLException(
						"Non-existant source/target node for edge with gml (source,target): "
								+ nodeMap.get(curEdge.getSource()) + ","
								+ nodeMap.get(curEdge.getTarget()));
			}
		}
		edgeNameSet = null;
		nodeMap = null;

		Iterator nit = Cytoscape.getRootGraph().nodesIterator();
		while (nit.hasNext()) {
			CyNode testnode = (CyNode) nit.next();
			// System.out.println("ROOT LIST: " + testnode.getIdentifier());
		}

		if (metanodeMap.size() != 0) {

			Set keySet = metanodeMap.keySet();
			Iterator it = keySet.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				createMetaNode(key, (List) metanodeMap.get(key));
			}
		}

	}

	private CyNode createMetaNode(String name, List children) {
		Iterator it = children.iterator();
		// System.out.println("Metanode = " + name);

		int[] childrenNodeIndices = new int[children.size()];
		int counter = 0;

		while (it.hasNext()) {

			cytoscape.generated2.Node childNode = (cytoscape.generated2.Node) it
					.next();

			Node targetChildNode = Cytoscape.getRootGraph().getNode(
					childNode.getId());

			// System.out.println("+------- child = "
			// + targetChildNode.getRootGraphIndex() + ": "
			// + targetChildNode.getIdentifier());

			childrenNodeIndices[counter] = targetChildNode.getRootGraphIndex();
			counter++;

		}

		int[] edgeIndices = Cytoscape.getRootGraph()
				.getConnectingEdgeIndicesArray(childrenNodeIndices);

		if (edgeIndices != null) {
			for (int i = 0; i < edgeIndices.length; i++) {

				// System.out.println("!! Edge: "
				// + Cytoscape.getRootGraph().getEdge(edgeIndices[i])
				// .getIdentifier());

				// if (edgeIndices[i] > 0) {
				// int rootEdgeIndex =
				// Cytoscape.getRootGraph().getgetRootGraphEdgeIndex(edgeIndices[i]);
				// edgeIndices[i] = rootEdgeIndex;
				// }// if rootEdgeIndex > 0
			}// for i
		}

		rootNodes.add(Cytoscape.getRootGraph().getNode(name));

		return null;
	}

	public List getMetanodeList() {
		return rootNodes;
	}

	protected void readAttributes() {

	}

	protected void readNode() {

	}

	protected void readEdge() {

	}

	/**
	 * Based on the graphic attribute, layout the graph.
	 * 
	 * @param myView
	 */

	public void layout(GraphView myView) {
		if (myView == null || myView.nodeCount() == 0) {
			return;
		}

		// Set background clolor
		if (backgroundColor != null) {
			myView.setBackgroundPaint(getColor(backgroundColor));
		}
		// myView.setBackgroundPaint(new Color(230, 230, 240));
		// Layout nodes
		layoutNode(myView);

		// Generate Visual Style
		

		
		// Layout edges
		layoutEdge(myView);
		
		
		VisualStyleBuilder vsb = new VisualStyleBuilder(networkName + ".style",
				nodeGraphicsMap, edgeGraphicsMap, null);
		vsb.buildStyle();
	}

	/**
	 * Layout nodes
	 * 
	 */
	protected void layoutNode(GraphView myView) {

		String label = null;
		int tempid = 0;

		NodeView view = null;

		for (Iterator it = nodes.iterator(); it.hasNext();) {
			// Extract a node from JAXB-generated object
			cytoscape.generated2.Node curNode = (cytoscape.generated2.Node) it
					.next();

			label = curNode.getLabel();
			Graphics graphics = (Graphics) curNode.getGraphics();

			nodeGraphicsMap.put(label, graphics);

			int nodeID = Cytoscape.getRootGraph().getNode(label)
					.getRootGraphIndex();

			view = myView.getNodeView(nodeID);

			if (label != null && view != null) {
				view.getLabel().setText(label);

			} else if (view != null) {
				view.getLabel().setText("node(" + tempid + ")");
			}

			if (graphics != null && view != null) {
				layoutNodeGraphics(myView, graphics, view);
			} else if (graphics == null) {
				System.out.println("Graphics info is not available for " + view.getLabel().getText());
			}

			// layoutNodeGraphics(myView, graphics, view);

		}
	}

	//
	// Extract node graphics information and display it.
	//
	protected void layoutNodeGraphics(GraphView myView, Graphics graphics,
			NodeView nodeView) {

		// Location and size of the node
		double x, y, h, w;

		x = graphics.getX();
		y = graphics.getY();
		h = graphics.getH();
		w = graphics.getW();

		nodeView.setXPosition(x);
		nodeView.setYPosition(y);
		// nodeView.setHeight(h * (rdn.nextInt(4)+1) * rdn.nextGaussian());
		// nodeView.setWidth(w * (rdn.nextInt(4)+1) * rdn.nextGaussian());

		nodeView.setHeight(h);
		nodeView.setWidth(w);

		// Set color
		String nodeColor = graphics.getFill();
		nodeView.setUnselectedPaint(getColor(nodeColor));

		// Set border line
		nodeView.setBorderPaint(getColor(graphics.getOutline()));
		if (graphics.getWidth() != null) {
			nodeView.setBorderWidth(graphics.getWidth().floatValue());
			// nodeView.setBorderWidth(rdn.nextInt(10));
		}

		String type = graphics.getType();

		if (type != null) {

			if (type.equals(ELLIPSE)) {
				nodeView.setShape(NodeView.ELLIPSE);
			} else if (type.equals(RECTANGLE)) {
				nodeView.setShape(NodeView.RECTANGLE);
			} else if (type.equals(DIAMOND)) {
				nodeView.setShape(NodeView.DIAMOND);
			} else if (type.equals(HEXAGON)) {
				nodeView.setShape(NodeView.HEXAGON);
			} else if (type.equals(OCTAGON)) {
				nodeView.setShape(NodeView.OCTAGON);
			} else if (type.equals(PARALELLOGRAM)) {
				nodeView.setShape(NodeView.PARALELLOGRAM);
			} else if (type.equals(TRIANGLE)) {
				nodeView.setShape(NodeView.TRIANGLE);
			}

		}
		// int rnd = rdn.nextInt(7);
		// if (rnd == 0) {
		// nodeView.setShape(NodeView.ELLIPSE);
		// } else if (rnd == 1) {
		// nodeView.setShape(NodeView.RECTANGLE);
		// } else if (rnd == 2) {
		// nodeView.setShape(NodeView.DIAMOND);
		// } else if (rnd == 3) {
		// nodeView.setShape(NodeView.HEXAGON);
		// } else if (rnd == 4) {
		// nodeView.setShape(NodeView.OCTAGON);
		// } else if (rnd == 5) {
		// nodeView.setShape(NodeView.PARALELLOGRAM);
		// } else if (rnd == 6) {
		// nodeView.setShape(NodeView.TRIANGLE);
		// }

		// This object includes non-GML graphics property.

		if (graphics.getAtt().size() != 0) {
			Att localGraphics = (Att) graphics.getAtt().get(0);
			Iterator it = localGraphics.getContent().iterator();
			Random trans = new Random();

			// Extract edge graphics attributes one by one.
			while (it.hasNext()) {
				Object obj = it.next();

				if (obj.getClass() == AttImpl.class) {
					AttImpl nodeGraphics = (AttImpl) obj;
					String attName = nodeGraphics.getName();
					String value = nodeGraphics.getValue();

					if (attName.equals("nodeTransparency")) {
						// nodeView.setTransparency(trans.nextFloat());
					} else if (attName.equals("borderLineType")) {

					}
				}
			}
		}

	}

	protected void layoutEdge(GraphView myView) {

		EdgeView view = null;

		// Extract an edge from JAXB-generated object
		for (Iterator it = edges.iterator(); it.hasNext();) {
			cytoscape.generated2.Edge curEdge = (cytoscape.generated2.Edge) it
					.next();

			Graphics graphics = (Graphics) curEdge.getGraphics();
			String edgeID = curEdge.getId();
			
			edgeGraphicsMap.put(edgeID, graphics);
			
			// System.out.println("Edge info@@@: " + edgeID);
			int rootindex = Cytoscape.getRootGraph().getEdge(edgeID)
					.getRootGraphIndex();
			view = myView.getEdgeView(rootindex);

			if (graphics != null && view != null) {
				layoutEdgeGraphics(myView, graphics, view);
			} else if (graphics == null) {
				// System.out.println("Null Graphics!!");
			}
		}

	}

	protected void layoutEdgeGraphics(GraphView myView, Graphics graphics,
			EdgeView edgeView) {

		edgeView.setStrokeWidth(((Number) graphics.getWidth()).floatValue());
		edgeView.setUnselectedPaint(getColor((String) graphics.getFill()));

		if (graphics.getAtt().size() != 0) {
			// This object includes non-GML graphics property.
			Att localGraphics = (Att) graphics.getAtt().get(0);

			Iterator it = localGraphics.getContent().iterator();

			// Extract edge graphics attributes one by one.
			while (it.hasNext()) {
				Object obj = it.next();

				if (obj.getClass() == AttImpl.class) {
					AttImpl edgeGraphics = (AttImpl) obj;
					String attName = edgeGraphics.getName();
					String value = edgeGraphics.getValue();

					if (attName.equals("sourceArrow")) {
						edgeView.setSourceEdgeEnd(Integer.parseInt(value));
					} else if (attName.equals("targetArrow")) {
						edgeView.setTargetEdgeEnd(Integer.parseInt(value));
					} else if (attName.equals("sourceArrowColor")) {
						edgeView.setSourceEdgeEndPaint(getColor(value));
					} else if (attName.equals("targetArrowColor")) {
						edgeView.setTargetEdgeEndPaint(getColor(value));
					} else if(attName.equals("edgeLineType")) {
						edgeView.setStroke(LineType.parseLineTypeText(value).getStroke());
					}
				}
			}
		}

		// edgeView.setSourceEdgeEnd(((Number) graphics.get)
		// .intValue());
	}

	/**
	 * Part of interface contract
	 */
	public int[] getNodeIndicesArray() {
		giny_nodes.trimToSize();
		return giny_nodes.elements();
	}

	/**
	 * Part of interace contract
	 */
	public int[] getEdgeIndicesArray() {
		giny_edges.trimToSize();
		return giny_edges.elements();
	}

	public String getNetworkID() {
		return networkName;
	}

	public String getNetworkName() {
		return networkName;
	}

	public RdfRDF getNetworkMetadata() {
		return metadata;
	}

	public Color getColor(String colorString) {
		return new Color(Integer.parseInt(colorString.substring(1), 16));
		// return new Color(rdn.nextInt(255),rdn.nextInt(255),rdn.nextInt(255),
		// rdn.nextInt(255));
	}

	public Color getBackgroundColor() {
		return getColor(backgroundColor);
	}

	public void read(boolean canonicalizeNodeNames) throws IOException {
		// TODO Auto-generated method stub

	}

	public RootGraph getRootGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	public CyAttributes getNodeAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public CyAttributes getEdgeAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	private void readAttributes(String targetName, List attrList, String type) {

		CyAttributes attributes = null;

		if (type.equals(EDGE)) {
			attributes = Cytoscape.getEdgeAttributes();
		} else {
			attributes = Cytoscape.getNodeAttributes();
		}
		Iterator it = attrList.iterator();

		while (it.hasNext()) {
			Object curAtt = it.next();

			String dataType = ((Att) curAtt).getLabel();
			if (dataType != null) {

				if (dataType.equals(STRING_TYPE)
						&& ((Att) curAtt).getValue() != null) {
					attributes.setAttribute(targetName, ((Att) curAtt)
							.getName(), ((Att) curAtt).getValue());
				} else if (dataType.equals(INT_TYPE)
						&& ((Att) curAtt).getValue() != null) {
					attributes.setAttribute(targetName, ((Att) curAtt)
							.getName(), new Integer(((Att) curAtt).getValue()));
				} else if (dataType.equals(FLOAT_TYPE)
						&& ((Att) curAtt).getValue() != null) {
					attributes.setAttribute(targetName, ((Att) curAtt)
							.getName(), new Double(((Att) curAtt).getValue()));
				} else if (dataType.equals(BOOLEAN_TYPE)
						&& ((Att) curAtt).getValue() != null) {
					attributes.setAttribute(targetName, ((Att) curAtt)
							.getName(), new Boolean(((Att) curAtt).getValue()));
				} else if (dataType.equals(LIST_TYPE)) {
					ArrayList listAttr = new ArrayList();
					Iterator listIt = ((Att) curAtt).getContent().iterator();

					while (listIt.hasNext()) {
						Object listItem = listIt.next();
						if (listItem != null
								&& listItem.getClass() == AttImpl.class) {
							listAttr.add(((AttImpl) listItem).getValue());
						}
					}
					attributes.setAttributeList(targetName, ((Att) curAtt)
							.getName(), listAttr);
				}
			}

		}
	}

}
