/*
  File: VisualStyle.java

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

import java.util.Vector;

import cytoscape.visual.calculators.Calculator;

import cytoscape.logger.CyLogger;


//----------------------------------------------------------------------------
/**
 * This class encapsulates a full set of visual mapping specifications for
 * Cytoscape. Currently this is implemented by holding a reference to three
 * appearance calculators, one for nodes, one for edges, and one for global
 * visual attributes.
 */
public class VisualStyle implements Cloneable {
	
	// Name of Visual Style
	private String name = "default";
	
	// Calculators associated with this VS.
	private NodeAppearanceCalculator nodeAC;
	private EdgeAppearanceCalculator edgeAC;
	private GlobalAppearanceCalculator globalAC;

	private VisualPropertyDependency deps;

	/**
	 * Keep track of number of times this style has been cloned.
	 */
	protected int dupeCount = 0;

	/**
	 * Get how many times this style has been cloned.
	 */
	public int getDupeCount() {
		return dupeCount;
	}

	/**
	 * Check if contained appearance calculators are using given calculator
	 *
	 * @param    c    calculator to check conflicts for
	 * @return    vector with: name of conflicting visual style (index 0),
	 *        name of conflicting attributes. If size == 1, then no conflicts
	 */
	public Vector checkConflictingCalculator(Calculator c) {
		Vector<String> conflicts = new Vector<String>();
		conflicts.add(name);

		for (Calculator nodeCalcs : nodeAC.getCalculators()) {
			if (nodeCalcs == c)
				conflicts.add(nodeCalcs.getVisualPropertyType().getPropertyLabel());
		}

		for (Calculator edgeCalcs : edgeAC.getCalculators()) {
			if (edgeCalcs == c)
				conflicts.add(edgeCalcs.getVisualPropertyType().getPropertyLabel());
		}

		return conflicts;
	}

	/**
	 * Perform deep copy of this VisualStyle.
	 */
	public Object clone() throws CloneNotSupportedException {
		VisualStyle copy = (VisualStyle) super.clone();
		String dupeFreeName;

		if (dupeCount != 0) {
			int dupeCountIndex = name.lastIndexOf(new Integer(dupeCount).toString());

			if (dupeCountIndex == -1)
				dupeFreeName = new String(name);
			else
				dupeFreeName = name.substring(0, dupeCountIndex);
		} else
			dupeFreeName = new String(name);

		copy.name = dupeFreeName;
		copy.dupeCount++;
		copy.nodeAC = (NodeAppearanceCalculator) this.nodeAC.clone();
		copy.edgeAC = (EdgeAppearanceCalculator) this.edgeAC.clone();
		copy.globalAC = (GlobalAppearanceCalculator) this.globalAC.clone();

		return copy;
	}

	/**
	 * Simple constructor, creates default node/edge/global appearance calculators.
	 */
	public VisualStyle(String name) {
		deps = new VisualPropertyDependencyImpl();
		setName(name);
		setNodeAppearanceCalculator(new NodeAppearanceCalculator(deps));
		setEdgeAppearanceCalculator(new EdgeAppearanceCalculator(deps));
		setGlobalAppearanceCalculator(new GlobalAppearanceCalculator());
	}

	/**
	 * Full constructor.
	 */
	public VisualStyle(String name, NodeAppearanceCalculator nac, EdgeAppearanceCalculator eac,
	                   GlobalAppearanceCalculator gac) {
		deps = new VisualPropertyDependencyImpl();
		setName(name);
		setNodeAppearanceCalculator(nac);
		setEdgeAppearanceCalculator(eac);
		setGlobalAppearanceCalculator(gac);
	}

	/**
	 * Copy constructor. Creates a default object if the argument is null.
	 * The name of this new object should be changed by calling setName
	 * with a new, unique name before adding it to a CalculatorCatalog.
	 */
	public VisualStyle(VisualStyle toCopy) {
		this(toCopy, toCopy.getName());
	}

