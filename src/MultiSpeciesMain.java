import java.util.ArrayList;
import java.util.Arrays;

public class MultiSpeciesMain {
    public static void main(String[] args){

        double specific_alpha = Math.log(11.5)/500.;
        //BioSystem.multiSpeciesDistribution(2.);
        //BioFrame bf = new BioFrame(); bf.setVisible(true);
        BioSystem.timeTillResistance(0.05);
    }
}
