
/*
  File: CyAttributes.java 
  
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

package cytoscape.data;

import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;

import java.util.List;
import java.util.Map;

/**
 * CyAttributes provides access to node and edge attributes within Cytoscape.
 * <P>
 * CyAttributes is a replacement for {@link GraphObjAttributes}, which will be
 * officially removed from the Cytoscape core in September, 2006.
 * <h3>Basic Concepts:</h3>
 * Each node and edge within Cytoscape can be annotated with one or more
 * attributes.  For example, a node representing a protein could have
 * attributes for description, species, NCBI Gene ID, UniProt ID, etc.
 * These attributes are set and retrieved via the CyAttributes interface.
 * <h3>Global Attributes:</h3>
 * There are two sets of global attributes, one for nodes, and one for edges.
 * To access these global attributes, use:
 * <UL>
 * <LI>{@link cytoscape.Cytoscape#getNodeAttributes()}
 * <LI>{@link cytoscape.Cytoscape#getEdgeAttributes()}
 * </UL>
 * <h3>Unique Identifiers:</h3>
 * CyAttributes uses unique identifiers to attach attributes to specific
 * nodes and edges.  The unique identifiers for nodes and edges are available
 * via the <CODE>getIdentifier()</CODE> method:
 * <UL>
 * <LI>For nodes, use {@link cytoscape.CyNode#getIdentifier()}.
 * <LI>For edges, use {@link cytoscape.CyEdge#getIdentifier()}.
 * </UL>
 * <h3>Data Type Restrictions:</h3>
 * CyAttributes uses a {@link cytoscape.data.attr.MultiHashMap} data structure,
 * which restricts attribute values to four data types:  <CODE>Boolean</CODE>,
 * <CODE>Integer</CODE>, <CODE>Double</CODE>, and <CODE>String</CODE>.  It does
 * not store arbitrary Java Objects.  We do this for three reasons:
 * <UL>
 * <LI>We want to provide complete import/export of all attribute values,
 * without having to rely on Java serialization.
 * <LI>We want to enable inter-plugin communication, such that plugins can
 * share attribute data without having to agree to use some predefined or
 * custom data structure ahead of time.
 * <LI>We want to build a universal node/edge attribute browser.
 * </UL>
 * <h3>Getting / Setting Attributes:  An Overview</h3>
 * There are three ways to get/set attributes.  They are (in order
 * of increasing complexity):
 * <UL>
 * <LI>Getting / setting individual values.</LI>
 * <LI>Getting / setting 'simple' lists and 'simple' maps.</LI>
 * <LI>Getting / setting arbitrarily complex data structures.</LI>
 * </UL>
 * Each of these approaches is detailed below.
 * <h3>Getting / Setting Individual Values:</h3>
 * This is the simplest option.  Attributes are restricted to the following four
 * types:
 * <UL>
 * <LI><CODE>Boolean</CODE>, <CODE>Integer</CODE>, <CODE>Double</CODE>,
 * <CODE>String</CODE>
 * </UL>
 * There are getters and setters for each of these four types.  For example,
 * the following code snippet sets and gets an Integer attribute value:
 * <PRE>
 * cyAttributes.setAttribute (node.getIdentifier(),
 *     "Rank", new Integer(3));
 * Integer value = cyAttributes.getIntegerAttribute
 *     (node.getIdentifier(), "Rank");
 * </PRE>
 * <h3>Getting / Setting Simple Lists:</h3>
 * A 'simple' list is defined as follows:
 * <UL>
 * <LI>All items within the list are of the same type, and are chosen
 * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
 * <CODE>Double</CODE> or <CODE>String</CODE>.
 * </UL>
 * In other words, a list of three <CODE>Integer</CODE> Objects is a simple
 * list, whereas a mix of two <CODE>Integer</CODE> and one <CODE>Boolean</CODE>
 * Object is not.
 * <P>
 * The following code snippet illustrates how to set a simple list of three
 * <CODE>Integer</CODE> Objects:
 * <PRE>
 * ArrayList list = new ArrayList();
 * list.add (new Integer(1));
 * list.add (new Integer(22));
 * list.add (new Integer(5));
 * cyAttributes.setList (node.getIdentifier(),
 *     "Rank List", list).
 * </PRE>
 * At run-time, the <CODE>setList</CODE> method will check that all items in
 * the list are of the same type, and are chosen from the following list:
 * <CODE>Boolean</CODE>, <CODE>Integer</CODE>, <CODE>Double</CODE>
 * or <CODE>String</CODE>.  If these criteria are not met, an
 * <CODE>IllegalArgumentException</CODE> will be thrown.
 * <P>
 * To get a simple list, use the
 * {@link CyAttributes#getAttributeList(String, String)} method.
 * <h3>Getting / Setting Simple Maps:</h3>
 * A 'simple' map is defined as follows:
 * <UL>
 * <LI>All keys within the map are of type:  <CODE>String</CODE>.
 * <LI>All values within the map are of the same type, and are chosen
 * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
 * <CODE>Double</CODE> or <CODE>String</CODE>.
 * </UL>
 * In other words, a map that contains three <CODE>Integer</CODE> values
 * is a simple map, whereas a map that contains two <CODE>Integer</CODE>
 * values and one <CODE>Boolean</CODE> value is not.
 * <P>
 * The following code snippet illustrates how to set a simple map
 * of three <CODE>String</CODE> Objects:
 * <PRE>
 * Map map = new HashMap();
 * map.put("name", "Reactome");
 * map.put("url", "http://www.reactome.org");
 * map.put("description", "Reactome - a knowledgebase of "
 *     + "biological processes");
 * cyAttributes.setAttributeMap(node.getIdentifier(),
 *     "external_db", map);
 * </PRE>
 * To get a simple map, use the
 * {@link CyAttributes#getAttributeMap(String, String)} method.
 * <h3>Working with Fixed Attribute Types:</h3>
 * Each attribute is bound to a specific data type, and this data type
 * is set and fixed the first time the attribute is used.  For example,
 * in Plugin 1, the following code sets "RANK" to be of type
 * <CODE>Integer</CODE>:
 * <PRE>
 * cyAttributes.setAttribute (node.getIdentifier(), "Rank", new Integer(3));
 * </PRE>
 * Later on, Plugin 2 executes the following code:
 * <PRE>
 * cyAttributes.setAttribute (node.getIdentifier(), "Rank",
 *     new String("Three"));
 * </PRE>
 * We are now attempting to set "RANK" to a String value, but "RANK" is already
 * fixed as an <CODE>Integer</CODE> data type.  Hence, the call is considered
 * invalid, and an <CODE>IllegalArgumentException</CODE> will be thrown.
 * <P>
 * The same situation can occur with simple lists and simple maps.  For example,
 * if PlugIn 1 defines a simple list of all <CODE>Integer</CODE> values, and
 * PlugIn 2 attempts to re-use this attribute with a list of <CODE>Double</CODE>
 * values, the call is considered invalid, and an
 * <CODE>IllegalArgumentException</CODE> will be thrown.
 * <P>
 * To prevent this type of problem, use
 * {@link CyAttributes#getType(String)} to determine the attribute's data
 * type <I>before</I> setting any new attributes.
 * <P>
 * To reset an attribute's data type, use
 * {@link CyAttributes#deleteAttribute(String)}.  Note that calling this method
 * will delete all attributes with this name, preparing the way for a clean
 * slate.  Please use with caution!
 * <h3>Working with Arbitrarily Complex Data Structures:</h3>
 * CyAttributes uses a {@link cytoscape.data.attr.MultiHashMap} data structure
 * to store attributes.  This data structure enables you to store arbitrarily
 * complex trees of data, but restricts the tree to Objects of type:
 * <CODE>Boolean</CODE>, <CODE>Integer</CODE>, <CODE>Double</CODE> or
 * <CODE>String</CODE>.
 * <P>
 * If you wish to store arbitarily complex data structures (e.g. anything
 * more complicated than a simple list or a simple map), you can do so by
 * obtaining the MultiHashMap via {@link CyAttributes#getMultiHashMap()},
 * and {@link CyAttributes#getMultiHashMapDefinition()},
 * and working on the data structure directly.  Complete information is
 * available in the {@link cytoscape.data.attr.MultiHashMap} javadocs.
 * <h3>Listening for Attribute Events:</h3>
 * As noted above, CyAttributes uses a {@link cytoscape.data.attr.MultiHashMap}
 * data structure to store attributes.  To listen to attribute events,
 * first obtain the MultiHashMap via {@link CyAttributes#getMultiHashMap()},
 * and then register a listener via
 * {@link
 * MultiHashMap#addDataListener(cytoscape.data.attr.MultiHashMapListener)}.
 * <P>Note that calls to
 * {@link CyAttributes#setAttributeList(String, String, java.util.List)}
 * and
 * {@link CyAttributes#setAttributeMap(String, String, java.util.Map)}
 * result in many calls to the MultiHashMap object.  For example, if you have
 * five elements in a List, the implementation code makes five calls to
 * the MultiHashMap, and you will therefore be notified of five separate
 * events, rathern than one global list event.
 * <P>
 *
 * @author Cytoscape Development Team
 */
