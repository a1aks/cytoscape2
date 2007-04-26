/*
 File: GenericEdgeSourceArrowCalculator.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute of Systems Biology
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

//----------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearance;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ArrowParser;

import cytoscape.visual.ui.VizMapUI;

//----------------------------------------------------------------------------
import giny.model.Edge;

import java.util.Map;
import java.util.Properties;


//----------------------------------------------------------------------------
/**
 *
 */
public class GenericEdgeSourceArrowCalculator extends GenericEdgeArrowCalculator
    implements EdgeArrowCalculator {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte getType() {
		return VizMapUI.EDGE_SRCARROW;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getPropertyLabel() {
		return "edgeSourceArrowCalculator";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getTypeName() {
		return "Edge Source Arrow";
	}

    /**
	 * Used to set the various properties throughout the hierarchy.
	 */
	private void set() {
		set( VizMapUI.EDGE_SRCARROW, "edgeSourceArrowCalculator", "Edge Source Arrow" );
	}


	GenericEdgeSourceArrowCalculator() {
		super();
		set();
	}

	/**
	 * Creates a new GenericEdgeSourceArrowCalculator object.
	 *
	 * @param name  DOCUMENT ME!
	 * @param m  DOCUMENT ME!
	 */
	public GenericEdgeSourceArrowCalculator(String name, ObjectMapping m) {
		super(name, m);
		set();
	}

	/**
	 * Creates a new GenericEdgeSourceArrowCalculator object.
	 *
	 * @param name  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 * @param baseKey  DOCUMENT ME!
	 */
	public GenericEdgeSourceArrowCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey);
		set();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param appr DOCUMENT ME!
	 * @param edge DOCUMENT ME!
	 * @param network DOCUMENT ME!
	 */
	public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
		apply(appr, edge, network, SOURCE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 * @param n DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Arrow calculateEdgeArrow(Edge e, CyNetwork n) {
		EdgeAppearance ea = new EdgeAppearance();
		apply(ea, e, n);

		return ea.getSourceArrow();
	}
}