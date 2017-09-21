import java.util.ArrayList;
import java.lang.Math.*;
import java.util.Random;

public class HMM{
	public Matrix A, B;
	public Matrix pi, lastS;
	
	private int N, M;
	
	public HMM(int N, int M){
		A = new Matrix(N,N);
		B = new Matrix(N,M);
		pi = new Matrix(1,N);
		this.N = N;
		this.M = M;
		lastS = new Matrix(1,N);
		
		Random rG = new Random();
		
		for (int i=0; i <N; i++){
			double sum = 0;
			for (int j=0; j < N; j++){
				double y = rG.nextDouble();
				if (i==j){
					A.set(i,j, 5+y);
				}
				else {
					A.set(i,j, y);
				} 
				sum+=y;
			}
			for (int j=0; j < N; j++){
				A.set(i,j, A.get(i,j)/(sum+5*N));
			}
			
		}
		
		for (int i=0; i <N; i++){
			double sum = 0;
			for (int j=1; j < M; j++){
				double u = rG.nextDouble();
				B.set(i,j, u);
				sum+=u;				
			}
			for (int j=1; j < M; j++){
				B.set(i,j, B.get(i,j)/sum );		
			}
		}
		double sum = 0;
		for (int i=0; i < N; i++ ){
			double y = rG.nextDouble();
			pi.set(0, i, y);
			sum+=y;
		}
		for (int i=0; i < N; i++ ){
			pi.set(0,i, pi.get(0,i)/sum);
			lastS.set(0,i, pi.get(0,i));
		}
		
		System.err.print("pi initial :" + pi);
		
		
	}
	
	public void init(Matrix Ai, Matrix Bi, Matrix p){
		A = Ai;
		B = Bi;
		p = p;
		N = Ai.getN();
		M = Bi.getM();
	}
	
