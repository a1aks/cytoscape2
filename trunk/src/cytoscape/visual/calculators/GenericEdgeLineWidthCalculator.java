
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

package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.EdgeAppearance;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.FloatParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeLineWidthCalculator extends EdgeCalculator {
	/**
	 * Creates a new GenericEdgeLineWidthCalculator object.
	 *
	 * @param name DOCUMENT ME!
	 * @param m DOCUMENT ME!
	 */
	public GenericEdgeLineWidthCalculator(String name, ObjectMapping m) {
		super(name, m, Float.class, EDGE_LINE_WIDTH);
	}

	/**
	* Creates a new GenericEdgeLineWidthCalculator object.
	*
	* @param name DOCUMENT ME!
	* @param m DOCUMENT ME!
	* @param c DOCUMENT ME!
	*/
	public GenericEdgeLineWidthCalculator(String name, ObjectMapping m, Class c) {
		super(name, m, c, EDGE_LINE_WIDTH);
	}

	/**
	 * Creates a new GenericEdgeLineWidthCalculator object.
	 *
	 * @param name DOCUMENT ME!
	 * @param props DOCUMENT ME!
	 * @param baseKey DOCUMENT ME!
	 */
	public GenericEdgeLineWidthCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, new FloatParser(), new Float(0), EDGE_LINE_WIDTH);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param appr DOCUMENT ME!
	 * @param edge DOCUMENT ME!
	 * @param network DOCUMENT ME!
	 */
	public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
		final Object lineWidth = getRangeValue(edge);

		// default has already been set - no need to do anything
		if ((lineWidth == null) || (lineWidth instanceof Number == false))
			return;

		appr.set(type, ((Number) lineWidth).floatValue());
	}
}
