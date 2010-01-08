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

import cytoscape.Cytoscape;

import cytoscape.visual.VisualProperty;
import cytoscape.visual.VisualPropertyType;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Graphics2D;

import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;


/**
 *
 */
public abstract class AbstractVisualProperty implements VisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	abstract public VisualPropertyType getType();

	/**
	 *  DOCUMENT ME!
	 */
	public void showDiscreteEditor() {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void showContinousEditor() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<Object, Icon> getIconSet() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param g2 DOCUMENT ME!
	 */
	public void paintIcon(Graphics2D g2) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getDefaultIcon() {
		return getIcon(getDefault());
	}
	
	abstract public Icon getIcon(final Object value); 

	protected Object getDefault() {
		return getType().getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nv DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToNodeView(NodeView nv, Object o) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ev DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToEdgeView(EdgeView ev, Object o) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param props DOCUMENT ME!
	 * @param baseKey DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object parseProperty(Properties props, String baseKey) {
		final VisualPropertyType type = getType();
        String s = props.getProperty(type.getDefaultPropertyKey(baseKey));
        if (s != null)
            return type.getValueParser().parseStringValue(s);
        else
            return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return null;
	}
}
