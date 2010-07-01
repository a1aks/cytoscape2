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

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;


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
		final boolean cancelSelectionAction = !selectFromFile();
		Cytoscape.getCurrentNetworkView().updateView();
	}

	private boolean selectFromFile() {
		final String fileName;

		try {
			fileName = FileUtil.getFile("Load Gene Selection File", FileUtil.LOAD).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return false;
		}

		CyNetwork network = Cytoscape.getCurrentNetwork();
		final HashSet<String> fileNodes = new HashSet<String>();

		try {
			BufferedReader bin = null;

			try {
				bin = new BufferedReader(new FileReader(fileName));

				String s;

				while ((s = bin.readLine()) != null) {
					String trimName = s.trim();

					if (trimName.length() > 0)
						fileNodes.add(trimName);
				}
			}
			finally {
				if (bin != null) {
					bin.close();
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Reading \"" + fileName + "\"",
			                              JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (fileNodes.size() == 0) {
			JOptionPane.showMessageDialog(null, "No nodes read from \"" + fileName + "\"!", "Warning!",
						      JOptionPane.WARNING_MESSAGE);
			return false;
		}

		// loop through all the node of the graph
		// selecting those in the file
		List nodeList = network.nodesList();
		giny.model.Node[] nodes = (giny.model.Node[]) nodeList.toArray(new giny.model.Node[0]);

		int selectCount = 0;
		for (int i = 0; i < nodes.length; i++) {
			giny.model.Node node = nodes[i];
			boolean selected = false;
			String canonicalName = node.getIdentifier();
			List synonyms = Semantics.getAllSynonyms(canonicalName, network);

			for (Iterator synI = synonyms.iterator(); synI.hasNext();) {
				if (fileNodes.contains((String) synI.next())) {
					selected = true;
					++selectCount;
					break;
				}
			}

			if (selected)
				network.setSelectedNodeState(node, true);
		}

		if (selectCount == 0) {
			JOptionPane.showMessageDialog(null, "No nodes listed in \"" + fileName + "\" were found in the current network view!",
			                              "Information",
			                              JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		return true;
	}

	public void menuSelected(MenuEvent e) {
		enableForNetwork();
	}
}
