import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.plugin.filter.Convolver;

public class Gaussian_Blur implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor I) {
		double sigma = 2.0;
		ImageProcessor II = I.convertToFloat();
        	ImageProcessor J = I.duplicate();
        	float[] H = makeGaussKernell1d(sigma);
        	Convolver cv = new Convolver();
        	cv.setNormalize(true);
        	cv.convolve(J, H, 1, H.length);
        	cv.convolve(J, H, H.length, 1);
        	II.multiply(0);
        	J.multiply(-1);
        	II.copyBits(J,0,0,Blitter.SUBTRACT);
        	I.insert(I.convertToByte(false), 0, 0);
	}

	private float[] makeGaussKernell1d(double sigma) {
		int center = (int) (3.0 * sigma);
		float[] kernel = new float[2 * center + 1];

		double sigma2 = sigma * sigma;
		for (int i = 0; i < kernel.length; i++) {
			double r = center - i;
			kernel[i] = (float) Math.exp(-0.5 * (r * r) / sigma2);
		}
		return kernel;
	}

}
