package cytoscape.visual.customgraphic.paint;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import cytoscape.render.stateful.PaintFactory;

public class GradientPaintFactory implements PaintFactory {
	
	private Color c1;
	private Color c2;

	public GradientPaintFactory(Color c1, Color c2) {
		this.c1 = c1;
		this.c2 = c2;
	}
	
	@Override
	public Paint getPaint(Rectangle2D bound) {
		return new GradientPaint((float)bound.getWidth()/2, 0, c2,
				(float)bound.getWidth()/2, (float)bound.getHeight()/2, c1);
	}

}
