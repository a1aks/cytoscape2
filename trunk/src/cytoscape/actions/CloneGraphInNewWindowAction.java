//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.actions;

import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.util.CytoscapeAction;

public class CloneGraphInNewWindowAction extends CytoscapeAction {
    
  public CloneGraphInNewWindowAction () {
    super("Whole graph");
    setPreferredMenu( "Select.To New Window" );
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

