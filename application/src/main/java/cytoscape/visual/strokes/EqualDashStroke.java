

package cytoscape.visual.strokes;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import cytoscape.visual.LineStyle;
import static cytoscape.visual.LineStyle.EQUAL_DASH;

public class EqualDashStroke extends BasicStroke implements WidthStroke {

	private float width;

	public EqualDashStroke(float width) {
		super(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
		      10.0f, new float[]{width * 2f,width * 2f}, 0.0f);

		this.width = width;
	}

	public WidthStroke newInstanceForWidth(float w) {
		return new EqualDashStroke(w);
	}

	public LineStyle getLineStyle() {
		return EQUAL_DASH;
	}

	public String toString() { return EQUAL_DASH.toString() + " " + Float.toString(width); }
}


