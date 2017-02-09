import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.plugin.filter.Convolver;

//import static java.lang.Math.pow;



public class Laplacian_Filter_with_c implements PlugInFilter {
    float c = (float)-4;
    public int setup (String arg, ImagePlus im) {
    return DOES_8G;// this plugin accepts 8-bit grayscale images
    }
    public void run(ImageProcessor I) {
        float[] H = { // filter array is one-dimensional!
            0f, (c/4), 0f,
            (c/4), ((float)1.0-c), (c/4),
            0f, (c/4), 0f };
        Convolver cv = new Convolver();
        cv.setNormalize(true); // do not use filter normalization
        cv.convolve(I, H, 3, 3); // apply the filter H to I
    }
}
