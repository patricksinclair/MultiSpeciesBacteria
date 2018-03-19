public class Bacteria {

    //genotype of the bacteria
    private int m;
    private static int min_genotype = 0, max_genotype = 10;
    private double d = 0.1, b = 0.1;

    public Bacteria(int m){
        this.m  = m;
    }

    public int getM(){
        return m;
    }
    public void setM(int m){
        this.m = m;
    }

    public double getB(){
        return b;
    }

    public void setB(double b){
        this.b = b;
    }

    public double getD(){
        return d;
    }

    public void setD(double d){
        this.d = d;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double beta(){
        return (double)m;
    }

    public double phi_c(double c){
        double phi_c = 1. - (c/beta())*(c/beta());
        return  (phi_c > 0.) ? phi_c : 0.;
    }

    //this growth rate is the same as the PRL paper, which relies on a Karrying Kapacity rather than nutrients
    public double replicationRate(double c, double N, double K){
        double gRate = phi_c(c)*(1. - N/K);
        return  (gRate > 0.) ? gRate : 0.;
    }



    public static int getMin_genotype(){
        return min_genotype;
    }
    public static int getMax_genotype(){
        return max_genotype;
    }
}
