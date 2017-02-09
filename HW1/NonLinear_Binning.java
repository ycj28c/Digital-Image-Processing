/***********************************************************************************************************
Problem 4 (25 points): Burger and Burge problem number 4.3 (page 51). For this problem, you should
create a plugin called NonLinear_Binning that solves the problem described. Create a table of 10
arbitrary ranges and pick appropriate intervals. It should be possible for the ranges you choose in your
program to be changed and the program recompiled
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

public class NonLinear_Binning implements PlugInFilter {
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;// this plugin accepts 8-bit grayscale
									// images
	}

	public void run(ImageProcessor ip) {
		// int[] H = new int[256];
		int image_bit = 8;
		// double temp = IJ.getNumber("enter number of range", 0);
		// int range = (int) temp;
		int[] H = new int[10];
		int w = ip.getWidth();
		int h = ip.getHeight();
		int s = 0;

		ImageProcessor histIp = new ByteProcessor(310, 250);
		histIp.setValue(255);// white
		histIp.fill();

		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int p = ip.getPixel(u, v);
				if (p >= 0 && p < 20)
					s = 0;
				// H[0]=H[0]+1;
				else if (p >= 20 && p < 50)
					s = 1;
				// H[1]=H[1]+1;
				else if (p >= 50 && p < 80)
					s = 2;
				// H[2]=H[2]+1;
				else if (p >= 80 && p < 100)
					s = 3;
				// H[3]=H[3]+1;
				else if (p >= 100 && p < 120)
					s = 4;
				// H[4]=H[4]+1;
				else if (p >= 120 && p < 140)
					s = 5;
				// H[5]=H[5]+1;
				else if (p >= 140 && p < 160)
					s = 6;
				// H[6]=H[6]+1;
				else if (p >= 160 && p < 190)
					s = 7;
				// H[7]=H[7]+1;
				else if (p >= 190 && p < 230)
					s = 8;
				// H[8]=H[8]+1;
				else if (p >= 230 && p < 256)
					s = 9;

				H[s] = H[s] + 1;
				histIp.putPixel(5 + s * 30, 250 - H[s] / 1000, 0);
			}
			// iterate over all image coordinates
			/*
			 * for (int u = 0; u < w; u++) { for (int v = 0; v < h; v++) { int p
			 * = ip.getPixel(u, v); int s = (int) (p * range / Math.pow(2,
			 * image_bit));// S = T(R) H[s] = H[s] + 1;
			 * histIp.putPixel(s*30,250-(int)H[s]/1000,0); }
			 */
		}
		ImagePlus histIm = new ImagePlus("showimage", histIp);
		histIm.show();
		// int[] H = ip.getHistogram();
		for (int count = 0; count < 10; count++) {
			IJ.log("H[" + count + "]:" + H[count]);

		}

	}
}