package circular;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.IJ;

public class circularHT {
    ImageProcessor np;
   public int a;
   public int b;
   public int radius;
   public  int[][][] houghArray;
    
   public circularHT(ImageProcessor np, int a, int b,int radius) {
		int h = np.getHeight();
    	int w = np.getWidth();
    	this.np = np;
    	this.a = a;
    	this.b = b;
    	this.radius = radius;
    	double rMax = Math.sqrt(h * h + w * w)/2;
    	this.houghArray = new int[a][b][radius];
    	fillHoughAccumulator();
    }
    
    public void fillHoughAccumulator() {
    	int h = np.getHeight();
    	int w = np.getWidth();
    	for (int v = 0; v < h; v++) {
            for (int u = 0; u < w; u++) {
                
                doPixel(u, v);
                
            }
        }
    }
    
   public void doPixel(int u, int v) {
        int height = np.getHeight();
        int wight = np.getWidth();
        int rMax = (int)Math.sqrt(height * height + wight * wight)/2;
        
        for(int radius=0;radius<rMax;radius++){
            
            int x,y;
            int d;
            x=0;
            y=radius;
            d=1-radius;
            while(x<=y){
                
                if(((u+radius<wight)&&(u-radius>0))&&((v+radius<height)&&(v-radius>0)))
                {
                    if(np.getPixel(u+x, v+y)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u+x, v-y)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u-x, v+y)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u-x, v-y)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u+y, v+x)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u+y, v-x)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u-y, v+x)>0){
                        houghArray[u][v][radius]++;}
                    if(np.getPixel(u-y, v-x)>0){
                        houghArray[u][v][radius]++;}
                }
                if(d<0){
                    d+=x*2.0+3;
                }
                else
                {
                    d+=2.0*(x-y)+5;
                    y--;
                }
                x++;
            }
        }
    }
    
    
    
}

