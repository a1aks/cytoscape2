/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.data.webservice.ui;

import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;

import ding.view.EdgeContextMenuListener;
import ding.view.NodeContextMenuListener;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.util.HashMap;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Context menu for web service clients.
 *
 * @author kono
 * @since Cytoscape 2.6
 * @version 0.5
 *
 */
public class WebServiceContextMenu implements NodeContextMenuListener, EdgeContextMenuListener {
	private HashMap<String, String> clientMap;
	private JMenu nodeRootMenu;
	private JMenu edgeRootMenu;

	/**
	 * Creates a new WebServiceContextMenu object.
	 */
	public WebServiceContextMenu() {
		clientMap = new HashMap<String, String>();
		nodeRootMenu = new JMenu("Use Web Services");
		edgeRootMenu = new JMenu("Use Web Services");
	}

	/**
	 *  Add this menu to the node context menu.
	 *
	 * @param nodeView DOCUMENT ME!
	 * @param menu DOCUMENT ME!
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		addMenu(nodeView, menu);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeView DOCUMENT ME!
	 * @param menu DOCUMENT ME!
	 */
	public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu) {
		addMenu(edgeView, menu);
	}

	private void addMenu(Object view, JPopupMenu menu) {
		if (menu == null)
			menu = new JPopupMenu();
		else {
			// Clean up menu
			nodeRootMenu.removeAll();
			edgeRootMenu.removeAll();
		}

		List<JMenuItem> context = null;
		final List<WebServiceClient> clients = WebServiceClientManager.getAllClients();

		for (WebServiceClient client : clients) {
			if (client instanceof WebServiceClientGUI) {
				if (view instanceof NodeView)
					context = ((WebServiceClientGUI) client).getNodeContextMenuItems((NodeView) view);
				else if (view instanceof EdgeView)
					context = ((WebServiceClientGUI) client).getEdgeContextMenuItems((EdgeView) view);

				if (context != null) {
					JMenu cMenu = new JMenu(client.getDisplayName());

					for (JMenuItem menuItem : context) {
						cMenu.add(menuItem);
					}

					nodeRootMenu.add(cMenu);
					clientMap.put(client.getDisplayName(), client.getClientID());
				}
			}
		}

		if(view instanceof NodeView) {
			menu.add(this.nodeRootMenu);
			if(nodeRootMenu.getItemCount() == 0)
				nodeRootMenu.setEnabled(false);
		} else {
			menu.add(this.edgeRootMenu);
			if(edgeRootMenu.getItemCount() == 0)
				edgeRootMenu.setEnabled(false);
		}
	}
}
