import java.util.ArrayList;
import java.util.Random;

public class BioSystem {

    Random rand = new Random();
    //the no of microhabitats in the system, their carrying capacity
    private int L, K;
    //steepness of exponential antibiotic gradient
    private double alpha;
    //array of the microhabitats
    private Microhabitat[] microhabitats;

    private double timeElapsed;
    private int initialRandPop = 100;
    private boolean populationDead;

    public BioSystem(int L, int K, double alpha){
        this.L = L;
        this.K = K;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;
        populationDead = false;

        for(int i = 0; i < L; i++){
            double c_i = Math.exp(alpha*i) - 1.;
            //microhabitats[i] = new Microhabitat(K, c_i);
            //here this usage of alpha as c_i is just for the uniform antibiotic concentration
            microhabitats[i] = new Microhabitat(K, alpha);
            microhabitats[i].addSomeRandoms(initialRandPop);
        }
    }


    public int getL(){return L;}
    public void setL(int L){this.L = L;}

    public int getK(){return K;}
    public void setK(int K){this.K = K;}

    public double getAlpha(){return alpha;}
    public void setAlpha(double alpha){this.alpha = alpha;}

    public double getTimeElapsed(){return timeElapsed;}

    //returns the total number of bacteria in the system
    public int getCurrentPopulation(){
        int runningTotal = 0;
        for(int i = 0; i < L; i++){
            runningTotal += microhabitats[i].getN();
        }
        return runningTotal;
    }

    public Microhabitat getMicrohabitat(int i){
        return microhabitats[i];
    }

    public Bacteria getBacteria(int mh_index, int bac_index){
        return microhabitats[mh_index].getABacteria(bac_index);
    }

    public int[] getRandomIndexes(int randomIndex){
        int indexCounter = 0;
        int mh_index = 0;
        int bac_index = 0;

        forloop:
        for(int i = 0; i < getL(); i++) {

            if((indexCounter + microhabitats[i].getN()) <= randomIndex) {
                indexCounter += microhabitats[i].getN();
                continue forloop;
            } else {
                mh_index = i;
                bac_index = randomIndex - indexCounter;
                break forloop;
            }
        }
        return new int[]{mh_index, bac_index};
    }


    public void migrate(int mh_index, int bac_index){


        //if direction > 0.5, bacteria moves forward, else backwards
        double direction = rand.nextDouble();
        int destination_index = L+1234;

        if(mh_index == 0){
            destination_index = 0+1;
            microhabitats[destination_index].getPopulation().add(microhabitats[mh_index].getPopulation().remove(bac_index));

        }else if(mh_index == L-1){
            destination_index = L-2;
            microhabitats[destination_index].getPopulation().add(microhabitats[mh_index].getPopulation().remove(bac_index));

        }else {

            if(direction >= 0.5) {
                destination_index = mh_index+1;
                //removes a bacteria bac_index from mh_index and places it within destination_index
                microhabitats[destination_index].getPopulation().add(microhabitats[mh_index].getPopulation().remove(bac_index));
            }
            else if(direction < 0.5) {
                destination_index = mh_index-1;
                microhabitats[destination_index].getPopulation().add(microhabitats[mh_index].getPopulation().remove(bac_index));
            }
        }



    }

    public void death(int mh_index, int bac_index){
        microhabitats[mh_index].removeABacterium(bac_index);
        if(getCurrentPopulation() == 0) populationDead = true;
    }

    public void replicate(int mh_index, int bac_index){

        Bacteria mother = microhabitats[mh_index].getABacteria(bac_index);
        Bacteria daughter = new Bacteria(mother.getM());
        microhabitats[mh_index].addABacteria(daughter);
    }


    public void performAction(){

        if(!populationDead){
            int randomIndex = rand.nextInt(getCurrentPopulation());
            int[] indexes = getRandomIndexes(randomIndex);
            int mh_index = indexes[0];
            int bac_index = indexes[1];

            Microhabitat rand_mh = microhabitats[mh_index];
            Bacteria rand_bac = rand_mh.getABacteria(bac_index);
            double c = rand_mh.getC(), N = (double)rand_mh.getN(), K = (double)rand_mh.getK();

            double migration_rate = rand_bac.getB();
            double death_rate = rand_bac.getD();
            double growth_rate = rand_bac.replicationRate(c, N, K);
            double R_max = 1.2;
            double rand_chance = rand.nextDouble()*R_max;

            if(rand_chance <= migration_rate) migrate(mh_index, bac_index);
            else if(rand_chance > migration_rate && rand_chance <= (migration_rate + death_rate)) death(mh_index, bac_index);
            else if(rand_chance > (migration_rate + death_rate) && rand_chance <= (migration_rate + death_rate + growth_rate))
            {replicate(mh_index, bac_index);}

            timeElapsed += 1./((double)getCurrentPopulation()*R_max);
        }
    }


    public static void multiSpeciesDistribution(double inputAlpha){

        int L = 500, K = 500, nGenotypes = Bacteria.getMax_genotype() - Bacteria.getMin_genotype();
        double duration = 100., interval = 20.;
        //double c = inputAlpha;
        int nReps = 10;
        String filename = "multiSpecies_popDistributions-c="+String.valueOf(inputAlpha);


        int[][][] repeatedPopData = new int[nReps][][];
        ///////-STUFF HAPPENS HERE-////////////////
        for(int r = 0; r < nReps; r++){
            boolean alreadyRecorded = false;
            int[][] multiSpeciesPopSizes = new int[L][];
            BioSystem bioSystem = new BioSystem(L, K, inputAlpha);

            while(bioSystem.timeElapsed <= duration){

                bioSystem.performAction();

                if((bioSystem.getTimeElapsed()%interval >= 0. && bioSystem.getTimeElapsed()%interval <= 0.01) && !alreadyRecorded){
                    System.out.println("rep: "+ String.valueOf(r)+"\ttime elapsed: "+String.valueOf(bioSystem.getTimeElapsed()));
                    alreadyRecorded = true;
                }
                if(bioSystem.getTimeElapsed()%interval >= 0.1 && alreadyRecorded) alreadyRecorded = false;
            }
            for(int i = 0; i < L; i++){

                multiSpeciesPopSizes[i] = bioSystem.microhabitats[i].getAll_N_of_M();
            }
            repeatedPopData[r] = multiSpeciesPopSizes;
        }
        //////////////////////////////////////////

        double[][] averagedMultiSpeciesPopSizes =  Toolbox.averagePopulationResults(repeatedPopData);
        Toolbox.writeMultiSpeciesPopSizesToFile(filename, averagedMultiSpeciesPopSizes);


    }


}
