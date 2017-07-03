package algorithm;

import project.Customer;
import project.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Marcin on 2017-06-21.
 */
public class Clark_Wright_Algorithm extends Algorithm {

    private List<Saving> savings;

    public Clark_Wright_Algorithm(Problem problem) {
        super(problem);
        super.setAlgorithmName("Clark-Wright Algorithm");
        savings = new ArrayList<>();
    }

    public List<Saving> getSavings() {
        return savings;
    }

    public void setSavings(List<Saving> routeSegmentsWithSavings) {
        this.savings = routeSegmentsWithSavings;
    }

    @Override
    public void runAlgorithm() {
        createSavings();
        sortSavings();
        Solution clarkWrightSolution = calculateSolution();
        saveSolution(clarkWrightSolution);
    }

    private void createSavings() {
        Customer depot = super.getProblem().getDepot();
        for (int i = 0; i < Database.getCustomerList().size(); i++) {
            for (int j = i; j < Database.getCustomerList().size(); j++) {
                if (i > 0 && i != j) {
                    Customer src = Database.getCustomerList().get(i);
                    Customer dst = Database.getCustomerList().get(j);

                    double saving = depot.getDistances().get(src.getId()) + depot.getDistances().get(dst.getId()) - src.getDistances().get(dst.getId());
                    savings.add(new Saving(src, dst, saving));
                    System.out.println("Saving for " + src.getId() + "-" + dst.getId() + ": " + saving + " km");
                }
            }
        }
    }

    private void sortSavings() {
        Collections.sort(savings, Comparator.comparingDouble(Saving::getSaving).reversed());
    }

    private Solution calculateSolution() {
        Solution solution = new Solution(getProblem().getProblemID(), getAlgorithmName());

        return solution;
    }

    private void saveSolution(Solution solution) {

    }
}
