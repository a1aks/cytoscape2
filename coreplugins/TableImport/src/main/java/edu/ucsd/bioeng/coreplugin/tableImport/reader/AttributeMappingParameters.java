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
package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;

import cytoscape.data.synonyms.Aliases;
import static edu.ucsd.bioeng.coreplugin.tableImport.reader.TextFileDelimiters.*;
import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader.ObjectType;
import static edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader.ObjectType.*;

import giny.model.Edge;
import giny.model.Node;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Parameter object for text table <---> CyAttributes mapping.<br>
 * <p>
 *  This object will be used by all attribute readers.
 * </p>
 *
 * @since Cytoscape 2.4
 * @version 0.9
 * @author Keiichiro Ono
 *
 */
public class AttributeMappingParameters implements MappingParameter {
	/**
	 *
	 */
	public static final String ID = "ID";
	private static final String DEF_LIST_DELIMITER = PIPE.toString();
	private static final String DEF_DELIMITER = TAB.toString();
	private final ObjectType objectType;
	private final int keyIndex;
	private final List<Integer> aliasIndex;
	private String[] attributeNames;
	private Byte[] attributeTypes;
	private Byte[] listAttributeTypes;
	private final String mappingAttribute;
	private List<String> delimiters;
	private String listDelimiter;
	private boolean[] importFlag;
	private Map<String, List<String>> attr2id;
	private CyAttributes attributes;
	private Aliases existingAliases;
	private final Map<String, String> networkTitle2ID;

	// Case sensitivity
	private Boolean caseSensitive;

	/**
	 * Creates a new AttributeMappingParameters object.
	 *
	 * @param objectType  DOCUMENT ME!
	 * @param delimiters  DOCUMENT ME!
	 * @param listDelimiter  DOCUMENT ME!
	 * @param keyIndex  DOCUMENT ME!
	 * @param mappingAttribute  DOCUMENT ME!
	 * @param aliasIndex  DOCUMENT ME!
	 * @param attrNames  DOCUMENT ME!
	 * @param attributeTypes  DOCUMENT ME!
	 * @param listAttributeTypes  DOCUMENT ME!
	 * @param importFlag  DOCUMENT ME!
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public AttributeMappingParameters(final ObjectType objectType, final List<String> delimiters,
	                                  final String listDelimiter, final int keyIndex,
	                                  final String mappingAttribute,
	                                  final List<Integer> aliasIndex, final String[] attrNames,
	                                  Byte[] attributeTypes, Byte[] listAttributeTypes,
	                                  boolean[] importFlag) throws Exception {
		this(objectType, delimiters, listDelimiter, keyIndex, mappingAttribute, aliasIndex,
		     attrNames, attributeTypes, listAttributeTypes, importFlag, true);
	}

	/**
	 * Creates a new AttributeMappingParameters object.
	 *
	 * @param objectType  DOCUMENT ME!
	 * @param delimiters  DOCUMENT ME!
	 * @param listDelimiter  DOCUMENT ME!
	 * @param keyIndex  DOCUMENT ME!
	 * @param mappingAttribute  DOCUMENT ME!
	 * @param aliasIndex  DOCUMENT ME!
	 * @param attrNames  DOCUMENT ME!
	 * @param attributeTypes  DOCUMENT ME!
	 * @param listAttributeTypes  DOCUMENT ME!
	 * @param importFlag  DOCUMENT ME!
	 *
	 * @throws Exception  DOCUMENT ME!
	 * @throws IOException  DOCUMENT ME!
	 */
	public AttributeMappingParameters(final ObjectType objectType, final List<String> delimiters,
	                                  final String listDelimiter, final int keyIndex,
	                                  final String mappingAttribute,
	                                  final List<Integer> aliasIndex, final String[] attrNames,
	                                  Byte[] attributeTypes, Byte[] listAttributeTypes,
	                                  boolean[] importFlag, Boolean caseSensitive)
	    throws Exception {
		this.listAttributeTypes = listAttributeTypes;
		this.caseSensitive = caseSensitive;

		if (attrNames == null) {
			throw new Exception("attributeNames should not be null.");
		}

		/*
		 * Error check: Key column number should be smaller than actual number
		 * of columns in the text table.
		 */
		if (attrNames.length < keyIndex) {
			throw new IOException("Key is out of range.");
		}

		/*
		 * These calues should not be null!
		 */
		this.objectType = objectType;
		this.keyIndex = keyIndex;
		this.attributeNames = attrNames;

		if (this.objectType == NETWORK) {
			networkTitle2ID = new HashMap<String, String>();

			Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();

			for (CyNetwork net : networkSet) {
				networkTitle2ID.put(net.getTitle(), net.getIdentifier());
			}
		} else {
			networkTitle2ID = null;
		}

		/*
		 * If attribute mapping is null, use ID for mapping.
		 */
		if (mappingAttribute == null) {
			this.mappingAttribute = ID;
		} else {
			this.mappingAttribute = mappingAttribute;
		}

		/*
		 * If delimiter is not available, use default value (TAB)
		 */
		if (delimiters == null) {
			this.delimiters = new ArrayList<String>();
			this.delimiters.add(DEF_DELIMITER);
		} else {
			this.delimiters = delimiters;
		}

		/*
		 * If list delimiter is null, use default "|"
		 */
		if (listDelimiter == null) {
			this.listDelimiter = DEF_LIST_DELIMITER;
		} else {
			this.listDelimiter = listDelimiter;
		}

		if (aliasIndex == null) {
			this.aliasIndex = new ArrayList<Integer>();
		} else {
			this.aliasIndex = aliasIndex;
		}

		/*
		 * If not specified, import everything as String attributes.
		 */
		if (attributeTypes == null) {
			this.attributeTypes = new Byte[attrNames.length];

			for (int i = 0; i < attrNames.length; i++) {
				this.attributeTypes[i] = CyAttributes.TYPE_STRING;
			}
		} else {
			this.attributeTypes = attributeTypes;
		}

		/*
		 * If not specified, import everything.
		 */
		if (importFlag == null) {
			this.importFlag = new boolean[attrNames.length];

			for (int i = 0; i < this.importFlag.length; i++) {
				this.importFlag[i] = true;
			}
		} else {
			this.importFlag = importFlag;
		}

		final Iterator it;

		switch (objectType) {
			case NODE:
				attributes = Cytoscape.getNodeAttributes();
				existingAliases = Cytoscape.getOntologyServer().getNodeAliases();
				it = Cytoscape.getRootGraph().nodesIterator();

				break;

			case EDGE:
				attributes = Cytoscape.getEdgeAttributes();
				existingAliases = Cytoscape.getOntologyServer().getEdgeAliases();
				it = Cytoscape.getRootGraph().edgesIterator();

				break;

			case NETWORK:
				attributes = Cytoscape.getNetworkAttributes();
				existingAliases = Cytoscape.getOntologyServer().getNetworkAliases();
				it = Cytoscape.getNetworkSet().iterator();

				break;

			default:
				attributes = null;
				it = null;
		}

		if ((this.mappingAttribute != null) && !this.mappingAttribute.equals(ID)) {
			buildAttribute2IDMap(it);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Aliases getAlias() {
		return existingAliases;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyAttributes getAttributes() {
		return attributes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<Integer> getAliasIndexList() {
		return aliasIndex;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String[] getAttributeNames() {
		// TODO Auto-generated method stub
		return attributeNames;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Byte[] getAttributeTypes() {
		return attributeTypes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Byte[] getListAttributeTypes() {
		return listAttributeTypes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean[] getImportFlag() {
		// TODO Auto-generated method stub
		return importFlag;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getKeyIndex() {
		// TODO Auto-generated method stub
		return keyIndex;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getListDelimiter() {
		// TODO Auto-generated method stub
		return listDelimiter;
	}

	/**
	 *  Returns attribute name for mapping.
	 *
	 * @return  Key CyAttribute name for mapping.
	 */
	public String getMappingAttribute() {
		return mappingAttribute;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ObjectType getObjectType() {
		// TODO Auto-generated method stub
		return objectType;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<String> getDelimiters() {
		return delimiters;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDelimiterRegEx() {
		StringBuffer delimiterBuffer = new StringBuffer();
		delimiterBuffer.append("[");

		for (String delimiter : delimiters) {
			if (delimiter.equals(" += +")) {
				return " += +";
			}

			delimiterBuffer.append(delimiter);
		}

		delimiterBuffer.append("]");

		return delimiterBuffer.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributeValue DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<String> toID(String attributeValue) {
		return attr2id.get(attributeValue);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<String, List<String>> getAttributeToIDMap() {
		return attr2id;
	}

	/**
	 * Building hashmap for attribute <--> object ID mapping.
	 *
	 */
	private void buildAttribute2IDMap(Iterator it) {
		// Mapping from attribute value to object ID.
		attr2id = new HashMap<String, List<String>>();

		String objectID = null;
		Object valObj = null;

		while (it.hasNext()) {
			switch (objectType) {
				case NODE:

					Node node = (Node) it.next();
					objectID = node.getIdentifier();

					if (CyAttributesUtils.getClass(mappingAttribute, attributes) == List.class) {
						valObj = attributes.getListAttribute(objectID, mappingAttribute);
					} else if (CyAttributesUtils.getClass(mappingAttribute, attributes) != Map.class) {
						valObj = attributes.getAttribute(objectID, mappingAttribute);
					}

					break;

				case EDGE:

					Edge edge = (Edge) it.next();
					objectID = edge.getIdentifier();

					if (CyAttributesUtils.getClass(mappingAttribute, attributes) == List.class) {
						valObj = attributes.getListAttribute(objectID, mappingAttribute);
					} else if (CyAttributesUtils.getClass(mappingAttribute, attributes) != Map.class) {
						valObj = attributes.getAttribute(objectID, mappingAttribute);
					}

					break;

				case NETWORK:
					// Not supported yet.
					it.next();

					break;

				default:
			}

			// Put the <attribute value>-<object ID list> pair to the Map object.
			if (valObj != null) {
				if (valObj instanceof List) {
					List keys = (List) valObj;

					for (Object key : keys) {
						if (key != null) {
							putAttrValue(key.toString(), objectID);
						}
					}
				} else {
					putAttrValue(valObj.toString(), objectID);
				}

				//				if (attr2id.containsKey(attributeValue)) {
				//					objIdList = (List<String>) attr2id.get(attributeValue);
				//				} else {
				//					objIdList = new ArrayList<String>();
				//				}
				//
				//				objIdList.add(objectID);
				//				attr2id.put(attributeValue, objIdList);
			}
		}
	}

	private void putAttrValue(String attributeValue, String objectID) {
		List<String> objIdList = null;

		if (attr2id.containsKey(attributeValue)) {
			objIdList = attr2id.get(attributeValue);
		} else {
			objIdList = new ArrayList<String>();
		}

		objIdList.add(objectID);
		attr2id.put(attributeValue, objIdList);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return attributeNames.length;
	}

	protected Map<String, String> getnetworkTitleMap() {
		return networkTitle2ID;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Boolean getCaseSensitive() {
		return caseSensitive;
	}
}
