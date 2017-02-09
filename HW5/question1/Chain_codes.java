import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.List;
import imagingbook.pub.regions.*;

public class Chain_codes implements PlugInFilter {
	
	static boolean listRegions = true;
	static boolean listContourPoints = false;
	static boolean showContours = true;
	
	public int setup(String arg, ImagePlus im) { 
		return DOES_ALL + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
	
	   	ByteProcessor bp = (ByteProcessor) ip.convertToByte(false);
	   	
	   	bp.autoThreshold();
	   	bp.invert();

		RegionContourLabeling segmenter = new RegionContourLabeling(bp);
		List<BinaryRegion> regions = segmenter.getRegions(true);
		BinaryRegion[] regionArray = regions.toArray(new BinaryRegion[0]);
		
		if (regionArray.length > 0) {
			ImagePlus im = IJ.createImage(" "," ",ip.getWidth(),ip.getHeight(),1);
	        ImageProcessor np = im.getProcessor();
	        im.show();
	        
			for(int m=0;m<regionArray.length;m++){
				BinaryRegion presentRegion = regionArray[m];
				Contour oc = presentRegion.getOuterContour();
				Point[] points = oc.getPointArray();
				freemanChain freeman = new freemanChain(points);
				String mess = "";
				for (int i = 0; i < points.length; i++) {
					mess += freeman.code[i];
				}
				IJ.log(mess);			
		        np.putPixel(points[0].x,points[0].y,0);		        
		        for (int i=0; i<points.length-1; i++) {
		        	switch(freeman.code[i]){
			        	case 0:
			        		np.putPixel(points[i].x+1,points[i].y,0);
	        				break;
			        	case 1:
			        		np.putPixel(points[i].x+1,points[i].y+1,0);
			        		break;
			        	case 2:
			        		np.putPixel(points[i].x,points[i].y+1,0);
			        		break;
			        	case 3:
			        		np.putPixel(points[i].x-1,points[i].y+1,0);
			        		break;
			        	case 4:
			        		np.putPixel(points[i].x-1,points[i].y,0);
			        		break;
			        	case 5:
			        		np.putPixel(points[i].x-1,points[i].y-1,0);
			        		break;
			        	case 6:
			        		np.putPixel(points[i].x,points[i].y-1,0);
			        		break;
			        	case 7:
			        		np.putPixel(points[i].x+1,points[i].y-1,0);
			        		break;
			        	default:
		        	}        				
		        }
			}	
			im.updateAndDraw();
		}
	}	
}