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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.Interpretations;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.io.SettingsSerializer;

/**
 * Dialog for adjusting the input and output directories for the batch analysis. This is the first step in the
 * automatic batch analysis on networks.
 * 
 * @author Yassen Assenov
 * @author Nadezhda Doncheva
 */
public class BatchSettingsDialog extends JDialog implements ActionListener {

	/**
	 * Initializes a new instance of <code>BatchSettingsDialog</code>.
	 * 
	 * @param aOwner
	 *            The <code>Frame</code> from which this dialog is displayed.
	 */
	public BatchSettingsDialog(Frame aOwner) {
		super(aOwner, Messages.DT_BATCHSETTINGS, true);

		inOutDirs = null;
		initControls();
		pack();
		setLocationRelativeTo(aOwner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		final Object src = e.getSource();
		if (src == btnInputDir) {
			final File file = updateSetting(choInputDir);
			if (file != null) {
				if (checkInputDir(file)) {
					txfInputDir.setText(file.getAbsolutePath());
				} else {
					txfInputDir.setText("");
					Utils.showErrorBox(Messages.DT_IOERROR, Messages.SM_BADINPUT);
				}
				updateStartButton();
			}

		} else if (src == btnOutputDir) {
			final File file = updateSetting(choOutputDir);
			if (file != null) {
				if (checkOutputDir(file)) {
					txfOutputDir.setText(file.getAbsolutePath());
				} else {
					txfOutputDir.setText("");
					Utils.showErrorBox(Messages.DT_IOERROR, Messages.SM_BADOUTPUT);
				}
				updateStartButton();
			}

		} else if (src == btnStart) {
			this.inOutDirs = new File[2];
			inOutDirs[0] = choInputDir.getSelectedFile();
			inOutDirs[1] = choOutputDir.getSelectedFile();
			setVisible(false);
			dispose();
		} else if (src == btnCancel) {
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Gets the settings for input and output directory for batch analysis.
	 * 
	 * @return Array of <code>File</code> instances, containing the input and output directory for batch
	 *         analysis.
	 */
	public File[] getInOutDirs() {
		return inOutDirs;
	}

	/**
	 * Gets the selected option for acceptable interpretations.
	 * 
	 * @return Interpretations to be applied for each network during batch processing, in the form of an
	 *         <code>Interpretations</code> instance.
	 */
	public Interpretations getInterpretations() {
		if (btnInterpretAll.isSelected()) {
			return Interpretations.ALL;
		}
		if (btnInterpretDir.isSelected()) {
			return Interpretations.DIRECTED;
		}
		return Interpretations.UNDIRECTED;
	}

	/**
	 * Creates and lays out the controls inside this dialog.
	 * <p>
	 * This method is called upon initialization only.
	 * </p>
	 */
	private void initControls() {

		// Create the outer box and panel
		Box contentPane = Box.createVerticalBox();
		Utils.setStandardBorder(contentPane);

		JPanel panTitle = new JPanel();
		panTitle.add(new JLabel(Messages.DT_BATCHSETTINGS));
		contentPane.add(panTitle);
		final int BS = Utils.BORDER_SIZE / 2;
		contentPane.add(Box.createVerticalStrut(BS));

		// Create file choosers and pre-load with paths in settings
		choInputDir = new JFileChooser();
		choInputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// choInputDir.setSelectedFile(new File(inputDir));
		choOutputDir = new JFileChooser();
		choOutputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// choOutputDir.setSelectedFile(new File(outputDir));

		// Initialize text fields for displaying currently selected paths
		final int columnsCount = Utils.BORDER_SIZE * 6;
		txfInputDir = new JTextField("", columnsCount);
		txfOutputDir = new JTextField("", columnsCount);

		// Create buttons for changing the settings
		JButton[] buttons = new JButton[2];
		buttons[0] = btnInputDir = Utils.createButton(Messages.DI_SELECTDIR, null, this);
		buttons[1] = btnOutputDir = Utils.createButton(Messages.DI_SELECTDIR, null, this);
		Utils.equalizeSize(buttons);

		// Add buttons for selecting paths and files
		JPanel panTop = new JPanel();
		JPanel panSelects = new JPanel(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		constr.insets.left = BS;
		constr.insets.right = BS;
		constr.gridx = constr.gridy = 0;

		// Layout the controls in tabular form
		addPathSelector(Messages.DI_INPUTDIR, txfInputDir, btnInputDir, panSelects, constr);
		addPathSelector(Messages.DI_OUTPUTDIR, txfOutputDir, btnOutputDir, panSelects, constr);

		panTop.add(panSelects);
		contentPane.add(panTop);
		contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));
		
		// Add radio buttons for network interpretations
		final JPanel panCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		final JPanel panInterpr = new JPanel(new BorderLayout());
		panInterpr.setBorder(new TitledBorder(Messages.DI_INTERPRS));
		btnInterpretAll = new JRadioButton(Messages.DI_INTERPR_ALL, true);
		btnInterpretDir = new JRadioButton(Messages.DI_INTERPR_DIRECTED);
		btnInterpretUndir = new JRadioButton(Messages.DI_INTERPR_UNDIRECTED);
		final ButtonGroup group = new ButtonGroup();
		group.add(btnInterpretAll);
		group.add(btnInterpretDir);
		group.add(btnInterpretUndir);
		panInterpr.add(btnInterpretAll, BorderLayout.PAGE_START);
		panInterpr.add(btnInterpretDir, BorderLayout.CENTER);
		panInterpr.add(btnInterpretUndir, BorderLayout.PAGE_END);
		panCenter.add(panInterpr);
		contentPane.add(panCenter);
		contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));

		// Add info about node attributes
		final JPanel panAttr = new JPanel(new FlowLayout(FlowLayout.CENTER, BS, BS));
		if (SettingsSerializer.getPluginSettings().getUseNodeAttributes()) {
			panAttr.add(new JLabel(Messages.DI_NODEATTR_SAVE));
		} else {
			panAttr.add(new JLabel(Messages.DI_NODEATTR_SAVENOT));
		}
		contentPane.add(panAttr);
		contentPane.add(Box.createVerticalStrut(Utils.BORDER_SIZE));
		
		// Add Start Analysis and Cancel buttons
		final JPanel panBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		final JPanel panButtons = new JPanel(new GridLayout(1, 2, Utils.BORDER_SIZE, 0));
		panButtons.add(btnCancel = Utils.createButton(Messages.DI_CANCEL, null, this));
		panButtons.add(btnStart = Utils.createButton(Messages.DI_STARTANALYSIS, null, this));
		panBottom.add(panButtons);
		updateStartButton();

		contentPane.add(panBottom);
		setContentPane(contentPane);
		setResizable(false);
	}

