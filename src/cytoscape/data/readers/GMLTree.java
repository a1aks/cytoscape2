package cytoscape.data.readers;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;
import java.text.DecimalFormat;
import giny.view.*;
import giny.model.*;
/**
 * This class wraps around GMLNode and provides various methods for
 * constructing a tree structure given other data.
 */
public class GMLTree{
	/**
	 * The root node for this graph
	 */
	GMLNode root;
	/**
	 * The version of the GMLSpec parsed here
	 */
	private static String VERSION = "1.0";
	/**
	 * The string used to open a GMLNode declaration
	 */
	private static String NODE_OPEN = "[";
	/**
	 * The string used to close a GMLNode declaration
	 */
	private static String NODE_CLOSE = "]";
	
	/**
	 * When getting a vector, used to specify the type
	 * for the contained objects
	 */
	public static int STRING = 0;
	/**
	 * When getting a vector, used to specify the type
	 * for the contained objects
	 */
	public static int DOUBLE = 1;
	/**
	 * When getting a vector, used to specify the type
	 * for the contained objects
	 */
	public static int INTEGER = 2;
	/**
	 * Create an empty GMLTree
	 */
	public GMLTree(){
		root = new GMLNode();
	}
	
	/**
	 * Create a GMLTree from the information contained in this GraphView. Currently
	 * this only concerns itself with x,y position information.
	 * @param myView the GraphView used to create the GMLTree
	 */
	public GMLTree(GraphView myView){
	    //DecimalFormat cf = new DecimalFormat("00");
	    DecimalFormat df = new DecimalFormat("####0.0#");
		//create a new root
		root = new GMLNode();
		//add the base level mappings
		root.addMapping("Creator",new GMLNode("\"Cytoscape\""));
		root.addMapping("Version",new GMLNode(VERSION));
		//create hte subnode which will hold the grpah information
		GMLNode graph = new GMLNode();
		root.addMapping("graph",graph);
		//for each node, add a mapping to the graph GMLNode
		Iterator viewIt = myView.getNodeViewsIterator();
		while(viewIt.hasNext()){
			NodeView currentView = (NodeView)viewIt.next();
			Node currentNode = currentView.getNode();
			//create a new GMLNode to hold information about currentNode
			GMLNode currentGML = new GMLNode();
			//add the information about currentNode
			currentGML.addMapping("id",new GMLNode(""+(-currentNode.getRootGraphIndex())));
			currentGML.addMapping("label",new GMLNode("\""+currentView.getLabel()+"\""));
			GMLNode graphics = new GMLNode();
			graphics.addMapping("x",new GMLNode(""+df.format(currentView.getXPosition())));
			graphics.addMapping("y",new GMLNode(""+df.format(currentView.getYPosition())));
			graphics.addMapping("h",new GMLNode(""+df.format(currentView.getHeight())));
			graphics.addMapping("w",new GMLNode(""+df.format(currentView.getWidth())));
 			Color nodeColor = (Color) currentView.getUnselectedPaint();
 			GMLNode nC = new GMLNode("\"#"
						 +Integer.toHexString(256+nodeColor.getRed()).substring(1)
 						 +Integer.toHexString(256+nodeColor.getGreen()).substring(1)
 						 +Integer.toHexString(256+nodeColor.getBlue()).substring(1)
						 +"\"");
 			graphics.addMapping("fill", nC);
			switch(currentView.getShape()) {
			case NodeView.RECTANGLE:
			    graphics.addMapping("type",new GMLNode("\"rectangle\"")); break;
			case NodeView.ELLIPSE:
			    graphics.addMapping("type",new GMLNode("\"oval\"")); break;
			}
			currentGML.addMapping("graphics",graphics);
			graph.addMapping("node",currentGML);
		}
		viewIt = myView.getEdgeViewsIterator();
		while(viewIt.hasNext()){
			EdgeView currentView = (EdgeView)viewIt.next();
			Edge currentEdge = currentView.getEdge();
			//crate a new GMLNode to hold information about currentEdge
			GMLNode currentGML = new GMLNode();
			//add the information about currentNode
			currentGML.addMapping("source",new GMLNode(""+(-currentEdge.getSource().getRootGraphIndex())));
			currentGML.addMapping("target",new GMLNode(""+(-currentEdge.getTarget().getRootGraphIndex())));
			GMLNode graphics = new GMLNode();
			graphics.addMapping("width",new GMLNode("1"));
			graphics.addMapping("type",new GMLNode("\"line\""));
			graphics.addMapping("fill",new GMLNode("\"#000000\""));
			currentGML.addMapping("graphics",graphics);
			graph.addMapping("edge",currentGML);
		}
	}

