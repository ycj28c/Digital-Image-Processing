/***********************************************************************************************************
Problem 1 (15 points): Gray level modification: Create an ImageJ plugin GrayLevel_Modification that
modifies the values of an 8©\bit grayscale input image according to the function s = 16 sqrt(r), where r is
the input intensity and s is the processed intensity. the factor of 16 guarantees that the result will be in
the range 0 to 255. Use this plugin to modify any currently open 8©\bit grayscale image.
 ***********************************************************************************************************/
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

//import java.math.*;

public class GrayLevel_Modification implements PlugInFilter {
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;// this plugin accepts 8-bit grayscale images
	}

	public void run(ImageProcessor ip) {
		// int[] H = new int[256];
		int w = ip.getWidth();
		int h = ip.getHeight();
		// iterate over all image coordinates
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int p = ip.getPixel(u, v);
				double temp;
				temp = Math.sqrt(p);
				int s = 16 * (int) temp;// S = T(R)
				// s = 255 - p invert
				ip.putPixel(u, v, s);
			}
		}
	}

}