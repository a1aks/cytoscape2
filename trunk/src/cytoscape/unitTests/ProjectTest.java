//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import cytoscape.Project;
//-----------------------------------------------------------------------------------------
public class ProjectTest extends TestCase {
//------------------------------------------------------------------------------
public ProjectTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testAll() throws Exception {
    File projectFile = new File("sampleProject.pro");
    File directory = projectFile.getAbsoluteFile().getParentFile();
    Project project = new Project("sampleProject.pro");
    
    assertTrue( project.getProjectFilename().equals("sampleProject.pro") );
    String intName = (new File(directory, "network.sif")).getPath();
    assertTrue( project.getInteractionsFilename().equals(intName) );
    String geomName = (new File(directory, "network.gml")).getPath();
    assertTrue( project.getGeometryFilename().equals(geomName) );
    String exprName = (new File(directory, "yabba.mrna")).getPath();
    assertTrue( project.getExpressionFilename().equals(exprName) );
    assertTrue( project.getNumberOfNodeAttributeFiles() == 4 );
    String noaName = (new File(directory, "age.noa")).getPath();
    assertTrue( project.getNodeAttributeFilenames()[0].equals(noaName) );
    assertTrue( project.getNodeAttributeFilenames()[1].endsWith("commonName.noa") );
    assertTrue( project.getNodeAttributeFilenames()[2].endsWith("frillycurtainlength.noa") );
    assertTrue( project.getNodeAttributeFilenames()[3].endsWith("species.noa") );
    assertTrue( project.getNumberOfEdgeAttributeFiles() == 3 );
    String edaName = (new File(directory, "edgeancillaryname.eda")).getPath();
    assertTrue( project.getEdgeAttributeFilenames()[0].equals(edaName) );
    assertTrue( project.getEdgeAttributeFilenames()[1].endsWith("edgeweight.eda") );
    assertTrue( project.getEdgeAttributeFilenames()[2].endsWith("interaction.eda") );
    assertTrue( project.getBioDataDirectory().equals("jar://privateServer") );
    assertTrue( project.getDefaultSpeciesName().equals("Saccharomyces cerevisiae") );
    assertTrue( project.getDefaultLayoutStrategy().equals("frisbee") );
    assertTrue( project.getProjectPropsFileName().equals("special.props") );
    assertTrue( project.getProjectVizmapPropsFileName().equals("vspecial.props") );
    assertTrue( project.getCanonicalize() == false );
    assertTrue( project.getOtherArgs().length == 2 );
    assertTrue( project.getOtherArgs()[0].equals("more") );
    assertTrue( project.getOtherArgs()[1].equals("args") );
}
//-------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(ProjectTest.class));
}
//-------------------------------------------------------------------------
}

