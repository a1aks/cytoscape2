/*
 File: NodeCalculator.java

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

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.MappingFactory;
import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ValueParser;

//------------------------------------------------------------------------------
import giny.model.Node;

import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JPanel;


//------------------------------------------------------------------------------
/**
 * NodeCalculator implements some UI features for calculators lower in the
 * object tree.
 */
public abstract class NodeCalculator extends AbstractCalculator {
    /**
     * Creates a new NodeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param c DOCUMENT ME!
     */
    @Deprecated
    public NodeCalculator(String name, ObjectMapping m, Class c) {
        this(name, m, c, null);
    }

    /**
     * Creates a new NodeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public NodeCalculator(String name, ObjectMapping m, Class c,
        VisualPropertyType type) {
        super(name, m, c, type);
    }

    /**
     * Constructor that calls {@link MappingFactory} to construct a new
     * ObjectMapping based on the supplied arguments.
     */
    @Deprecated
    public NodeCalculator(String name, Properties props, String baseKey,
        ValueParser parser, Object defObj) {
        this(name,
            MappingFactory.newMapping(props, baseKey + ".mapping", parser,
                defObj, ObjectMapping.NODE_MAPPING),
            defObj.getClass(), null);
    }

    /**
     * Creates a new NodeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param parser DOCUMENT ME!
     * @param defObj DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public NodeCalculator(String name, Properties props, String baseKey,
        ValueParser parser, Object defObj, VisualPropertyType type) {
        super(name,
            MappingFactory.newMapping(props, baseKey + ".mapping", parser,
                defObj, ObjectMapping.NODE_MAPPING),
            defObj.getClass(), type);
    }

    /**
     * Get the UI for node calculators. Display a JPanel with a JPanel from
     * AbstractCalculator {@link AbstractCalculator#getUI} and the underlying
     * mapper's UI JPanel in a FlowLayout.
     *
     * @param parent
     *            Parent dialog for the child UI
     * @param n
     *            CyNetwork representing the graph
     *
     * @return JPanel containing JComboBox
     */
    public JPanel getUI(JDialog parent, CyNetwork n) {
        // everything comes straight from the superclass
        return super.getUI(
            Cytoscape.getNodeAttributes(),
            parent,
            n);
    }

    /**
     * Simple passthrough method that is specific to nodes. See
     * AbstractCalculator.getAttrBundle() for more details.
     */
    protected Map getAttrBundle(String name) {
        return super.getAttrBundle(
            name,
            Cytoscape.getNodeAttributes());
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    abstract public void apply(NodeAppearance appr, Node node, CyNetwork network);
}
