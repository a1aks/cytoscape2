// geneInfo.java:  of BioDataServer

/** Copyright (c) 2002 Institute for Sytems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** California Institute of Technology and Japan Science and Technology
 ** Corporation have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** California Institute of Technology or the Japan Science and Technology
 ** Corporation be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** California Institute of Technology and/or Japan Science and Technology
 ** Corporation have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.BindingPair;
import cytoscape.data.BindingPairXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class geneInfo {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length == 0) {
    System.out.println ("geneInfo <geneName> [-b (show binding pairs)]");
    System.exit (0);
     }

  String geneName = args [0];
  boolean showBindingPairs = false;

  if (args.length >= 2)
    showBindingPairs = true;
       
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);
  System.out.println (server.getGeneInfo (geneName));
  //System.out.println (server.getMolecularFunction (geneName));
  //if (showBindingPairs)
  //  System.out.println (server.getBindingPairs (geneName));

} // main
//------------------------------------------------------------------------------
} // geneInfo