public interface CyAttributes {
    /**
     * This type corresponds to java.lang.Boolean.
     */
    public final byte TYPE_BOOLEAN = MultiHashMapDefinition.TYPE_BOOLEAN;

    /**
     * This type corresponds to java.lang.Double.
     */
    public final byte TYPE_FLOATING =
      MultiHashMapDefinition.TYPE_FLOATING_POINT;

    /**
     * This type corresponds to java.lang.Integer.
     */
    public final byte TYPE_INTEGER = MultiHashMapDefinition.TYPE_INTEGER;

    /**
     * This type corresponds to java.lang.String.
     */
    public final byte TYPE_STRING = MultiHashMapDefinition.TYPE_STRING;

    /**
     * This type corresponds to a 'simple' list.
     * <P>
     * A 'simple' list is defined as follows:
     * <UL>
     * <LI>All items within the list are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     */
    public final byte TYPE_SIMPLE_LIST = -2;

    /**
     * This type corresponds to a 'simple' hash map.
     * <P>
     * A 'simple' map is defined as follows:
     * <UL>
     * <LI>All keys within the map are of type:  <CODE>String</CODE>.
     * <LI>All values within the map are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     */
    public final byte TYPE_SIMPLE_MAP = -3;

    /**
     * This type corresponds to a data structure of arbitrary complexity,
     * e.g. anything more complex than a 'simple' list or a 'simple' map.
     * <P>
     * For complete details, refer to the class comments, or
     * {@link CyAttributes#getMultiHashMap()}.
     */
    public final byte TYPE_COMPLEX = -4;

