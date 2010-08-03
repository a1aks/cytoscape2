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

package cytoscape.visual;

import java.util.Properties;

import cytoscape.visual.converter.ValueToStringConverterManager;

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

	public Object clone() {
		EdgeAppearance ga = new EdgeAppearance();
		ga.copy(this);
		return ga;
	}
	
	public Properties getDefaultProperties(final String baseKey) {

		Properties props = new Properties();

		for (VisualPropertyType type : VisualPropertyType.values()) {
			if(type.isNodeProp())
				continue;
			
			String key = type.getDefaultPropertyKey(baseKey);
			String value = ValueToStringConverterManager.manager
					.toString(vizProps.get(type));
			if (key != null && value != null) {
				props.setProperty(key, value);
			}
		}

		return props;
	}
}
