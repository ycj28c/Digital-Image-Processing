/**
 * This sample code is made available as part of the book "Digital Image
 * Processing - An Algorithmic Introduction using Java" by Wilhelm Burger
 * and Mark J. Burge, Copyright (C) 2005-2008 Springer-Verlag Berlin,
 * Heidelberg, New York.
 * Note that this code comes with absolutely no warranty of any kind.
 * See http://www.imagingbook.com for details and licensing conditions.
 *
 * Date: 2007/11/10
 */

package dftt;
import imagingbook.lib.math.Complex;

public class Dft1d {
    
	double[] cosTable;
	double[] sinTable;
	Complex[] G;
    
	Dft1d(int M){ //Constructor
		makeCosTable(M);
		makeSinTable(M);
		G = new Complex[M];	//this is done only ONCE when object is created!
		for (int i = 0; i < M; i++) {
			G[i] = new Complex(0,0);
		}
	}
    
	void makeCosTable(int M){
		cosTable = new double[M];
		for (int i=0; i<M; i++){
			cosTable[i]= Math.cos(2*Math.PI*i/M);
		}
	}
    
	void makeSinTable(int M){
		sinTable = new double[M];
		for (int i=0; i<M; i++){
			sinTable[i]= Math.sin(2*Math.PI*i/M);
		}
	}
    
	public Complex[] DFT(Complex[] g, boolean forward) {
		int M = g.length;
		double s = 1 / Math.sqrt(M); //common scale factor
		for (int u = 0; u < M; u++) {
			double sumRe = 0;
			double sumIm = 0;
			for (int m = 0; m < M; m++) {
				double gRe = g[m].re;
				double gIm = g[m].im;
				int k = (u * m) % M;
				double cosPhi = cosTable[k];
				double sinPhi = sinTable[k];
				if (forward)
					sinPhi = -sinPhi;
				//complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
				sumRe += gRe * cosPhi - gIm * sinPhi;
				sumIm += gRe * sinPhi + gIm * cosPhi;
			}
			G[u].re = s * sumRe;
			G[u].im = s * sumIm;
		}
		return G;
	}
}