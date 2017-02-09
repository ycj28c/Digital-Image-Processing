import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramMatcher;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.Util;

public class histogram_spec_averages implements PlugInFilter {

	public int setup(String arg0, ImagePlus imA) {
		return DOES_8G;
	}

	public void run(ImageProcessor ipA) {
		int[] windowList = WindowManager.getIDList();
		if (windowList == null) {
			IJ.noImage();
			// return false;
		}
		// get image titles
		ImagePlus[] imp = new ImagePlus[windowList.length];
		ImagePlus target_image = new ImagePlus();
		String[] windowTitles = new String[windowList.length];
		for (int i = 0; i < windowList.length; i++) {
			imp[i] = WindowManager.getImage(windowList[i]);
			if (imp != null)
				windowTitles[i] = imp[i].getShortTitle();
			else
				windowTitles[i] = "untitled";
		}
		int img2Index = 0;
		GenericDialog gd = new GenericDialog("Select Target Image");
		gd.addChoice("Target Image:", windowTitles, windowTitles[0]);
		gd.showDialog();
		if (gd.wasCanceled()) {
			// return false;
		} else {
			img2Index = gd.getNextChoiceIndex();
			target_image = WindowManager.getImage(windowList[img2Index]);
			// return true;
		}

		ImageProcessor iptarget = target_image.getProcessor();
		int[] Htarget = iptarget.getHistogram();
		int[] H_ave = new int[Htarget.length];
		// int[] hh = new int[Htarget.length];
		// IJ.showMessage("ddddd:"+hB.length);
		/*
		 * for(int i=0;i<Htarget.length;i++){ Htarget = (hB[i]+hC[i])/2; }
		 */
		for (int i = 0; i < windowList.length; i++) {
			for (int j = 0; j < Htarget.length; j++) {
				if (i == img2Index)
					continue; // the target image not counted
				H_ave[j] = imp[i].getProcessor().getHistogram()[j]
						/ Htarget.length;
			}
		}

		HistogramMatcher m = new HistogramMatcher();
		int[] F = m.matchHistograms(Htarget, H_ave);
		iptarget.applyTable(F);

		for (int count = 0; count < windowList.length; count++) {
			if (count == img2Index)
				IJ.log("target image: " + windowTitles[count]);
			else
				IJ.log("reference image:" + windowTitles[count]);

		}
	}
}
