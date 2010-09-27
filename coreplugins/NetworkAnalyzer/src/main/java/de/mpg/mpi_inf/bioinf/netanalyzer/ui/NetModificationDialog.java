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

package de.mpg.mpi_inf.bioinf.netanalyzer.ui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;

import cytoscape.CyNetwork;
import cytoscape.util.OpenBrowser;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;

/**
 * Dialog for selecting networks on which to apply analysis or a basic modification.
 * 
 * @author Yassen Assenov
 */
public class NetModificationDialog extends NetworkListDialog implements ActionListener {

	/**
	 * Initializes a new instance of <code>NetworkModificationDialog</code> with a modification warning.
	 * 
	 * @param aOwner
	 *            The <code>Frame</code> from which this dialog is displayed.
	 * @param aTitle
	 *            Window's title.
	 * @param aLabel
	 *            Label to be displayed on top of the network list.
	 * @param aHelpURL
	 *            URL of the page to be displayed when the user clicks on the &quot;Help&quot; button.
	 * 
	 * @throws HeadlessException
	 *             If <code>GraphicsEnvironment.isHeadless()</code> returns <code>true</code>.
	 */
	public NetModificationDialog(Frame aOwner, String aTitle, String aLabel, String aHelpURL)
			throws HeadlessException {
		this(aOwner, aTitle, aLabel, aHelpURL, true);
	}

	/**
	 * Initializes a new instance of <code>NetworkModificationDialog</code>.
	 * 
	 * @param aOwner
	 *            The <code>Frame</code> from which this dialog is displayed.
	 * @param aTitle
	 *            Window's title.
	 * @param aLabel
	 *            Label to be displayed on top of the network list.
	 * @param aHelpURL
	 *            URL of the page to be displayed when the user clicks on the &quot;Help&quot; button.
	 * @param aWarning
	 *            Flag indicating if a modification warning must be displayed. The text of the modification
	 *            warning is {@link Messages#SM_NETMODIFICATION}.
	 * 
	 * @throws HeadlessException
	 *             If <code>GraphicsEnvironment.isHeadless()</code> returns <code>true</code>.
	 */
	public NetModificationDialog(Frame aOwner, String aTitle, String aLabel, String aHelpURL, boolean aWarning)
			throws HeadlessException {
		super(aOwner, aTitle);

		helpURL = aHelpURL;
		selectedNetworks = null;
		initControls(aLabel, aWarning);
		pack();
		setLocationRelativeTo(aOwner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		final Object source = e.getSource();
		if (source == btnCancel) {
			setVisible(false);
			dispose();
		} else if (source == btnHelp) {
			OpenBrowser.openURL(helpURL);
		} else if (source == btnOK) {
			// Store the list of networks selected by the user
			final int[] indices = listNetNames.getSelectedIndices();
			final int size = indices.length;
			selectedNetworks = new CyNetwork[size];
			for (int i = 0; i < size; ++i) {
				selectedNetworks[i] = networks.get(indices[i]);
			}

			setVisible(false);
			dispose();
		}
	}

	/**
	 * Gets an array of all networks selected by the user.
	 * 
	 * @return A non-empty array of all networks selected by the user; <code>null</code> if the user has not
	 *         closed this dialog by clicking on the &quot;OK&quot; button.
	 */
	public CyNetwork[] getSelectedNetworks() {
		return selectedNetworks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		// Update the enabled status of the "OK" button.
		btnOK.setEnabled(isNetNameSelected());
	}

	/**
	 * Initializes and lays out additional controls to be added to this dialog.
	 * <p>
	 * Extender classes can override this method to create control(s).
	 * </p>
	 * 
	 * @return Newly initialized control or container with controls; <code>null</code> if no additional
	 *         controls are to be placed on the content pane of this dialog.
	 */
	protected JComponent initAdditionalControls() {
		return null;
	}

	/**
	 * Creates and lays out the controls inside this dialog.
	 * <p>
	 * This method is called upon initialization only.
	 * </p>
	 * 
	 * @param aLabel
	 *            Label to be displayed on top of the network list.
	 * @param aWarning
	 *            Flag indicating if a modification warning must be displayed. The text of the modification
	 *            warning is {@link Messages#SM_NETMODIFICATION}.
	 */
	protected void initControls(String aLabel, boolean aWarning) {
		Box contentPane = Box.createVerticalBox();
		Utils.setStandardBorder(contentPane);

		// Add the main message
		final JPanel panTitle = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panTitle.add(new JLabel(aLabel));
		contentPane.add(panTitle);
		contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));

		// Add a list of loaded networks to select from
		final JScrollPane scroller = new JScrollPane(listNetNames);
		final JPanel panNetList = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panNetList.add(scroller);
		contentPane.add(panNetList);
		contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));

		final JComponent additional = initAdditionalControls();
		if (additional != null) {
			additional.setAlignmentX(0.5f);
			contentPane.add(additional);
			contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));
		}

		// Add OK and Cancel buttons
		JPanel panButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, Utils.BORDER_SIZE, 0));
		btnOK = Utils.createButton(Messages.DI_OK, null, this);
		btnOK.setEnabled(false);
		btnCancel = Utils.createButton(Messages.DI_CANCEL, null, this);
		btnHelp = Utils.createButton(Messages.DI_HELP, null, this);
		Utils.equalizeSize(btnOK, btnCancel, btnHelp);
		panButtons.add(btnOK);
		panButtons.add(btnCancel);
		panButtons.add(Box.createHorizontalStrut(Utils.BORDER_SIZE * 2));
		panButtons.add(btnHelp);
		contentPane.add(panButtons);

		// Add a warning message
		if (aWarning) {
			final JPanel panWarning = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			panWarning.add(new JLabel(Messages.SM_NETMODIFICATION, SwingConstants.LEADING));
			contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));
			contentPane.add(panWarning);
		}
		setContentPane(contentPane);
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 1576652348409963833L;

	/**
	 * &quot;Cancel&quot; button.
	 */
	private JButton btnCancel;

	/**
	 * &quot;Help&quot; button.
	 */
	private JButton btnHelp;

	/**
	 * &quot;OK&quot; button.
	 */
	private JButton btnOK;

	/**
	 * URL of the page to be displayed when the user clicks on the &quot;Help&quot; button.
	 */
	private String helpURL;

	/**
	 * Array of networks selected by the user.
	 */
	private CyNetwork[] selectedNetworks;
}
