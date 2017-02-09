/***********************************************************************************************************
Problem 2 (15 points): (Thresholding): Create a plugin called Median_Threshold that sets the threshold
value to the median of the histogram. Use this plugin to threshold an input image 8©\bit grayscale image.
Recall that the median m is the value that satisfies P(x < m) = P(x > m), in other words, half of the
intensity values are less and half of the values are greater than m.
 ***********************************************************************************************************/
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
//import java.math.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class Median_Threshold implements PlugInFilter {
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;// this plugin accepts 8-bit grayscale
						// images
	}

	public void run(ImageProcessor ip) {
		int[] H = new int[256];
		// private static int m = 0;
		int pixel_number = 0;
		int histogram_number = 0;
		int w = ip.getWidth();
		int h = ip.getHeight();

		// iterate over all image coordinates
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int p = ip.getPixel(u, v);
				if (H[p] == 0) {
					histogram_number++;
				}
				H[p] = H[p] + 1;
				pixel_number++;
			}
		}
		int temp = 0;
		int pcount = 0;
		for (pcount = 0; pcount < 255; pcount++) {
			temp = temp + H[pcount];
			if (temp > pixel_number / 2){
				pcount++;
				break;
			}	
		}
		int hcount = 0;
		temp = histogram_number / 2 + 1;
		for (hcount = 0; hcount < 255; hcount++) {
			if (H[hcount] != 0) {
				temp--;
				if (temp == 0)
					break;
			}
		}
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int p = ip.getPixel(u, v);
				if (p < pcount)
					ip.putPixel(u, v, 0);
				else
					ip.putPixel(u, v, 255);
			}
		}
		IJ.showMessage("Caculate median", "total pixels = " + pixel_number
				+ "\nmedian pixel=" + pcount + "\nmedian intensity value="
				+ hcount);
	}
}