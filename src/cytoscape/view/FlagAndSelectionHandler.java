//---------------------------------------------------------------------------
//  $Revision$ 
//  $Date$
//  $Author$
//---------------------------------------------------------------------------
package cytoscape.view;
//---------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import giny.model.*;
import giny.view.*;

import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEventListener;
import cytoscape.data.FlagEvent;
//---------------------------------------------------------------------------
/**
 * This class synchronizes the flagged status of nodes and edges as held by a
 * FlagFilter object of a network with the selection status of the corresponding
 * node and edge views in a GraphView. An object will be selected in the view
 * iff the matching object is flagged in the FlagFilter.
 */
public class FlagAndSelectionHandler implements FlagEventListener, GraphViewChangeListener {
    
    FlagFilter flagFilter;
    GraphView view;
    
    /**
     * Standard constructor takes the flag filter and the view that should be
     * synchronized. It is assumed that the two objects are synchronized when
     * supplied to this constructor.
     */
    public FlagAndSelectionHandler(FlagFilter flagFilter, GraphView view) {
        this.flagFilter = flagFilter;
        flagFilter.addFlagEventListener(this);
        this.view = view;
        view.addGraphViewChangeListener(this);
    }
    
    /**
     * Responds to selection events from the view by setting the matching flagged
     * state in the FlagFilter object.
     */
    public void graphViewChanged(GraphViewChangeEvent event) {
        //GINY bug: the event we get frequently has the correct indices
        //but incorrect Node and Edge objects. For now we get around this
        //by converting indices to graph objects ourselves
        GraphView source = (GraphView)event.getSource();
        RootGraph rootGraph = source.getGraphPerspective().getRootGraph();
        if (event.isNodesSelectedType()) {
            //Node[] selNodes = event.getSelectedNodes();
            //List selList = Arrays.asList(selNodes);
            int[] selIndices = event.getSelectedNodeIndices();
            List selList = new ArrayList();
            for (int index = 0; index < selIndices.length; index++) {
                Node node = rootGraph.getNode(selIndices[index]);
                selList.add(node);
            }
            flagFilter.setFlaggedNodes(selList, true);
        } else if (event.isNodesUnselectedType()) {
            //Node[] unselNodes = event.getUnselectedNodes();
            //List unselList = Arrays.asList(unselNodes);
            int[] unselIndices = event.getUnselectedNodeIndices();
            List unselList = new ArrayList();
            for (int index = 0; index < unselIndices.length; index++) {
                Node node = rootGraph.getNode(unselIndices[index]);
                unselList.add(node);
            }
            flagFilter.setFlaggedNodes(unselList, false);
        } else if (event.isEdgesSelectedType()) {
            //Edge[] selEdges = event.getSelectedEdges();
            //List selList = Arrays.asList(selEdges);
            int[] selIndices = event.getSelectedEdgeIndices();
            List selList = new ArrayList();
            for (int index = 0; index < selIndices.length; index++) {
                Edge edge = rootGraph.getEdge(selIndices[index]);
                selList.add(edge);
            }
            flagFilter.setFlaggedEdges(selList, true);
        } else if (event.isEdgesUnselectedType()) {
            //Edge[] unselEdges = event.getUnselectedEdges();
            //List unselList = Arrays.asList(unselEdges);
            int[] unselIndices = event.getUnselectedEdgeIndices();
            List unselList = new ArrayList();
            for (int index = 0; index < unselIndices.length; index++) {
                Edge edge = rootGraph.getEdge(unselIndices[index]);
                unselList.add(edge);
            }
            flagFilter.setFlaggedEdges(unselList, false);
        }
    }
    
    /**
     * Responds to events indicating a change in the flagged state of one or more
     * nodes or edges. Sets the corresponding selection state for views of those
     * objects in the graph view.
     */
    public void onFlagEvent(FlagEvent event) {
        if (event.getTargetType() == FlagEvent.SINGLE_NODE) {//single node
            setNodeSelected( (Node)event.getTarget(), event.getEventType() );
        } else if (event.getTargetType() == FlagEvent.SINGLE_EDGE) {//single edge
            setEdgeSelected( (Edge)event.getTarget(), event.getEventType() );
        } else if (event.getTargetType() == FlagEvent.NODE_SET) {//multiple nodes
            Set nodeSet = (Set)event.getTarget();
            for (Iterator iter = nodeSet.iterator(); iter.hasNext(); ) {
                Node node = (Node)iter.next();
                setNodeSelected( node, event.getEventType() );
            }
        } else if (event.getTargetType() == FlagEvent.EDGE_SET) {//multiple edges
            Set edgeSet = (Set)event.getTarget();
            for (Iterator iter = edgeSet.iterator(); iter.hasNext(); ) {
                Edge edge = (Edge)iter.next();
                setEdgeSelected( edge, event.getEventType() );
            }
        } else {//unexpected target type
            return;
        }
    }
    
    /**
     * Helper method to set selection for a node view.
     */
    private void setNodeSelected(Node node, boolean selectOn) {
        NodeView nodeView = view.getNodeView(node);
        if (nodeView == null) {return;} //sanity check
        //Giny fires a selection event even if there's no change in state
        //we trap this by only requesting a selection if there's a change
        if (nodeView.isSelected() != selectOn) {
            nodeView.setSelected(selectOn);
        }
    }
    
    /**
     * Helper method to set selection for an edg view.
     */
    private void setEdgeSelected(Edge edge, boolean selectOn) {
        EdgeView edgeView = view.getEdgeView(edge);
        if (edgeView == null) {return;} //sanity check
        //Giny fires a selection event even if there's no change in state
        //we trap this by only requesting a selection if there's a change
        if (edgeView.isSelected() != selectOn) {
            edgeView.setSelected(selectOn);
        }
    }
}

