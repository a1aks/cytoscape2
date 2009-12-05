/*
 File: EdgeAppearance.java

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

//----------------------------------------------------------------------------
import static cytoscape.visual.VisualPropertyType.EDGE_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_FONT_FACE;
import static cytoscape.visual.VisualPropertyType.EDGE_FONT_SIZE;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_SHAPE;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_SHAPE;
import static cytoscape.visual.VisualPropertyType.EDGE_TOOLTIP;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW;

import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.FloatParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.ObjectToString;

import giny.model.Edge;

import giny.view.EdgeView;
import giny.view.Label;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.Properties;


/**
 * Objects of this class hold data describing the appearance of an Edge.
 */
public class EdgeAppearance extends Appearance {

	/**
	 * Creates a new EdgeAppearance object.
	 */
	public EdgeAppearance() {
		super();
	}

	/**
	 * Clone.
	 */
    public Object clone() {
        EdgeAppearance ga = new EdgeAppearance();
        ga.copy(this);
        return ga;
	}

}
