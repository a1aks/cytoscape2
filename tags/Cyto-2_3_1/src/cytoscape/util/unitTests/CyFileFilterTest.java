
package cytoscape.util.unitTests;

import java.util.Set;

import cytoscape.data.readers.unitTests.FileImporterTest;

import junit.framework.TestCase;
import cytoscape.util.CyFileFilter;

/**
 * Tests Implementation of CyFileFilter.
 *
 * TODO:  Add to DataSuite
 */
public class CyFileFilterTest extends TestCase {
	String EXT1 = "goo";
	String EXT2 = "sif";
	String EXT3 = "doc";
	String DESC1 = "If google had a file type";
	String DESC2 = "Interactions file type";
	String DESC3 = "Microsoft Word file Type";
	String DESC4 = "All file types";
	String DUMMY_NATURE = "Human Designed";
    String[] FILTERS = {EXT1, EXT2};
    CyFileFilter cff1;
	CyFileFilter cff2;
	CyFileFilter cff3;
	CyFileFilter cff4;
	CyFileFilter cff5;
	CyFileFilter cff6;
	CyFileFilter cff7;

	public void setUp() throws Exception {
		cff1 = new CyFileFilter();
		cff2 = new CyFileFilter(EXT1);
		cff3 = new CyFileFilter(EXT1, DESC1);
		cff4 = new CyFileFilter(FILTERS);
		cff5 = new CyFileFilter(FILTERS, DESC4);
		cff6 = new CyFileFilter(EXT1, DESC1, DUMMY_NATURE);
		cff7 = new CyFileFilter(FILTERS, DESC4, DUMMY_NATURE);
	}
	
	public void tearDown() throws Exception {
		CyFileFilter cff1 = null;
		CyFileFilter cff2 = null;
		CyFileFilter cff3 = null;
		CyFileFilter cff4 = null;
		CyFileFilter cff5 = null;
		CyFileFilter cff6 = null;
		CyFileFilter cff7 = null;
	}

	public void testGetExtension() throws Exception
	{
		//Error could result if getExtension includes the preceeding period
		String test = cff1.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		test = cff2.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		test = cff3.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		test = cff4.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		test = cff5.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		test = cff6.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		test = cff7.getExtension("Google.goo");
		assertEquals(EXT1, test);
		
		boolean exists = cff1.isExtensionListInDescription();
		assertTrue(exists);
		cff1.setExtensionListInDescription(false);
		exists = cff1.isExtensionListInDescription();
		assertFalse(exists);
		
	}
	
