/*
 * Copyright (c) 2006, 2007, 2008, 2010, Max Planck Institute for Informatics, Saarbruecken, Germany.
 *
 * This file is part of NetworkAnalyzer.
 * 
 * NetworkAnalyzer is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * NetworkAnalyzer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with NetworkAnalyzer. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkStats;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.io.StatsSerializer;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.AnalysisDialog;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.Utils;

/**
 * Action handler for the menu item &quot;Load Network Statistics&quot;.
 * 
 * @author Yassen Assenov
 */
public class LoadNetstatsAction extends CytoscapeAction {

	/**
	 * Initializes a new instance of <code>LoadNetstatsAction</code>.
	 */
	public LoadNetstatsAction() {
		super(Messages.AC_LOAD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			final CytoscapeDesktop desktop = Cytoscape.getDesktop();
			final JFileChooser dialog = AnalysisDialog.netstatsDialog;
			final int openIt = dialog.showOpenDialog(desktop);
			if (openIt == JFileChooser.APPROVE_OPTION) {
				openNetstats(dialog.getSelectedFile());
				Utils.removeSelectedFile(dialog);
			}
			if (openIt == JFileChooser.ERROR_OPTION) {
				Utils.showErrorBox(desktop, Messages.DT_GUIERROR, Messages.SM_GUIERROR);
			}
		} catch (InnerException ex) {
			// NetworkAnalyzer internal error
			CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
		}
	}

	/**
	 * Opens the given network statistics file and visualizes its contents in an analysis dialog.
	 * <p>
	 * In case the file could not be opened, or if it contains invalid data, an informative error box is
	 * displayed.
	 * </p>
	 * 
	 * @param aFile
	 *            Network statistics file to be open.
	 */
	public static void openNetstats(File aFile) {
		final CytoscapeDesktop desktop = Cytoscape.getDesktop();
		try {
			final NetworkStats stats = StatsSerializer.load(aFile);
			final AnalysisDialog d = new AnalysisDialog(desktop, stats, null);
			d.setVisible(true);
		} catch (IOException ex) {
			// FileNotFoundException, IOException
			Utils.showErrorBox(desktop, Messages.DT_IOERROR, Messages.SM_IERROR);
		} catch (NullPointerException ex) {
			Utils.showErrorBox(desktop, Messages.DT_WRONGDATA, Messages.SM_WRONGDATAFILE);
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 5924903386374164549L;
}
