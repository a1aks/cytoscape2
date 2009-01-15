/*
  File: CyUndo.java

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
package cytoscape.util.undo;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;
import cytoscape.view.CytoscapeDesktop;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import undo.Undo;

/**
 * A tiny class for supporting undo in the Cytoscape context.  If you
 * want to post an edit, use CyUndo.getUndoableEditSupport().postEdit(yourEdit).
 */
public class CyUndo extends Undo {

	protected static UndoManager undoManager; 
	protected static CyLogger logger = CyLogger.getLogger(CyUndo.class);

	static {
		undoManager = getUndoManager();
		undoManager.setLimit(getLimit());
		new UndoMonitor();
	}

	private static int getLimit() {
		int lim;
		try { 
			lim = Integer.parseInt( CytoscapeInit.getProperties().getProperty("undo.limit") );
		} catch ( Exception e ) {
			logger.warn("Non-integer value for property 'undo.limit'", e);
			lim = 10;
		}

		if ( lim < 0 )
			lim = 10;

		return lim;
	}
}

