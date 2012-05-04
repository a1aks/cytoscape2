/*
  File: InterpreterTest.java

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
package org.cytoscape.equations.interpreter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.*;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.equations.EqnParser;
import org.cytoscape.equations.Function;
import org.cytoscape.equations.Parser;


public class InterpreterTest extends TestCase {
	static private class BadReturnFunction implements Function {
		public String getName() { return "BAD"; }
		public String getFunctionSummary() { return "Returns an invalid type at runtime."; }
		public String getUsageDescription() { return "Call this with \"BAD()\"."; }
		public Class getReturnType() { return Double.class; }
		public Class validateArgTypes(final Class[] argTypes) { return argTypes.length == 0 ? Double.class : null; }
		public Object evaluateFunction(final Object[] args) { return new Integer(1); }
		public List<Class> getPossibleArgTypes(final Class[] leadingArgs) { return null; }
	}

	private final EqnCompiler compiler = new EqnCompiler();

	public void testSimpleStringConcatExpr() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("s1", String.class);
		assertTrue(compiler.compile("=\"Fred\"&${s1}", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("s1", new IdentDescriptor("Bob"));
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals("FredBob", interpreter.run());
	}

	public void testSimpleExpr() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("BOB", Double.class);
		assertTrue(compiler.compile("=42 - 12 + 3 * (4 - 2) + ${BOB:12}", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("BOB", new IdentDescriptor(-10.0));
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(26.0), interpreter.run());
	}

	public void testUnaryPlusAndMinus() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("attr1", Double.class);
		attribNameToTypeMap.put("attr2", Double.class);
		assertTrue(compiler.compile("=-17.8E-14", attribNameToTypeMap));
		assertTrue(compiler.compile("=+(${attr1} + ${attr2})", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("attr1", new IdentDescriptor(5.5));
		nameToDescriptorMap.put("attr2", new IdentDescriptor(6.5));
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(12.0), interpreter.run());
	}

	public void testFunctionCall() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=42 + log(4 - 2)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(42.0 + Math.log10(4.0 - 2.0)), interpreter.run());
	}

	public void testExponentiation() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=2^3^4 - 0.0002", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(Math.pow(2.0, Math.pow(3.0, 4.0)) - 0.0002), interpreter.run());
	}

	public void testComparisons() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("x", Double.class);
		attribNameToTypeMap.put("y", Double.class);
		attribNameToTypeMap.put("limit", Double.class);
		assertTrue(compiler.compile("=${x} <= ${y}", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("x", new IdentDescriptor(1.2));
		nameToDescriptorMap.put("y", new IdentDescriptor(-3.8e-12));
		nameToDescriptorMap.put("limit", new IdentDescriptor(-65.23e12));
		final Interpreter interpreter1 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Boolean(false), interpreter1.run());
		
		assertTrue(compiler.compile("=-15.4^3 > ${limit}", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Boolean(true), interpreter2.run());
	}

	public void testVarargs() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertFalse(compiler.compile("=LOG()", attribNameToTypeMap));
		assertTrue(compiler.compile("=LOG(1)", attribNameToTypeMap));
		assertTrue(compiler.compile("=LOG(1,2)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(Math.log(1.0)/Math.log(2.0)), interpreter.run());
		assertFalse(compiler.compile("=LOG(1,2,3)", attribNameToTypeMap));
	}

	public void testFixedArgs() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertFalse(compiler.compile("=ABS()", attribNameToTypeMap));
		assertTrue(compiler.compile("=ABS(-1.5e10)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(1.5e10), interpreter.run());
		assertFalse(compiler.compile("=ABS(1,2)", attribNameToTypeMap));
	}

	public void testDEFINED() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("x", Double.class);
		assertTrue(compiler.compile("=defined(x)", attribNameToTypeMap));
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("x", new IdentDescriptor(1.2));
		final Interpreter interpreter1 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Boolean(true), interpreter1.run());

		assertTrue(compiler.compile("=DEFINED(${limit})", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Boolean(false), interpreter2.run());
	}

	public void testIntegerToFloatingPointConversion() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("BOB", Long.class);

		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("BOB", new IdentDescriptor(new Long(3)));

		assertTrue(compiler.compile("=$BOB > 5.3", attribNameToTypeMap));
		final Interpreter interpreter1 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Boolean(false), interpreter1.run());

		assertTrue(compiler.compile("=$BOB <= 5.3", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Boolean(true), interpreter2.run());
	}

	public void testMixedModeArithmetic() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		attribNameToTypeMap.put("x", Long.class);

		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		nameToDescriptorMap.put("x", new IdentDescriptor(new Long(3)));

		assertTrue(compiler.compile("=$x + 2.0", attribNameToTypeMap));
		final Interpreter interpreter1 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(5.0), interpreter1.run());

		assertTrue(compiler.compile("=TRUE + TRUE", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		assertEquals(new Double(2.0), interpreter2.run());
	}

	public void testFunctionWithBadRuntimeReturnType() throws Exception {
		final EqnParser eqnParser = Parser.getParser();
		final Function badReturnFunction = new BadReturnFunction();
		if (eqnParser.getFunction(badReturnFunction.getName()) == null) // Avoid duplicate registration!
			eqnParser.registerFunction(badReturnFunction);

		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		assertTrue(compiler.compile("=BAD()", attribNameToTypeMap));

		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();
		final Interpreter interpreter = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
		try {
			interpreter.run();
		} catch (final IllegalStateException e) {
			// If we get here, everything is as expected and we let the test pass!
		}
	}

	public void testComparisonsWithBooleans() throws Exception {
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		final Map<String, IdentDescriptor> nameToDescriptorMap = new HashMap<String, IdentDescriptor>();

		assertTrue(compiler.compile("=TRUE < FALSE", attribNameToTypeMap));
		final Interpreter interpreter1 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter1.run());

		assertTrue(compiler.compile("=FALSE < TRUE", attribNameToTypeMap));
		final Interpreter interpreter2 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(true), interpreter2.run());

		assertTrue(compiler.compile("=\"a\" < TRUE", attribNameToTypeMap));
		final Interpreter interpreter3 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(true), interpreter3.run());

		assertTrue(compiler.compile("=\"ZYX\" < FALSE", attribNameToTypeMap));
		final Interpreter interpreter4 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(true), interpreter4.run());

		assertTrue(compiler.compile("=\"a\" > TRUE", attribNameToTypeMap));
		final Interpreter interpreter5 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter5.run());

		assertTrue(compiler.compile("=\"ZYX\" > FALSE", attribNameToTypeMap));
		final Interpreter interpreter6 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter6.run());

		assertTrue(compiler.compile("=TRUE < \"a\"", attribNameToTypeMap));
		final Interpreter interpreter7 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter7.run());

		assertTrue(compiler.compile("=FALSE < \"ZYX\"", attribNameToTypeMap));
		final Interpreter interpreter8 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter8.run());

		assertTrue(compiler.compile("=TRUE < 0", attribNameToTypeMap));
		final Interpreter interpreter9 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter9.run());

		assertTrue(compiler.compile("=FALSE < -1", attribNameToTypeMap));
		final Interpreter interpreter10 = new Interpreter(compiler.getEquation(), nameToDescriptorMap);
                assertEquals(new Boolean(false), interpreter10.run());
	}
}
