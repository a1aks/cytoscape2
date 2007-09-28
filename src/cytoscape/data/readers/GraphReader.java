/*
  File: GraphReader.java

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
package cytoscape.data.readers;

import cytoscape.CyNetwork;

import cytoscape.data.CyAttributes;

import cytoscape.task.TaskMonitor;

import cytoscape.layout.CyLayoutAlgorithm;

import giny.model.RootGraph;

import giny.view.GraphView;

import java.io.IOException;


/**
 * Interface for Reading in Cytoscape Graphs.
 *
 * @author Cytoscape Development Group.
 */
public interface GraphReader {
	/**
	 * Reads/imports the graph.
	 *
	 * @throws IOException IO Errors.
	 */
	public void read() throws IOException;

	/**
	 * Lays out the graph.
	 *
	 * @param myView
	 * @deprecated Use getLayoutAlgorithm().doLayout(view) instead. Will be removed 5/2008.
	 */
	public void layout(GraphView myView);

	/**
	 * Returns the CyLayoutAlgorithm used to layout the graph
	 *
	 * @param myView
	 */
	public CyLayoutAlgorithm getLayoutAlgorithm();

	/**
	 * Gets an array of node indices that participate in the newly created graph.
	 *
	 * @return array of node indices from the root graph.
	 */
	public int[] getNodeIndicesArray();

	/**
	 * Gets an array of edges indices that participate in the newly created graph.
	 *
	 * @return array of edges indices from the root graph.
	 */
	public int[] getEdgeIndicesArray();

	/**
	 * Execute whatever post-processing is required.
	 */
	public void doPostProcessing(CyNetwork network);

	/**
	 * Gets the name of the network.
	 * @return network name.
	 */
	public String getNetworkName();
}
