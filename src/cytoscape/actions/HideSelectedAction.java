//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import giny.model.*;
import giny.view.*;
import java.util.*;



import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class HideSelectedAction extends AbstractAction  {

    NetworkView networkView;

    public HideSelectedAction(NetworkView networkView) {
        super ();
        this.networkView = networkView;

    }
    

    public void actionPerformed (ActionEvent e) {
	if (networkView.getCytoscapeObj().getConfiguration().isYFiles()) {    
	  String callerID = "HideSelectedAction.actionPerformed";
	  networkView.getNetwork().beginActivity(callerID);
	  GraphUtils.hideSelectedNodes( networkView.getNetwork().getGraph(),
					networkView.getGraphHider() );
	  GraphUtils.hideSelectedEdges( networkView.getNetwork().getGraph(),
					networkView.getGraphHider() );
	  networkView.redrawGraph(false, false);
	  networkView.getNetwork().endActivity(callerID);
	}
	else { // using giny
		
			GinyUtils.hideSelectedNodes(networkView.getView());
			GinyUtils.hideSelectedEdges(networkView.getView());
		
	}//!Yfiles
			
		
    }//action performed

}

