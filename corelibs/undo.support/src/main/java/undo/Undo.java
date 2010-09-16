/*
  File: Undo.java

  Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package undo;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;


/**
 * Rudimentary support for undo.  To post an edit to the manager, call
 * Undo.getUndoableEditSupport().postEdit(yourEdit).  To handle the edits,
 * you should extend this class and write actions, etc. that use the 
 * UndoManager.
 */
public class Undo {

	private static UndoManager m_undoManager;
	private static UndoableEditSupport m_undoSupport; 

	/**
	 * Returns the UndoManager. To preserve encapsulation and to prevent
	 * the wrong hands for mucking with your edit stack, don't make this
	 * public!  Rather, extend the class and use this method with the 
	 * class and package to set up actions, etc..
	 * @return the UndoManager used for managing the edits.
	 */
	protected static UndoManager getUndoManager() {
		return m_undoManager;
	}

	/**
	 * Use this method to get the UndoableEditSupport which you should use
	 * to post edits: Undo.getUndoableEditSupport().postEdit(yourEdit).
	 * @return the UndoableEditSupport used for posting edits. 
	 */
	public static UndoableEditSupport getUndoableEditSupport() {
		return m_undoSupport;
	}

	static {
		m_undoManager = new UndoManager();
		m_undoSupport = new UndoableEditSupport();
		m_undoSupport.addUndoableEditListener( m_undoManager );
	}
}