	/**
	 * Create a GMLTree from data contained in a file
	 * @param filename The name of the file used to create this GMLTree
	 */
	public GMLTree(String filename){
		LinkedList tokenList = new LinkedList();
		TextFileReader reader = new TextFileReader(filename);
		reader.read();

		// handle the lines -> build GMLToken list
		StringTokenizer lines = new StringTokenizer(reader.getText(), "\n");

		while (lines.hasMoreTokens()) {
	    		StringTokenizer tokens = new StringTokenizer(lines.nextToken());
	    		while (tokens.hasMoreTokens()){
				tokenList.add(tokens.nextToken());
			}
    		}

		root = initializeTree(tokenList);
		
	}

	/**
	 * Static helper method to build a tree from a list of tokens. Maybe I should change
	 * the GraphView constructor so that it can use this function.
	 */
	private static GMLNode initializeTree(List tokens){
		GMLNode result = new GMLNode();
		while(tokens.size()>0){
			String current = (String)tokens.remove(0);
			//expecting this be a key or a close node symbol
			if(current.equals(NODE_OPEN)){
				throw new RuntimeException("Error parsing GML file");
			}
			if(current.equals(NODE_CLOSE)){
				return result;	
			}
			//now find the thing we are trying to map
			String key = current;
			if(tokens.size() == 0){
				throw new RuntimeException("Error parsing GML file");
			}
			current = (String)tokens.remove(0);
			if(current.equals(NODE_OPEN)){
				result.addMapping(key,initializeTree(tokens));
			}
			else if(current.equals(NODE_CLOSE)){
				throw new RuntimeException("Error parsing GML file");
			}
			else{
				result.addMapping(key, new GMLNode(current));
			}
		}
		return result;
	}

	/**
	 * Get string representation
	 * @return string representation
	 */
	public String toString(){
		//this function basically just calls toString on the root
		String result =  root.toString();
		return result.substring(3,result.length()-2)+"\n";
	}

	/**
	 * Return a vector of information stored in gmlNodes
	 * @param keys A vector of strings representing a sequence of keys down the tree
	 * @param type The type of vector to return. See public static values for specifying type
	 * @return A vector. The type of this vector is determined by type
	 */
	private Vector getVector(Vector keys,int type){
		Vector result = new Vector();
		GMLTree.getVector(root,keys,0,type,result);
		return result;
	}

	/**
	 * Return a vector of information stored in gmlNodes
	 * @param keys A string representing a delimited sequence of keys which are used to look up values in the tree.
	 * @param delim A string representing the delimiter used in keys
	 * @param type The type of vector to return. See public static values for specifying type
	 * @return A vector. The type of this vector is determined by type
	 */
	public Vector getVector(String keys,String delim,int type){
		Vector keyVector = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(keys, delim);
		while(tokenizer.hasMoreTokens()){
			keyVector.add(tokenizer.nextToken());
		}
		return getVector(keyVector,type);
	
	}

	/**
	 * A recursive private static helper method to get a vector of values
	 * @param root The current GMLFile for which we are getting values
	 * @param keys A vector of strings representing a sequence of keys down the tree
	 * @param index The current position in the key vector (to find hte current key we need to look up)
	 * @param result The vector to which we add result data.
	 */
	private static void getVector(GMLNode root, Vector keys, int index, int type, Vector result){
		Vector mapped = root.getMapping((String)keys.get(index));
		if(mapped != null){
			Iterator it = mapped.iterator();
			if(index >= keys.size()-1){
				while(it.hasNext()){
					GMLNode current = (GMLNode)it.next();
					if(current.terminal){
						if(type == STRING){
							result.add(current.stringValue());
						}
						else if(type == INTEGER){
							result.add(current.integerValue());
						}
						else if(type == DOUBLE){
							result.add(current.doubleValue());
						}
						else{
							throw new IllegalArgumentException("bad type");
						}
					}
				}
			}
			else{
				while(it.hasNext()){
					GMLNode current = (GMLNode)it.next();
					if(!current.terminal){
						GMLTree.getVector(current,keys,index+1,type,result);
					}
				}
			}
		}
	}
}
