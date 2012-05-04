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

import giny.model.Node;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import de.mpg.mpi_inf.bioinf.netanalyzer.ConnComponentAnalyzer;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.CCInfo;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;

/**
 * Dialog listing the connected components of a network. This dialog also allows for a connected
 * component to be saved as a new network.
 * 
 * @author Yassen Assenov
 */
public class ConnComponentsDialog extends JDialog
	implements ActionListener, DocumentListener, ListSelectionListener {

	/**
	 * Initializes a new instance of <code>ConnComponentsDialog</code>.
	 * 
	 * @param aOwner The <code>Frame</code> from which this dialog is displayed.
	 * @param aNetwork Target network, to which the connected components belong.
	 * @param aComponents Array of all the connected components of <code>aNetwork</code>.
	 * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless()</code> returns
	 *         <code>true</code>.
	 */
	public ConnComponentsDialog(Frame aOwner, CyNetwork aNetwork, CCInfo[] aComponents)
		throws HeadlessException {
		super(aOwner, Messages.DT_CONNCOMP, true);
		network = aNetwork;
		components = aComponents;
		initControls();
		pack();
		setResizable(false);
		setLocationRelativeTo(aOwner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (btnClose == src) {
			setVisible(false);
			dispose();
		} else if (btnExtract == src) {
			CCInfo comp = components[listComp.getSelectedIndex()];
			ArrayList<Node> nodes = new ArrayList<Node>(ConnComponentAnalyzer.getNodesOf(network, comp));
			// TODO: [Cytoscape 2.8] Check if the returned list is parameterized
			List<?> edges = network.getConnectingEdges(nodes);
			Cytoscape.createNetwork(nodes, edges, fieldNetName.getText());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		int i = listComp.getSelectedIndex() + 1;
		boolean enabled = (i != 0);
		btnExtract.setEnabled(enabled);
		if (enabled) {
			String title = network.getTitle();
			if (title == null) {
				title = "_" + i;
			} else if (title.endsWith(".gml") || title.endsWith(".sif")) {
				final int cut = title.length() - 4;
				title = title.substring(0, cut) + "_" + i + title.substring(cut);
			} else {
				title = title + "_" + i;
			}
			fieldNetName.setText(title);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) {
		updateBtnExtract();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {
		updateBtnExtract();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {
		// Event is not processed
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 6103679911736096166L;

	/**
	 * Creates and lays out the controls inside this dialog.
	 * <p>
	 * This method is called upon initialization only.
	 * </p>
	 */
	private void initControls() {
		Box contentPane = new Box(BoxLayout.PAGE_AXIS);
		Utils.setStandardBorder(contentPane);

		String tt = "<html>" + Messages.DI_CCOF + "<b>" + network.getTitle() + "</b>";
		JPanel panTitle = new JPanel();
		panTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		panTitle.add(new JLabel(tt));
		contentPane.add(panTitle);

		final int compCount = components.length;
		String[] ccItems = new String[compCount];
		for (int i = 0; i < compCount; ++i) {
			ccItems[i] = Messages.DI_COMP + " " + (i + 1) + " (" + components[i].getSize() + ")";
		}
		listComp = new JList(ccItems);
		listComp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listComp.addListSelectionListener(this);
		if (compCount < 8) {
			listComp.setVisibleRowCount(compCount);
		}
		JScrollPane scrollList = new JScrollPane(listComp);
		scrollList.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel panList = new JPanel();
		panList.setBorder(BorderFactory.createTitledBorder(Messages.DI_CONNCOMP));
		panList.add(scrollList);
		contentPane.add(panList);

		JPanel panExtract = new JPanel(new BorderLayout());
		panExtract.setBorder(BorderFactory.createTitledBorder(Messages.DI_EXTRCOMP));
		panExtract.add(new JLabel(Messages.DI_EXTRCOMPLONG), BorderLayout.NORTH);
		fieldNetName = new JTextField();
		fieldNetName.getDocument().addDocumentListener(this);
		panExtract.add(fieldNetName, BorderLayout.CENTER);
		btnExtract = Utils.createButton(Messages.DI_EXTR, null, this);
		btnExtract.setEnabled(false);
		JPanel panExtr = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panExtr.add(btnExtract);
		panExtract.add(panExtr, BorderLayout.SOUTH);
		contentPane.add(panExtract);

		JPanel panButtons = new JPanel();
		panButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnClose = Utils.createButton(Messages.DI_CLOSE, null, this);
		panButtons.add(btnClose);
		contentPane.add(panButtons);
		setContentPane(contentPane);
	}

	/**
	 * Updates the &quot;enabled&quot; status of the Extract button.
	 */
	private void updateBtnExtract() {
		if ("".equals(fieldNetName.getText()) || listComp.getSelectedIndex() == -1) {
			btnExtract.setEnabled(false);
		} else {
			btnExtract.setEnabled(true);
		}
	}

	/**
	 * Network that contains the connected components listed in this dialog.
	 */
	private CyNetwork network;

	/**
	 * List of all connected component contained in {@link #network}.
	 */
	private CCInfo[] components;

	/**
	 * &quot;Close&quot; button.
	 */
	private JButton btnClose;

	/**
	 * &quot;Extract&quot; button.
	 */
	private JButton btnExtract;

	/**
	 * List control that contains the connected components.
	 */
	private JList listComp;

	/**
	 * Text field for entering the name of a new network.
	 */
	private JTextField fieldNetName;
}
