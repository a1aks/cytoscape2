package cytoscape.data.readers;
import java.util.*;

/**
 * This class represents a GMLNode. A GMLNode can be either a 
 * GML terminal, or contain a mapping from string keys to other
 * GMLNodes
 **/
public class GMLNode{
	/**
	 * True if this node is a terminal. (A terminal node is one 
	 * that just contains a piece of data (string,double,int) and
	 * not further mapping to GMLNodes
	 **/
	public boolean terminal;
	/**
	 * Remeber whether this data was initially quoted
	 */
	private boolean quotes = false;
	
	/**
	 * If this node is a terminal, this will contain
	 * the associated data in string form
	 */
	private String string_value;
	
	/**
	 * The string used in indenting the display
	 */
	private static String TAB = "\t";
	/**
	 * This will only be defined if the node is not a
	 * terminal. It contains a mapping from Strings to a vector
	 * of GMLNodes
	 */
	private HashMap key2GMLNodeVec;

	/**
	 * This constructor create a terminal GMLNode.
	 * @param string_value The terminal value of the GMLNode
	 */
	public GMLNode(String string_value){
		if(string_value.startsWith("\"")&&string_value.endsWith("\"")){
			quotes = true;
			string_value = string_value.substring(1,string_value.length()-1);
		}
		this.string_value = string_value;
		terminal = true;
	}

	/**
	 * This constructor creates a non-terminal GMLNode.
	 * The mapping is initially empty. Mappings are added
	 * using addMapping
	 */
	public GMLNode(){
		terminal = false;
		key2GMLNodeVec = new HashMap();
	}

	/**
	 * Return the string representation of this GMLNode. This will
	 * recursively print out all mapped subGMLNodes if present.
	 */
	public String toString(){
		return toString("");
	}
	
	/**
	 * Private recursive helper method to print out the contents of the
	 * node
	 * @param indent The indent to use when printing out this node
	 */
	private String toString(String indent){
		String result = "";
		if(terminal){
			if(!quotes){
				return TAB+string_value;
			}else{
				return TAB+"\""+string_value+"\"";
			}
		}
		else{
			result += "\n"+indent+"[\n";
			Iterator it = key2GMLNodeVec.keySet().iterator();
			while(it.hasNext()){
				String key = (String)it.next();
				Iterator mapIt = ((Vector)key2GMLNodeVec.get(key)).iterator();
				while(mapIt.hasNext()){
					GMLNode next = (GMLNode)mapIt.next();
					result += (indent+key+next.toString(indent+TAB)+"\n");	
				}
			}
			result += (indent+"]");
		}
		return result;
	}
	
	/**
	 * Get the value of this node as a Double
	 * @return The value of this node as a double
	 * @throws RuntimeException if the value of this node cannot be interpreted as a Double
	 */
	public Double doubleValue(){
		return new Double(string_value);
	}

	/**
	 * Get the value of this node as an Integer
	 * @return The value of this node as an Integer
	 * @throws RuntimeException if the value of this node cannot be interpreted as an Integer
	 */
	public Integer integerValue(){
		return new Integer(string_value);
	}

	/**
	 * Get the value of this node as a String
	 * @return The value of this node as a String
	 */
	public String stringValue(){
		return string_value;
	}

	/**
	 * Add a mapping from key to node
	 * @param key The key for the mapping
	 * @param node The node which will map to the key
	 */
	public void addMapping(String key, GMLNode node){
		Vector values;
		values = (Vector)key2GMLNodeVec.get(key);
		if(values == null){
			values = new Vector();
			key2GMLNodeVec.put(key,values);
		}
		values.add(node);
	}

	/**
	 * Return the data mapped by this key
	 * @param key The key to look up
	 * @return A vector of GMLNodes (may be null)
	 */
	public Vector getMapping(String key){
		return (Vector)key2GMLNodeVec.get(key);
	}
		
}
