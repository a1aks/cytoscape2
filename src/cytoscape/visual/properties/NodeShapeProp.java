/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.visual.properties;

import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.ui.icon.NodeIcon;

import cytoscape.visual.VisualPropertyDependency;

import giny.view.NodeView;

import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;


/**
 *
 */
public class NodeShapeProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_SHAPE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<Object, Icon> getIconSet() {
		return NodeShape.getIconSet();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(final Object value) {
		final NodeIcon icon = new NodeIcon((NodeShape)value);
		icon.setBottomPadding(-2);

		return icon;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nv DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToNodeView(NodeView nv, Object o, VisualPropertyDependency dep) {
		if ((o == null) || (nv == null))
			return;

		final int newShape = ((NodeShape) o).getGinyShape();

		if (nv.getShape() != newShape)
			nv.setShape(newShape);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return NodeShape.RECT;
	}
}
