// AnnotationAndOntologyFullTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.annotation.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
//------------------------------------------------------------------------------
/**
 * test the Annotation class
 */
public class AnnotationAndOntologyFullTest extends TestCase {


//------------------------------------------------------------------------------
public AnnotationAndOntologyFullTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
/**
 * an ad hoc test which pinpointed a bug in the following term in my xml version 
 * of kegg pathway ontology.  i had reversed the 'id' and 'isa' values.
 *  
 *  <ontologyTerm>
 *    <name> Unknown Metabolism of Other Amino Acids Subtype </name>
 *    <id> 80106 </id>
 *    <isa> 80006 </isa>    
 *  </ontologyTerm>
 * 
 *  80006 is 'Metabolism of Other Amino Acids'
 * this reversal caused halo's VNG0606G to be described as
 *
 * VNG0606G:   90001  80005  272  
 * VNG0606G:   80006  450  
 * VNG0606G:   90001  80002  920  
 *
 * rather than
 *
 * VNG0606G:  90001  80005  272  
 * VNG0606G:  90001  80006  450  
 * VNG0606G:  90001  80002  920  
 *
 * with my xml bug, the 1st level categorization of annotation #2 is 80006, not 90001
 * 
 */
public void testHaloKegg () throws Exception
{ 
  System.out.println ("testHaloKegg");
  String filename = "../../kegg/haloMetabolicPathway.xml";
  AnnotationXmlReader reader = new AnnotationXmlReader (new File (filename));
  Annotation keggHalo = reader.getAnnotation ();
  System.out.println (keggHalo);
  String orf = "VNG0606G";
  int [] terms = keggHalo.getClassifications (orf);

  Ontology ontology = keggHalo.getOntology ();
  assertTrue (terms.length == 3);
  for (int i=0; i < terms.length; i++) {
    int [][] paths = ontology.getAllHierarchyPaths (terms [i]);
    System.out.print (orf + ": " + terms [i] + " paths: " + paths.length + ":  ");
    for (int p=0; p < paths.length; p++) {
      assertTrue (paths[p].length == 3);
      for (int q=0; q < paths [p].length; q++)
        System.out.print (paths [p][q] + "  ");
      } // for p
      System.out.println ();
    } // for i

} // testHaloKegg
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (AnnotationAndOntologyFullTest.class));
}
//------------------------------------------------------------------------------
} // AnnotationAndOntologyFullTest