    /**
     * This type corresponds to an attribute which has not been defined.
     */
    public final byte TYPE_UNDEFINED = -1;

    /**
     * Gets an array of all attribute names.
     *
     * @return an array of String Objects.
     */
    public String[] getAttributeNames();

    /**
     * Determines if the specified id/attributeName pair exists.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return true or false.
     */
    public boolean hasAttribute(String id, String attributeName);

    /**
     * Sets an id/attributeName pair of type Boolean.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @param value         boolean value.
     * @throws IllegalArgumentException Indicates that this attribute has
     *                                  already been defined with a data type,
     *                                  and this data type is not of type:
     *                                  TYPE_BOOLEAN.
     */
    public void setAttribute(String id, String attributeName, Boolean value)
            throws IllegalArgumentException;

    /**
     * Sets an id/attributeName pair of type Integer.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @param value         integer value.
     * @throws IllegalArgumentException Indicates that this attribute has
     *                                  already been defined with a data type,
     *                                  and this data type is not of type:
     *                                  TYPE_INTEGER.
     */
    public void setAttribute(String id, String attributeName, Integer value)
            throws IllegalArgumentException;

    /**
     * Sets an id/attributeName pair of type Double.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @param value         double value.
     * @throws IllegalArgumentException Indicates that this attribute has
     *                                  already been defined with a data type,
     *                                  and this data type is not of type:
     *                                  TYPE_FLOATING.
     */
    public void setAttribute(String id, String attributeName, Double value)
            throws IllegalArgumentException;

