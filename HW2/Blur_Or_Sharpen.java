import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.plugin.filter.Convolver;

public class Blur_Or_Sharpen implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	public void unsharpMask(ImageProcessor ip, double sigma, double w) {
		ImageProcessor I = ip.convertToFloat();
		ImageProcessor J = I.duplicate();
		// float [] H =GaussKernel1d.create(sigma);
		float[] H = makeGaussKernell1d(sigma);
		Convolver cv = new Convolver();
		cv.setNormalize(true);
		cv.convolve(J, H, 1, H.length);
		cv.convolve(J, H, H.length, 1);

		I.multiply(1 + w);
		J.multiply(w);
		I.copyBits(J, 0, 0, Blitter.SUBTRACT);
		ip.insert(I.convertToByte(false), 0, 0);
	}

	public void run(ImageProcessor I) {
		double sigma = 2.0;
		double w = 1.0;

		unsharpMask(I, sigma, w);
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
