package cytoscape.data.readers;
import java.util.*;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;
import java.text.DecimalFormat;
import giny.view.*;
import giny.model.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.view.CyNetworkView;
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
   * When getting a vector, used to specify the type
   * for the contained objects
   */
  public static int GMLTREE = 3;
  /**
   * Create an empty GMLTree
   */
  public GMLTree(){
    root = new GMLNode();
  }
  public GMLTree(GMLNode init){
    root = init;
  }
	
  /**
   * Create a GMLTree from the information contained in this GraphView. Currently
   * this only concerns itself with x,y position information.
   * @param myView the GraphView used to create the GMLTree
   */
  public GMLTree( CyNetworkView networkView){
    //DecimalFormat cf = new DecimalFormat("00");
    DecimalFormat df = new DecimalFormat("####0.0#");
    GraphView myView = networkView.getView();
    GraphObjAttributes nodeAttributes = networkView.getNetwork().getNodeAttributes();
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
    // Currently supported GML fields for "node"
    // "id"      node ID (integer from getRootGraphIndex())
    // "label"   node label
    // "graphics" specify the following
    //      "x", "y"  node x,y locations
    //      "h", "w"  node height/width
    //      "fill"    node fill color
    //      "type"    node shape {"rectangle", "oval"}
    // 
    while(viewIt.hasNext()){
      NodeView currentView = (NodeView)viewIt.next();
      Node currentNode = currentView.getNode();
      //create a new GMLNode to hold information about currentNode
      GMLNode currentGML = new GMLNode();
      //add the information about currentNode
      currentGML.addMapping("id",new GMLNode(""+(-currentNode.getRootGraphIndex())));
      String name = nodeAttributes.getCanonicalName(currentView.getNode());
      if (name == null) {
	name = "unknown";
      } // end of if ()
	    
      String label = "\""+name+"\"";
      //brackets are reserved characters, let's replace them with parens
      label.replaceAll("\\"+NODE_CLOSE, ")");
      label.replaceAll("\\"+NODE_OPEN, "(");
      currentGML.addMapping("label",new GMLNode(label));
      GMLNode graphics = new GMLNode();
      graphics.addMapping("x",new GMLNode(""+df.format(currentView.getXPosition())));
      graphics.addMapping("y",new GMLNode(""+df.format(currentView.getYPosition())));
      graphics.addMapping("h",new GMLNode(""+df.format(currentView.getHeight())));
      graphics.addMapping("w",new GMLNode(""+df.format(currentView.getWidth())));
      Color nodeColor = (Color) currentView.getUnselectedPaint();
      graphics.addMapping("fill", new GMLNode("\""+getColorHexString(nodeColor)+"\""));
      switch(currentView.getShape()) {
      case NodeView.RECTANGLE:
	graphics.addMapping("type",new GMLNode("\"rectangle\"")); break;
      case NodeView.ELLIPSE:
	graphics.addMapping("type",new GMLNode("\"oval\"")); break;
      }
      currentGML.addMapping("graphics",graphics);
      graph.addMapping("node",currentGML);
    }
    // Currently supported GML fields for "edge"
    // "source"   node ID from getRootGraphIndex()
    // "target"   node ID from getRootGraphIndex()
    // "label"    edge label from edgeAttributes.getCanonicalName
    // "graphics"
    //      "width"   edge line width
    //      "type"    currently only "line"
    //      "fill"    edge color

    viewIt = myView.getEdgeViewsIterator();
    GraphObjAttributes edgeAttributes = networkView.getNetwork().getEdgeAttributes();
    while(viewIt.hasNext()){
      EdgeView currentView = (EdgeView)viewIt.next();
      Edge currentEdge = currentView.getEdge();
      //crate a new GMLNode to hold information about currentEdge
      GMLNode currentGML = new GMLNode();
      //add the information about currentEdge
      currentGML.addMapping("source",new GMLNode(""+(-currentEdge.getSource().getRootGraphIndex())));
      currentGML.addMapping("target",new GMLNode(""+(-currentEdge.getTarget().getRootGraphIndex())));
	    
      //label is a little bit different because I need to look it up from edgeattributes
      String interaction = (String)edgeAttributes.get("interaction",edgeAttributes.getCanonicalName(currentEdge)); 
      if(interaction != null){
	currentGML.addMapping("label",new GMLNode("\""+interaction+"\""));
      }
      GMLNode graphics = new GMLNode();
      graphics.addMapping("width",new GMLNode(""+df.format(currentView.getStrokeWidth())));

      switch(currentView.getLineType()) {
      case EdgeView.STRAIGHT_LINES: {
	  graphics.addMapping("type",new GMLNode("\"line\""));
	  Point2D [] pointsArray = currentView.getBend().getDrawPoints();
	  if(pointsArray.length > 2) {
	     GMLNode Line = new GMLNode();
             for(int i=0; i<pointsArray.length; i++) {
		 GMLNode point = new GMLNode();
		 point.addMapping("x", new GMLNode(""+df.format(pointsArray[i].getX())));
		 point.addMapping("y", new GMLNode(""+df.format(pointsArray[i].getY())));
		 Line.addMapping("point", point);
	     }
	     graphics.addMapping("Line", Line);	   
	  }
      } break;
      //case EdgeView.CURVED_LINES: { // not implemented
      //      graphics.addMapping("type",new GMLNode("\"polygon\"")); // type "polygon" for splines
      //      GMLNode curvedLine = new GMLNode();
      //} break;
      }

      Color edgeColor = (Color) currentView.getUnselectedPaint();
      graphics.addMapping("fill",new GMLNode("\""+getColorHexString(edgeColor)+"\""));
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

    // handle the quotes -> build GMLToken list
    //find the quoted strings first so we can preserve their white space
    StringTokenizer quotes = new StringTokenizer(reader.getText(),"\"",true);

    while (quotes.hasMoreTokens()) {
      StringTokenizer tokens = new StringTokenizer(quotes.nextToken());
      while (tokens.hasMoreTokens()){
	tokenList.add(tokens.nextToken());
      }
      if(quotes.hasMoreTokens()){
	//clear the quote, this token must be "\""
	String nextToken = quotes.nextToken();
	//check to see if it is the empty string
	if(quotes.hasMoreTokens()){
	  nextToken = quotes.nextToken();
	  //check for empty string
	  if(!nextToken.equals("\"")){
	    //this was the quoted empty string
	    tokenList.add("\""+nextToken+"\"");
	    //now look for and clear the close quote
	    if(quotes.hasMoreTokens()){
	      //since the last token was not a quote character
	      //this next token has to be the quote character,
	      //and we can clear it
	      quotes.nextToken();
	    }
	    else{
	      throw new RuntimeException("Open quote with no end quote");
	    }
	  }
	  else{
	    //add a token for the quoted empty string
	    tokenList.add("\"\"");
	  }
	}
	else{
	  throw new RuntimeException("GMLFile ended with open quote");
	}
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
    String lineSep = System.getProperty("line.separator");
    String result =  root.toString();
    return result.substring(3,result.length()-2) + lineSep;
  }

  /**
   * Get the String representation of the 6 character hexidecimal RGB values
   * i.e. #ff000a
   * @param Color The color to be converted
   */
  public String getColorHexString(Color c) {
    return ("#"
	    +Integer.toHexString(256+c.getRed()).substring(1)
	    +Integer.toHexString(256+c.getGreen()).substring(1)
	    +Integer.toHexString(256+c.getBlue()).substring(1)
	    );
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
      //System.out.println("Level " + index + " " + (String)keys.get(index) + " has " + mapped.size());
      if(index >= keys.size()-1){
	while(it.hasNext()){
	  GMLNode current = (GMLNode)it.next();
	  //if(current.terminal){
	    if(type == STRING){
	      result.add(current.stringValue());
	    }
	    else if(type == INTEGER){
	      result.add(current.integerValue());
	    }
	    else if(type == DOUBLE){
	      result.add(current.doubleValue());
	    }
	    else if(type == GMLTREE){
		//System.out.println(current.toString());
		result.add(new GMLTree(current));
	    }
	    else{
	      throw new IllegalArgumentException("bad type");
	    }
	  //}
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
    } //may need to handle missing/empty values
    //else {
    //System.out.println("ADDING NULL AT LEVEL " + index + " " + (String)keys.get(index));
    //result.add(null);
    //}
  }

}
