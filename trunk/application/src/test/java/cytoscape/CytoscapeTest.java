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
package cytoscape;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.ImportHandler;
import cytoscape.data.Semantics;

import giny.model.Edge;
import giny.model.Node;

import junit.framework.TestCase;

import java.io.IOException;

import java.util.*;


/**
 *
 */
public class CytoscapeTest extends TestCase {
	CyNetwork cytoNetwork;
	String title;
	int nodeCount;
	int edgeCount;

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
		cytoNetwork = null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetImportHandler() throws Exception {
		ImportHandler importHandler = Cytoscape.getImportHandler();
		assertEquals(importHandler.getClass(), ImportHandler.class);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testNullNetwork() throws Exception {
		cytoNetwork = Cytoscape.getNullNetwork();

		title = cytoNetwork.getTitle();
		assertEquals("0", title);

		nodeCount = cytoNetwork.getNodeCount();
		assertEquals(0, nodeCount);

		edgeCount = cytoNetwork.getEdgeCount();
		assertEquals(0, edgeCount);
	}

	//try creating a network
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testCreateNonexistentNetwork() throws Exception {
		try {
			cytoNetwork = Cytoscape.createNetworkFromFile("nonexistentNetwork");
		} catch (Exception e) {
			System.out.println("this is the expected exception");
			e.printStackTrace();
			assertEquals(1, 1);

			return;
		}

		fail("Did not catch expected exception");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void testBug839() throws IOException {
		Set<String> nodes = new HashSet<String>();
		nodes.add("n1");
		nodes.add("n2");
		nodes.add("n3");
		nodes.add("n4");
		nodes.add("n5");

		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/bug_839.sif");

		// check that all nodes exist
		Iterator it = cytoNetwork.nodesIterator();

		while (it.hasNext()) {
			Node n = (Node) it.next();
			assertTrue("checking node " + n.getIdentifier(), nodes.contains(n.getIdentifier()));
		}

		Set<String> edges = new HashSet<String>();
		edges.add(CyEdge.createIdentifier("n1", "activates", "n2"));
		edges.add(CyEdge.createIdentifier("n1", "activates", "n4"));
		edges.add(CyEdge.createIdentifier("n1", "activates", "n5"));
		edges.add(CyEdge.createIdentifier("n2", "activates", "n1"));
		edges.add(CyEdge.createIdentifier("n2", "activates", "n5"));
		edges.add(CyEdge.createIdentifier("n3", "inhibits", "n3"));
		edges.add(CyEdge.createIdentifier("n3", "inhibits", "n4"));
		edges.add(CyEdge.createIdentifier("n3", "inhibits", "n5"));
		edges.add(CyEdge.createIdentifier("n4", "activates", "n1"));
		edges.add(CyEdge.createIdentifier("n4", "activates", "n2"));
		edges.add(CyEdge.createIdentifier("n4", "activates", "n4"));
		edges.add(CyEdge.createIdentifier("n5", "activates", "n1"));
		edges.add(CyEdge.createIdentifier("n5", "activates", "n4"));
		edges.add(CyEdge.createIdentifier("n5", "activates", "n5"));

		it = cytoNetwork.edgesIterator();

		while (it.hasNext()) {
			Edge e = (Edge) it.next();
			assertTrue("checking edge " + e.getIdentifier(), edges.contains(e.getIdentifier()));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void testgetCyEdgeWithStrings() throws IOException {
		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/directedGraph.sif");

		assertEquals(2, cytoNetwork.getNodeCount());
		assertEquals(4, cytoNetwork.getEdgeCount());

		String en1 = CyEdge.createIdentifier("a", "pp", "b");

		// edge should exist in network already
		CyEdge ce1 = Cytoscape.getCyEdge("a", en1, "b", "pp");
		assertNotNull(ce1);

		CyEdge ce1_again = Cytoscape.getCyEdge("a", en1, "b", "pp");
		assertTrue(ce1 == ce1_again);

		// edge should be created
		String en2 = CyEdge.createIdentifier("a", "xx", "b");
		CyEdge ce2 = Cytoscape.getCyEdge("a", en2, "b", "pp");
		assertNotNull(ce2);

		// should create a different edge because of directedness
		String en3 = CyEdge.createIdentifier("b", "pp", "a");
		CyEdge ce3 = Cytoscape.getCyEdge("b", en3, "a", "pp");
		assertTrue(ce1 != ce3);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void testgetCyEdgeWithNodes() throws IOException {
		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/directedGraph.sif");

		Node a = Cytoscape.getCyNode("a");
		Node b = Cytoscape.getCyNode("b");
		Node c = Cytoscape.getCyNode("c", true);
		String attr = Semantics.INTERACTION;

		// test directed edges
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(b, a, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(a, a, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(a, a, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "pd", false, true));
		assertNull(Cytoscape.getCyEdge(b, a, attr, "pd", false, true));

		// test undirectedness
		assertNotNull(Cytoscape.getCyEdge(b, a, attr, "pd", false, false));

		// test non-existent edge
		assertNull(Cytoscape.getCyEdge(a, c, attr, "pp", false, true));

		// test bad attr_value
		assertNull(Cytoscape.getCyEdge(a, b, attr, "xx", false, true));

		// test create node
		assertNotNull(Cytoscape.getCyEdge(a, c, attr, "pd", true, true));

		// make sure we got the node we created
		assertNotNull(Cytoscape.getCyEdge(a, c, attr, "pd", false, true));
	}

	public void testGetCyEdgeWithBadNodeStrings() throws Exception {
		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/directedGraph.sif");
		final int numEdges = Cytoscape.getRootGraph().getEdgeCount();
		final int numNodes = Cytoscape.getRootGraph().getNodeCount();

		// valid source, target, interaction, should create a new edge
		CyEdge e1 = Cytoscape.getCyEdge("a", "missing edge name" , "b", "asdfasdf");
		assertEquals( e1.getIdentifier(), "a (asdfasdf) b" );
		assertEquals( numEdges + 1, Cytoscape.getRootGraph().getEdgeCount() );
		assertEquals( numNodes, Cytoscape.getRootGraph().getNodeCount() );

		// non-existant source, valid target, interaction, should add new node and new edge 
		CyEdge e2 = Cytoscape.getCyEdge("missing source", "missing edge name" , "b", "zz");
		assertEquals( e2.getIdentifier(), "missing source (zz) b" );
		assertEquals( numEdges + 2, Cytoscape.getRootGraph().getEdgeCount() );
		assertEquals( numNodes + 1, Cytoscape.getRootGraph().getNodeCount() );

		// valid source, non-existant target, interaction, should add new node and new edge 
		CyEdge e3 = Cytoscape.getCyEdge("a", "missing edge name" , "missing target", "zz");
		assertEquals( e3.getIdentifier(), "a (zz) missing target" );
		assertEquals( numEdges + 3, Cytoscape.getRootGraph().getEdgeCount() );
		assertEquals( numNodes + 2, Cytoscape.getRootGraph().getNodeCount() );

		// null source, valid target, interaction, should return null
		CyEdge e4 = Cytoscape.getCyEdge(null, "missing edge name" , "b", "zz");
		assertNull( e4 );

		// valid source, null target, interaction, should return null
		CyEdge e5 = Cytoscape.getCyEdge("a", "missing edge name" , null, "zz");
		assertNull( e5 );

		// final checks
		assertEquals( numEdges + 3, Cytoscape.getRootGraph().getEdgeCount() );
		assertEquals( numNodes + 2, Cytoscape.getRootGraph().getNodeCount() );
	}

	public void testSetNestedNetworkAndGetNestedNetwork() throws Exception {
		CyNode node = Cytoscape.getCyNode("nestedNode", true);
		assertNull(node.getNestedNetwork());
		CyNetwork network = Cytoscape.createNetwork("nestedTest");
		node.setNestedNetwork(network);
		assertEquals(network, node.getNestedNetwork());
		String id = network.getIdentifier();
		assertTrue(((CyNetwork)node.getNestedNetwork()).getIdentifier().equals(id));
	}
}