	/**
	 * Copy constructor with new name. Creates a default object if the first
	 * argument is null, otherwise copies the members of the first argument.
	 * The name of this new VisualStyle will be equal to the second argument;
	 * the caller should ensure that this is a unique name.
	 *
	 * @throws NullPointerException if the second argument is null
	 */
	public VisualStyle(final VisualStyle toCopy, final String newName) {
		if (toCopy == null)
			return;
		
		if (newName == null)
			throw new NullPointerException("Unexpected null name in VisualStyle constructor");

		deps = toCopy.getDependency();
		if (deps == null)
			deps = new VisualPropertyDependencyImpl();
		
		setName(newName);
		setNodeAppearanceCalculator((NodeAppearanceCalculator)toCopy.getNodeAppearanceCalculator().clone());
		setEdgeAppearanceCalculator((EdgeAppearanceCalculator)toCopy.getEdgeAppearanceCalculator().clone());
		
		try {
			setGlobalAppearanceCalculator((GlobalAppearanceCalculator)toCopy.getGlobalAppearanceCalculator().clone());
		} catch (CloneNotSupportedException e) {
			CyLogger.getLogger(VisualStyle.class).warn("Internal error - Unable to clone global appearance calculator!");
			// e.printStackTrace();
		}

	}

	/**
	 * Returns the name of this object, as returned by getName.
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Returns the name of this object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this visual style. This should be a unique name, or
	 * a collision will ocur when adding this to a CalcualtorCatalog.
	 *
	 * @param n  the new name
	 * @return   the old name
	 */
	public String setName(String n) {
		String tmp = name;
		name = n;

		return tmp;
	}

	/**
	 * Get the NodeAppearanceCalculator for this visual style.
	 */
	public NodeAppearanceCalculator getNodeAppearanceCalculator() {
		return nodeAC;
	}

	/**
	 * Set the NodeAppearanceCalculator for this visual style. A default
	 * NodeAppearanceCalculator will be created and used if the argument
	 * is null.
	 *
	 * @param nac  the new NodeAppearanceCalculator
	 * @return  the old NodeAppearanceCalculator
	 */
	public NodeAppearanceCalculator setNodeAppearanceCalculator(NodeAppearanceCalculator nac) {
		NodeAppearanceCalculator tmp = nodeAC;
		nodeAC = (nac == null) ? new NodeAppearanceCalculator(deps) : nac;
		return tmp;
	}

	/**
	 * Get the EdgeAppearanceCalculator for this visual style.
	 */
	public EdgeAppearanceCalculator getEdgeAppearanceCalculator() {
		return edgeAC;
	}

	/**
	 * Set the EdgeAppearanceCalculator for this visual style. A default
	 * EdgeAppearanceCalculator will be created and used if the argument
	 * is null.
	 *
	 * @param nac  the new EdgeAppearanceCalculator
	 * @return  the old EdgeAppearanceCalculator
	 */
	public EdgeAppearanceCalculator setEdgeAppearanceCalculator(EdgeAppearanceCalculator eac) {
		EdgeAppearanceCalculator tmp = edgeAC;
		edgeAC = (eac == null) ? new EdgeAppearanceCalculator(deps) : eac;

		return tmp;
	}

	/**
	 * Get the GlobalAppearanceCalculator for this visual style.
	 */
	public GlobalAppearanceCalculator getGlobalAppearanceCalculator() {
		return globalAC;
	}

	/**
	 * Set the GlobalAppearanceCalculator for this visual style. A default
	 * GlobalAppearanceCalculator will be created and used if the argument
	 * is null.
	 *
	 * @param nac  the new GlobalAppearanceCalculator
	 * @return  the old GlobalAppearanceCalculator
	 */
	public GlobalAppearanceCalculator setGlobalAppearanceCalculator(GlobalAppearanceCalculator gac) {
		GlobalAppearanceCalculator tmp = globalAC;
		globalAC = (gac == null) ? new GlobalAppearanceCalculator() : gac;

		return tmp;
	}

	public VisualPropertyDependency getDependency() {
		return deps;
	}
}
