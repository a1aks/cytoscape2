/*
  File: Text.java

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


import java.text.DecimalFormat;

import org.cytoscape.equations.AbstractFunction;
import org.cytoscape.equations.ArgDescriptor;
import org.cytoscape.equations.ArgType;
import org.cytoscape.equations.FunctionUtil;


public class Text extends AbstractFunction {
	public Text() {
		super(new ArgDescriptor[] {
				new ArgDescriptor(ArgType.FLOAT, "value", "Any number."),
				new ArgDescriptor(ArgType.OPT_STRING, "format", "How to format the first argument using the conventions of the Java DecimalFormat class."),
			});
	}

	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "TEXT"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns a number formatted as text."; }

	public Class getReturnType() { return String.class; }

	/**
	 *  @param args the function arguments which must be either one or two objects of type Double
	 *  @return the result of the function evaluation which is the first argument formatted as a string
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Boolean
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final double value = FunctionUtil.getArgAsDouble(args[0]);

		if (args.length == 1)
			return Double.toString(value);
		else {
			final String format = FunctionUtil.getArgAsString(args[1]);
			if (!isValidFormat(format))
				throw new IllegalArgumentException("\"" + format +"\" is not a valid format string for the TEXT() function!");

			final DecimalFormat decimalFormat;
			try {
				decimalFormat = new DecimalFormat(format);
			} catch (final Exception e) {
				throw new IllegalStateException("we should *never* get here!");
			}

			try {
				return decimalFormat.format(value).toString();
			} catch (final Exception e) {
				throw new IllegalStateException("we should *never* get here (2): " + e);
			}
		}
	}

	private boolean isValidFormat(final String format) {
		try {
			new DecimalFormat(format);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
}
