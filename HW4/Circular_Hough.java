/*
 * Burger & Burge Exercise 9.4 (page 171) (30 points): Implement as an ImageJ plugin, called
	Circular_Hough, the Hough Transform for finding circles and circular arcs with varying radii.
	Make use of a fast algorithm for generating circles, such as described in sec 9.4, in the accumulator
	array
 */

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import java.awt.*;
import java.util.Arrays;

import ij.gui.*;

public class Circular_Hough implements PlugInFilter {
	//set the parameter
	ImageProcessor ip;
	byte imageValues[]; 

	//the array to save the circle data
	double[][][] houghArray;
	int lut[][][]; 
	double temp[];
	
	//set circle parameter
	public int radiusMin = 1;  
	public int radiusMax = 20; 
	public int radiusInc = 2;
	public int maxCircles = 10;
	
	//the image parameter
	public int width; 
	public int height; 
	public int depth; 
	public int offset; 
	public int offx; 
	public int offy; 

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	}
	
	/*public void Circular_Hough(ImageProcessor ip, int nAng, int nRad){
		this.ip = ip;
		this.xCtr = ip.getWidth()/2;
		this.yCtr = ip.getHeight()/2;
		this.nAng = nAng;
		this.dAng = Math.PI/nAng;
		this.nRad = nRad;
		this.cRad = nRad/2;
		double rMax = Math.sqrt(xCtr*xCtr+yCtr*yCtr);
		this.dRad = (2.0* rMax)/nRad;
		this.houghArray = new int[nAng][nRad];
		fillHoughAccumulator();
		//houghTransform();
	}
	//brute force fill the accumulator
	void fillHoughAccumulator(){
		int h = ip.getHeight();
		int w = ip.getWidth();
		for(int v=0;v<h;v++){
			for(int u=0;u<w;u++){
				if(ip.get(u,v)>0){
					doPixel(u,v);
				}
			}
		}
	}
	//set value in the accumulator space
	void doPixel(int u, int v){
		int x = u -xCtr, y=v-yCtr;
		for(int i=0;i<nAng;i++){
			double theta = dAng *i;
			int r=cRad+(int)Math.rint((x*Math.cos(theta)+y*Math.sin(theta))/dRad);
			if(r>=0&&r<nRad){
				houghArray[i][r]++;
			}
		}
	}*/
	
	//set the Accumulator Space
    private int CreateAccumulatorSpace() {
        int i = 0;
        int incDen = Math.round (8F * radiusMin);  // increment denominator
        lut = new int[2][incDen][depth];
        for(int radius = radiusMin;radius <= radiusMax;radius = radius+radiusInc) {
            i = 0;
            for(int incNun = 0; incNun < incDen; incNun++) {
                double angle = (2*Math.PI * (double)incNun) / (double)incDen;
                int indexR = (radius-radiusMin)/radiusInc;
                int rcos = (int)Math.round ((double)radius * Math.cos (angle));
                int rsin = (int)Math.round ((double)radius * Math.sin (angle));
                if((i == 0) | (rcos != lut[0][i][indexR]) & (rsin != lut[1][i][indexR])) {
                    lut[0][i][indexR] = rcos;
                    lut[1][i][indexR] = rsin;
                    i++;
                }
            }
        }
        return i;
    }

    //transform the data
    private void fillHoughAccumulator () {
        int lutSize = CreateAccumulatorSpace();
        houghArray = new double[width][height][depth];
        int k = width - 1;
        int l = height - 1;
        for(int y = 1; y < l; y++) {
            for(int x = 1; x < k; x++) {
                for(int radius = radiusMin;radius <= radiusMax;radius = radius+radiusInc) {
                    if( imageValues[(x+offx)+(y+offy)*offset] != 0 )  {// Edge pixel found
                        int indexR=(radius-radiusMin)/radiusInc;
                        for(int i = 0; i < lutSize; i++) {
                            int a = x + lut[1][i][indexR]; 
                            int b = y + lut[0][i][indexR]; 
                            if((b >= 0) & (b < height) & (a >= 0) & (a < width)) {
                            	houghArray[a][b][indexR] += 1;
                            }
                        }
                    }
                }
            }
        }
    }
	public void run(ImageProcessor ip) {
		imageValues = (byte[])ip.getPixels();
        Rectangle r = ip.getRoi();

        offx = r.x;
        offy = r.y;
        width = r.width;
        height = r.height;
        offset = ip.getWidth();
        
        GenericDialog gd = new GenericDialog("Hough Parameters", IJ.getInstance());
        gd.addNumericField("Minimum radius:", 1, 0);
        gd.addNumericField("Maximum radius:", 20, 0);
        gd.addNumericField("Increment radius:", 2, 0);
        gd.showDialog();

        radiusMin = (int) gd.getNextNumber();
        radiusMax = (int) gd.getNextNumber();
        radiusInc = (int) gd.getNextNumber();
        depth = ((radiusMax-radiusMin)/radiusInc)+1;
        maxCircles = 10;
        
        fillHoughAccumulator ();
        

        //Circular_Hough(ip, 256, 256);
		/*double[] b = new double[width*height*100];
		for(int i=1; i<width-2; i++) {           //change the 3 demension to 1 demension
			for(int j=1; j<height-2; j++) {
				for(int k=radiusMin+1;k<(radiusMax-radiusMin)/radiusInc-1;k++)
					b[((width*i)+j)*height+k] = houghArray[i][j][k];
			}
		}*/
		/*for(int i=1; i<houghArray.length-2; i++){
		    for(int j=1; i<houghArray[i].length-2; j++){
		        for(int k=radiusMin; k<houghArray[i][j].length; k++)
		        	System.out.println(houghArray[i][j][k]);
		    }
		}*/
		//get max 10 value in accumulator space
		/*Arrays.sort(b);
		for(int i=1;i<11;i++){
			IJ.log("top"+i+":" + b[b.length-i]);
		}*/
        int f = 0;
        for(int i=0; i<width; i++) {           //change the 3 demension to 1 demension
			for(int j=0; j<height; j++) {
				for(int k=radiusMin;k<(radiusMax-radiusMin)/radiusInc;k++)
					//temp[f] = houghArray[i][j][k];
					IJ.log("houghArray["+i+"]["+j+"]["+k+"]:" + houghArray[i][j][k]);
					//f++;
			}
		}
	}

}
