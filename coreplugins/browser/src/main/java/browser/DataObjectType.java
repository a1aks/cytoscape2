/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package browser;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import giny.model.GraphObject;




public enum DataObjectType {
	NODES("Node"),
	EDGES("Edge"),
	NETWORK("Network");

	private String dispName;

	private DataObjectType(String dispName) {
		this.dispName = dispName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDisplayName() {
		return dispName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyAttributes getAssociatedAttributes() {
		if (this == NODES)
			return Cytoscape.getNodeAttributes();
		else if (this == EDGES)
			return Cytoscape.getEdgeAttributes();
		else if (this == NETWORK)
			return Cytoscape.getNetworkAttributes();

		return null;
	}

	public Iterable<String> getAssociatedIdentifiers() {
		final Set<String> ids = new HashSet<String>();
		if (this == NODES) {
			final List<GraphObject> nodes = (List<GraphObject>)Cytoscape.getCyNodesList();
			for (final GraphObject node : nodes)
				ids.add(node.getIdentifier());
			return ids;
		}
		else if (this == EDGES) {
			final List<GraphObject> edges = (List<GraphObject>)Cytoscape.getCyEdgesList();
			for (final GraphObject edge : edges)
				ids.add(edge.getIdentifier());
			return ids;
		}
		else if (this == NETWORK) {
			final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
			for (final CyNetwork network : networks)
				ids.add(network.getIdentifier());
			return ids;
		}

		return null;
	}
}
