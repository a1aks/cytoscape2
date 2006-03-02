
/*
  File: ImportBioDataServerAction.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute of Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.util.swing.BioDataServerWizard;

/**
 * Action allows the loading of a BioDataServer from the gui.
 * 
 * added by dramage 2002-08-20
 * 
 * Mod by kono 2005-09-16
 * Added new biodataserver wizard
 */
public class ImportBioDataServerAction extends CytoscapeAction {

	static final int SUCCESS = 0;
	
	BioDataServerWizard wiz;
	
	public ImportBioDataServerAction() {
		super("Ontology Wizard...");
		setPreferredMenu("File.Import.Ontology");
	}

	/*
	 *  (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		// Get the file name
		final String name;
		int wizardResult;

		// Create the wizard to choose biodataserver
		wiz = new BioDataServerWizard();
		wizardResult = wiz.show();
		if( wizardResult == SUCCESS ) {
			System.out.println( "Succesfully loaded Data Server.");
		}
	}
}
