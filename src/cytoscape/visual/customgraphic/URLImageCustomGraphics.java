package cytoscape.visual.customgraphic;

import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.visual.customgraphic.paint.TexturePaintFactory;

public class URLImageCustomGraphics extends AbstractCyCustomGraphics {

	private static final String DEF_TAG = "bitmap image";
	// Defining padding
	private static final double PAD = 10;
	private static final double R = 28;

	private CustomGraphic cg;

	private BufferedImage originalImage;
	private BufferedImage scaledImage;
	
	private URL sourceUrl;

	public URLImageCustomGraphics(String url) throws IOException {
		super(url);
		this.tags.add(DEF_TAG);
		createImage(url);
		buildCustomGraphics(originalImage);
	}
	
	
	/**
	 * 
	 * @param name - display name of this object.  NOT UNIQUE!
	 * @param img
	 */
	public URLImageCustomGraphics(String name, BufferedImage img) {
		super(name);
		if(img == null)
			throw new IllegalArgumentException("Image cannot be null.");
		
		this.tags.add(DEF_TAG);
		this.originalImage = img;
		buildCustomGraphics(originalImage);
	}

	private void buildCustomGraphics(BufferedImage targetImg) {
		cgList.clear();
		
		Rectangle2D bound = null;
		Paint paint = null;
		final int imageW = targetImg.getWidth();
		final int imageH = targetImg.getHeight();

		final Shape background = new java.awt.geom.RoundRectangle2D.Double(
				-imageW / 2d - PAD, -imageH / 2d - PAD, imageW + PAD * 2d,
				imageH + PAD * 2d, R, R);

		bound = new Rectangle2D.Double(-imageW / 2, -imageH / 2, imageW, imageH);
		final PaintFactory paintFactory = new TexturePaintFactory(targetImg);

		cg = new CustomGraphic(bound, paintFactory);
		cgList.add(cg);
	}

	private void createImage(String url) throws IOException {
		if(url == null)
			throw new IllegalStateException("URL string cannot be null.");
		
		final URL imageLocation = new URL(url);
		sourceUrl = imageLocation;
		originalImage = ImageIO.read(imageLocation);
		
		if(originalImage == null)
			throw new IOException("Could not create an image from this location: " + imageLocation.toString());
		
		System.out.println("######## Image Created: " + originalImage);
	}

	@Override
	public Image getImage() {
		if (scaledImage == null)
			return originalImage;
		else
			return scaledImage;
	}

	@Override
	public Image resizeImage(int width, int height) {
		final Image img = originalImage.getScaledInstance(width, height,
				Image.SCALE_AREA_AVERAGING);
		try {
			scaledImage = ImageUtil.toBufferedImage(img);
		} catch (InterruptedException e) {
			// Could not get scaled one
			e.printStackTrace();
			return originalImage;
		}
		buildCustomGraphics(scaledImage);
		return scaledImage;
	}
	
	public Image resetImage() {
		if(scaledImage != null) {
			scaledImage.flush();
			scaledImage = null;
		}
		buildCustomGraphics(originalImage);
		return originalImage;
	}
	
	
	/**
	 * This will be used to save this Visual Property.
	 */
	public String toString() {
		return this.getClass().getName() + "," + this.hashCode() + "," + this.displayName;
	}
	
	
	public URL getSourceURL() {
		return this.sourceUrl;
	}
	
}
