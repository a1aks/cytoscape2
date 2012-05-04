
package cytoscape.visual.strokes;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import static cytoscape.visual.LineStyle.DOT;

public class DotStroke extends ShapeStroke {

	public DotStroke(float width) {
		super( new Shape[] { new Ellipse2D.Float(0, 0, width, width) }, width * 2f, DOT, width );
		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new DotStroke(w);
	}
}


