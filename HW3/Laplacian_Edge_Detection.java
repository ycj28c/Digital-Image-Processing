import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.plugin.filter.Convolver;
import ij.IJ;
import ij.process.*;
import ij.gui.GenericDialog;
import ij.WindowManager;

public class Laplacian_Edge_Detection implements PlugInFilter {

	ImagePlus imold;
	// global varible
	public double sigma = (double) 1;
	public int threshold = 50;
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
		np.insert(ip.convertToByte(false), 0, 0);

		float[][] kernel = Gaussian2d();
		float[][] kernel1dx = getDxBykernel(kernel);
		float[][] kernel1dy = getDyBykernel(kernel);
		float[][] kernel1dxx = getDx2Bykernel(kernel);
		float[][] kernel1dyy = getDy2Bykernel(kernel);

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
		int[][] copy1 = new int[width][heigth];
		int[][] GM = new int[width][heigth];
		int[][] ZC = new int[width][heigth];

		for (int v = 0; v < heigth; v++) {
			for (int u = 0; u < width; u++) {
				double sum1 = 0;
				double sum2 = 0;
				double sum3 = 0;
				double sum4 = 0;
				for (int j = -L; j <= L; j++) {
					for (int i = -K; i <= K; i++) {
						int x = u + i;
						int y = v + j;
						if (x < 0) {
							x = 0;
						} else if (x > width) {
							x = width;
						}
						if (y < 0) {
							y = 0;
						} else if (y > heigth) {
							y = heigth;
						}
						int p = copy.getPixel(x, y);
						float c1 = kernel1dx[j + L][i + K];
						float c2 = kernel1dy[j + L][i + K];
						sum1 = sum1 + c1 * p;
						sum2 = sum2 + c2 * p;
						float c3 = kernel1dxx[j + L][i + K];
						float c4 = kernel1dyy[j + L][i + K];
						sum3 = sum3 + c3 * p;
						sum4 = sum4 + c4 * p;
					}

				}
				//combine the gradient magnitude,laplacian and zero crossing fiter
				int q = (int) Math.round(Math.sqrt((sum1 * sum1)
						+ (sum2 * sum2)));

				if (q >= threshold) {
					GM[u][v] = 255;
				} else {
					GM[u][v] = 0;
				}

				ZC[u][v] = (int) Math.round(sum3 + sum4);
				copy1[u][v] = (int) Math.round(sum3 + sum4);
				if (v > 0 && u > 0) {
					int pixel = copy1[u][v];
					int yprev = copy1[u][v - 1];
					int xprev = copy1[u - 1][v];
					if ((pixel > 0) && (xprev < 0))
						ZC[u][v] = 255;
					else if ((pixel > 0) && (yprev < 0))
						ZC[u][v] = 255;
					else if ((pixel < 0) && (xprev > 0))
						ZC[u - 1][v] = 255;
					else if ((pixel < 0) && (yprev > 0))
						ZC[u][v - 1] = 255;
					else
						ZC[u][v] = 0;
				}
			}
		}
		for (int y = 0; y < heigth; y++) {
			for (int x = 0; x < width; x++) {
				if (ZC[x][y] == 255 && GM[x][y] == 255)
					np.putPixel(x, y, 255);
				else
					np.putPixel(x, y, 0);
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
	
	public float[][] getDxBykernel(float[][] kernel) {
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
	
	public float[][] getDx2Bykernel(float[][] kernel) {
		int center = (int) (3.0 * sigma);
		int size = center * 2 + 1;
		float[][] H = new float[size][size];
		double sigma2 = sigma * sigma;
		double sigma4 = sigma2 * sigma2;
		for (int x = -center; x <= center; x++) {
			double c = ((x * x - sigma2) / sigma4);
			for (int i = 0; i < size; i++) {
				H[i][x + center] = (float) c * kernel[i][x + center];
			}
		}
		return H;
	}
	
	public float[][] getDyBykernel(float[][] kernel) {

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

	public float[][] getDy2Bykernel(float[][] kernel) {
		int center = (int) (3.0 * sigma);
		int size = center * 2 + 1;
		float[][] H = new float[size][size];
		double sigma2 = sigma * sigma;
		double sigma4 = sigma2 * sigma2;
		for (int y = -center; y <= center; y++) {
			double c = ((y * y - sigma2) / sigma4);
			for (int i = 0; i < size; i++) {
				H[y + center][i] = (float) c * kernel[y + center][i];
			}
		}
		return H;
	}
	
	private float[][] normalizeData(float[][] kernel) {
		float min = kernel[0][0];
		int center = (int) (3.0 * sigma);
		for (int x = -center; x <= center; x++) {
			for (int y = -center; y <= center; y++) {
				if (min > kernel[x + center][y + center]) {
					min = kernel[x + center][y + center];
				}
			}
		}
		for (int x = -center; x <= center; x++) {
			for (int y = -center; y <= center; y++) {
				kernel[x + center][y + center] = kernel[x + center][y + center]
						/ min;
			}
		}
		return kernel;
	}
	
	private boolean showDialog() {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Choose Parameter");
		dlg.addMessage("Tips:");
		//dlg.addMessage("Its fake interface, we use Gaussian 2, but you can change the sigma value!");
		//dlg.addCheckbox("Gaussian 1st", isGaussian1st);
		//dlg.addCheckbox("Gaussian 2nd", isGaussian2nd);
		dlg.addNumericField("Simgma", sigma, 0);
		dlg.addNumericField("Threshold", threshold, 0);
		dlg.showDialog();
		if (dlg.wasCanceled())
			return false;
		//isGaussian1st = dlg.getNextBoolean();
		//isGaussian2nd = dlg.getNextBoolean();
		sigma = dlg.getNextNumber();
		threshold = (int)dlg.getNextNumber();
		if (dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}

}
