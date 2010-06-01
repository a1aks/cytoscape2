package cytoscape.visual.properties;

import giny.view.NodeView;
import giny.view.ObjectPosition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.ObjectPlacerGraphic;
import cytoscape.visual.ui.icon.NodeIcon;
import ding.view.DNodeView;
import ding.view.ObjectPositionImpl;

public class NodeCustomGraphicsPositionProp extends AbstractVisualProperty {
	
	private static final int ICON_SIZE = 55;
	
	

	private int index;

	public NodeCustomGraphicsPositionProp(final Integer index) {
		super();
		this.index = index - 1;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.getCustomGraphicsPositionType(index);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param labelPos
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Icon getIcon(Object value) {

		final BufferedImage bi = new BufferedImage(ICON_SIZE, ICON_SIZE,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();

		final ObjectPlacerGraphic lp = new ObjectPlacerGraphic(
				(ObjectPosition) value, ICON_SIZE, false, "Custom Graphics", null,
				null);
		lp.paint(g2);

		NodeIcon icon = new NodeIcon() {

			private static final long serialVersionUID = -3190338664704873605L;

			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 0));
				super.paintIcon(c, g, x, y);
				g2d.drawImage(bi, 10, -5, null);
			}
		};

		return icon;
	}

	/**
	 * Apply Object Position to DNodeView's Custom Graphics.
	 * 
	 * @param nv
	 *            - NodeView. Currently, only supports DNodeView implementation.
	 * @param o
	 *            This should be an ObjectPosition.
	 */
	public void applyToNodeView(NodeView nv, Object o,
			VisualPropertyDependency dep) {

		// This implementation is for Ding only.
		if ((o == null) || (nv == null) || o instanceof ObjectPosition == false
				|| nv instanceof DNodeView == false)
			return;

		final ObjectPosition p = (ObjectPosition) o;
		final DNodeView dv = (DNodeView) nv;
		final NodeCustomGraphicsProp customGraphicsProp = (NodeCustomGraphicsProp) VisualPropertyType
				.getCustomGraphicsType(index).getVisualProperty();

		
		final Set<CustomGraphic> currentCG = customGraphicsProp.getCurrentCustomGraphics(dv);
		if(currentCG == null || currentCG.size() == 0) {
			System.out.println(nv.getNode().getIdentifier() + ": CG is empty for this location! " + VisualPropertyType
					.getCustomGraphicsPositionType(index));
			return;
		}

		System.out.println(p.toString() + " = ============= Process Position Prop ======================= " + nv.getNode().getIdentifier());
		
		final List<CustomGraphic> newList = new ArrayList<CustomGraphic>();
		for (CustomGraphic g : currentCG) {
			newList.add(dv.setCustomGraphicsPosition(g, p));
			dv.removeCustomGraphic(g);
		}

		currentCG.clear();
		currentCG.addAll(newList);


		System.out.println(VisualPropertyType
				.getCustomGraphicsPositionType(index)
				+ ": Number of registered Custom Graphics: "
				+ dv.getNumCustomGraphics());
		}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return new ObjectPositionImpl();
	}
}
