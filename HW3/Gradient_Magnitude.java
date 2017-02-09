import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.plugin.filter.Convolver;
import ij.IJ;
import ij.process.*;
import ij.gui.GenericDialog;
import ij.WindowManager;

public class Gradient_Magnitude implements PlugInFilter {

	ImagePlus imold;
	// global varible
	public double sigma = (double) 1;
	public boolean isGaussian1st = false;
	public boolean isGaussian2nd = true;
	public float k[][];

	public int setup(String arg, ImagePlus im) {
		this.imold = im;
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		showDialog();

		ImageProcessor I = ip.convertToFloat();
		ImageProcessor J = ip.duplicate();
		int width = ip.getWidth();
		int heigth = ip.getHeight();

		ImagePlus im = IJ.createImage("", "", width, heigth, 1);
		ImageProcessor np = im.getProcessor();
		// ImageProcessor npold = imold.getProcessor();
		np.insert(ip.convertToByte(false), 0, 0);

		// calculate kernel
		float[][] kernel = Gaussian2d();
		float[][] kernel1dx = getDxByKernel(kernel);
		float[][] kernel1dy = getDyByKernel(kernel);

		float su = (float) 0;
		for (int i = 0; i < kernel[0].length; i++) {
			for (int j = 0; j < kernel.length; j++) {
				su = su + kernel[i][j];
			}
		}
		double s = 1.0 / su;
		int K = kernel[0].length / 2;
		int L = kernel.length / 2;

		ImageProcessor copy = np.duplicate();

		for (int v = 0; v <= heigth; v++) {
			for (int u = 0; u <= width; u++) {
				// compute filter result for position (u,v)
				double sum1 = 0;
				double sum2 = 0;
				for (int j = -L; j <= L; j++) {
					for (int i = -K; i <= K; i++) {

						int x = u + i;
						int y = v + j;
						if (x <= 0) {
							x = width + x - 1;
						} else if (x >= width) {
							x = 0;
						}

						if (y <= 0) {
							y = heigth + y - 1;
						} else if (y >= heigth) {
							y = 0;
						}
						int p = copy.getPixel(x, y);

						float c1 = kernel1dx[j + L][i + K];
						float c2 = kernel1dy[j + L][i + K];
						sum1 = sum1 + c1 * p;
						sum2 = sum2 + c2 * p;

					}

				}
				//calculate gradient_magnitude
				int q = (int) Math.round(Math.sqrt((sum1 * sum1)
						+ (sum2 * sum2)));

				if (q <= 0)
					q = 0;
				if (q > 255)
					q = 255;
				np.putPixel(u, v, q);

			}

		}
		im.show();
	}

	public float[][] Gaussian2d() {
		int center = (int) (3.0 * sigma);
		int size = center * 2 + 1;
		float[][] H = new float[size][size];
		double sigma2 = sigma * sigma;
		for (int i = -center; i <= center; i++) {
			for (int j = -center; j <= center; j++) {
				double r2 = i * i + j * j;
				double sum = -(r2 / (2 * sigma2));
				H[i + center][j + center] = (float) Math.exp(sum);
			}
		}
		return H;
	}

	public float[][] Gaussian2dVersion2() {
		int halfwidth = 0;
		this.sigma = sigma;
		if (sigma != 0) {
			int width = (int) (6 * sigma + 1);
			if (width % 2 == 0)
				width++;
			halfwidth = width / 2;
			k = new float[halfwidth * 2 + 1][halfwidth * 2 + 1];
			for (int m = -halfwidth; m <= halfwidth; m++)
				for (int l = -halfwidth; l <= halfwidth; l++)
					k[m + halfwidth][l + halfwidth] = function(l, m);
		} else
			halfwidth = 0;
		return k;
	}

	protected float function(double x, double y) {
		return (float) (1.0 / (2 * Math.PI * Math.pow(sigma, 2)) * Math.exp(-(x
				* x + y * y)
				/ (2 * Math.pow(sigma, 2))));
	}

	public float[][] getDxByKernel(float[][] kernel) {
		int center = (int) (3.0 * sigma);
		int size = center * 2 + 1;
		float[][] H = new float[size][size];
		double sigma2 = sigma * sigma;
		for (int x = -center; x <= center; x++) {
			double c = -(x / sigma2);
			for (int i = 0; i < size; i++) {
				H[i][x + center] = (float) c * kernel[i][x + center];
			}
		}
		return H;
	}

	public float[][] getDyByKernel(float[][] kernel) {

		int center = (int) (3.0 * sigma);
		int size = center * 2 + 1;
		float[][] H = new float[size][size];
		double sigma2 = sigma * sigma;
		for (int y = -center; y <= center; y++) {
			double c = -(y / sigma2);
			for (int i = 0; i < size; i++) {
				H[y + center][i] = (float) c * kernel[y + center][i];
			}
		}
		return H;
	}

	private boolean showDialog() {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Choose Parameter");
		dlg.addMessage("Tips:");
		//dlg.addMessage("Its fake interface, we use Gaussian 2, but you can change the sigma value!");
		//dlg.addCheckbox("Gaussian 1st", isGaussian1st);
		//dlg.addCheckbox("Gaussian 2nd", isGaussian2nd);
		dlg.addNumericField("Simgma", sigma, 0);
		dlg.showDialog();
		if (dlg.wasCanceled())
			return false;
		//isGaussian1st = dlg.getNextBoolean();
		//isGaussian2nd = dlg.getNextBoolean();
		sigma = dlg.getNextNumber();
		if (dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}
}
