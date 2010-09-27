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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.border.TitledBorder;


import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.dec.Decorator;

/**
 * Chart display panel with a title border.
 * <p>
 * Clicking on the title of the border expands/collapses the contents of panel.
 * </p>
 * 
 * @author Yassen Assenov
 */
public class ChartExpandablePanel extends ChartDisplayPanel
	implements MouseListener, MouseMotionListener {

	/**
	 * Initializes a new instance of <code>ChartExpandablePanel</code>.
	 * <p>
	 * The panel is initially expanded.
	 * </p>
	 * 
	 * @param aOwner Owner dialog.
	 * @param aID ID of complex parameter to be displayed.
	 * @param aVisualizer Visualizer of the complex parameter to be displayed.
	 */
	public ChartExpandablePanel(JDialog aOwner, String aID, ComplexParamVisualizer aVisualizer) {
		this(aOwner, aID, aVisualizer, true, null);
	}

	/**
	 * Initializes a new instance of <code>ChartExpandablePanel</code>.
	 * 
	 * @param aOwner Owner dialog.
	 * @param aID ID of complex parameter to be displayed.
	 * @param aVisualizer Visualizer of the complex parameter to be displayed.
	 * @param aExpanded Flag indicating if the panel must be initially expanded or hidden.
	 */
	public ChartExpandablePanel(JDialog aOwner, String aID, ComplexParamVisualizer aVisualizer, boolean aExpanded) {
		this(aOwner, aID, aVisualizer, aExpanded, null);
	}

	/**
	 * Initializes a new instance of <code>ChartBorderPanel</code>.
	 * 
	 * @param aOwner Owner dialog.
	 * @param aID ID of complex parameter to be displayed.
	 * @param aVisualizer Visualizer of the complex parameter to be displayed.
	 * @param aExpanded Flag indicating if the panel must be initially expanded or hidden.
	 * @param aDecorators Decorator instances for the complex parameter visualized.
	 */
	public ChartExpandablePanel(JDialog aOwner, String aID, ComplexParamVisualizer aVisualizer, boolean aExpanded,
		Decorator[] aDecorators) {
		super(aOwner, aID, aVisualizer, aDecorators);

		initControls(aExpanded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnChartSettings) {
			if (changeVisSettings()) {
				String titleText = visualizer.getTitle();
				titleExpanded = "- " + titleText;
				titleHidden = "+ " + titleText;
				title.setTitle(titleExpanded);
				repaint();
			}
		} else {
			super.actionPerformed(e);
		}
	}

	/**
	 * Checks if this panel is currently expanded.
	 * <p>
	 * The panel is expanded when the controls it contains are visible, otherwise the panel is
	 * referred to as hidden.
	 * </p>
	 * 
	 * @return <code>true</code> when this control is expanded; <code>false</code> otherwise.
	 */
	public boolean isExpanded() {
		return getComponent(0).isVisible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getY() <= titleHeight) {
			setExpanded(!isExpanded());
			ownerDialog.pack();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// Event is not processed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// Event is not processed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// Event is not processed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		if (getCursor() == onTitleCursor) {
			setCursor(null);
			title.setTitleColor(normalTitleColor);
			setToolTipText(null);
			repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		// Event is not processed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		if (e.getY() <= titleHeight) {
			if (getCursor() != onTitleCursor) {
				setCursor(onTitleCursor);
				title.setTitleColor(selTitleColor);
				if (isExpanded()) {
					setToolTipText(Messages.TT_CLICK2HIDE);
				} else {
					setToolTipText(Messages.TT_CLICK2EXPAND);
				}
				repaint();
			}
		} else {
			if (getCursor() == onTitleCursor) {
				setCursor(null);
				title.setTitleColor(normalTitleColor);
				setToolTipText(null);
				repaint();
			}
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 8700420732833264603L;

	/**
	 * Mouse cursor when over the top border of this panel.
	 */
	private static Cursor onTitleCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	/**
	 * Creates and lays out the controls inside this panel.
	 * <p>
	 * This method is called upon initialization only.
	 * </p>
	 * 
	 * @param aExpanded Flag indicating if the panel must be initially expanded or hidden.
	 */
	private void initControls(boolean aExpanded) {

		String titleText = visualizer.getTitle();
		titleExpanded = "- " + titleText;
		titleHidden = "+ " + titleText;
		title = BorderFactory.createTitledBorder(titleExpanded);
		setBorder(title);
		titleHeight = title.getMinimumSize(this).height / 2;
		normalTitleColor = title.getTitleColor();
		selTitleColor = Utils.invertOf(normalTitleColor);
		addMouseListener(this);
		addMouseMotionListener(this);
		if (aExpanded == false) {
			setExpanded(false);
		}
	}

	/**
	 * Changes the &quot;expanded&quot; status of this control.
	 * 
	 * @param aExpanded Flag indicating if this panel must become expanded or hidden.
	 */
	private void setExpanded(boolean aExpanded) {
		Component[] controls = getComponents();
		for (int i = 0; i < controls.length - 1; ++i) {
			controls[i].setVisible(aExpanded);
		}
		((TitledBorder) getBorder()).setTitle(aExpanded ? titleExpanded : titleHidden);
	}

	/**
	 * Border of the panel.
	 */
	private TitledBorder title;

	/**
	 * Title text when this panel is expanded, that is, when the controls inside it are visible.
	 */
	private String titleExpanded;

	/**
	 * Title text when this panel is hidden, that is, when the controls inside it are invisible.
	 */
	private String titleHidden;

	/**
	 * Color of title text when mouse cursor is <b>not</b> over the top border of this panel.
	 */
	private Color normalTitleColor;

	/**
	 * Color of title text when mouse cursor is over the top border of this panel.
	 */
	private Color selTitleColor;

	/**
	 * Height, in pixels, of the top border of this panel.
	 */
	private int titleHeight;
}