	public void train(ArrayList<Integer> obs){
		int T = obs.size();
		boolean one=false;
		for (int i =0; i < N; i++){
			if (i == obs.get(0)){
				pi.set(0,i,1.0);
				one=true;
			}
			else {
				pi.set(0,i, 0.0);
			}
		}
		if (!one){
		}
		pi.set(0,0,1.0);
		lastS = pi;
		Matrix alphas = new Matrix(N, T);
      Matrix betas = new Matrix(N, T);
      Matrix c = new Matrix(1,T);
      Matrix gammas= new Matrix(N, T);

      double[][][] diGammas = new double[T][N][N];

      //init
      int maxIters = 5;
      int iters = 0;
      double oldLogProb = Double.NEGATIVE_INFINITY;

      boolean iterate = true;
      while (iterate){
        //alpha-pass
        c.set(0,0,0);
        for (int i=0; i < N; i++){
          alphas.set(i,0, pi.get(0,i)* B.get(i, obs.get(0)) );
          c.set(0,0, c.get(0,0) + alphas.get(i,0));
        }

        // System.out.println(iters+" 0 "+c.get(0,0));
        c.set(0,0, 1/c.get(0,0));
        for (int i=0; i < N; i++){
          alphas.set(i,0, c.get(0,0)*alphas.get(i,0));
        }

        for (int t=1; t < T; t++){
          c.set(0,t,0);
          for (int i=0; i < N; i++){
            alphas.set(i,t,0);
            for (int j=0; j < N; j++){
              alphas.set(i,t, alphas.get(i,t)+ alphas.get(j,t-1)*A.get(j,i));
          }
          alphas.set(i,t, alphas.get(i,t)*B.get(i,obs.get(t)));
          c.set(0,t, c.get(0,t)+alphas.get(i,t));

        }
        // System.out.println(iters+" "+t+" "+c.get(0,t));
          c.set(0,t, 1/c.get(0,t));
          for (int i=0; i < N; i++){
            alphas.set(i,t, alphas.get(i,t)*c.get(0,t));
          }
        }

        //beta-pass
        for (int i=0; i < N; i++){
          betas.set(i, T-1, c.get(0,T-1));
        }

        for (int t = T-2; t >= 0; t--){
          for (int i=0; i < N; i++){
            betas.set(i,t,0);
            for (int j=0; j < N; j++){
              betas.set(i, t, betas.get(i,t)  + A.get(i,j)*B.get(j, obs.get(t+1))*betas.get(j, t+1));
            }
            betas.set(i,t, betas.get(i,t)*c.get(0,t));
          }
        }



      //gammas
      for (int t=0; t < T-1; t++){
        double denom = 0;
        for (int i=0; i < N; i++){
          for (int j=0; j < N; j++){
            denom+= alphas.get(i,t)*A.get(i,j)*B.get(j, obs.get(t+1))*betas.get(j, t+1);
          }
        }

        for (int i=0; i < N; i++){
          gammas.set(i,t,0);
          for (int j=0; j < N; j++){
            diGammas[t][i][j]= (alphas.get(i,t)*A.get(i,j)*B.get(j, obs.get(t+1))*betas.get(j, t+1))/denom;
            gammas.set(i,t, gammas.get(i,t) + diGammas[t][i][j]);
          }
        }
      }

      double denomi= 0;
      for (int i=0; i < N; i++){
        denomi = denomi + alphas.get(i, T-1);
      }
      for (int i=0; i < N; i++){
        gammas.set(i, T-1, alphas.get(i, T-1)/denomi);
      }

      // re-estimation
      //pi
      for (int i=0; i < N; i++){
        pi.set(0,i, gammas.get(i,0));
		lastS.set(0, i, gammas.get(i,T-1));
      }
      //A
      for (int i=0; i < N; i++){
        for (int j=0; j < N; j++){
          double numer = 0;
          double denom = 0;
          for (int t =0; t < T-1;t++){
            numer+= diGammas[t][i][j];
            denom+=gammas.get(i,t);
          }
          // System.out.println("Denom 174 " + denom);
          A.set(i,j, numer/denom);
        }
      }
	  
      //B
      for (int i=0; i < N; i++){
        for (int j=0; j < M; j++){
          double numer = 0;
          double denom = 0;
          for (int t =0; t < T-1;t++){
            if (obs.get(t)==j){
              numer+=gammas.get(i,t);
            }
            denom+=gammas.get(i,t);
          }
          // System.out.println("Denom 189 " + denom);
          B.set(i,j, numer/denom);
        }
      }


      //log P
      double logProb = 0;
      for (int i=0; i < T; i++){
        logProb = logProb + Math.log10(c.get(0,i));
      }
			logProb = -logProb;
			// System.out.println(logProb);


      //iterate ?
      iters++;
      if (iters < maxIters && logProb > oldLogProb ){
        // System.out.println("iter= "+iters);
        oldLogProb = logProb;
      }
      else {
        iterate = false;
		}

	}
	}
	
	public double predictNextMoveProb(int T){
		Matrix states = lastS;
		states = lastS.prod(B);
		double res = 0;
		for (int i=0; i < states.getM(); i++){
			if (states.get(0,i) > res){
				res = states.get(0,i);
			}
		}
		
		return res;
	}
	
	public int predictNextMove(int T){
		Matrix states = lastS;
		states = lastS.prod(B);
		double res = 0;
		int moveState=0;
		for (int i=0; i < states.getM(); i++){
			if (states.get(0,i) > res){
				moveState=i;
			}
		}
		return moveState;
	}

 public double[] alphaPass(double[] probaPrevState, int numObservation){
            double[] returnValue = new double[6];
            double value ;
            for (int i = 0 ; i < 6 ; i++){
                value = 0;
                for (int j = 0; j < 6; j++){
                    value = value + probaPrevState[j]*A.get(j,i);
                }
                value = value * B.get(i, numObservation);
                returnValue[i] = value;
            }
            return returnValue;
        }

}
