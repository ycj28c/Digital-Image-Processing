import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import imagingbook.pub.regions.BinaryRegion;
import imagingbook.pub.regions.DepthFirstLabeling;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.RegionLabeling;

public class Region_labeling implements PlugInFilter {
	
	static final int BACKGROUND = 0;
	double ra = 0;
    double rb = 0;
    double xd = 0;
    double yd = 0;
    
	public int setup(String arg, ImagePlus im) { 
		return DOES_ALL + NO_CHANGES; 
	}
	
	//calculate moment Mpq
	public static double moment(ImageProcessor ip, int p, int q){
		double Mpq = 0.0;
		for(int v = 0; v<ip.getHeight();v++){
			for(int u = 0; u<ip.getWidth();u++){
				if(ip.getPixel(u,v) != BACKGROUND){ //binary image, R is all 1
					Mpq += Math.pow(u,p) * Math.pow(v, q);
				}
			}
		}
		return Mpq;
	}
	
	//calculate central Moment Upq(R)
	public static double centralMoment(ImageProcessor ip, int p, int q){
		double m00 = moment(ip, 0, 0); //binary region area, zero-order moment
		double xCtr = moment(ip, 1, 0)/m00;
		double yCtr = moment(ip, 0, 1)/m00;
		double cMpq = 0.0;
		for(int v = 0; v<ip.getHeight();v++){
			for(int u = 0; u<ip.getWidth();u++){
				if(ip.getPixel(u,v) != BACKGROUND){  //binary image, R is all 1
					cMpq += Math.pow(u - xCtr, p) * Math.pow(v - yCtr, q);
				}
			}
		}
		return cMpq;
	}
	
	//calculate normalize central Moment
	public static double normalCentralMoment(ImageProcessor ip, int p, int q){
		double m00 = moment(ip, 0, 0);
		double norm = Math.pow(m00, (double)(p + q +2)/2);
		return centralMoment(ip, p , q)/norm;
	}
	
	//calculate orientation
//	public static double orientation(ImageProcessor ip){
//		double u11 = centralMoment(ip, 1 , 1); //p = 1, q = 1
//		double u20 = centralMoment(ip, 2 , 0); //p = 2, q = 0
//		double u02 = centralMoment(ip, 0 , 2); //p = 0, q = 2
//		double tan2R = (2*u11)/(u20-u02);//tan(2 sita R)
//		double sitaR = 1/2 * Math.atan((2*u11)/(u20-u02));
//		return tan2R;
//	}
	public double orientation(RegionLabeling labeling, BinaryRegion r){
        int label = r.getLabel();
    	Rectangle bb = r.getBoundingBox();
    	double xc = r.getCenter().x;
    	double yc = r.getCenter().y;
    	double u11 = 0;	
        double u20 = 0;
        double u02 = 0;
        double sita = 0;
    	for (int v=bb.y; v<bb.y+bb.height; v++) {
    		for (int u=bb.x; u<bb.x+bb.width; u++) {
    			if (labeling.getLabel(u,v) == label) {
    				u11 = u11 + (u - xc) * (v - yc);
                    u20 = u20 + (u - xc) * (u - xc);
                    u02 = u02 + (v - yc) * (v - yc);           
    			}
    		}
    	}
        sita = 0.5*Math.atan((2*u11)/(u20-u02));
        return sita;
    }
	
	public void vectorSet(RegionLabeling labeling, BinaryRegion r){     
        int label = r.getLabel();
    	Rectangle bb = r.getBoundingBox();
    	double xs = r.getCenter().x;
    	double ys = r.getCenter().y;
    	double u11 = 0;
        double u20 = 0;
        double u02 = 0;
        double sita = 0;
    	for (int v=bb.y; v<bb.y+bb.height; v++) {
    		for (int u=bb.x; u<bb.x+bb.width; u++) {
    			if (labeling.getLabel(u,v) == label) {
    				u11 = u11 + (u - xs) * (v - ys);
                    u20 = u20 + (u - xs) * (u - xs);
                    u02 = u02 + (v - ys) * (v - ys);                  
    			}
    		}
    	}    
        double A = 2*u11;
        double B = u20-u02;
        if(A==0&&B==0){
            xd = 0;
            yd = 0;
        }
        else if(A>=0){
            xd = Math.sqrt(0.5*(1+(B/Math.sqrt(A*A+B*B))));
            yd = Math.sqrt(0.5*(1-(B/Math.sqrt(A*A+B*B))));
        }
        else{
            xd = Math.sqrt(0.5*(1+(B/Math.sqrt(A*A+B*B))));
            yd = -Math.sqrt(0.5*(1-(B/Math.sqrt(A*A+B*B))));
        }
    }
	//calculate eccentricity
//	public static double eccentricity(ImageProcessor ip){
//		double u11 = centralMoment(ip, 1 , 1); //p = 1, q = 1
//		double u20 = centralMoment(ip, 2 , 0); //p = 2, q = 0
//		double u02 = centralMoment(ip, 0 , 2); //p = 0, q = 2
//		double a1 = u20 + u02 + Math.sqrt(Math.pow((u20 - u02),2) + 4 * Math.pow(u11, 2)); //book 11.30
//		double a2 = u20 + u02 - Math.sqrt(Math.pow((u20 - u02),2) + 4 * Math.pow(u11, 2));
//		double Ecc = a1/a2;
//		return Ecc;
//	}
	public double Eccentricity(RegionLabeling labeling, BinaryRegion r){
        int label = r.getLabel();
    	Rectangle bb = r.getBoundingBox();
    	double xs = r.getCenter().x;	// centroid of this region
    	double ys = r.getCenter().y;
        int R = r.getSize();
    	double u11 = 0;	// x/y sums
        double u20 = 0;
        double u02 = 0;
        double sita = 0;      
    	// collect all coordinates with exactly this label
    	for (int v=bb.y; v<bb.y+bb.height; v++) {
    		for (int u=bb.x; u<bb.x+bb.width; u++) {
    			if (labeling.getLabel(u,v) == label) {
    				u11 = u11 + (u - xs) * (v - ys);
                    u20 = u20 + (u - xs) * (u - xs);
                    u02 = u02 + (v - ys) * (v - ys);                 
    			}
    		}
    	}
        double a1 = u20+u02+Math.sqrt((u20-u02)*(u20-u02) + 4*u11*u11);
        double a2 = u20+u02-Math.sqrt((u20-u02)*(u20-u02) + 4*u11*u11);
        double Ecc = a1/a2;
        ra = Math.sqrt(2*a1/R);
        rb = Math.sqrt(2*a2/R);
        return Ecc;        
    }
	
