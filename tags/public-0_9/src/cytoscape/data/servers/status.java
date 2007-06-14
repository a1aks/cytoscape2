// status.java:  of BioDataServer
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.BindingPair;
import cytoscape.data.readers.BindingPairXmlReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class status {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  String serverShortName = "biodata";
  if (args.length == 1)
    serverShortName = args [0];

  String serverName = "rmi://localhost/" + serverShortName;
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // status