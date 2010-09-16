
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

package fing.model;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;


// Package visible class.
class FNode implements Node {
	// Variables specific to public get/set methods.
	RootGraph m_rootGraph = null;
	int m_rootGraphIndex = 0;
	String m_identifier = null;
	
	// New feature in 2.7: Nexted Network
	private GraphPerspective nextedNetwork;

	// Package visible constructor.
	FNode() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphPerspective getGraphPerspective() {
		return m_rootGraph.createGraphPerspective(m_rootGraph.getNodeMetaChildIndicesArray(m_rootGraphIndex),
		                                          m_rootGraph.getEdgeMetaChildIndicesArray(m_rootGraphIndex));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setGraphPerspective(GraphPerspective gp) {
		if (gp.getRootGraph() != m_rootGraph)
			return false;

		final int[] nodeInx = gp.getNodeIndicesArray();
		final int[] edgeInx = gp.getEdgeIndicesArray();

		for (int i = 0; i < nodeInx.length; i++)
			m_rootGraph.addNodeMetaChild(m_rootGraphIndex, nodeInx[i]);

		for (int i = 0; i < edgeInx.length; i++)
			m_rootGraph.addEdgeMetaChild(m_rootGraphIndex, edgeInx[i]);

		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public RootGraph getRootGraph() {
		return m_rootGraph;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getRootGraphIndex() {
		return m_rootGraphIndex;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		return m_identifier;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param new_id DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setIdentifier(String new_id) {
		m_identifier = new_id;

		return true;
	}

	public GraphPerspective getNestedNetwork() {
		return nextedNetwork;
	}

	public void setNestedNetwork(GraphPerspective nextedNetwork) {
		this.nextedNetwork = nextedNetwork;
	}
}
