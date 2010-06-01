/*
 File: XGMMLParser.java

 Copyright (c) 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.readers;


/**
 *  Utility class used by some readers to record equation attributes for later evaluation.
 */
class AttribEquation {
	private final String id;
	private final String attrName;
	private final String equation;
	private final byte dataType;
	private final int lineNumber;

	AttribEquation(final String id, final String attrName, final String equation, final byte dataType, final int lineNumber) {
		this.id         = id;
		this.attrName   = attrName;
		this.equation   = equation;
		this.dataType   = dataType;
		this.lineNumber = lineNumber;
	}

	AttribEquation(final String id, final String attrName, final String equation, final byte dataType) {
		this(id, attrName, equation, dataType, -1);
	}

	String getID() { return id; }
	String getAttrName() { return attrName; }
	String getEquation() { return equation; }
	byte getDataType() { return dataType; }
	int getLineNumber() { return lineNumber; }
}
