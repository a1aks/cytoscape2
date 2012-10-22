// $Id: NetworkUtil.java,v 1.12 2007/05/01 15:56:45 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath2.util;

// imports 

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import ding.view.NodeContextMenuListener;
import giny.view.NodeView;
import cytoscape.coreplugins.biopax.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Manages network neighborhood map - node context menus
 *
 * @author Benjamin Gross.
 */
public class NetworkListener implements PropertyChangeListener, NodeContextMenuListener {

    /**
     * Context menu title.
     */
    private static final String CONTEXT_MENU_TITLE = "View network neighborhood map";

    /**
     * Context menu item command.
     */
    private static final String PC_WEB_SERVICE_URL = "/webservice.do?version=3.0&cmd=get_neighbors&q=";

    /**
     * Property change listener - to get network/network view destroy events.
     *
     * @param event PropertyChangeEvent
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getPropertyName().equals(Cytoscape.NETWORK_LOADED)) {
            CyNetwork cyNetwork = (CyNetwork) ((Object[]) event.getNewValue())[0];
            if (cyNetwork != null && BioPaxUtil.isBioPaxNetwork(cyNetwork)) {
                // setup the context menu
                CyNetworkView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
                if (view != null) view.addNodeContextMenuListener(this);
            }
        }
    }

    /**
     * Our implementation of NodeContextMenuListener.addNodeContextMenuItems(..).
     */
    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {

        // check if we have already added menu item
        if (contextMenuExists(menu)) return;

        // setup refs to get network attributes
        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        CyNetworkView view = Cytoscape.getCurrentNetworkView();

        // grab web services url from network attributes
        String webServicesURL = networkAttributes.getStringAttribute(view.getNetwork().getIdentifier(),
                "biopax.web_services_url");
        if (webServicesURL.startsWith("http://")) {
            webServicesURL = webServicesURL.substring(7);
        }

        // grab data sources from network attributes - already encoded
        String dataSources = networkAttributes.getStringAttribute(view.getNetwork().getIdentifier(),
                "biopax.data_sources");

        // generate menu url
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        CyNode cyNode = (CyNode) nodeView.getNode();
        String biopaxID = nodeAttributes.getStringAttribute(cyNode.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_RDF_ID);
        biopaxID = biopaxID.replace("CPATH-", "");
        String neighborhoodParam = "Neighborhood: " + nodeAttributes.getStringAttribute(cyNode.getIdentifier(), BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);

        // encode some parts of the url
        try {
            neighborhoodParam = URLEncoder.encode(neighborhoodParam, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // if exception occurs leave encoded string, but cmon, utf-8 not supported ??
            // anyway, at least encode spaces, and commas (data sources)
            neighborhoodParam = neighborhoodParam.replaceAll(" ", "%20");
        }

        final String urlString = "http://127.0.0.1:27182/" + webServicesURL +
                PC_WEB_SERVICE_URL + biopaxID + "&neighborhood_title=" + neighborhoodParam +
                "&data_source=" + dataSources;

        // add new menu item
        JMenuItem item = new JMenuItem(new AbstractAction(CONTEXT_MENU_TITLE) {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            URL url = new URL(urlString);
                            url.getContent();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        menu.add(item);
    }

    /**
     * Method checks if we have already added a neighborhood map context menu
     * to given menu.
     *
     * @param menu JPopupMenu
     * @return boolean
     */
    private boolean contextMenuExists(JPopupMenu menu) {

        for (MenuElement element : menu.getSubElements()) {
            Component component = element.getComponent();
            if (component instanceof JMenuItem) {
                String text = ((JMenuItem) component).getText();
                if (text != null && text.equals(CONTEXT_MENU_TITLE)) return true;
            }
        }

        // outta here
        return false;
    }

}