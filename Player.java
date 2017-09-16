import java.util.ArrayList;

class Player {

    public Player() {
		turnCount = 0;
		notShoot = 0;
		doShoot = 0;
    }

    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     *
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */
		 		 
		 /* not sure it's clever but I'm gonna do it anyway */
		 
		 if (turnCount == 0 || pState.getRound() != round){
			 obs = new ArrayList[pState.getNumBirds()];
			 hmm = new HMM[pState.getNumBirds()];
			 for (int i = 0; i < pState.getNumBirds(); i++){
				 obs[i] = new ArrayList<Integer>();
				 hmm[i]=new HMM(5,11);
			 }
			 
			 round = pState.getRound();
		 } 
		 Action nextAction = cDontShoot;
		 notShoot++;
		 for (int b=0; b < pState.getNumBirds(); b++){
			(obs[b]).add(pState.getBird(b).getLastObservation()+1);
		 }
		 if (turnCount >= 90){
			
			if ((turnCount%100)%90 == 0){
			for (int b=	0; b < pState.getNumBirds(); b++){
				hmm[b].train(obs[b]);
			}	 
			}
				 
				 //Pour un seul piaf. 
				 // Quand il y en a plusieurs, chercher le max des probas et shooter
	/* 			 if (hmm[0].predictNextMoveProb(turnCount) > 0.8 && !pState.getBird(0).isDead())  {
					 nextAction=new Action(0, hmm[0].predictNextMove(turnCount) -1);
					 System.err.println("SHOOT in " + turnCount);
					 System.err.println("Proba "+ hmm[0].predictNextMoveProb(turnCount));
				} else {
					 System.err.println("Didn't shoot");
				 } */
			int bird=-1;
			double prob= -1;
			for (int b=0; b < pState.getNumBirds(); b++){
				System.err.println(hmm[b].predictNextMoveProb(turnCount));
				if (!pState.getBird(b).isDead() && (hmm[b].predictNextMoveProb(turnCount) > prob)){
					bird = b;
					prob = hmm[b].predictNextMoveProb(turnCount) ;
				}
			}
			System.err.println("bird : " + bird);
			if (bird == -1){ //should mean that all birds have been shot
				turnCount++;
				return cDontShoot;
			}
			
			if (prob > 0.99){
				doShoot++; notShoot--;
				nextAction = new Action(bird, hmm[bird].predictNextMove(turnCount) - 1);
			}

		}
		 
		 
		 turnCount++;
		 
		System.err.println("not :" + notShoot + "  do :" + doShoot);
		return nextAction;
		
		
        // This line chooses not to shoot.
        // return cDontShoot;

        // This line would predict that bird 0 will move right and shoot at it.
        // return Action(0, MOVE_RIGHT);
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */
		

        int[] lGuess = new int[pState.getNumBirds()];
        for (int i = 0; i < pState.getNumBirds(); ++i)
            lGuess[i] = Constants.SPECIES_UNKNOWN;
        return lGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    }

    public static final Action cDontShoot = new Action(-1, -1);
	
	private int turnCount;
	private ArrayList<Integer> obs[];
	private HMM[] hmm;
	private int round;
	private int notShoot;
	private int doShoot;
}