    /**
     * Sets an id/attributeName pair of type String.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @param value         string value.
     * @throws IllegalArgumentException Indicates that this attribute has
     *                                  already been defined with a data type,
     *                                  and this data type is not of type:
     *                                  TYPE_STRING.
     */
    public void setAttribute(String id, String attributeName, String value)
            throws IllegalArgumentException;

    /**
     * Gets a Boolean value at the specified id/attributeName.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return Boolean object, or null if no id/attributeName pair is found.
     * @throws ClassCastException Indicates that the specified attribute
     *                            is not of type:  TYPE_BOOLEAN.
     */
    public Boolean getBooleanAttribute(String id, String attributeName)
            throws ClassCastException;

    /**
     * Gets an Integer value at the specified id/attributeName.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return Integer object, or null if no id/attributeName pair is found.
     * @throws ClassCastException Indicates that the specified attribute
     *                            is not of type:  TYPE_INTEGER.
     */
    public Integer getIntegerAttribute(String id, String attributeName)
            throws ClassCastException;

    /**
     * Gets a Double value at the specified id/attributeName.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return Double object, or null if no id/attributeName pair is found..
     * @throws ClassCastException Indicates that the specified attribute
     *                            is not of type:  TYPE_FLOATING.
     */
    public Double getDoubleAttribute(String id, String attributeName)
            throws ClassCastException;

    /**
     * Gets a String value at the specified id/attributeName.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return String object, or null if no id/attributeName pair is found.
     * @throws ClassCastException Indicates that the specified attribute
     *                            is not of type:  TYPE_STRING.
     */
    public String getStringAttribute(String id, String attributeName)
            throws ClassCastException;

    /**
     * Gets the data type of the specified attribute.
     *
     * @param attributeName Attribute Name.
     * @return one of: TYPE_BOOLEAN, TYPE_INTEGER, TYPE_FLOATING,
     *         TYPE_STRING, TYPE_SIMPLE_LIST, TYPE_SIMPLE_MAP, TYPE_COMPLEX,
     *         TYPE_UNDEFINED.
     */
    public byte getType(String attributeName);

//     /**
//      * Sets the data type of a new attribute.
//      *
//      * @param attributeName Attribute Name.
//      * @param type one of: TYPE_BOOLEAN, TYPE_INTEGER, TYPE_FLOATING,
//      *         TYPE_STRING, TYPE_SIMPLE_LIST, TYPE_SIMPLE_MAP, TYPE_COMPLEX,
//      *         TYPE_UNDEFINED.
//      * @exception IllegalStateException if attributeName is already defined
//      *   and is not of the specified type.
//      */
//     public void setType(String attributeName, byte type);

    /**
     * Deletes the id/attributeName pair.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return true indicates attribute was
     *         successfully removed.
     */
    public boolean deleteAttribute(String id, String attributeName);

    /**
     * Deletes the specified attribute.
     * <P>
     * Calling this method deletes all id/attributeName pairs with this
     * attributeName, and resets the specified attribute data type to:
     * TYPE_UNDEFINED.  Please use with caution!
     *
     * @param attributeName attribute name.
     * @return true indicates attribute was successfully reset.
     */
    public boolean deleteAttribute(String attributeName);

    /**
     * Sets a simple list of attributes.
     * <p/>
     * <P>A simple list is defined as follows:
     * <UL>
     * <LI>All items within the list are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     * <P>
     * If the above requirements are not met, an IllegalArgumentException
     * will be thrown.
     * <P>
     * Implementation note:   calling this method results in many calls to the
     * MultiHashMap back-end data store.  For example, if you have
     * five elements in a List, the implementation code makes five calls to
     * the MultiHashMap.  Therefore, if you are listening to MultiHashMap
     * events, you will be notified of five separate events, rather than one
     * global list event.
     *
     * @param id   unique identifier.
     * @param list attribute name.
     * @param list List Object.
     * @throws IllegalArgumentException Simple List requirements have not
     *                                  been met, or this attribute has already
     *                                  been defined with a data type, and this
     *                                  data type is not of type:
     *                                  TYPE_SIMPLE_LIST.
     */
    public void setAttributeList(String id, String attributeName, List list)
            throws IllegalArgumentException;

