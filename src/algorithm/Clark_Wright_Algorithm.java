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
public class Clark_Wright_Algorithm {

    private List<Saving> savings = new ArrayList<>();
    private Problem problem;

    public Clark_Wright_Algorithm(Problem problem) {
        this.problem = problem;
    }

    public List<Saving> getSavings() {
        return savings;
    }

    public void setSavings(List<Saving> routeSegmentsWithSavings) {
        this.savings = routeSegmentsWithSavings;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public void createSavings() {
        Customer depot = problem.getDepot();
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

    public void sortSavings() {
        Collections.sort(savings, Comparator.comparingDouble(Saving::getSaving).reversed());
    }
}
