
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

package filter.view;

import filter.model.*;

import javax.swing.*;
import javax.swing.tree.*;


//import giny.model.*;
/**
 *
 */
public class FilterView extends JPanel {
	//FilterEditorPanel filterEditorPanel;
	FilterListPanel filterListPanel;

	/**
	 * Creates a new FilterView object.
	 */
	public FilterView() {
		super();

		initialize();
	}

	protected void initialize() {
		//filterEditorPanel = new FilterEditorPanel();
		filterListPanel = new FilterListPanel();
		//JSplitPane pane0 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, filterEditorPanel, filterListPanel );

		//add( pane0 );
		add(filterListPanel);

		//filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener( filterEditorPanel );
		//FilterManager.defaultManager().addEditor( new DefaultFilterEditor() );
		//FilterManager.defaultManager().addEditor( new FilterTreeEditor() );
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").startsWith("Windows")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// TODO: Error handling.
				System.err.println("Hey. Error loading L&F: on Windows");

				// TODO: REMOVE
				// e.printStackTrace();
			}
		} else {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} catch (Exception e) {
				// TODO: Error handling.
				System.err.println("Hey. Error loading L&F: on NOT Windows");

				// TODO: REMOVE
				//e.printStackTrace();
			}
		}

		JFrame frame = new JFrame("Filters");
		frame.getContentPane().add(new FilterView());
		frame.pack();
		frame.setVisible(true);
	}
}