	public void findRegion(ImageProcessor ip){
		// Make sure we have a proper byte image:
	   	ByteProcessor bp = (ByteProcessor) ip.convertToByte(false);
	   	
	   	bp.autoThreshold();
	   	bp.invert();
	   	
	   	// Create the region labeler / contour tracer:
	   	RegionContourLabeling segmenter = new RegionContourLabeling(bp);
	   	
	   	// Retrieve the list of detected regions:
	   	List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
	 	IJ.log("Detected regions (sorted by size): " + regions.size());
	 	for (BinaryRegion r: regions) {
	 		IJ.log(r.toString());
	 	}
	}
	
	double mu_11 (RegionLabeling labeling, BinaryRegion r) {
    	int label = r.getLabel();
    	Rectangle bb = r.getBoundingBox();
    	double xc = r.getCenter().x;
    	double yc = r.getCenter().y;
    	double s11 = 0;	
    	for (int v=bb.y; v<bb.y+bb.height; v++) {
    		for (int u=bb.x; u<bb.x+bb.width; u++) {
    			if (labeling.getLabel(u,v) == label) {
    				s11 = s11 + (u - xc) * (v - yc);
    			}
    		}
    	}
    	return s11;
    }
	
	public static void getEllipse(ImageProcessor ip){ //book 11.31
		double u11 = centralMoment(ip, 1 , 1); //p = 1, q = 1
		double u20 = centralMoment(ip, 2 , 0); //p = 2, q = 0
		double u02 = centralMoment(ip, 0 , 2); //p = 0, q = 2
		double a1 = u20 + u02 + Math.sqrt(Math.pow((u20 - u02),2) + 4 * Math.pow(u11, 2)); //book 11.30
		double a2 = u20 + u02 - Math.sqrt(Math.pow((u20 - u02),2) + 4 * Math.pow(u11, 2));		
		//ra = Math.pow((2*a1)/Math.abs(R), 0.5); //major
		//rb = Math.pow((2*a2)/Math.abs(R), 0.5);//minor axes
	}
	
	public void run(ImageProcessor ip) {  	
		findRegion(ip);
		
		RegionLabeling labeling = null;
		labeling = new DepthFirstLabeling((ByteProcessor) ip);
		List<BinaryRegion> region_set = labeling.getRegions();
		ImageProcessor label_image = labeling.makeLabelImage(false);
		String mess = "";
		
		for (BinaryRegion r : region_set) {
            double xc = r.getCenter().x;
            double yc = r.getCenter().y;  
            double mu11 = mu_11(labeling, r);
            double sita = orientation(labeling, r);
            int temp = label_image.getPixel((int)xc,(int)yc);
            
            //set vector
            vectorSet(labeling, r);
            //draw rectangle
            Rectangle bb = r.getBoundingBox();
            
            double pi = 3.1415;
            double increasem = 0.005;
            for(double t = 0; t < 2*pi; t = t + increasem){
                double x = xc + Math.cos(sita)*ra*Math.cos(t)
                		-Math.sin(sita)*rb*Math.sin(t);
                double y = yc + Math.sin(sita)*ra*Math.cos(t)
                		+Math.cos(sita)*rb*Math.sin(t);
                label_image.putPixel((int)x,(int)y,temp);
            }
            
            double ecce = Eccentricity(labeling, r);
            if(ecce>0){
            	mess += "\nEccentricity = "+ ecce;
            }

            double xm = xc;
            double ym = yc;
            for(double t = 0; t<(bb.height);t++){
               xm = xm + xd;
               ym = ym + yd;
               label_image.putPixel((int)xm,(int)ym,0);
            }
            IJ.log(mess);
		}
		ImagePlus NewImage = new ImagePlus("", label_image);
		NewImage.show();
	}
}