
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

import giny.model.Node;
import java.util.*;
import cytoscape.Cytoscape;


public class NodeInteractionFilter extends InteractionFilter {
	
	public NodeInteractionFilter() {
		//Set selection for node
		advancedSetting.setNode(true);
		advancedSetting.setEdge(false);
	}

	public NodeInteractionFilter(String pName) {
		name = pName;
		//	Set selection for node
		advancedSetting.setNode(true);
		advancedSetting.setEdge(false);
	}
		
	
	public void apply() {
		if ( !childChanged ) 
			return;

		if (network == null) {
			setNetwork(Cytoscape.getCurrentNetwork());
		}
		
		if (network == null) {
			return;
		}
		
		//Make sure the pass filter is current
		if (passFilter == null) {
			passFilter = new CompositeFilter("None");
		}
		
		if (!passFilter.getName().equalsIgnoreCase("None")) {
			passFilter.setNetwork(network);
			passFilter.apply();		
		}	

		List<Node> nodes_list = null;

		// NodeInteractionFilter will select node only
		nodes_list = network.nodesList();
		int objectCount = nodes_list.size();
		node_bits = new BitSet(objectCount); // all the bits are false at very beginning
			
		if ((nodeType != NODE_UNDEFINED)&&(!passFilter.getName().equalsIgnoreCase("None"))) {
			for (int i=0; i<objectCount; i++) {
				if (isHit((Node)nodes_list.get(i))) {
					node_bits.set(i);
				}
			}			
		}
			
		if (negation) {
			node_bits.flip(0, objectCount);
		}

		childChanged = false;
	}


	private boolean isHit(Node pNode) {
		
		// Get the list of relevant edges for this node
		List adjacentEdges = null;
		
		if (nodeType == NODE_SOURCE) {
			adjacentEdges = network.getAdjacentEdgesList(pNode, true, false, true);
		}
		else if (nodeType == NODE_TARGET) {
			adjacentEdges = network.getAdjacentEdgesList(pNode, true, true, false);
		}
		else if (nodeType == NODE_SOURCE_TARGET) {
			adjacentEdges = network.getAdjacentEdgesList(pNode, true, true, true);
		}
		else { //nodeType == NODE_UNDEFINED --Neither source or target is selected
			return false;
		}
		
		if (adjacentEdges == null || adjacentEdges.size() == 0) {
			return false;
		}
				
		BitSet passFilter_edgeBits = passFilter.getEdgeBits();

		int edgeIndex = -1;
		for (int i=0; i < adjacentEdges.size(); i++) {
			edgeIndex = network.edgesList().indexOf(adjacentEdges.get(i));
		
			if (passFilter_edgeBits.get(edgeIndex) == true) {
				return true;
			}
		}
		
		return false;
	}
}
