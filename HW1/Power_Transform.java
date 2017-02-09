/***********************************************************************************************************
Problem 3 (15 points): Power Transform: Create an ImageJ plugin Power_Transform which performs a
power law transformation on an image. This function should an 8©\bit grayscale image and the gamma
value (as a variable you can modify in your program) to transform the image. Remember that power law
transformations are achieved using the simple formula:
where s is the processed pixel value, r is the original pixel value, ¦Ã is the parameter controlling the power
law transformation and c is a constant usually set to 1. Try this new function out on the following images
(spine.jpg and runway.jpg), experimenting with different values for ¦Ã. In the comments of your plugin,
state what values of ¦Ã worked best for each spine.jpg and for runway.jpg
 ***********************************************************************************************************/
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class Power_Transform implements PlugInFilter {
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;// this plugin accepts 8-bit grayscale images
	}

	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		double gamma = IJ.getNumber("enter number of gamma:", 0);
		// runway.jpg use gamma 2.5
		// spine.jgp use gamma 0.4
		int K = 256;
		int aMax = K - 1;

		int[] Fgc = new int[K];
		for (int a = 0; a < K; a++) {
			double aa = (double) a / aMax;
			double bb = Math.pow(aa, gamma);
			int b = (int) Math.round(bb * aMax);
			Fgc[a] = b;
		}
		ip.applyTable(Fgc);
		// iterate over all image coordinates
		/*
		 * for (int u = 0; u < w; u++) { for (int v = 0; v < h; v++) { int r =
		 * ip.getPixel(u, v); int c = 1; // best rumway.jpg y = 0.9 // best
		 * spine.jgp y = 1.3 int s = (int) Math.pow(r, gamma); s = clamp(s);
		 * ip.putPixel(u, v, s); } }
		 */
	}

	private int clamp(int x) {
		if (x < 0)
			x = 0;
		if (x > 255)
			x = 255;
		return x;
	}
}