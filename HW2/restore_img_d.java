import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.plugin.filter.Convolver;


public class restore_img_d implements PlugInFilter {
    float c = (float)0.5;
    public int setup (String arg, ImagePlus im) {
    return DOES_ALL;
    }
    public void run(ImageProcessor I) {
        int[] H = {
            -1, -1, -1,
            -1, 13, -1,
            -1, -1, -1 };
        
        int[] H2 = {
            -1, -1, -1,
            -1, 20, -1,
            -1, -1, -1 };
       
        I.convolve3x3(H);       
        I.convolve3x3(H2);
    }
}