	/**
	 * Adds a path selector to the dialog.
	 * 
	 * @param aMessage
	 *            Label of the path to be selected.
	 * @param aField
	 *            Field for the selected path name.
	 * @param aButton
	 *            Button to be pressed for choosing the path.
	 * @param aPanel
	 *            Panel to add aMessage, aField and aButton.
	 * @param aConstr
	 *            Constraints for visualization.
	 */
	private void addPathSelector(String aMessage, JTextField aField, JButton aButton, JPanel aPanel,
			GridBagConstraints aConstr) {
		aConstr.gridx = 0;
		aConstr.anchor = GridBagConstraints.LINE_END;
		aPanel.add(new JLabel(aMessage), aConstr);

		aConstr.gridx = 1;
		aConstr.anchor = GridBagConstraints.CENTER;
		aField.setEditable(false);
		aPanel.add(aField, aConstr);

		aConstr.gridx = 2;
		aPanel.add(aButton, aConstr);
		aConstr.gridy++;
	}

	/**
	 * Updates the status of the button for starting the batch processing.
	 */
	private void updateStartButton() {
		if ("".equals(txfInputDir.getText()) || "".equals(txfOutputDir.getText())) {
			btnStart.setEnabled(false);
		} else {
			btnStart.setEnabled(true);
		}
	}

