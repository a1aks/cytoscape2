/*
  File: Mid.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.equations.builtins;


import org.cytoscape.equations.AbstractFunction;
import org.cytoscape.equations.ArgDescriptor;
import org.cytoscape.equations.ArgType;
import org.cytoscape.equations.FunctionUtil;


public class Mid extends AbstractFunction {
	public Mid() {
		super(new ArgDescriptor[] {
				new ArgDescriptor(ArgType.STRING, "text", "The source text."),
				new ArgDescriptor(ArgType.INT, "start", "The first position of the selected substring."),
				new ArgDescriptor(ArgType.INT, "count", "The length of the selected substring."),
			});
	}

	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "MID"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Selects a substring of some text."; }

	public Class getReturnType() { return String.class; }

	/**
	 *  @param args the function arguments which must be either one or two objects of type Double
	 *  @return the result of the function evaluation which is the natural logarithm of the first argument
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Boolean
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final String text = FunctionUtil.getArgAsString(args[0]);

		final int start;
		try {
			start = (int)FunctionUtil.getArgAsLong(args[1]);
		} catch (final Exception e) {
			throw new IllegalArgumentException("can't convert \"" + args[1] + "\" to a start position in a call to MID()!");
		}

		final int count;
		try {
			count = (int)FunctionUtil.getArgAsLong(args[2]);
		} catch (final Exception e) {
			throw new IllegalArgumentException("can't convert \"" + args[2] + "\" to a count in a call to MID()!");
		}

		if (start < 1)
			throw new IllegalArgumentException("illegal start position in call to MID()!");
		if (count < 0)
			throw new IllegalArgumentException("illegal character count in call to MID()!");
		if (count >= text.length() - start + 1)
			return text.substring(start - 1);
		return text.substring(start - 1, start + count - 1);
	}
}
