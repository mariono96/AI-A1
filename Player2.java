import java.util.ArrayList;

class Player2 {

    private int nbRound;

    private Matrix birdMove;
    private int[] nbBirdsSpecies;
    private HMM[] hmmSpecies;

    
    public Player2() {
        nbRound = 0;

        birdMove = new Matrix(Constants.COUNT_SPECIES, Constants.COUNT_MOVE);
        nbBirdsSpecies = new int[Constants.COUNT_SPECIES];
        hmmSpecies = new HMM[Constants.COUNT_SPECIES];
        for (int i = 0; i < Constants.COUNT_SPECIES; i++){
            hmmSpecies[i] = new HMM(Constants.COUNT_SPECIES, Constants.COUNT_MOVE);
        }
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

        // This line chooses not to shoot.
        return cDontShoot;

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
        double[] probaSeq;
        double[] probaSeqSpecies;
        for (int i = 0; i < pState.getNumBirds(); i++){
            int returnValue = 5;
            if (nbRound !=0){
                probaSeq = new double[Constants.COUNT_SPECIES];
                // Compute the proba of the observations depending on the species
                for(int numSpecies = 0; numSpecies < Constants.COUNT_SPECIES;numSpecies++){
                    probaSeqSpecies = new double[Constants.COUNT_SPECIES];
                    for (int k = 0; k < Constants.COUNT_SPECIES; k++){
                        probaSeqSpecies[k] = hmmSpecies[numSpecies].pi.get(0,k) * hmmSpecies[numSpecies].B.get(k,pState.getBird(i).getObservation(0));
                    }
                    for (int j = 1; j < 10; j++){
                        probaSeqSpecies = hmmSpecies[numSpecies].alphaPass(probaSeqSpecies, pState.getBird(i).getObservation(j));
                    }
                    double sum = 0;
                    for (int j = 0; j < Constants.COUNT_SPECIES; j++){
                        sum += probaSeqSpecies[j];
                    }
                    probaSeq[numSpecies] = sum;
                }
                double max = 0;
                for (int j = 0; j < Constants.COUNT_SPECIES; j++){
                    if (probaSeq[j] > max){
                        returnValue = j;
                        max = probaSeq[j];
                    }
                }
            }
            switch(returnValue){
                case 0 : lGuess[i] = Constants.SPECIES_PIGEON ; break;
                case 1 : lGuess[i] = Constants.SPECIES_RAVEN ; break;
                case 2 : lGuess[i] = Constants.SPECIES_SKYLARK ; break;
                case 3 : lGuess[i] = Constants.SPECIES_SWALLOW ; break;
                case 4 : lGuess[i] = Constants.SPECIES_SNIPE ; break;
                case 5 : lGuess[i] = Constants.SPECIES_BLACK_STORK ; break;
                default : lGuess[i] = Constants.SPECIES_UNKNOWN;
            }
        }
      
            
        

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
        // Count the number of each type of birds

        ArrayList<Integer> obs;
        for (int i = 0; i < pState.getNumBirds(); i++){
            obs = new ArrayList<Integer>();
            for (int j = 0; j < pState.getBird(i).getSeqLength(); j++){
                if (pState.getBird(i).getObservation(j)!= -1){
                    obs.add(pState.getBird(i).getObservation(j));
                }
            }
            hmmSpecies[pSpecies[i]].train(obs);
        }
        nbRound ++;
        for (int i = 0; i < Constants.COUNT_SPECIES; i++){
            System.err.println("Voici le hmm dorrespondant à l'espece : " + i);
            System.err.println("A :");
            System.err.println(hmmSpecies[i].A);
            System.err.println("B :");
            System.err.println(hmmSpecies[i].B);
            System.err.println("Pi :");
            System.err.println(hmmSpecies[i].pi);
        }
    }

    public static final Action cDontShoot = new Action(-1, -1);
}