    /**
     * Gets a 'simple' list of attributes for the id/attributeName pair.
     * <p/>
     * <P>A 'simple' list is defined as follows:
     * <UL>
     * <LI>All items within the list are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     * <P>
     * <B>Note:</B>  The returned List is useful for read operations only.
     * If you add, edit, or delete elements within this list, these changes
     * will not be stored, unless you explicitly call setAttributeList() again.
     * For example:
     *
     * <PRE>
     * List list = nodeAttributes.getAttributeList(id, attributeName);
     *
     * //  Add new item
     * list.add(new String("Hello, World");
     *
     * //  Save modified list back
     * nodeAttributes.setAttributeList(id, attributeName, list);
     * </PRE>
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return List object.
     * @throws ClassCastException Indicates that the specified attribute
     *                            is not of type:  TYPE_SIMPLE_LIST.
     */
    public List getAttributeList(String id, String attributeName)
            throws ClassCastException;

    /**
     * Sets a 'simple' map of attribute values.
     * <P>
     * A 'simple' map is defined as follows:
     * <UL>
     * <LI>All keys within the map are of type:  String.
     * <LI>All values within the map are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     * <P>
     * If the above requirements are not met, an
     * <CODE>IllegalArgumentException</CODE> will be thrown.
     *
     * <P>
     * Implementation note:   calling this method results in
     * many calls to the MultiHashMap back-end data store.  For example, if
     * you have five elements in a Map, the implementation code makes five
     * calls to the MultiHashMap.  Therefore, if you are listening to
     * MultiHashMap events, you will be notified of five separate events,
     * rather than one global map event.
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @param map           Map Object.
     * @throws IllegalArgumentException Simple Map requirements have not
     *                                  been met or this attribute has already
     *                                  been defined with a data type, and this
     *                                  data type is not of type:
     *                                  TYPE_SIMPLE_MAP.
     *
     */
    public void setAttributeMap(String id, String attributeName,
            Map map) throws IllegalArgumentException;

    /**
     * Gets a 'simple' map of attribute values.
     * <p/>
     * <P>A 'simple' map is defined as follows:
     * <UL>
     * <LI>All keys within the map are of type:  String.
     * <LI>All values within the map are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     * <P>
     * <B>Note:</B>  The returned Map is useful for read operations only.
     * If you add, edit, or delete elements within this Map, these changes
     * will not be stored, unless you explicitly call setAttributeMap()
     * again.  For example:
     *
     * <PRE>
     * Map map = nodeAttributes.getAttributeMap(id, attributeName);
     *
     * //  Add new item
     * map.put(new String("Message"), new String("Hello, World"));
     *
     * //  Save modified map back
     * nodeAttributes.setAttributeMap(id, attributeName, map);
     * </PRE>
     *
     * @param id            unique identifier.
     * @param attributeName attribute name.
     * @return Map Object.
     * @throws ClassCastException Indicates that the specified attribute
     *                            is not of type:  TYPE_HASH_MAP.
     */
    public Map getAttributeMap(String id, String attributeName);

    /**
     * Gets the MultiHashMap Object, where we store attribute values.
     * <p/>
     * <P>By using MultiHashMap and MultiHashMapDefinition directly,
     * you can store arbitrarily complex data structures.
     * Recommended for advanced coders only.
     * <p/>
     *
     * @return MultiHashMap Object.
     */
    public MultiHashMap getMultiHashMap();

    /**
     * Gets the MultiHashMapDefinition Object, where we store attribute
     * definitions.
     * <P>
     * <P>By using MultiHashMap and MultiHashMapDefinition directly, you can
     * store arbitrarily complex data structures (e.g. anything more
     * complicated that 'simple' lists and 'simple' maps).  Recommended for
     * advanced coders only.
     * <p/>
     *
     * @return MultiHashMapDefinition Object.
     */
    public MultiHashMapDefinition getMultiHashMapDefinition();
}
