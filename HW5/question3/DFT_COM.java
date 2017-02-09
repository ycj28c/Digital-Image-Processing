import dftt.*;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.gui.GenericDialog;

public class DFT_COM implements PlugInFilter{
    
	static boolean center = true;
    
	static boolean todft = true;
	static boolean toimage = true;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G+NO_CHANGES;
	}
    
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Binary Region Labeling");
		gd.addCheckbox("Image To Dft", todft);
		gd.addCheckbox("Dft to Image", toimage);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		todft = gd.getNextBoolean();
		toimage = gd.getNextBoolean();
		return true;
	}
	
	public void run(ImageProcessor ip) {
		getUserInput();
		
		FloatProcessor ip2 = (FloatProcessor) ip.convertToFloat();
		Dft2d dft = new Dft2d(ip2,center);
        if(todft){
        	dft.doDft2d();
        	dft.makePowerSpectrum();
        	ImageProcessor ipP = dft.makePowerImage();
        	dft.swapQuadrants(ipP);
     		ImagePlus win = new ImagePlus(" ",ipP);
     		win.show();
        }
        if(toimage){
        	dft.setInverse();
    		dft.doDft2d();
    		dft.makePowerSpectrum();
            ImageProcessor ipP2 = dft.makeImageBack();
    		ImagePlus WinInverse = new ImagePlus(" ",ipP2);
    		WinInverse.show();
        }
	}
    
}