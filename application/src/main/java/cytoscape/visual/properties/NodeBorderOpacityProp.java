package cytoscape.visual.properties;

import cytoscape.visual.VisualPropertyDependency;

import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Properties;

import javax.swing.Icon;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.icon.NodeIcon;

public class NodeBorderOpacityProp extends AbstractVisualProperty {

	public NodeBorderOpacityProp() {
		validator = new OpacityValidator();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_BORDER_OPACITY;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(final Object value) {
		final NodeIcon icon = new NodeIcon() {
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 0));
				super.paintIcon(c, g, x, y);
				g2d.translate(0, -2);

				final Color color = ((Color) VisualPropertyType.NODE_BORDER_COLOR
				                                                                   .getDefault(Cytoscape.getVisualMappingManager()
				                                                                                        .getVisualStyle()));
				g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
				                       ((Number) value).intValue()));
				g2d.setStroke(new BasicStroke(2f));
				g2d.draw(super.newShape);
				g2d.translate(0, 2);

				g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawString(value.toString(), c.getX() + 7,
				               (int) ((c.getHeight() / 2) + 7));

				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
			}
		};

		return icon;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nv DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToNodeView(NodeView nv, Object o, VisualPropertyDependency dep) {
		if ((o == null) || (nv == null))
			return;

		Integer tp = ((Color) nv.getBorderPaint()).getAlpha();

		Integer newTp = ((Number) o).intValue();

		if (tp != newTp) {
			final Color oldPaint = (Color) nv.getBorderPaint();
			nv.setBorderPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(),
			                                oldPaint.getBlue(), newTp));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return new Integer(255);
	}
}
