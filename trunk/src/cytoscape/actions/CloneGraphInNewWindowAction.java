//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;

public class CloneGraphInNewWindowAction extends CytoscapeAction {

  public CloneGraphInNewWindowAction () {
    super("Clone current network");
    setPreferredMenu( "File.New.Network" );
  }

  public void actionPerformed(ActionEvent e) {

    CyNetwork current_network = Cytoscape.getCurrentNetwork();
    CyNetwork new_network = Cytoscape.createNetwork( current_network.getNodeIndicesArray(),
                                                     current_network.getEdgeIndicesArray(),
                                                     current_network.getTitle()+" copy");
    new_network.setExpressionData( current_network.getExpressionData() );

    String title = " selection";
    Cytoscape.createNetworkView( new_network, title );

  }

}

