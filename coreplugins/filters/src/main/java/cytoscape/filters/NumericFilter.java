
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

package cytoscape.filters;

import giny.model.Edge;
import giny.model.Node;

import java.util.*;

import csplugins.quickfind.util.QuickFind;
import csplugins.widgets.autocomplete.index.TextIndex;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.filters.util.FilterUtil;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.NumberIndex;


/**
 * This is a Cytoscape specific filter that will pass nodes if
 * a selected attribute matches a specific value.
 */

public class NumericFilter<T extends Number> extends AtomicFilter {

	private T lowBound, highBound;

	public NumericFilter() {
	}

	public NumericFilter(String pCtrlAttri, int pIndexType, NumberIndex pIndex) {
		controllingAttribute = pCtrlAttri;
		index_type = pIndexType;
		quickFind_index = pIndex;
	}

	public boolean passesFilter(Object obj) {
		return false;
	}
	public T getLowBound(){
		return lowBound;
	}

	public T getHighBound(){
		return highBound;
	}

	public void setLowBound(T pLowBound){
		lowBound = pLowBound;
	}

	public void setHighBound(T pHighBound){
		highBound = pHighBound;
	}

	public void setRange(T pLowBound, T pUpBound){
		lowBound = pLowBound;
		highBound = pUpBound;
	}
	
	public void apply() {
				
		List<Node> nodes_list = null;
		List<Edge> edges_list=null;

		int objectCount = -1;
		if (index_type == QuickFind.INDEX_NODES) {
			nodes_list = network.nodesList();
			objectCount = nodes_list.size();
			node_bits = new BitSet(objectCount); // all the bits are false initially
		}
		else if (index_type == QuickFind.INDEX_EDGES) {
			edges_list = network.edgesList();
			objectCount = edges_list.size();
			edge_bits = new BitSet(objectCount); // all the bits are false initially
		}
		else {
			CyLogger.getLogger(NumericFilter.class).error("StringFilter: Index_type is undefined.");
			return;
		}

		if (lowBound == null || highBound == null || network == null || !FilterUtil.hasSuchAttribute(controllingAttribute,index_type)) {
			return;
		}
		
		//If quickFind_index does not exist, build the Index
		//if (quickFind_index == null) {
		quickFind_index = FilterUtil.getQuickFindIndex(controllingAttribute, network, index_type);
		//}

		//System.out.println(" NumberFilter.apply(): objectCount = " + objectCount);
		NumberIndex numberIndex = (NumberIndex) quickFind_index;
		List list = numberIndex.getRange(lowBound, highBound);

		if (list.size() == 0) {
			return;
		}

		int index;		
		if (index_type == QuickFind.INDEX_NODES) {
			for (Object obj : list) {
				index = nodes_list.lastIndexOf((Node) obj);
				node_bits.set(index, true);
			}
		} else if (index_type == QuickFind.INDEX_EDGES) {
			for (Object obj : list) {
				index = edges_list.lastIndexOf((Edge) obj);
				edge_bits.set(index, true);
			}
		}		
		
		if (negation) {
			if (index_type == QuickFind.INDEX_NODES) {
				node_bits.flip(0, objectCount);
			}
			if (index_type == QuickFind.INDEX_EDGES) {
				edge_bits.flip(0, objectCount);
			}
		}		
	}

	/**
	 * 
	 */
	public String toString() {
		return "NumericFilter="+controllingAttribute + ":" + negation+ ":"+lowBound+":" + highBound+ ":"+index_type;
	}
	
	//public NumericFilter clone() {
	//	return new NumericFilter(attributeName, searchValues);
	//}

}
