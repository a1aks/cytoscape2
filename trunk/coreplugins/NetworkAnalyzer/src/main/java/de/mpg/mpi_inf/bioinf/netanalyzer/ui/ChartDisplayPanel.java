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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;

import cytoscape.logger.CyLogger;
import cytoscape.util.OpenBrowser;
import de.mpg.mpi_inf.bioinf.netanalyzer.InnerException;
import de.mpg.mpi_inf.bioinf.netanalyzer.Plugin;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.ComplexParam;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.filter.ComplexParamFilter;
import de.mpg.mpi_inf.bioinf.netanalyzer.dec.Decorator;
import de.mpg.mpi_inf.bioinf.netanalyzer.sconnect.HelpConnector;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.charts.JFreeChartConn;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.filter.ComplexParamFilterDialog;

/**
 * Panel used to display chart.
 * <p>
 * In addition to a chart control (<code>ChartPanel</code>), this panel also contains buttons for
 * enlarged view, saving and viewing/editing visual settings, and others.
 * </p>
 * 
 * @author Yassen Assenov
 */
public class ChartDisplayPanel extends JPanel implements ActionListener {

	/**
	 * Initializes a new instance of <code>ChartDisplayPanel</code>.
	 * 
	 * @param aOwner
	 *            Owner dialog.
	 * @param aID
	 *            ID of complex parameter to be displayed.
	 * @param aVisualizer
	 *            Visualizer of the complex parameter to be displayed.
	 */
	public ChartDisplayPanel(JDialog aOwner, String aID, ComplexParamVisualizer aVisualizer) {
		this(aOwner, aID, aVisualizer, null);
	}

