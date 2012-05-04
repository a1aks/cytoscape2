/*
 * Copyright (c) 2006, 2007, 2008, 2010, Max Planck Institute for Informatics, Saarbruecken, Germany.
 *
 * This file is part of NetworkAnalyzer.
 * 
 * NetworkAnalyzer is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * NetworkAnalyzer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with NetworkAnalyzer. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.mpg.mpi_inf.bioinf.netanalyzer.data.settings;

import java.awt.Color;

import org.jdom.Element;
import org.w3c.dom.DOMException;

/**
 * Storage class for visual settings on bars in a bar chart.
 * 
 * @author Yassen Assenov
 */
public final class BarsSettings extends Settings {

	/**
	 * Initializes a new instance of <code>BarsSettings</code> based on the given XML node.
	 * 
	 * @param aElement Node in XML settings file that identifies bar settings.
	 * @throws DOMException When the given element is not an element node with the expected name ({@link #tag})
	 *         or when the subtree rooted at <code>aElement</code> does not have the expected
	 *         structure.
	 */
	public BarsSettings(Element aElement) {
		super(aElement);
	}

	/**
	 * Gets the color of the bars.
	 * 
	 * @return Color the bars are filled with.
	 */
	public Color getBarColor() {
		return barColor;
	}

	/**
	 * Sets the color of the bars.
	 * 
	 * @param aBarColor New color to be used for filling bars.
	 */
	public void setBarColor(Color aBarColor) {
		barColor = aBarColor;
	}

	/**
	 * Tag name used in XML settings file to identify bar settings.
	 */
	static final String tag = "bars";

	/**
	 * Name of the tag identifying the color of bars.
	 */
	static final String barColorTag = "barcolor";

	/**
	 * Initializes a new instance of <code>BarsSettings</code>.
	 * <p>
	 * The initialized instance contains no data and therefore this constructor is used only by the
	 * {@link Settings#clone()} method.
	 * </p>
	 */
	BarsSettings() {
		super();
	}

	/**
	 * Color of the bars.
	 */
	Color barColor;
}
