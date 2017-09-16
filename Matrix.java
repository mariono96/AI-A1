import java.util.ArrayList;

public class Matrix
{
	private int m,n;
	private double val[][];

	public int getM() {
		return m;
	}
	public int getN(){
		return n;
	}
	public double[][] getVal(){
		return val;
	}

	public double get(int i, int j){
		return val[i][j];
	}
	public void set(int i, int j, double v){
		val[i][j] = v;
	}
	public Matrix(int ni, int mi){
		m = mi;
		n = ni;
		val = new double[n][m];
		for (int i=0; i < n; i++){
			for (int j=0; j < m; j++){
				val[i][j]=0;
			}
		}
	}
	
	public Matrix(Matrix B){
		m = B.getM();
		n=B.getN();
		val = new double[B.getN()][B.getM()];
		val=B.getVal();
	}

	public Matrix(int ni, int mi, double vali[][]){
		m = mi;
		n = ni;
		val = vali;
	}



	public Matrix prod(Matrix B)  {
		Matrix A = new Matrix(this.getN(), B.getM());
		if (this.getM() != B.getN()){
			System.err.println("Sizes incompatible for product");
		}

		for (int i=0; i < A.getN(); i++){
			for (int j=0; j < A.getM(); j++){
				double s = 0;
				for (int k=0; k < B.getN(); k++){
					s+=this.get(i,k)*B.get(k, j);
				}
				A.set(i,j, s);
			}
		}
		return A;
	}

	public String toString() {
		String s= n +" "+m;
	    for (int i=0; i < this.getN(); i++){
			for (int j=0; j < this.getM(); j++){
				s+=" " + this.get(i,j);
			}
	    }
		return s;
    }

		public Matrix getVecFromCol(int col){
			Matrix vec = new Matrix(1, m);
			for(int j=0; j < m; j++){
				vec.set(0,j,this.get(j,0));
			}
			return vec;
		}


}
