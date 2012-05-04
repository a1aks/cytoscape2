// $Id: AboutPanel.java,v 1.7 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
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
package cytoscape.coreplugins.biopax.view;

import cytoscape.CytoscapeInit;
import cytoscape.coreplugins.biopax.BiopaxPlugin;
import cytoscape.coreplugins.biopax.action.LaunchExternalBrowser;


import java.awt.*;

import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


/**
 * Displays information "About this PlugIn...".
 *
 * @author Ethan Cerami
 */
public class AboutPanel extends JPanel {
	/**
	 * Constructor.
	 *
	 * @param title        PlugIn Title.
	 * @param majorVersion PlugIn Major Version.
	 * @param minorVersion PlugIn Minor Version.
	 */
	public AboutPanel(String title, int majorVersion, int minorVersion) {
		this.setLayout(new BorderLayout());

		JTextPane textPane = new JTextPane();
        textPane.setBorder(new EmptyBorder(5,5,5,5));
        textPane.setEditable(false);
		textPane.setContentType("text/html");
        textPane.addHyperlinkListener(new LaunchExternalBrowser());

        StringBuffer temp = new StringBuffer();
		temp.append("<HTML><BODY>");
        temp.append("<h2>");
        temp.append(title + ", Version:  " + majorVersion + ". " + minorVersion);
        temp.append("</h2>");
        temp.append("<P>");

		Properties cytoProps = CytoscapeInit.getProperties();

		if (cytoProps != null) {
			String proxyHost = (String) cytoProps.get(BiopaxPlugin.PROXY_HOST_PROPERTY);
			String proxyPort = (String) cytoProps.get(BiopaxPlugin.PROXY_PORT_PROPERTY);

			if ((proxyHost != null) && (proxyPort != null)) {
				temp.append("HTTP Proxy Support Enabled:  ");
				temp.append(proxyHost + " [Port:  " + proxyPort + "]");
			} else {
				temp.append("HTTP Proxy Support:  Not Enabled.");
			}
		}

		temp.append("<BR><BR>Plugin released by:  Sander Group, "
		            + "<A class=\"link\" HREF=\"http://www.cbio.mskcc.org/\">"
		            + "Computational Biology Center</A>, "
		            + "<A class=\"link\" HREF=\"http://www.mskcc.org\">Memorial Sloan-Kettering "
		            + "Cancer Center</A>.");
		temp.append("<BR><BR>For any questions concerning this plugin, please "
		            + "contact:<BR><BR><UL><LI> - Gary Bader:  baderg AT mskcc.org"
		            + "<LI> - Ethan Cerami:  cerami AT cbio.mskcc.org"
		            + "<LI> - Benjamin Gross:  grossb AT cbio.mskcc.org"
		            + "<BR><BR>This software is made available under the "
		            + "<A class=\"link\" HREF=\"http://www.gnu.org/licenses/lgpl.html\">LGPL "
		            + "(Lesser General Public License)</A>.");
		temp.append("</BODY></HTML>");
		textPane.setText(temp.toString());
		textPane.setText(temp.toString());

		JScrollPane scrollPane = new JScrollPane(textPane);
		this.add(scrollPane, BorderLayout.CENTER);
	}
}