	/**
	 * Updates the chosen (input/output) file.
	 * 
	 * @param aChooser
	 *            JFileChooser, from which a new file has to be chosen.
	 * @return Chosen file.
	 */
	private File updateSetting(JFileChooser aChooser) {
		if (aChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return aChooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Checks the output directory, i.e. if aFile exists, is a directory, is empty and is writable.
	 * 
	 * @param aFile
	 *            File to be checked.
	 * @return <code>true</code> if the output directory fulfills the criterion, and <code>false</code>
	 *         otherwise.
	 */
	public static boolean checkOutputDir(File aFile) {
		try {
			if (aFile.exists() && aFile.isDirectory() && aFile.list().length == 0 && aFile.canWrite()) {
				return true;
			}
		} catch (Exception ex) {
			// Fall through
		}
		return false;
	}

	/**
	 * Checks the output directory given by aFileName, i.e. if the directory exists, is a directory, is empty
	 * and is writable.
	 * 
	 * @param aFileName
	 *            Name of the directory to check.
	 * @return <code>true</code> if the output directory fulfills the criterion, and <code>false</code>
	 *         otherwise.
	 */
	public static boolean checkOutputDir(String aFileName) {
		try {
			return checkOutputDir(new File(aFileName));
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Checks the input directory, i.e. if aFile exists, is a directory, is not empty and is readable.
	 * 
	 * @param aFile
	 *            File to be checked.
	 * @return <code>true</code> if the input directory fulfills the criterion, and <code>false</code>
	 *         otherwise.
	 */
	public static boolean checkInputDir(File aFile) {
		try {
			if (aFile.exists() && aFile.isDirectory() && aFile.canRead() && aFile.listFiles().length > 0) {
				return true;
			}
		} catch (Exception ex) {
			// Fall through
		}
		return false;
	}

	/**
	 * Checks the input directory given by aFileName, i.e. if the directory exists, is a directory, is empty
	 * and is writable.
	 * 
	 * @param aFileName
	 *            Name of the directory to check.
	 * @return <code>true</code> if the input directory fulfills the criterion, and <code>false</code>
	 *         otherwise.
	 */
	public static boolean checkInputDir(String aFileName) {
		try {
			return checkInputDir(new File(aFileName));
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 7767545827871267966L;

	/**
	 * &quot;Cancel&quot; button.
	 */
	private JButton btnCancel;

	/**
	 * Button to select input directory (.sif, .gml, .xgmml files)
	 */
	private JButton btnInputDir;

	/**
	 * Button to select output directory (.netstats files)
	 */
	private JButton btnOutputDir;

	/**
	 * Radio button for applying all possible interpretations.
	 */
	private JRadioButton btnInterpretAll;

	/**
	 * Radio button for applying only interpretations that treat the networks as directed.
	 */
	private JRadioButton btnInterpretDir;

	/**
	 * Radio button for applying only interpretations that treat the networks as undirected.
	 */
	private JRadioButton btnInterpretUndir;

	/**
	 * Start analysis button.
	 */
	private JButton btnStart;

	/**
	 * Directory chooser for the Networks.
	 */
	private JFileChooser choInputDir;

	/**
	 * Directory chooser for the &quot;.netstat&quot; files.
	 */
	private JFileChooser choOutputDir;

	/**
	 * Two-element array storing the input and output directory as selected by the user.
	 */
	private File[] inOutDirs;

	/**
	 * Text field showing the name of the selected input directory.
	 */
	private JTextField txfInputDir;

	/**
	 * Text field showing the name of the selected output directory.
	 */
	private JTextField txfOutputDir;
}
