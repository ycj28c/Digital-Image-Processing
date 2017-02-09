import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.IJ;
import circular.circularHT;

public class newbresen implements PlugInFilter{
     
    double sigma = 1;
    int K,L;
    int threshold = 1;
    
    public int setup (String arg, ImagePlus im) {
	       return DOES_ALL;
	    }
   
    
    public void run(ImageProcessor ip) {
       
        int wight = ip.getWidth();
        int height = ip.getHeight();
        
        ImagePlus im = IJ.createImage("new pix"," ",wight,height,1);
        ImageProcessor np = im.getProcessor();
        
        np.insert(ip.convertToByte(false),0,0);
        
   
       
        float[][] kernel = GaussianKernel2d();
        
        int[][] q1 = new int[wight][height];
        int[][] cp = new int[wight][height];
        int[][] th = new int[wight][height];
        int[][] zero = new int[wight][height];
        
        float[][] kernel1dxx = getXXDirectionDeviation(kernel);
        float[][] kernel1dyy = getYYDirectionDeviation(kernel);
      
    int len = kernel.length;
    
         K = kernel[0].length/2;
        L = kernel.length/2;
        
        
        ImageProcessor copy = np.duplicate();
        
        
        
     // (u,v)
        for(int v=0;v<height;v++){
            for (int u = 0; u < wight; u++) {
            	
            	
                
                float s1 = 0;
                float s2 = 0;
                for (int j = -L; j <= L; j++) {
                    for (int i = -K; i <= K; i++) {
                        
                   int x = u+i;
                   int y = v+j;
                     if(x<0)
                     {
                         x=u+i+wight;
                     }
                       else if (x>=wight)
                          {
                              x=u+i-wight;
                          }
                    
                        if(y<0){y=v+j+height;}
                        else if (y>=height)
                        {y=v+j-height;}
                        
                   int pixel1 = copy.getPixel(x, y);
                        
                   float c1 = kernel1dxx[j+L][i+K];
                   float c2 = kernel1dyy[j+L][i+K];
                         s1 = s1 + c1 * pixel1;
                         s2 = s2 + c2 * pixel1;
                        
                    }
                    
                }
                
                
                q1[u][v] = (int) Math.round(s1+s2);
                cp[u][v] = (int) Math.round(s1+s2);
                
                
                int q = (int) Math.round(Math.sqrt((s1*s1)+(s2*s2)));
              
                
                if(u==0||v==0){
                    np.putPixel(u,v,0);
                }
                
                else if(u>0&&v>0){
                int pixel = cp[u][v];
                int yp = cp[u][v-1];
                int xp = cp[u-1][v];
                
                
                if (pixel>0&&xp<0)     zero[u][v]=255;
                else if (pixel>0&&yp<0)zero[u][v]=255;
                else if (pixel<0&&xp>0)zero[u][v]=255;
                else if (pixel<0&&yp>0)zero[u][v]=255;
                else zero[u][v]=0;
                }
                
         }
        
    }
        for (int y = 0; y < height; y++){
            for (int x = 0; x < wight; x++){
                if (zero[x][y]==255) np.putPixel(x,y,255);
                else np.putPixel(x,y,0);;
                
                
            }
            
        }
       
        int counter=0;
      
        int rm = (int)Math.sqrt(height * height + wight * wight)/2;
        int [] a1=new int[height*wight];
        int [] b1=new int[height*wight];
        int [] r1=new int[height*wight];
        int [] max1 =new int[height*wight];
        
       
        
        
        circularHT ht = new circular.circularHT(np,wight, height, rm);
        int max[][][]=ht.houghArray;
        
        
        for (int i=0;i<wight;i++){
        	for(int j=0;j<height;j++){
                for(int r=0;r<rm;r++){
    
                    if(max[i][j][r]>30&&max[i][j][r]>4.1*r)
                    {
                        a1[counter]=i;
                        b1[counter]=j;
                        r1[counter]=r;
                        counter++;
                    }
                }
            }
        }
 
        
        
       ImagePlus xm = IJ.createImage("new pix1"," ",wight,height,1);
        ImageProcessor xp = xm.getProcessor();
        
    
        
       for(int i=0;i<counter;i++){
        	
    	   int x,y;int d;
    	   x=0;
    	   y=r1[i];
    	   d=1-r1[i];
    	   while(x<=y){
    	   	
    	   if(a1[i]+x<wight&&a1[i]-x>0&&b1[i]+y<height&&b1[i]-y>0&&a1[i]+y<wight&&a1[i]-y>0&&b1[i]+x<height&&b1[i]-x>0)
    	   {
    	   xp.putPixel(a1[i]+x,b1[i]+y,0);
    	   xp.putPixel(a1[i]+x,b1[i]-y,0);
    	   xp.putPixel(a1[i]-x,b1[i]+y,0);
    	   xp.putPixel(a1[i]-x,b1[i]-y,0);
    	   xp.putPixel(a1[i]+y,b1[i]+x,0);
    	   xp.putPixel(a1[i]+y,b1[i]-x,0);
    	   xp.putPixel(a1[i]-y,b1[i]+x,0);
    	   xp.putPixel(a1[i]-y,b1[i]-x,0);
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
        
       xm.show();
}
    
    
    public float[][] GaussianKernel2d()
    {
        int center = (int) (3.0*sigma);
        int size = center * 2 + 1;
        float[][] H = new float[size][size];
        double sigma2 = sigma * sigma;
        for(int i=-center; i<=center; i++)
        {
            for(int j=-center; j<=center; j++)
            {
                double r2 = i*i + j*j;
                double sum = -(r2/(2*sigma2));
                H[i + center][j + center] = (float)Math.exp(sum);
            }
        }
        return H;
    }
    
    public float[][] getXXDirectionDeviation(float[][] kernel)
    {
        int center = (int) (3.0*sigma);
        int size = center * 2 + 1;
        float[][] H = new float[size][size];
        double sigma2 = sigma * sigma;
        double sigma4 = sigma2 * sigma2;
        for(int x=-center; x<=center; x++)
        {
            double c = ((x*x - sigma2)/sigma4);
            for(int i=0; i<size; i++)
            {
                H[i][x + center] = (float)c * kernel[i][x + center];
            }
        }
        return H;
    }
    
    
    public float[][] getYYDirectionDeviation(float[][] kernel)
    {
        int center = (int) (3.0*sigma);
        int size = center * 2 + 1;
        float[][] H = new float[size][size];
        double sigma2 = sigma * sigma;
        double sigma4 = sigma2 * sigma2;
        for(int y=-center; y<=center; y++)
        {
            double c = ((y*y - sigma2)/sigma4);
            for(int i=0; i<size; i++)
            {
                H[y + center][i] = (float)c * kernel[y + center][i];
            }
        }
        return H;
    }
    
}

    
    