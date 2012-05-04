/*
 Copyright (c) 2006, 2007, 2009 The Cytoscape Consortium (www.cytoscape.org)

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

import java.util.List;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.BitSet;
import java.util.LinkedList;

import csplugins.quickfind.util.QuickFind;

public class CompositeFilter implements CyFilter {

	protected List<CyFilter> children;
	protected boolean negation;
	//Relation relation;
	protected String name;
	protected BitSet node_bits, edge_bits;
	protected boolean childChanged = true;// so we calculate the first time through
	protected CyFilter parent;
	protected String description;
	protected AdvancedSetting advancedSetting = null;
	//private int indexType = -1; //QuickFind.INDEX_NODES //QuickFind.INDEX_EDGES 
	protected CyNetwork network;
	private CyLogger logger = null;

	protected Hashtable compositeNotTab = new Hashtable<CompositeFilter, Boolean>();
	
	public CompositeFilter() {
		logger = CyLogger.getLogger(FilterPlugin.class);
		advancedSetting = new AdvancedSetting();
		children = new LinkedList<CyFilter>();
	}

	public CompositeFilter(String pName) {
		name = pName;
		logger = CyLogger.getLogger(FilterPlugin.class);
		advancedSetting = new AdvancedSetting();
		children = new LinkedList<CyFilter>();
	}
		
	public void setNetwork(CyNetwork pNetwork) {
		if (network != null && network == pNetwork) {
			return;
		}
		network = pNetwork;
		// Set network for all the children
		if (children == null || children.size() == 0){
			return;
		}
		for (int i=0; i< children.size(); i++) {
			children.get(i).setNetwork(pNetwork);
			children.get(i).childChanged();
		}
		childChanged();
	}
	
	public CyNetwork getNetwork(){
		return network;
	}

	public Hashtable getNotTable() {
		return compositeNotTab;
	}
	
	public void setNotTable(CompositeFilter pFilter, boolean pNot) {
		compositeNotTab.put(pFilter, new Boolean(pNot));
		childChanged();
	}
	
	public boolean passesFilter(Object obj) {
		
		List<Node> nodes_list = null;
		List<Edge> edges_list=null;

		int index = -1;
		if (obj instanceof Node) {
			nodes_list = network.nodesList();
			index = nodes_list.lastIndexOf((Node) obj);	
			return node_bits.get(index);			
		}
		
		if (obj instanceof Edge) {
			edges_list = network.edgesList();
			index = edges_list.lastIndexOf((Edge) obj);	
			return edge_bits.get(index);			
		}
		
		return false;
	}
	
	public void setNegation(boolean pNegation) {
		negation = pNegation;
	}
	public boolean getNegation() {
		return negation;
	}
	
	private void calculateNodeBitSet() {
		//System.out.println("Entering CompositeFilter.calculatNodeBits() ... ");	
	
		// set the initial bits to a clone of the first child
		if (children.get(0).getNodeBits() == null) {
			node_bits = new BitSet(network.getNodeCount());	
		}
		else {
			node_bits = (BitSet) children.get(0).getNodeBits().clone();						
		}

		// now perform the requested relation with each subsequent child
		for ( int i = 1; i < children.size(); i++ ) {
			CyFilter n = children.get(i);
			if ( advancedSetting.getRelation() == Relation.AND ) {	
				if (n.getNodeBits() == null) {
					node_bits = new BitSet();//all set to false
					return;
				}
				if ((n instanceof CompositeFilter)&&(((Boolean)compositeNotTab.get(n)).booleanValue()==true)) {
					BitSet tmpBitSet = (BitSet) n.getNodeBits().clone();					
					tmpBitSet.flip(0, network.getNodeCount());
					node_bits.and(tmpBitSet);											
				}
				else {
					node_bits.and(n.getNodeBits());											
				}
				
			} else if ( advancedSetting.getRelation() == Relation.OR ) {
				if (n.getNodeBits() != null) {
					if ((n instanceof CompositeFilter)&&(((Boolean)compositeNotTab.get(n)).booleanValue()==true)) {
						BitSet tmpBitSet = (BitSet) n.getNodeBits().clone();
						tmpBitSet.flip(0, network.getNodeCount());
						node_bits.or(tmpBitSet);											
					}
					else {
						node_bits.or(n.getNodeBits());						
					}
				}
			}
			else { //advancedSetting.getRelation() == Relation.XOR|NOR 
				logger.warn("CompositeFilter: Relation.XOR|NOR: not implemented yet");
			} 
		}

		if (negation) {
				node_bits.flip(0, network.getNodeCount());
		}
	}
	
	
	private void calculateEdgeBitSet() {
		//System.out.println("Entering CompositeFilter.calculatEdgeBits() ... ");	
				
		// if there are no children, just return an empty bitset
		if ( children.size() <= 0 ) {
			edge_bits = new BitSet();
			return;
		}

		// set the initial bits to a clone of the first child
		if (children.get(0).getEdgeBits() == null) {
			edge_bits = new BitSet();
		}
		else {
			edge_bits = (BitSet) children.get(0).getEdgeBits().clone();						
		}

		// now perform the requested relation with each subsequent child
		for ( int i = 1; i < children.size(); i++ ) {
			CyFilter n = children.get(i);
			if ( advancedSetting.getRelation() == Relation.AND ) {	
				if (n.getEdgeBits() == null) {
					edge_bits =  new BitSet(); 
					return;//all set to false
				}
				if ((n instanceof CompositeFilter)&&(((Boolean)compositeNotTab.get(n)).booleanValue()==true)) {
					BitSet tmpBitSet = (BitSet) n.getEdgeBits().clone();
					tmpBitSet.flip(0, network.getEdgeCount());
					edge_bits.and(tmpBitSet);											
				}
				else {
					edge_bits.and(n.getEdgeBits());											
				}				
			} else if ( advancedSetting.getRelation() == Relation.OR ) {
				if (n.getEdgeBits() != null) {
					if ((n instanceof CompositeFilter)&&(((Boolean)compositeNotTab.get(n)).booleanValue()==true)) {
						BitSet tmpBitSet = (BitSet) n.getEdgeBits().clone();
						tmpBitSet.flip(0, network.getEdgeCount());
						edge_bits.or(tmpBitSet);											
					}
					else {
						edge_bits.or(n.getEdgeBits());						
					}
				}
			}
			else { //advancedSetting.getRelation() == Relation.XOR|NOR 
				logger.warn("CompositeFilter: Relation.XOR|NOR: not implemented yet");
			} 
		}

		if (negation) {
				edge_bits.flip(0, network.getEdgeCount());
		}
	}
	
	public void apply() {
		if (network == null) {
			setNetwork(Cytoscape.getCurrentNetwork());			
		}
		
		//System.out.println("CompositeFilter.apply() ....");
		//System.out.println("\tNetwork.getIdentifier() = " + network.getIdentifier());

		// only recalculate the bits if the child has actually changed
		if ( !childChanged ) 
			return;
				
		// if there are no children, just create empty bitSet
		if ( children.size() <= 0 ) {
			node_bits = new BitSet(network.getNodeCount());
			edge_bits = new BitSet(network.getEdgeCount());
			return;
		}

		updateSelectionType();
		
		if (advancedSetting.isNodeChecked()) {
			calculateNodeBitSet();
		}
		if (advancedSetting.isEdgeChecked()) {
			calculateEdgeBitSet();
		}
				
		// record that we've calculated the bits
		childChanged = false;
	}
	
	
	private void updateSelectionType() {
		boolean selectNode = false;
		boolean selectEdge = false;
		//List<CyFilter> childFilters = theFilter.getChildren();
		for (int i=0; i< children.size(); i++) {
			CyFilter child = children.get(i);
			if (child instanceof AtomicFilter) {
				AtomicFilter tmp = (AtomicFilter) child;
				if (tmp.getIndexType() == QuickFind.INDEX_NODES) {
					selectNode = true;
				}
				if (tmp.getIndexType() == QuickFind.INDEX_EDGES) {
					selectEdge = true;
				}
			}
			else if (child instanceof CompositeFilter) {
				CompositeFilter tmp = (CompositeFilter) child;
				if (tmp.getAdvancedSetting().isNodeChecked()) {
					selectNode = true;
				}
				if (tmp.getAdvancedSetting().isEdgeChecked()) {
					selectEdge = true;
				}
			}
		}//end of for loop
		
		advancedSetting.setNode(selectNode);
		advancedSetting.setEdge(selectEdge);
	}

	public BitSet getEdgeBits() {
		apply();
		return edge_bits;
	}
	
	public BitSet getNodeBits() {
		apply();
		return node_bits;
	}

	
	public void removeChild( CyFilter pChild ) {
		if (pChild instanceof CompositeFilter) {
			compositeNotTab.remove(pChild);
		}
		children.remove(pChild);		
		childChanged();		
	}

	public void removeChildAt( int pChildIndex ) {
		if (children.get(pChildIndex) instanceof CompositeFilter) {
			compositeNotTab.remove(children.get(pChildIndex));
		}
		children.remove(pChildIndex);		
		childChanged();		
	}

	public void addChild( AtomicFilter pChild ) {
		pChild.setNetwork(network);
		children.add( pChild );

		// so the the child can communicate with us 
		// (i.e. so we know when the child changes)
		pChild.setParent(this);

		// to force this class to recalculate and to
		// notify parents
		childChanged();
	}

	public void addChild( CompositeFilter pChild, boolean pNot ) {
		pChild.setNetwork(network);
		children.add( pChild );
		compositeNotTab.put(pChild, new Boolean(pNot));

		// so the the child can communicate with us 
		// (i.e. so we know when the child changes)
		pChild.setParent(this);

		// to force this class to recalculate and to
		// notify parents
		childChanged();
	}

	
	// called by any children
	public void childChanged() {
		childChanged = true;
		// pass the message on to the parent
		if ( parent != null )
			parent.childChanged();
	}

	public CyFilter getParent() {
		return parent;
	}

	public void setParent(CyFilter f) {
		parent = f;
	}

	public List<CyFilter> getChildren() {
		return children;		
	}

	public String getName() {
		return name;
	}
	
	public void setName(String pName) {
		name = pName;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String pDescription) {
		description = pDescription;
	}

	public Relation getRelation() {
		return advancedSetting.getRelation();
	}

	public void setRelation(Relation pRelation) {
		advancedSetting.setRelation(pRelation);
	}
	
	public AdvancedSetting getAdvancedSetting() {
		return advancedSetting;
	}

	public void setAdvancedSetting(AdvancedSetting pAdvancedSetting) {
		advancedSetting = pAdvancedSetting;
	}

    /**
     * Returns the display String used by the filter panel combobox.
     * Required as toString is used to provide the persistable representation
     * of the filter state.
     *
     * @return
     */
    public String getLabel() {
        AdvancedSetting as = getAdvancedSetting();
        String prefix = "";
        if (as.isGlobalChecked()) {
            prefix = "global: ";
        }
        if (as.isSessionChecked()) {
            prefix += "session: ";
        }

        return prefix + getName();
    }

	/**
	 * @return the string represention of this Filter.
	 */
	public String toString()
	{
		String retStr = "<Composite>\n";
		
		retStr = retStr + "name=" + name + "\n";
		retStr = retStr + advancedSetting.toString() + "\n";
		retStr = retStr + "Negation=" + negation + "\n";

		for (int i=0; i< children.size(); i++) {

			if (children.get(i) instanceof AtomicFilter) {
				AtomicFilter atomicFilter = (AtomicFilter)children.get(i);
				retStr = retStr + atomicFilter.toString()+"\n";
			}
			else  {// it is a CompositeFilter
				CompositeFilter tmpFilter = (CompositeFilter)children.get(i);
				retStr = retStr + "CompositeFilter=" + tmpFilter.getName()+ ":" + compositeNotTab.get(tmpFilter)+"\n";
			}
		}
		retStr += "</Composite>";

		return retStr;
	}

	/**
	 */
	public boolean equals(Object other_object) {
		if (!(other_object instanceof CompositeFilter)) {
			return false;
		}
		CompositeFilter theOtherFilter = (CompositeFilter) other_object;
		
		if (theOtherFilter.toString().equalsIgnoreCase(this.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * CompositeFilter may be cloned.
	 */
	public Object clone() {
		logger.warn("CompositeFilter.clone() not implemented yet");
		
		return null;
	}	

}
