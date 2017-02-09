/***********************************************************************************************************
Problem 5 (30 points): Burger and Burge problem number 5.1 (page 83). For this problem, you should
create a plugin called AutoContrast_quantiles.
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

public class AutoContrast_quantiles implements PlugInFilter {
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;// this plugin accepts 8-bit grayscale images
	}

	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		int H[] = new int[256];
		int a_min = 0;
		int a_max = 255;
		int a_low = 0;
		int a_high = 0;
		int pixel_number = w * h;
		double intensity_range = 0.01;

		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int p = ip.getPixel(u, v);
				H[p] = H[p] + 1;
			}
		}
		int temp = 0;
		for (int mincount = 0; mincount < 256; mincount++) {
			temp = temp + H[mincount];
			if (temp > pixel_number * intensity_range){
				a_low = mincount;
				break;
			}	
		}
		temp = 0;
		for (int maxcount = 0; maxcount < 256; maxcount++) {
			temp = temp + H[maxcount];
			if (temp > pixel_number * (1 - intensity_range)){
				a_high = maxcount;	
				break;
			}
		}
		
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int p = ip.getPixel(u, v);
				if (p <= a_low) {
					ip.putPixel(u, v, a_min);
				} else if (p >= a_high) {
					ip.putPixel(u, v, a_max);
				} else {
					int s = a_min + (p - a_low) * (a_max - a_min)
							/ (a_high - a_low);
					ip.putPixel(u, v, s);
				}

			}
		}
		// iterate over all image coordinates
		/*
		 * for (int u = 0; u < w; u++) { for (int v = 0; v < h; v++) { int p =
		 * ip.getPixel(u, v); if (p < a_low) { a_low = p; } if (p > a_high) {
		 * a_high = p; } } } for (int u = 0; u < w; u++) { for (int v = 0; v <
		 * h; v++) { int p = ip.getPixel(u, v); double s = (p - a_low) * 255 /
		 * (a_high - a_low); ip.putPixel(u, v, (int) s); } }
		 */
		IJ.showMessage("a_high:" + a_high + "\na_low:" + a_low);
	}
}