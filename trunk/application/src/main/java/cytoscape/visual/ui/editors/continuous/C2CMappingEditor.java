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
package cytoscape.visual.ui.editors.continuous;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import org.jdesktop.swingx.multislider.Thumb;
import org.jdesktop.swingx.multislider.TrackRenderer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.util.List;

import java.beans.PropertyChangeEvent;

import javax.swing.ImageIcon;


/**
 * Continuous-Continuous mapping editor.<br>
 *
 * <p>
 *     This is a editor for continuous values, i.e., numbers.
 * </p>
 *
 * @version 0.7
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 *
  */
public class C2CMappingEditor extends ContinuousMappingEditorPanel {
	
	private static final long serialVersionUID = -3555354576428996187L;
	
	// Default value for below and above.
	private static final Float DEF_BELOW_AND_ABOVE = 1f;
	
	private static final Float DEFAULT_MIN = 0f;
	private static final Float DEFAULT_MAX = 100f;

	/**
	 * Creates a new C2CMappingEditor object.
	 * This is for mappings from numbers to numbers.
	 *
	 * @param type DOCUMENT ME!
	 */
	public C2CMappingEditor(VisualPropertyType type) {
		super(type);
		abovePanel.setVisible(false);
		belowPanel.setVisible(false);
		pack();
		setSlider();

		// Add two sliders by default.
		if ((mapping != null) && (mapping.getPointCount() == 0)) {
			addSlider(DEFAULT_MIN, 10f);
			addSlider(DEFAULT_MAX, 30f);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 * @param title DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 */
	public static Object showDialog(final int width, final int height, final String title,
	                                final VisualPropertyType type) {
		editor = new C2CMappingEditor(type);
		final Dimension size = new Dimension(width, height);
		editor.setPreferredSize(size);
		editor.setSize(size);
		
		editor.setTitle(title);
		editor.setAlwaysOnTop(true);
		editor.setLocationRelativeTo(Cytoscape.getDesktop());
		editor.setVisible(true);

		return editor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ImageIcon getIcon(final int iconWidth, final int iconHeight,
	                                VisualPropertyType type) {
		editor = new C2CMappingEditor(type);

		TrackRenderer rend = editor.slider.getTrackRenderer();

		if (rend instanceof ContinuousTrackRenderer) {
			rend.getRendererComponent(editor.slider);

			return ((ContinuousTrackRenderer) rend).getTrackGraphicIcon(iconWidth, iconHeight);
		} else {
			return null;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ImageIcon getLegend(final int width, final int height,
	                                  final VisualPropertyType type) {
		editor = new C2CMappingEditor(type);

		final ContinuousTrackRenderer rend = (ContinuousTrackRenderer) editor.slider.getTrackRenderer();
		rend.getRendererComponent(editor.slider);

		return rend.getLegend(width, height);
	}

	
	// Add slider to the editor.
	private void addSlider(float position, float value) {
		final double maxValue = EditorValueRangeTracer.getTracer().getMax(type);

		BoundaryRangeValues newRange;

		if (mapping.getPointCount() == 0) {
			slider.getModel().addThumb(position, value);

			newRange = new BoundaryRangeValues(below, 5f, above);
			mapping.addPoint(maxValue / 2, newRange);

			if (position != DEFAULT_MIN && position != DEFAULT_MAX)
				selectThumbAtPosition(position);

			slider.repaint();
			repaint();

			return;
		}

		// Add a new white thumb 
		slider.getModel().addThumb(position, value);

		// Pick Up first point.
		final ContinuousMappingPoint previousPoint = mapping.getPoint(mapping.getPointCount() - 1);

		final BoundaryRangeValues previousRange = previousPoint.getRange();
		newRange = new BoundaryRangeValues(previousRange);

		newRange.lesserValue = slider.getModel().getSortedThumbs()
		                             .get(slider.getModel().getThumbCount() - 1);
		newRange.equalValue = 5f;
		newRange.greaterValue = previousRange.greaterValue;
		mapping.addPoint(maxValue, newRange);

		updateMap();

		if (position != DEFAULT_MIN && position != DEFAULT_MAX) {
			updateCytoscape();

			// Make this slider the selected one
			selectThumbAtPosition(position);
		}

		slider.repaint();
		repaint();
	}

	@Override
	protected void addButtonActionPerformed(ActionEvent evt) {
		addSlider(51f, 5f);
	}

	
	/**
	 * Remove selected slider.
	 */
	@Override protected void deleteButtonActionPerformed(ActionEvent evt) {
		if(slider.getModel().getThumbCount() <=0)
			return;

		final int selectedIndex = ((TriangleThumbRenderer)slider.getThumbRenderer()).getSelectedIndex();
		
		if(selectedIndex<0)
			return;
	
		slider.getModel().removeThumb(selectedIndex);
		mapping.removePoint(selectedIndex);

		updateMap();
		updateCytoscape();
		((ContinuousTrackRenderer) slider.getTrackRenderer()).removeSquare(selectedIndex);

		mapping.fireStateChanged();

		repaint();	
	}

	private void setSlider() {
		slider.updateUI();

		final double minValue = EditorValueRangeTracer.getTracer().getMin(type);
		double actualRange = EditorValueRangeTracer.getTracer().getRange(type);

		BoundaryRangeValues bound;
		Float fraction;

		if (allPoints != null && allPoints.size() != 0) {

			for (ContinuousMappingPoint point : allPoints) {
				bound = point.getRange();
	
				fraction = ((Number) ((point.getValue().doubleValue() - minValue) / actualRange)).floatValue() * 100;
				slider.getModel().addThumb(fraction, ((Number) bound.equalValue).floatValue());
			}

			below = (Number) allPoints.get(0).getRange().lesserValue;
			above = (Number) allPoints.get(allPoints.size() - 1).getRange().greaterValue;
		} else {
			below = DEF_BELOW_AND_ABOVE;
			above = DEF_BELOW_AND_ABOVE;
		}

		/*
		 * get min and max for the value object
		 */
		TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

		ContinuousTrackRenderer cRend = new ContinuousTrackRenderer(type,
		                                                            (Number) below, (Number) above, mapping, slider);
		cRend.addPropertyChangeListener(this);

		slider.setThumbRenderer(thumbRend);
		slider.setTrackRenderer(cRend);
		slider.addMouseListener(new ThumbMouseListener());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ContinuousMappingEditorPanel.BELOW_VALUE_CHANGED)) {
			below = evt.getNewValue();
		} else if (evt.getPropertyName().equals(ContinuousMappingEditorPanel.ABOVE_VALUE_CHANGED)) {
			above = evt.getNewValue();
		}
	}
}
