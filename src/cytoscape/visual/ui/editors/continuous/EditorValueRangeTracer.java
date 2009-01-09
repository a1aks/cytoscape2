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
package cytoscape.visual.ui.editors.continuous;

import cytoscape.visual.VisualPropertyType;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class EditorValueRangeTracer {
	private static EditorValueRangeTracer tracer;

	static {
		tracer = new EditorValueRangeTracer();
	}

	private final class Range {
		private Double min;
		private Double max;

		public Range(Double min, Double max) {
			this.min = min;
			this.max = max;
		}

		public void setMin(final Double min) {
			this.min = min;
		}

		public void setMax(final Double max) {
			this.max = max;
		}

		public Double getMin() {
			return min;
		}

		public Double getMax() {
			return max;
		}

		public Double getRange() {
			return Math.abs(min - max);
		}
	}

	private Map<VisualPropertyType, Range> rangeMap;

	private EditorValueRangeTracer() {
		rangeMap = new HashMap<VisualPropertyType, Range>();

		for (VisualPropertyType v : VisualPropertyType.values()) {
			Range r = new Range(0d, 0d);
			rangeMap.put(v, r);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static EditorValueRangeTracer getTracer() {
		return tracer;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Double getRange(VisualPropertyType t) {
		return rangeMap.get(t).getRange();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Double getMin(VisualPropertyType t) {
		return rangeMap.get(t).getMin();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Double getMax(VisualPropertyType t) {
		return rangeMap.get(t).getMax();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 * @param min DOCUMENT ME!
	 */
	public void setMin(VisualPropertyType t, Double min) {
		rangeMap.get(t).setMin(min);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 * @param max DOCUMENT ME!
	 */
	public void setMax(VisualPropertyType t, Double max) {
		rangeMap.get(t).setMax(max);
	}
}
