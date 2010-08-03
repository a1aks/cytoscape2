package cytoscape.visual.customgraphic.impl.vector;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.customgraphic.IDGenerator;
import cytoscape.visual.customgraphic.Layer;
import cytoscape.visual.customgraphic.impl.DLayer;
import cytoscape.visual.customgraphic.paint.GradientPaintFactory;

public class GradientRoundRectangleLayer extends GradientLayerCustomGraphics {
	
	// Name of this custom graphics.
	private static final String NAME = "Glossy Round Rectangle Layer";
	private int r =20;
	
	
	public GradientRoundRectangleLayer() {
		this(IDGenerator.getIDGenerator().getNextId(), NAME);
	}
	
	
	public GradientRoundRectangleLayer(Long id, String name) {
		super(id, name);
	}
	
	protected void renderImage(Graphics graphics) {
		super.renderImage(graphics);
		
		final Graphics2D g2d = (Graphics2D) graphics;
		// Render
		update();
		g2d.setPaint(paintFactory.getPaint(bound.getBounds2D()));
		g2d.fillRoundRect(rendered.getMinX(), rendered.getMinY(), 
				rendered.getWidth(), rendered.getHeight(), r, r);
	}
	
	
	public void update() {
		// First, remove all layers.
		layers.clear();
		
		r = (int)(Math.min(width, height)/4f);
		bound = new RoundRectangle2D.Double(-width / 2, -height / 2,
																	width, height, r, r);
		paintFactory = new GradientPaintFactory(c1.getValue(), c2.getValue());
		final CustomGraphic cg = new CustomGraphic(bound, paintFactory);
		final Layer layer = new DLayer(cg, 1);
		layers.add(layer);
	}

}