	/**
	 * Initializes a new instance of <code>ChartDisplayPanel</code>.
	 * 
	 * @param aOwner
	 *            Owner dialog.
	 * @param aID
	 *            ID of complex parameter to be displayed.
	 * @param aVisualizer
	 *            Visualizer of the complex parameter to be displayed.
	 * @param aDecorators
	 *            Decorator instances for the complex parameter visualized.
	 */
	public ChartDisplayPanel(JDialog aOwner, String aID, ComplexParamVisualizer aVisualizer,
			Decorator[] aDecorators) {
		super();
		visualizer = aVisualizer;
		id = aID;
		decorators = aDecorators;
		originalParam = aVisualizer.getComplexParam();
		chart = aVisualizer.createControl();
		ownerDialog = aOwner;

		initControls();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btnEnlarged) {
				displayEnlarged();
			} else if (source == btnSaveChart) {
				SaveChartDialog dialog = new SaveChartDialog(ownerDialog, chart);
				dialog.setVisible(true);
			} else if (source == btnSaveData) {
				saveChartData();
			} else if (source == btnFilter) {
				performFilter();
			} else if (source == btnChartSettings) {
				if (changeVisSettings()) {
					repaint();
				}
			} else if (source == btnHelp) {
				OpenBrowser.openURL(HelpConnector.getParamURL(id));
			} else if (btnsDec != null) {
				for (int i = 0; i < btnsDec.length; ++i) {
					if (source == btnsDec[i]) {
						Decorator dec = decorators[i];
						if (dec.isActive()) {
							dec.undecorate(chart);
						} else {
							dec.decorate(ownerDialog, chart, visualizer, true);
						}
						btnsDec[i].setText(dec.getButtonLabel());
						btnsDec[i].setToolTipText(dec.getButtonToolTip());
						break;
					}
				}
			}
		} catch (InnerException ex) {
			// NetworkAnalyzer internal error
			CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
		}
	}

	/**
	 * Displays a dialog for changing the visual settings and aplies the changes if necessary.
	 * 
	 * @return <code>true</code> if the user has pressed one of &quot;Set Default&quot; or
	 *         &quot;OK&quot; buttons and changes were applied; <code>false</code> otherwise.
	 */
	protected boolean changeVisSettings() {
		// Display dialog to view/edit the settings
		if (visualizer.showSettingsDialog(ownerDialog) != SettingsDialog.STATUS_CANCEL) {
			// User has pressed SET DEFAULT or OK
			final JFreeChart newChart = visualizer.updateControl(chart);
			if (decorators != null) {
				for (int i = 0; i < decorators.length; ++i) {
					if (decorators[i].isActive()) {
						decorators[i].decorate(ownerDialog, newChart, visualizer, false);
					}
				}
			}
			JFreeChartConn.setChart(chartPanel, chart = newChart);
			return true;
		}
		return false;
	}

	/**
	 * Visualizer instance for the displayed parameter chart. Used for viewing/editing of visual
	 * settings.
	 */
	protected ComplexParamVisualizer visualizer;

	/**
	 * Owner dialog of this panel.
	 */
	protected JDialog ownerDialog;

	/**
	 * &quot;Chart Settings&quot; button.
	 */
	protected JButton btnChartSettings;

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 1662216221117859713L;

	/**
	 * Creates and lays out the controls inside this panel.
	 * <p>
	 * This method is called upon initialization only.
	 * </p>
	 */
	private void initControls() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(createChartPanel());

		final Box buttonPanel = Box.createVerticalBox();
		final List<JButton> buttons = new ArrayList<JButton>(8);
		btnEnlarged = Utils.createButton(Messages.DI_VIEWENLARGED, Messages.TT_VIEWENLARGED, this);
		if (Plugin.hasFilter(originalParam.getClass())) {
			btnFilter = Utils.createButton(Messages.DI_FILTERDATA, Messages.TT_FILTERDATA, this);
		} else {
			btnFilter = null;
		}
		btnSaveChart = Utils.createButton(Messages.DI_EXPORTCHART, Messages.TT_SAVECHART, this);
		btnSaveData = Utils.createButton(Messages.DI_EXPORTDATA, Messages.TT_SAVEDATA, this);
		btnChartSettings = Utils.createButton(Messages.DI_CHARTSETTINGS, Messages.TT_CHARTSETTINGS,
				this);
		btnHelp = Utils.createButton(Messages.DI_HELP, Messages.TT_ONLHELP, this);

		buttonPanel.add(btnChartSettings);
		buttons.add(btnChartSettings);
		buttonPanel.add(btnEnlarged);
		buttons.add(btnEnlarged);
		if (btnFilter != null) {
			buttonPanel.add(btnFilter);
			buttons.add(btnFilter);
		}
		buttonPanel.add(Box.createVerticalStrut(Utils.BORDER_SIZE * 2));
		if (decorators != null) {
			btnsDec = new JButton[decorators.length];
			for (int i = 0; i < decorators.length; ++i) {
				Decorator d = decorators[i];
				btnsDec[i] = Utils.createButton(d.getButtonLabel(), d.getButtonToolTip(), this);
				buttonPanel.add(btnsDec[i]);
				buttons.add(btnsDec[i]);
			}
			buttonPanel.add(Box.createVerticalStrut(Utils.BORDER_SIZE * 2));
		}
		buttonPanel.add(btnSaveChart);
		buttons.add(btnSaveChart);
		buttonPanel.add(btnSaveData);
		buttons.add(btnSaveData);
		buttonPanel.add(Box.createVerticalStrut(Utils.BORDER_SIZE * 2));
		buttonPanel.add(btnHelp);
		buttons.add(btnHelp);
		buttonPanel.add(Box.createVerticalGlue());
		// Ensure buttons are large enough to fit all possible messages
		final JButton[] btnsArray = new JButton[buttons.size()];
		Utils.setSizes(buttons.toArray(btnsArray), preferred, preferred);
		add(buttonPanel);
		add(Box.createHorizontalGlue());
	}

	/**
	 * Creates a Swing control that displays this panel's chart.
	 * <p>
	 * The newly created panel is stored in the {@link #chartPanel} field.
	 * </p>
	 * 
	 * @return The newly created panel instance that displays the chart.
	 */
	private JPanel createChartPanel() {
		chartPanel = JFreeChartConn.createPanel(chart);
		Dimension size = chartPanel.getPreferredSize();
		size.setSize(size.getWidth() / 3 * 2, size.getHeight() / 3 * 2);
		chartPanel.setPreferredSize(size);
		return chartPanel;
	}

	/**
	 * Creates a dialog window which contains the chart in its preferred size.
	 */
	private void displayEnlarged() {
		JDialog dialog = new JDialog(ownerDialog, visualizer.getTitle(), false);
		dialog.getContentPane().add(JFreeChartConn.createPanel(chart));
		dialog.pack();
		dialog.setLocationRelativeTo(ownerDialog);
		dialog.setVisible(true);
	}

	/**
	 * Handles the pressing of the {@link #btnFilter} button.
	 * <p>
	 * After asking the user for confirmation, this method adds or removes filter to the complex
	 * parameter displayed.
	 * </p>
	 */
	private void performFilter() {
		if (originalParam == visualizer.getComplexParam()) {
			// Create filter
			try {
				final Class<?> paramClass = originalParam.getClass();
				final Class<?> filterClass = Plugin.getFilterDialogClass(paramClass);
				final Constructor<?> constr = filterClass.getConstructors()[0];
				Object[] cParams = new Object[] { ownerDialog, Messages.DT_FILTERDATA,
						originalParam, visualizer.getSettings() };
				ComplexParamFilterDialog d = (ComplexParamFilterDialog) constr.newInstance(cParams);
				ComplexParamFilter filter = d.showDialog();
				if (filter != null) {
					btnFilter.setText(Messages.DI_REMOVEFILTER);
					btnFilter.setToolTipText(Messages.TT_REMOVEFILTER);
					visualizer.setComplexParam(filter.filter(originalParam));
					chart = visualizer.createControl();
					JFreeChartConn.setChart(chartPanel, chart);
				}
			} catch (InnerException ex) {
				// NetworkAnalyzer internal error
				CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
			} catch (SecurityException ex) {
				Utils.showErrorBox(this, Messages.DT_SECERROR, Messages.SM_SECERROR2);
			} catch (Exception ex) {
				// ClassCastException, ClassNotFoundException, IllegalAccessException
				// IllegalArgumentException, InstantiationException, InvocationTargetException
				// NetworkAnalyzer internal error
				CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
			}

		} else {
			// Remove filter
			int res = JOptionPane.showConfirmDialog(this, Messages.SM_REMOVEFILTER,
					Messages.DT_REMOVEFILTER, JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				btnFilter.setText(Messages.DI_FILTERDATA);
				btnFilter.setToolTipText(Messages.TT_FILTERDATA);
				visualizer.setComplexParam(originalParam);
				chart = visualizer.createControl();
				JFreeChartConn.setChart(chartPanel, chart);
			}
		}
	}

	/**
	 * Displays a dialog for saving the complex parameter data into a text file.
	 * <p>
	 * If the user presses the &quot;Save&quot; button of the shown dialog, the complex parameter is
	 * saved to the chosen file.
	 * </p>
	 */
	private void saveChartData() {
		JFileChooser saveFileDialog = new JFileChooser();
		int saveIt = saveFileDialog.showSaveDialog(this);
		if (saveIt == JFileChooser.APPROVE_OPTION) {
			FileWriter writer = null;
			try {
				File file = saveFileDialog.getSelectedFile();
				if (Utils.canSave(file, this)) {
					writer = new FileWriter(file);
					visualizer.getComplexParam().save(writer, false);
				}
			} catch (IOException ex) {
				Utils.showErrorBox(this, Messages.DT_IOERROR, Messages.SM_OERROR);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						// Fall through
					}
				}
			}
		}
	}

	private static Dimension preferred = null;

	static {
		final JButton b = new JButton("");
		preferred = b.getPreferredSize();
		final String[] texts = new String[] { Messages.DI_VIEWENLARGED, Messages.DI_FILTERDATA,
				Messages.DI_EXPORTCHART, Messages.DI_EXPORTDATA, Messages.DI_CHARTSETTINGS,
				Messages.DI_HELP, Messages.DI_FITLINE, Messages.DI_REMOVELINE, Messages.DI_FITPL, Messages.DI_REMOVEPL };
		for (final String text : texts) {
			b.setText(text);
			Utils.ensureSize(preferred, b.getPreferredSize());
		}
	}

	/**
	 * Decorators registered for the complex parameter instance visualized.
	 */
	private Decorator[] decorators;

	/**
	 * Original (unfiltered) complex parameter displayed in this panel.
	 */
	private ComplexParam originalParam;

	/**
	 * Chart to be displayed.
	 */
	private JFreeChart chart;

	/**
	 * Swing control that displays the chart.
	 */
	private JPanel chartPanel;

	/**
	 * &quot;View Enlarged&quot; button.
	 */
	private JButton btnEnlarged;

	/**
	 * &quot;Save Chart&quot; button.
	 */
	private JButton btnSaveChart;

	/**
	 * &quot;Save Data&quot; button.
	 */
	private JButton btnSaveData;

	/**
	 * &quot;Filter Data&quot; / &quot;Remove Filter&quot; button.
	 */
	private JButton btnFilter;

	/**
	 * Buttons for the decorators.
	 */
	private JButton[] btnsDec;

	/**
	 * &quot;Help&quot; button.
	 */
	private JButton btnHelp;

	/**
	 * ID of the visualized complex parameter.
	 */
	private String id;
}
