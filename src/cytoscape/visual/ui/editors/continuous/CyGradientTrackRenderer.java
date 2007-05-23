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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.multislider.Thumb;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class CyGradientTrackRenderer extends JComponent implements VizMapperTrackRenderer {
	private int trackHeight = 40;
	private final Font SMALL_FONT = new Font("SansSerif", Font.BOLD, 10);
	private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 12);

	//private Paint checker_paint;
	private JXMultiThumbSlider<Color> slider;
	private double minValue;
	private double maxValue;
	private double range;
	private Color below;
	private Color above;
	private String attrName;

	/**
	 * Creates a new GradientTrackRenderer object.
	 *
	 * @param gradientPicker
	 *            DOCUMENT ME!
	 */
	public CyGradientTrackRenderer(double minValue, double maxValue, Color below, Color above,
	                               String title) {
		//checker_paint = ColorUtil.getCheckerPaint();
		this.below = below;
		this.above = above;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.attrName = title;

		this.range = Math.abs(maxValue - minValue);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g
	 *            DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		super.paint(g);
		paintComponent(g);
	}

	protected void paintComponent(Graphics gfx) {
		Graphics2D g = (Graphics2D) gfx;

		// Turn AA on
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//		 calculate the track area
		int thumb_width = 12;
		int track_width = slider.getWidth() - thumb_width;
		g.translate(thumb_width / 2, 12);

		// get the list of colors
		List<Thumb<Color>> stops = slider.getModel().getSortedThumbs();
		int len = stops.size();

		if (len != 0) {
			// set up the data for the gradient
			float[] fractions = new float[len + 2];
			Color[] colors = new Color[len + 2];
			int i = 1;

			colors[0] = below;
			fractions[0] = stops.get(0).getPosition() / 100;

			for (Thumb<Color> thumb : stops) {
				colors[i] = (Color) thumb.getObject();

				fractions[i] = thumb.getPosition() / 100;

				g.setColor(colors[i]);
				g.setFont(SMALL_FONT);

				String valueString;
				Double value = minValue + (fractions[i] * range);

				if ((Math.abs(minValue) < 3) || (Math.abs(maxValue) < 3))
					valueString = String.format("%.4f", value);
				else
					valueString = String.format("%.2f", value);

				final int stringWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(),
				                                                          valueString);
				final int curPosition = (int) (track_width * fractions[i]);

				if (curPosition < (stringWidth / 2))
					g.drawString(valueString, curPosition, trackHeight + 15);
				else if ((track_width - curPosition) < (stringWidth / 2))
					g.drawString(valueString, curPosition - stringWidth, trackHeight + 15);
				else
					g.drawString(valueString, curPosition - (stringWidth / 2), trackHeight + 15);

				i++;
			}

			colors[colors.length - 1] = above;
			fractions[fractions.length - 1] = stops.get(stops.size() - 1).getPosition() / 100;

			g.setStroke(new BasicStroke(1.0f));

			// fill in the gradient
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(track_width, trackHeight);

			drawGradient(g, start, end, fractions, colors);
		}

		// Define rectangle
		Rectangle2D rect = new Rectangle(0, 0, track_width, trackHeight);
		g.setColor(Color.gray);
		g.drawLine((int) rect.getBounds2D().getMinX(), (int) rect.getBounds2D().getMaxY(), 8,
		           (int) rect.getBounds2D().getMaxY() + 25);
		g.setFont(SMALL_FONT);
		g.drawString("Min=" + minValue, (int) rect.getBounds2D().getMinX(),
		             (int) rect.getBounds2D().getMaxY() + 38);

		g.drawLine((int) rect.getBounds2D().getMaxX(), (int) rect.getBounds2D().getMaxY(),
		           (int) rect.getBounds2D().getMaxX() - 8, (int) rect.getBounds2D().getMaxY() + 25);
		g.setFont(SMALL_FONT);

		final String maxString = "Max=" + maxValue;
		g.drawString(maxString,
		             (int) rect.getBounds2D().getMaxX()
		             - SwingUtilities.computeStringWidth(g.getFontMetrics(), maxString),
		             (int) rect.getBounds2D().getMaxY() + 38);

		g.setFont(TITLE_FONT);

		final int titleWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), attrName);
		g.setColor(Color.black);
		g.drawString(attrName, ((int) rect.getBounds2D().getWidth() / 2) - (titleWidth / 2),
		             (int) rect.getBounds2D().getMaxY() + 33);

		// draw a border
		g.draw(rect);
		g.translate(-thumb_width / 2, -12);
	}

	private static void drawGradient(Graphics2D g, Point2D start, Point2D end, float[] fractions,
	                                 Color[] colors) {
		if (fractions.length < 1)
			return;

		final int width = (int) (end.getX() - start.getX());
		final int height = (int) (end.getY() - start.getY());

		if (colors.length == 3) {
			final int pivot = (int) (fractions[1] * width);
			g.setColor(colors[0]);
			g.fillRect((int) start.getX(), (int) start.getY(), pivot, height);
			g.setColor(colors[2]);
			g.fillRect(pivot, (int) start.getY(), width - pivot, height);

			g.setColor(colors[1]);
			g.drawLine(pivot, (int) start.getY(), pivot, height);
		} else if (colors.length > 3) {
			int pivot = (int) (fractions[1] * width);
			g.setColor(colors[0]);
			g.fillRect((int) start.getX(), (int) start.getY(), pivot, height);

			int nextPivot;

			for (int i = 1; i < (colors.length - 2); i++) {
				nextPivot = (int) (width * fractions[i + 1]);

				GradientPaint gp = new GradientPaint(pivot, height / 2, colors[i], nextPivot,
				                                     height / 2, colors[i + 1]);
				g.setPaint(gp);
				g.fillRect(pivot, 0, nextPivot - pivot, height);
				pivot = nextPivot;
			}

			final int lastPivot = (int) (fractions[fractions.length - 1] * width);
			g.setColor(colors[colors.length - 1]);
			g.fillRect(lastPivot, (int) start.getY(), width - lastPivot, height);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param slider
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public JComponent getRendererComponent(JXMultiThumbSlider slider) {
		this.slider = slider;
		trackHeight = slider.getHeight() - 50;

		return this;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x
	 *            DOCUMENT ME!
	 * @param y
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getObjectInRange(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x
	 *            DOCUMENT ME!
	 * @param y
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getToolTipForCurrentLocation(int x, int y) {
		// TODO Auto-generated method stub
		return null;
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
	public ImageIcon getTrackGraphicIcon(int iconWidth, int iconHeight) {
		if (slider == null) {
			return null;
		}

		final BufferedImage bi = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = bi.createGraphics();

		// Turn AA on.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		/*
		 * Fill background
		 */
		g2.setColor(Color.white);
		g2.fillRect(0, 0, iconWidth, iconHeight);

		List<Thumb<Color>> stops = slider.getModel().getSortedThumbs();
		int len = stops.size();

		if (len != 0) {
			// set up the data for the gradient
			float[] fractions = new float[len + 2];
			Color[] colors = new Color[len + 2];
			int i = 1;

			colors[0] = below;
			fractions[0] = stops.get(0).getPosition() / 100;

			for (Thumb<Color> thumb : stops) {
				colors[i] = (Color) thumb.getObject();
				fractions[i] = thumb.getPosition() / 100;
				i++;
			}

			colors[colors.length - 1] = above;
			fractions[fractions.length - 1] = stops.get(stops.size() - 1).getPosition() / 100;

			// fill in the gradient
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(iconWidth - 3, iconHeight - 9);

			drawGradient(g2, start, end, fractions, colors);
			
		}

		
		// Draw border line
		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(Color.DARK_GRAY);
		g2.drawRect(0, 0, iconWidth - 3, iconHeight - 9);
		
		g2.setFont(new Font("SansSerif", Font.BOLD, 9));

		String numbers = String.format("%.2f", minValue);
		int strWidth;
		g2.setColor(Color.black);
		g2.drawString(numbers, 0, iconHeight);

		numbers = String.format("%.2f", maxValue);
		strWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), numbers);
		g2.drawString(numbers, iconWidth - strWidth - 2, iconHeight);

		return new ImageIcon(bi);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Double getSelectedThumbValue() {
		final float position = slider.getModel().getThumbAt(slider.getSelectedIndex()).getPosition();
		final double thumbVal = (((position / 100) * range) - Math.abs(minValue));

		return thumbVal;
	}
}
