package cytoscape.data.readers;

import java.util.HashMap;
import java.util.Map;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;


public class NNFParser {
	private static final String INTERACTION = "interaction";
	private static final String NAME = "name";

	private static final String ROOT = "-";

	// For performance, these fields will be reused.
	private String[] parts;
	private int length;

	private String moduleName;
	private String sourceName;

	private Map<String, CyNode> nodeMap;
	private Map<String, CyEdge> edgeMap;

//	private CyMetaNode metaNode;
	private CyNode sourceNode;
	private CyNode targetNode;
	private CyEdge edge;
	
	// Parent network of all graph objects in the file
	private CyNetwork rootNetwork;

	public NNFParser() {
		nodeMap = new HashMap<String, CyNode>();
		edgeMap = new HashMap<String, CyEdge>();
	}

	/**
	 * Parse an entry in NNF file.
	 * 
	 * @param rootNetwork
	 * @param line
	 */
	public void parse(String line) {
		System.out.println("\n\nCurrent Line: " + line);
		
		// Split with white space chars
		parts = line.split("\\s");
		length = parts.length;
//
//		if (length == 2) {
//			// This is a line with no edge.
//			metaNode = processModule(parts[0], rootNetwork);
//			sourceNode = processNode(parts[1], rootNetwork, null);
//			if (metaNode != null && sourceNode != null)
//				metaNode.getSubNetwork().addNode(sourceNode);
//
//		} else if (length == 4) {
//			// Line with an edge
//			metaNode = processModule(parts[0], rootNetwork);
//			processEdge(metaNode, parts[1], parts[2], parts[3], rootNetwork);
//		} else {
//			// TODO: Other length is invalid.  Handle error here.
//		}
	}

//	private CyMetaNode processModule(String entry) {
//		CyMetaNode module = null;
//		if ((moduleName = entry.trim()) != null
//				&& moduleName.equals(ROOT) == false
//				&& nodeMap.containsKey(moduleName) == false) {
//			module = rootNetwork.addMetaNode();
//			module.attrs().set(NAME, moduleName);
//			nodeMap.put(moduleName, module);
//		} else if (nodeMap.containsKey(moduleName)) {
//			System.out.println("\tModule already exist: " + moduleName);
//			// This metanode already exists.  But not sure actually metanode or regular node
////			if (nodeMap.get(moduleName) instanceof CyMetaNode && ((CyMetaNode)nodeMap.get(moduleName)).getSubNetwork() != null){
//			if(rootNetwork.getMetaNodeList().contains(nodeMap.get(moduleName))) {
//				module = (CyMetaNode) nodeMap.get(moduleName);
//			} else {
//				System.out.println("\tConverting: " + moduleName + ", metanode count = " + rootNetwork.getMetaNodeList());
//				
//				module = rootNetwork.convert(nodeMap.get(moduleName));
//				nodeMap.put(moduleName, module);
//				System.out.println("\tConverting: " + moduleName + ", metanode count = " + rootNetwork.getMetaNodeList());
//			}
//		}
//		if(module != null)
//			System.out.println("\tGot Module: " + module.attrs().get(NAME, String.class));
//		return module;
//	}
//
//	private void processEdge(CyMetaNode parent, String source, String edgeType,
//			String target) {
//		// Create source and target
//
//		if (parent == null) {
//			sourceNode = processNode(source, rootNetwork, null);
//			targetNode = processNode(target, rootNetwork, null);
//			edge = rootNetwork.addEdge(sourceNode, targetNode, true);
//		} else {
//			sourceNode = processNode(source, rootNetwork, parent.getSubNetwork());
//			targetNode = processNode(target, rootNetwork, parent.getSubNetwork());
//			edge = parent.getSubNetwork().addEdge(sourceNode, targetNode, true);
//		}
//
//		edge.attrs().set(INTERACTION, edgeType.trim());
//		edgeMap.put(
//				source.trim() + "(" + edgeType.trim() + ")" + target.trim(),
//				edge);
//
//	}
//
//	private CyNode processNode(String nodeName, CyRootNetwork network, CySubNetwork parent) {
//		CyNode node = null;
//		if ((sourceName = nodeName.trim()) != null
//				&& nodeMap.containsKey(sourceName) == false) {
//			
//			System.out.println("\tNode does not exist yet.  Creating: " + sourceName);
//			node = network.addNode();
//			if(parent != null)
//				parent.addNode(node);
//			
//			node.attrs().set(NAME, sourceName);
//			nodeMap.put(sourceName, node);
//		
//		} else {
//			node = nodeMap.get(sourceName);
//			 
//			if (parent != null) {
//				parent.addNode(node);
//				
//				if(node instanceof CyMetaNode && ((CyMetaNode) node).getSubNetwork() != null) {
//					System.out.println("\tGot Meta Node: " +  node.attrs().get(NAME, String.class) + ", Type = " + node.getClass());
//					
//					for(CyNode n: ((CyMetaNode) node).getSubNetwork().getNodeList()) {
//						parent.addNode(n);
//					}
//				}
//				
//			}
//			
//		}
//		
//		return node;
//	}
//	
//	private void recursiveAdd() {
//		
//	}

}
