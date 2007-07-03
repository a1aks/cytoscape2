/*
  File: CyEdge.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape;

import cytoscape.giny.CytoscapeFingRootGraph;

import giny.model.*;


/**
 *
 */
public class CyEdge implements giny.model.Edge {
	// Variables specific to public get/set methods.
	CytoscapeFingRootGraph m_rootGraph = null;
	int m_rootGraphIndex = 0;
	String m_identifier = null;

	/**
	 * Creates a new CyEdge object.
	 *
	 * @param root  DOCUMENT ME!
	 * @param rootGraphIndex  DOCUMENT ME!
	 */
	public CyEdge(RootGraph root, int rootGraphIndex) {
		this.m_rootGraph = (CytoscapeFingRootGraph) root;
		this.m_rootGraphIndex = rootGraphIndex;
		this.m_identifier = new Integer(m_rootGraphIndex).toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public giny.model.Node getSource() {
		return m_rootGraph.getNode(m_rootGraph.getEdgeSourceIndex(m_rootGraphIndex));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public giny.model.Node getTarget() {
		return m_rootGraph.getNode(m_rootGraph.getEdgeTargetIndex(m_rootGraphIndex));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isDirected() {
		return m_rootGraph.isEdgeDirected(m_rootGraphIndex);
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
		if (new_id == null) {
			m_rootGraph.setEdgeIdentifier(m_identifier, 0);
		} else {
			m_rootGraph.setEdgeIdentifier(new_id, m_rootGraphIndex);
		}

		m_identifier = new_id;

		return true;
	}

	/**
	 * A static method used to create edge identifiers.
	 */
	public static String createIdentifier(String source, String attribute_value, String target) {
		return source + " (" + attribute_value + ") " + target;
	}
}