	public void testConstructors() {
		//cff1 (if no filters always accept)
		boolean exists = cff1.accept("hi." + EXT2);
		assertTrue(exists);		
		exists = cff1.accept("hi." + EXT1);
		assertTrue(exists);
		
		String description = cff1.getDescription();
		assertEquals(description, "(*.*)");
		
		String nature = cff1.getFileNature();
		assertEquals(nature, "UNKNOWN");
		
		
		//cff2
		exists = cff2.accept("hi." + EXT2);
		assertFalse(exists);
		exists = cff2.accept("hi." + EXT1);
		assertTrue(exists);
		
		description = cff2.getDescription();
		assertEquals(description, "(*.goo)");
		nature = cff2.getFileNature();
		assertEquals(nature, "UNKNOWN");
		
		
		//cff3
		exists = cff3.accept("hi." + EXT2);
		assertFalse(exists);
		exists = cff3.accept("hi." + EXT1);
		assertTrue(exists);
		
		cff3.setExtensionListInDescription(false);
		description = cff3.getDescription();
		assertEquals(description, DESC1);
		nature = cff3.getFileNature();
		assertEquals(nature, "UNKNOWN");
		
		//cff4
		exists = cff4.accept("hi." + EXT3);
		assertFalse(exists);
		exists = cff4.accept("hi." + EXT2);
		assertTrue(exists);
		exists = cff4.accept("hi." + EXT1);
		assertTrue(exists);
		
		Set extensions = cff4.getExtensionSet();
		int size = extensions.size();
		assertEquals(2, size);
		
		description = cff4.getDescription();
		assertEquals("(*.goo, *.sif)", description);
		nature = cff4.getFileNature();
		assertEquals(nature, "UNKNOWN");
		
		//cff5
		exists = cff5.accept("hi." + EXT3);
		assertFalse(exists);
		exists = cff5.accept("hi." + EXT2);
		assertTrue(exists);
		exists = cff5.accept("hi." + EXT1);
		assertTrue(exists);
		
		extensions = cff5.getExtensionSet();
		size = extensions.size();
		assertEquals(2, size);
		
		cff5.setExtensionListInDescription(false);
		description = cff5.getDescription();
		cff5.setExtensionListInDescription(false);
		assertEquals(description, DESC4);
		nature = cff5.getFileNature();
		assertEquals(nature, "UNKNOWN");
		
		//cff6
		exists = cff6.accept("hi." + EXT2);
		assertFalse(exists);
		exists = cff6.accept("hi." + EXT1);
		assertTrue(exists);
		
		cff6.setExtensionListInDescription(false);
		description = cff6.getDescription();
		assertEquals(description, DESC1);
		nature = cff6.getFileNature();
		assertEquals(nature, DUMMY_NATURE);
		
		//cff7
		exists = cff7.accept("hi." + EXT3);
		assertFalse(exists);
		exists = cff7.accept("hi." + EXT2);
		assertTrue(exists);
		exists = cff7.accept("hi." + EXT1);
		assertTrue(exists);
		
		extensions = cff7.getExtensionSet();
		size = extensions.size();
		assertEquals(2, size);
		
		cff7.setExtensionListInDescription(false);
		description = cff7.getDescription();
		
		assertEquals(description, DESC4);
		nature = cff7.getFileNature();
		assertEquals(nature, DUMMY_NATURE);
	}
	
	public void testAddExtension() {
		//cff1
		cff1.addExtension(EXT3);
		boolean exists = cff1.accept("hi." + EXT3);
		assertTrue(exists);
		
		//cff2
		exists = cff2.accept("hi." + EXT3);
		assertFalse(exists);
		cff2.addExtension(EXT3);
		exists = cff2.accept("hi." + EXT3);
		assertTrue(exists);
		
		//cff3
		exists = cff3.accept("hi." + EXT3);
		assertFalse(exists);
		cff3.addExtension(EXT3);
		exists = cff3.accept("hi." + EXT3);
		assertTrue(exists);
		
		//cff4
		exists = cff4.accept("hi." + EXT3);
		assertFalse(exists);
		cff4.addExtension(EXT3);
		exists = cff4.accept("hi." + EXT3);
		assertTrue(exists);
		
		//cff5
		exists = cff5.accept("hi." + EXT3);
		assertFalse(exists);
		cff5.addExtension(EXT3);
		exists = cff5.accept("hi." + EXT3);
		assertTrue(exists);
		
		//cff6
		exists = cff6.accept("hi." + EXT3);
		assertFalse(exists);
		cff6.addExtension(EXT3);
		exists = cff6.accept("hi." + EXT3);
		assertTrue(exists);
		
		//cff7
		exists = cff7.accept("hi." + EXT3);
		assertFalse(exists);
		cff7.addExtension(EXT3);
		exists = cff7.accept("hi." + EXT3);
		assertTrue(exists);	
		
	}
	
	public void testSets() {
		String description = cff1.getDescription();
		assertEquals(description, "(*.*)");
		String nature = cff1.getFileNature();
		assertEquals(nature, "UNKNOWN");
		
		cff1.setDescription(DESC1);
		cff1.setExtensionListInDescription(false);
		cff1.setFileNature(DUMMY_NATURE);
		
		description = cff1.getDescription();
		assertEquals(DESC1, description);
		nature = cff1.getFileNature();
		assertEquals(DUMMY_NATURE, nature);
		
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(CyFileFilterTest.class);
	}

}
