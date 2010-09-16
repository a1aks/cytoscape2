
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

package filter.cytoscape;

import cytoscape.*;
import cytoscape.CyNetwork;

import cytoscape.data.*;

import cytoscape.util.*;

import cytoscape.view.*;

import filter.model.*;

import filter.view.*;

import java.awt.event.*;

import javax.swing.*;


/**
 *
 */
public class FilterManagerAction extends CytoscapeAction {
	protected FilterView filterView;
	protected JFrame frame;

	/**
	 * Creates a new FilterManagerAction object.
	 */
	public FilterManagerAction() {
		super("Edit Filters");
		setPreferredMenu("Select");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_E,
		                    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if (filterView == null) {
			filterView = new FilterView();
		}

		if (frame == null) {
			frame = new JFrame("Edit Filters");
			frame.getContentPane().add(filterView);
			frame.pack();
		}

		frame.setVisible(true);
	}
}
