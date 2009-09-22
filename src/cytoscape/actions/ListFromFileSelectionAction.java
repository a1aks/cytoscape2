/*
  File: ListFromFileSelectionAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.Semantics;

import cytoscape.util.*;

import cytoscape.view.CyNetworkView;

import giny.view.*;

import java.awt.event.ActionEvent;

import java.io.*;

import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class ListFromFileSelectionAction extends CytoscapeAction {
	/**
	 * Creates a new ListFromFileSelectionAction object.
	 */
	public ListFromFileSelectionAction() {
		super("From File...");
		setPreferredMenu("Select.Nodes");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		boolean cancelSelectionAction = !selectFromFile();
		Cytoscape.getCurrentNetworkView().updateView();
	}

	private boolean selectFromFile() {
		// get the file name
		final String name;

		try {
			name = FileUtil.getFile("Load Gene Selection File", FileUtil.LOAD).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return false;
		}

		CyNetwork network = Cytoscape.getCurrentNetwork();
        List fileNodes = new ArrayList();

		try {
			BufferedReader bin = null;

			try {
				bin = new BufferedReader(new FileReader(name));

				String s;

				while ((s = bin.readLine()) != null) {
					String trimName = s.trim();

					if (trimName.length() > 0) {
						fileNodes.add(trimName);
					}
				}
            }
            finally {
                if (bin != null) {
                    bin.close();
                }
            }

			// loop through all the node of the graph
			// selecting those in the file
			List nodeList = network.nodesList();
			giny.model.Node[] nodes = (giny.model.Node[]) nodeList.toArray(new giny.model.Node[0]);

			for (int i = 0; i < nodes.length; i++) {
				giny.model.Node node = nodes[i];
				boolean select = false;
				String canonicalName = node.getIdentifier();
				List synonyms = Semantics.getAllSynonyms(canonicalName, network);

				for (Iterator synI = synonyms.iterator(); synI.hasNext();) {
					if (fileNodes.contains((String) synI.next())) {
						select = true;

						break;
					}
				}

				if (select) {
					//CyNetworkView view = Cytoscape.getCurrentNetworkView();
					//NodeView nv = view.getNodeView(node.getRootGraphIndex());
					//nv.setSelected(true);
					network.setSelectedNodeState(node, true);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Reading \"" + name + "\"",
			                              JOptionPane.ERROR_MESSAGE);

			return false;
		}

		return true;
	}

    public void menuSelected(MenuEvent e) {
        enableForNetwork();
    }
}
