import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Microhabitat {
    Random rand = new Random();
    //karrying kapacity of the microhabitat
    private int K;
    //concentration of antibiotic present in the microhabitat
    private double c;
    private ArrayList<Bacteria> population;


    public Microhabitat(int K, double c){
        this.K = K;
        this.c = c;
        this.population = new ArrayList<>();
    }

    public int getK(){return K;}
    public void setK(int K){this.K  = K;}

    public double getC(){return c;}
    public void setC(double c){this.c = c;}

    //returns the no. of bacteria in the microhabitat
    public int getN(){
        return population.size();
    }

    public int getN_of_M(int m){
        int runningTotal = 0;
        for(Bacteria bac : population){
            if(bac.getM() == m) runningTotal++;
        }
        return runningTotal;
    }

    public int[] getAll_N_of_M(){
        int minGeno = Bacteria.getMin_genotype();
        int maxGeno = Bacteria.getMax_genotype();
        int[] speciesPopSizes = new int[maxGeno+1];

        for(int m = 0; m <= maxGeno; m++){
            speciesPopSizes[m] = getN_of_M(m);
        }

        return speciesPopSizes;
    }

    public ArrayList<Bacteria> getPopulation(){
        return population;
    }

    public Bacteria getABacteria(int i){
        return population.get(i);
    }

    public void removeABacterium(int i){ population.remove(i); }
    public void addABacteria(Bacteria bac){ population.add(bac); }


    //this adds a specified no. of bcteria to the microhabitat.
    //their genotypes are randomly assigned
    public void addSomeRandoms(int nBacteria){
        int min_geno = Bacteria.getMin_genotype();
        int max_geno = Bacteria.getMax_genotype()+1;

        for(int i = 0; i < nBacteria; i++){
            int rand_m  = rand.nextInt(max_geno - min_geno) + min_geno;
            population.add(new Bacteria(rand_m));
        }
    }
}
