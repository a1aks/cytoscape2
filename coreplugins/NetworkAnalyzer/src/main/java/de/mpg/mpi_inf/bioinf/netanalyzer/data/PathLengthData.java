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

package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Data type storing information about the shortest path lengths from one node to other nodes in the
 * networks.
 * 
 * @author Yassen Assenov
 */
public class PathLengthData {

	/**
	 * Number of shortest path lengths accumulated in this instance.
	 */
	private int count;

	/**
	 * Sum of all shortest path lengths accumulated in this instance.
	 */
	private long totalLength;

	/**
	 * Maximum length of a shortest path accumulated in this instance.
	 */
	private int maxLength;

	/**
	 * Initializes a new instance of <code>PathLengthData</code>.
	 */
	public PathLengthData() {
		count = 0;
		totalLength = 0;
		maxLength = 0;
	}

	/**
	 * Accumulates a new shortest path length to this data instance.
	 * 
	 * @param aLength Length of shortest path to be accumulated.
	 */
	public void addSPL(int aLength) {
		count++;
		totalLength += aLength;
		if (maxLength < aLength) {
			maxLength = aLength;
		}
	}

	/**
	 * Gets the number of shortest path lengths.
	 * 
	 * @return Number of shortest path lengths accumulated in this instance.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the total length of shortest paths.
	 * 
	 * @return Sum of all shortest path lengths accumulated in this instance.
	 */
	public long getTotalLength() {
		return totalLength;
	}

	/**
	 * Longest among the shortest path lengths added to this data instance.
	 * 
	 * @return Maximum length of a shortest path accumulated in this instance.
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * Average shortest path length.
	 * 
	 * @return Average length of a shortest path accumulated in this instance.
	 * 
	 * @throws IllegalStateException If no SPLs were accumulated in this instance ({@link #getCount()}<code> == 0</code>).
	 */
	public double getAverageLength() {
		if (count == 0) {
			throw new IllegalStateException();
		}
		return ((double) totalLength) / count;
	}
}
