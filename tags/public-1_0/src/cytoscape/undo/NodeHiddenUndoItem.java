//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// NodeHiddenUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import java.util.*;
import y.base.*;

/**
 * Supports undo for node hiding using UndoableGraphHider.
 */
public class NodeHiddenUndoItem implements UndoItem {

    Node node;
    Set edges;
    UndoableGraphHider hider;

    NodeHiddenUndoItem ( UndoableGraphHider hider, Node node, Set edges) {
	this.node  = node;
        this.edges = edges;
	this.hider = hider;
    }

    /**
     * Removes the node from the graph
     */
    public boolean undo() {
	hider.undoHide(node);
        for (Iterator si = edges.iterator(); si.hasNext(); ) {
            Edge e = (Edge)si.next();
            hider.undoHide(e);
        }
	return true;
    }

    /**
     * Re-inserts the node into the graph
     */
    public boolean redo() {
	hider.redoHide(node);
        for (Iterator si = edges.iterator(); si.hasNext(); ) {
            Edge e = (Edge)si.next();
            hider.redoHide(e);
        }
	return true;
    }
}


