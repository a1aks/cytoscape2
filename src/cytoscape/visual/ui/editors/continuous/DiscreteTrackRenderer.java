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

import giny.view.ObjectPosition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.Thumb;

import cytoscape.Cytoscape;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;
import cytoscape.visual.ui.ObjectPlacerGraphic;
import cytoscape.visual.ui.icon.VisualPropertyIcon;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class DiscreteTrackRenderer<T> extends JComponent implements VizMapperTrackRenderer {
	
	private static final long serialVersionUID = 5024551458030362213L;
	
	/*
	 * Constants for diagram.
	 */
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 12);
	private static final int ICON_SIZE = VisualPropertyIcon.DEFAULT_ICON_SIZE;
	private static final int ICON_SIZE_CG = 50;
	
	private static final Color ICON_COLOR = new Color(10, 100, 255, 200);
	private static final int THUMB_WIDTH = 12;
	private static final int V_PADDING = 20;
	
	private int trackHeight = 70;
	private int arrowBarYPosition = trackHeight + 50;
	private int smallIconSize = 20;
	
	private T below;
	private T above;
	private final VisualPropertyType type;
	private String title;

	// Mainly for Icons
	private List<T> rangeObjects;

	private JXMultiThumbSlider<T> slider;

	/**
	 * Creates a new DiscreteTrackRenderer object.
	 *
	 * @param type  DOCUMENT ME!
	 * @param minValue  DOCUMENT ME!
	 * @param maxValue  DOCUMENT ME!
	 * @param below  DOCUMENT ME!
	 * @param above  DOCUMENT ME!
	 */
	public DiscreteTrackRenderer(final VisualPropertyType type,
	                             T below, T above) {

		this.below = below;
		this.above = above;

		this.type = type;

		if (type.isNodeProp())
			title = Cytoscape.getVisualMappingManager().getVisualStyle()
			                 .getNodeAppearanceCalculator().getCalculator(type).getMapping(0)
			                 .getControllingAttributeName();
		else
			title = Cytoscape.getVisualMappingManager().getVisualStyle()
			                 .getEdgeAppearanceCalculator().getCalculator(type).getMapping(0)
			                 .getControllingAttributeName();

		this.setBackground(Color.white);
		this.setForeground(Color.white);
	}


	/**
	 * DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		super.paint(g);
		paintComponent(g);
	}

	protected void paintComponent(Graphics gfx) {
		trackHeight = slider.getHeight() - 100;
		arrowBarYPosition = trackHeight + 50;

		// Turn AA on
		Graphics2D g = (Graphics2D) gfx;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int track_width = slider.getWidth() - THUMB_WIDTH;

		g.translate(THUMB_WIDTH / 2, 12);

		double minValue = EditorValueRangeTracer.getTracer().getMin(type);
		double maxValue = EditorValueRangeTracer.getTracer().getMax(type);
		double valueRange = EditorValueRangeTracer.getTracer().getRange(type);
		
		//		 get the list of tumbs
		final List<Thumb<T>> stops = slider.getModel().getSortedThumbs();

		int numPoints = stops.size();

		// set up the data for the gradient
		float[] fractions = new float[numPoints];
		Object[] objectValues = new Object[numPoints];

		/*
		 * Find min, max, and ranges
		 */
		int i = 0;

		for (Thumb<T> thumb : stops) {
			objectValues[i] = thumb.getObject();
			fractions[i] = thumb.getPosition();
			i++;
		}

		/*
		 * Draw arrow bar
		 */
		g.setStroke(new BasicStroke(1.0f));
		g.setColor(Color.black);
		g.drawLine(0, arrowBarYPosition, track_width, arrowBarYPosition);

		Polygon arrow = new Polygon();
		arrow.addPoint(track_width, arrowBarYPosition);
		arrow.addPoint(track_width - 20, arrowBarYPosition - 8);
		arrow.addPoint(track_width - 20, arrowBarYPosition);
		g.fill(arrow);

		g.setColor(Color.gray);
		g.drawLine(0, arrowBarYPosition, 15, arrowBarYPosition - 30);
		g.drawLine(15, arrowBarYPosition - 30, 25, arrowBarYPosition - 30);

		g.setFont(SMALL_FONT);
		g.drawString("Min=" + minValue, 28, arrowBarYPosition - 25);

		g.drawLine(track_width, arrowBarYPosition, track_width - 15, arrowBarYPosition + 30);
		g.drawLine(track_width - 15, arrowBarYPosition + 30, track_width - 25,
		           arrowBarYPosition + 30);

		final String maxStr = "Max=" + maxValue;
		int strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), maxStr);
		g.drawString(maxStr, track_width - strWidth - 26, arrowBarYPosition + 35);

		g.setFont(SMALL_FONT);
		g.setColor(Color.black);
		strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), title);
		g.drawString(title, (track_width / 2) - (strWidth / 2), arrowBarYPosition + 35);

		/*
		 * If no points, just draw empty box.
		 */
		if (numPoints == 0) {
			g.setColor(BORDER_COLOR);
			g.setStroke(new BasicStroke(1.5f));
			g.drawRect(0, 5, track_width, trackHeight);

			return;
		}

		g.setStroke(new BasicStroke(1.0f));

		/*
		 * Fill background
		 */
		g.setColor(Color.white);
		g.fillRect(0, 5, track_width, trackHeight);

		rangeObjects = buildIconArray(stops.size() + 1);

		int newX = 0;

		Point2D p1 = new Point2D.Float(0, 5);
		Point2D p2 = new Point2D.Float(0, 5);

		int iconLocX;
		int iconLocY;

		final int iconSize;
		if(type.getDataType().equals(CyCustomGraphics.class))
			iconSize = ICON_SIZE_CG;
		else
			iconSize = ICON_SIZE;
		/*
		 * Draw separators and icons
		 */
		for (i = 0; i < stops.size(); i++) {
			newX = (int) (track_width * (fractions[i] / 100));

			p2.setLocation(newX, 5);
			g.setColor(Color.black);
			g.setStroke(STROKE1);

			g.drawLine(newX, 5, newX, trackHeight + 4);

			g.setColor(Color.DARK_GRAY);
			g.setFont(new Font("SansSerif", Font.BOLD, 10));

			final Float curPositionValue = ((Number) (((fractions[i] / 100) * valueRange)
			                         + minValue)).floatValue();
			final String valueString = String.format("%.5f", curPositionValue);

			int flipLimit = 90;
			int borderVal = track_width - newX;

			if (((i % 2) == 0) && (flipLimit < borderVal)) {
				g.drawLine(newX, arrowBarYPosition, newX + 20, arrowBarYPosition - 15);
				g.drawLine(newX + 20, arrowBarYPosition - 15, newX + 30,
				           arrowBarYPosition - 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX + 33, arrowBarYPosition - 11);
			} else if (((i % 2) == 1) && (flipLimit < borderVal)) {
				g.drawLine(newX, arrowBarYPosition, newX + 20, arrowBarYPosition + 15);
				g.drawLine(newX + 20, arrowBarYPosition + 15, newX + 30,
				           arrowBarYPosition + 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX + 33, arrowBarYPosition + 19);
			} else if (((i % 2) == 0) && (flipLimit >= borderVal)) {
				g.drawLine(newX, arrowBarYPosition, newX - 20, arrowBarYPosition - 15);
				g.drawLine(newX - 20, arrowBarYPosition - 15, newX - 30,
				           arrowBarYPosition - 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX - 90, arrowBarYPosition - 11);
			} else {
				g.drawLine(newX, arrowBarYPosition, newX - 20, arrowBarYPosition + 15);
				g.drawLine(newX - 20, arrowBarYPosition + 15, newX - 30,
				           arrowBarYPosition + 15);
				g.setColor(Color.black);
				g.drawString(valueString, newX - 90, arrowBarYPosition + 19);
			}

			g.setColor(Color.black);
			g.fillOval(newX - 3, arrowBarYPosition - 3, 6, 6);

			
			iconLocX = newX - (((newX - (int) p1.getX()) / 2) + (iconSize / 2));
			iconLocY = ((trackHeight) / 2) - (iconSize / 2) + 5;

			if (i == 0)
				drawIcon(below, g, iconLocX, iconLocY, iconSize);
			else
				drawIcon(objectValues[i], g, iconLocX, iconLocY, iconSize);

			p1.setLocation(p2);
		}

		/*
		 * Draw last region (above region)
		 */
		p2.setLocation(track_width, 5);

		iconLocX = track_width - (((track_width - (int) p1.getX()) / 2) + (iconSize / 2));
		iconLocY = ((trackHeight) / 2) - (iconSize / 2) + 5;
	
		drawIcon(above, g, iconLocX, iconLocY, iconSize);
		/*
		 * Finally, draw border line (rectangle)
		 */
		g.setColor(BORDER_COLOR);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRect(0, 5, track_width, trackHeight);

		g.translate(-THUMB_WIDTH / 2, -12);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param slider DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JComponent getRendererComponent( JXMultiThumbSlider slider) {
		this.slider = slider;
		return this;
	}

	
	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getToolTipForCurrentLocation(int x, int y) {
		int oldX = 0;
		int newX;

		final List<Thumb<T>> stops = slider.getModel().getSortedThumbs();

		int i = 1;

		for (Thumb<T> thumb : stops) {
			newX = (int) (slider.getWidth() * (thumb.getPosition() / 100));

			if ((oldX <= x) && (x <= newX) && (V_PADDING < y) && (y < (V_PADDING + trackHeight)))
				return "This is region " + i;

			i++;
			oldX = newX + 1;
		}

		if ((oldX <= x) && (x <= slider.getWidth()) && (V_PADDING < y)
		    && (y < (V_PADDING + trackHeight)))
			return "Last Area: " + oldX + " - " + slider.getWidth() + " (x, y) = " + x + ", " + y;

		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getObjectInRange(int x, int y) {
		final int range = getRangeID(x, y);

		if (range == -1)
			return null;

		if (range == 0) {
			return null;
		} else if (range == slider.getModel().getThumbCount()) {
		}

		return rangeObjects.get(getRangeID(x, y));
	}

	/*
	 * Get region id.
	 *
	 * +-------------------------------------------
	 * |    0     |      1      |     2     |  ...
	 * +-------------------------------------------
	 *
	 */
	protected int getRangeID(int x, int y) {
		int oldX = 0;
		int newX;

		final List<Thumb<T>> stops = slider.getModel().getSortedThumbs();
		Thumb<T> thumb;
		int i;

		for (i = 0; i < stops.size(); i++) {
			thumb = stops.get(i);
			newX = (int) (slider.getWidth() * (thumb.getPosition() / 100));

			if ((oldX <= x) && (x <= newX) && (V_PADDING < y) && (y < (V_PADDING + trackHeight)))
				return i;

			oldX = newX + 1;
		}

		if ((oldX <= x) && (x <= slider.getWidth()) && (V_PADDING < y)
		    && (y < (V_PADDING + trackHeight)))
			return i;

		// Invalid range
		return -1;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param iconWidth DOCUMENT ME!
	 * @param iconHeight DOCUMENT ME!
	 * @param mapping DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight,
	                                            ContinuousMapping mapping) {
		final BufferedImage bi = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = bi.createGraphics();

		// Turn Anti-alias on
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		final int leftSpace = 2;
		int trackHeight = iconHeight - 15;
		int trackWidth = iconWidth - leftSpace - 5;

		g2.setBackground(Color.white);

		/*
		 * Draw background
		 */
		g2.setColor(Color.white);
		g2.fillRect(0, 0, iconWidth, iconHeight);
		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(Color.black);

		/*
		 * Compute fractions from mapping
		 */
		List<ContinuousMappingPoint> points = mapping.getAllPoints();
		final int pointCount = points.size();

		/*
		 * If no points, just return empty rectangle.
		 */
		if (pointCount == 0) {
			g2.drawRect(leftSpace, 0, trackWidth, trackHeight);

			return new ImageIcon(bi);
		}

		Object[] objValues = new Object[pointCount + 2];

		objValues[0] = points.get(0).getRange().lesserValue;

		if (pointCount == 1) {
			objValues[1] = points.get(0).getRange().equalValue;
			objValues[2] = points.get(0).getRange().greaterValue;
		} else {
			// "Above" value
			objValues[objValues.length - 1] = points.get(points.size() - 1).getRange().greaterValue;

			for (int i = 0; i < pointCount; i++)
				objValues[i + 1] = points.get(i).getRange().equalValue;
		}

		return new ImageIcon(bi);
	}

	private static List buildIconArray(int size) {
		List<ImageIcon> icons = new ArrayList<ImageIcon>();

		Map iconMap = NodeShape.getIconSet();

		Object[] keys = iconMap.keySet().toArray();

		for (int i = 0; i < size; i++)
			icons.add((ImageIcon) iconMap.get(keys[i]));

		return icons;
	}

	/*
	 * Draw icon object based on the given data type.
	 */
	private void drawIcon(Object key, Graphics2D g, int x, int y, final int size) {
		if(key == null)
			return;
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.translate(x, y);
		g.setColor(ICON_COLOR);
		g.setStroke(STROKE2);

		// Special drawing code for shapes.
		switch (type) {
			case NODE_SHAPE:

				final VisualPropertyIcon icon = (VisualPropertyIcon) type.getVisualProperty()
				                                                         .getIconSet().get(key);
				icon.setIconHeight(size);
				icon.setIconWidth(size);
				g.fill(icon.getShape());

				break;

			case EDGE_SRCARROW_SHAPE:
			case EDGE_TGTARROW_SHAPE:

				final VisualPropertyIcon arrowIcon = ((VisualPropertyIcon) type.getVisualProperty()
				                                                         .getIconSet().get(key));
				if(arrowIcon == null) {
					break;
				}
				final int newSize = size;
				arrowIcon.setIconHeight(newSize);
				arrowIcon.setIconWidth(((Number)(newSize*2.5)).intValue());
				
				g.translate(-newSize, 0);
				arrowIcon.paintIcon(this, g, x, y);
				g.translate(newSize, 0);

				break;

			case NODE_FONT_FACE:
			case EDGE_FONT_FACE:

				final Font font = (Font) key;
				final String fontName = font.getFontName();
				g.setFont(new Font(fontName, font.getStyle(), size));
				g.drawString("A", 0, size);

				final int smallFontSize = ((Number) (size * 0.25)).intValue();
				g.setFont(new Font(fontName, font.getStyle(), smallFontSize));

				int stringWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), fontName);
				g.drawString(fontName, (size / 2) - (stringWidth / 2), size + smallFontSize + 2);

				break;

			case NODE_LINE_STYLE:
			case EDGE_LINE_STYLE:

				final Stroke stroke = ((LineStyle) key).getStroke(2.0f);
				final int newSize2 = (int) (size * 1.5);
				g.translate(0, -size * 0.25);
				g.setColor(Color.DARK_GRAY);
				g.drawRect(0, 0, size, newSize2);
				g.setStroke(stroke);
				g.setColor(ICON_COLOR);
				g.drawLine(size - 1, 1, 1, newSize2 - 1);
				g.translate(0, size * 0.25);

				break;
				
			default:
				break;
		}
		
		
		if(type.getDataType().equals(ObjectPosition.class)) {
			// Object Positions
			
			final ObjectPlacerGraphic lp = new ObjectPlacerGraphic((ObjectPosition) key,
			                                                     (int) (size * 1.5), false, type.getName(), null, null);
			lp.paint(g);
		} else if(type.getDataType().equals(CyCustomGraphics.class)) {
			// Custom Graphics
			final CyCustomGraphics cg = (CyCustomGraphics) key;
			final Image rendered = cg.getRenderedImage();
			if(rendered != null)
				g.drawImage(rendered, 0, 0, size, size, null);
		} else {
			// Draw as String.
			g.drawString(key.toString(), 0, g.getFont().getSize()*2);
		}

		g.translate(-x, -y);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param iconWidth DOCUMENT ME!
	 * @param iconHeight DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight) {
		return drawIcon(iconWidth, iconHeight, false);
	}
	
	public ImageIcon getLegend(int iconWidth, int iconHeight) {
		return drawIcon(iconWidth, iconHeight, true);
	}
	
	private ImageIcon drawIcon(int iconWidth, int iconHeight, boolean detail) {
		if (slider == null) {
			return null;
		}

		final BufferedImage bi = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = bi.createGraphics();

		// Turn AA on.
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		// Fill background
		g.setColor(Color.white);
		g.fillRect(0, 0, iconWidth, iconHeight);
		
		double minValue = EditorValueRangeTracer.getTracer().getMin(type);
		double maxValue = EditorValueRangeTracer.getTracer().getMax(type);
		double valueRange = EditorValueRangeTracer.getTracer().getRange(type);

		int track_width = iconWidth;
		int trackHeight = iconHeight - 8;
		if(detail) {
			trackHeight = iconHeight - 30;
			smallIconSize = (int) (trackHeight * 0.5);
		} else {
			trackHeight = iconHeight - 8;
		}

		//		 get the list of tumbs
		final List<Thumb<T>> stops = slider.getModel().getSortedThumbs();

		int numPoints = stops.size();

		// set up the data for the gradient
		float[] fractions = new float[numPoints];
		Object[] objectValues = new Object[numPoints];

		/*
		 * Find min, max, and ranges
		 */
		int i = 0;

		for (Thumb thumb : stops) {
			objectValues[i] = thumb.getObject();
			fractions[i] = thumb.getPosition();
			i++;
		}

		/*
		 * If no points, just draw empty box.
		 */
		if (numPoints == 0) {
			g.setColor(BORDER_COLOR);
			g.setStroke(new BasicStroke(1.0f));
			g.drawRect(0, 0, track_width - 3, trackHeight);

			return new ImageIcon(bi);
		}

		rangeObjects = buildIconArray(stops.size() + 1);

		int newX = 0;

		Point2D p1 = new Point2D.Float(0, 5);
		Point2D p2 = new Point2D.Float(0, 5);

		int iconLocX;
		int iconLocY;

		/*
		 * Draw separators and icons
		 */
		for (i = 0; i < stops.size(); i++) {
			newX = (int) (track_width * (fractions[i] / 100));

			p2.setLocation(newX, 0);
			iconLocX = newX - (((newX - (int) p1.getX()) / 2) + (smallIconSize / 2));
			iconLocY = ((trackHeight) / 2) - (smallIconSize / 2);

			if (i == 0) {
				drawIcon(below, g, iconLocX, iconLocY, smallIconSize);
			} else {
				drawIcon(objectValues[i], g, iconLocX, iconLocY, smallIconSize);
			}

			g.setColor(Color.DARK_GRAY);
			g.setStroke(STROKE1);
			g.drawLine(newX, 0, newX, trackHeight);

			p1.setLocation(p2);
		}

		/*
		 * Draw last region (above region)
		 */
		p2.setLocation(track_width, 0);

		iconLocX = track_width - (((track_width - (int) p1.getX()) / 2) + (smallIconSize / 2));
		iconLocY = ((trackHeight) / 2) - (smallIconSize / 2);
		drawIcon(above, g, iconLocX, iconLocY, smallIconSize);

		/*
		 * Finally, draw border line (rectangle)
		 */
		g.setColor(BORDER_COLOR);
		g.setStroke(new BasicStroke(1.0f));
		g.drawRect(0, 0, track_width - 3, trackHeight);
		
		
		g.setFont(new Font("SansSerif", Font.BOLD, 9));

		final String minStr = String.format("%.2f", minValue);
		final String maxStr = String.format("%.2f", maxValue);
		int strWidth;
		g.setColor(Color.black);
		if(detail) {
			String fNum = null;
			for(int j=0; j<fractions.length; j++) {
				fNum = String.format("%.2f", ((fractions[j]/100)*valueRange) + minValue);
				strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), fNum);
				g.drawString(fNum, (fractions[j]/100)*iconWidth-strWidth/2, iconHeight-20);
			}
			
			g.drawString(minStr, 0, iconHeight);
			strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), maxStr);
			g.drawString(maxStr, iconWidth - strWidth - 2, iconHeight);
			
			g.setFont(TITLE_FONT);

			final int titleWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), title);
			g.setColor(Color.black);
			g.drawString(title, (iconWidth / 2) - (titleWidth / 2),
			             iconHeight-5);
			Polygon p = new Polygon();
			p.addPoint(iconWidth,iconHeight-9);
			p.addPoint(iconWidth-15,iconHeight-15 );
			p.addPoint(iconWidth-15,iconHeight-9 );
			g.fillPolygon(p);
			g.drawLine(0, iconHeight-9, (iconWidth / 2) - (titleWidth / 2)-3, iconHeight-9);
			g.drawLine((iconWidth / 2) + (titleWidth / 2)+3, iconHeight-9, iconWidth, iconHeight-9);
			
		} else {
			g.drawString(minStr, 0, iconHeight);
			strWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), maxStr);
			g.drawString(maxStr, iconWidth - strWidth - 2, iconHeight);
		}
		
		
		

		return new ImageIcon(bi);
	}
	

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Double getSelectedThumbValue() {
		final double minValue = EditorValueRangeTracer.getTracer().getMin(type);
		final double valueRange = EditorValueRangeTracer.getTracer().getRange(type);
		
		final float position = slider.getModel().getThumbAt(slider.getSelectedIndex()).getPosition();

		return (((position / 100) * valueRange) + minValue);
	}
}
