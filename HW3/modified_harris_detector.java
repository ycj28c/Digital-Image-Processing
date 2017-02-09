import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import hwthreejar.HarrisCornerDetector;
import ij.process.ImageStatistics;

public class modified_harris_detector implements PlugInFilter {
	
	ImagePlus im;
	static int nmax = 0;	//points to show
	double contrastRate = 0;
	//int col_Depth = 0;

    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
    	contrastRate = getcontrast();
    	//int w = ip.getWidth();
		//int h = ip.getHeight();
    	//pixelNumber = w*h;
    	//col_Depth = getBitDepth();
		
    	HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) //dialog canceled or error
			return; 
		HarrisCornerDetector hcd = new HarrisCornerDetector(ip, params);
		hcd.findCorners();
		ImageProcessor result = hcd.showCornerPoints(ip, nmax);
		(new ImagePlus("Corners from " + im.getTitle(), result)).show();
    }
    
	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		//dlg.addNumericField("Alpha", params.alpha, 3);
		//set threshold
		params.threshold = (int)(contrastRate*20000);
		dlg.addMessage("Tips:");
		dlg.addMessage("The program will calculate threshold automaticlly according to the image content!");
		dlg.addMessage("You can also change the value in the field below to see the effect.");
		dlg.addNumericField("Input Threshold", params.threshold, 0);
		//dlg.addCheckbox("Clean up corners", params.doCleanUp);
		//dlg.addNumericField("Corners to show (0 = show all)", nmax, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		//params.alpha = dlg.getNextNumber();
		//set threshold from 0 - 1,000,000,
		params.threshold = (int) dlg.getNextNumber();
		//params.doCleanUp = dlg.getNextBoolean();
		//nmax = (int) dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}	
		return true;
	}
	
	public double getcontrast(){ //only accept 8bit image!
		ImagePlus imp = IJ.getImage();
        ImageStatistics stats = imp.getStatistics();
        //IJ.log("Area: "+stats.area);
        //IJ.log("Mean: "+stats.mean);
        //IJ.log("Max: "+stats.max);
        //IJ.log("Min: "+stats.min);
        double rate = Math.pow((stats.max-stats.min),3)/(255*255*255);
        return rate;
	}
}
