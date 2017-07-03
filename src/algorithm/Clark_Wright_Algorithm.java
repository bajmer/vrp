package algorithm;

import project.Customer;
import project.Database;
import project.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Marcin on 2017-06-21.
 */
public class Clark_Wright_Algorithm extends Algorithm {

    private final String name = "Clark-Wright Algorithm";
    private List<Saving> savings;

    public Clark_Wright_Algorithm(Problem problem) {
        super(problem);
        super.setAlgorithmName(name);
        super.setSolution(new Solution(problem.getProblemID(), name));
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
        savings.forEach(Saving -> System.out.println("Lista oszczednosci: " + Saving.getFirst().getId() + "-" + Saving.getSecond().getId()));
        calculateSolution();
        saveSolution();
    }

    private void createSavings() {
        Customer depot = super.getProblem().getDepot();
        for (int i = 0; i < Database.getCustomerList().size(); i++) {
            for (int j = i; j < Database.getCustomerList().size(); j++) {
                if (i > 0 && i != j) {
                    Customer first = Database.getCustomerList().get(i);
                    Customer second = Database.getCustomerList().get(j);

                    double saving = depot.getDistances().get(first.getId()) + depot.getDistances().get(second.getId()) - first.getDistances().get(second.getId());
                    savings.add(new Saving(first, second, saving));
                    System.out.println("Saving for " + first.getId() + "-" + second.getId() + ": " + saving + " km");
                }
            }
        }
    }

    private void sortSavings() {
        Collections.sort(savings, Comparator.comparingDouble(Saving::getSaving).reversed());
    }

    private void calculateSolution() {
        for (Saving saving : savings) {
            System.out.println("Badanie oszczednosci " + saving.getFirst().getId() + " - " + saving.getSecond().getId());
//            żaden klient nie należy do trasy
            if (!isCustomerInRoute(saving.getFirst()) && !isCustomerInRoute(saving.getSecond())) {
                if ((saving.getFirst().getPackageWeight() + saving.getSecond().getPackageWeight()) <= getProblem().getVehicleCapacity()) {
                    Route route = new Route();
                    route.addCustomer(saving.getFirst());
                    route.addCustomer(saving.getSecond());
                    System.out.println("Nowa trasa ID " + route.getId() + ": " + saving.getFirst().getId() + "-" + saving.getSecond().getId());

                    if (!getSolution().getListOfRoutes().contains(route)) {
                        getSolution().getListOfRoutes().add(route);
                    }
                }
            }
//            pierwszy klient nie należy do trasy, a drugi jest brzegowym węzłem trasy
            else if (!isCustomerInRoute(saving.getFirst())) {
                for (Route route : getSolution().getListOfRoutes()) {
                    if (route.canAddCustomer(saving.getFirst().getPackageWeight(), getProblem().getVehicleCapacity())) {
                        if (route.isCustomerFirstInRoute(saving.getSecond())) {
                            route.addCustomerFirstPlace(saving.getFirst());
                            System.out.println("Dodanie klienta " + saving.getFirst().getId() + " na początek trasy o ID " + route.getId());
                            route.getCustomersInRoute().forEach(Customer -> System.out.print(Customer.getId() + "-"));
                            System.out.println();
                            break;
                        } else if (route.isCustomerLastInRoute(saving.getSecond())) {
                            route.addCustomer(saving.getFirst());
                            System.out.println("Dodanie klienta " + saving.getFirst().getId() + " na koniec trasy o ID " + route.getId());
                            route.getCustomersInRoute().forEach(Customer -> System.out.print(Customer.getId() + "-"));
                            System.out.println();
                            break;
                        }
                    }
                }
            }
//            drugi klient nie należy do trasy, a pierwszy jest brzegowym węzłem trasy
            else if (!isCustomerInRoute(saving.getSecond())) {
                for (Route route : getSolution().getListOfRoutes()) {
                    if (route.canAddCustomer(saving.getSecond().getPackageWeight(), getProblem().getVehicleCapacity())) {
                        if (route.isCustomerFirstInRoute(saving.getFirst())) {
                            route.addCustomerFirstPlace(saving.getSecond());
                            System.out.println("Dodanie klienta " + saving.getSecond().getId() + " na początek trasy o ID " + route.getId());
                            route.getCustomersInRoute().forEach(Customer -> System.out.print(Customer.getId() + "-"));
                            System.out.println();
                            break;
                        } else if (route.isCustomerLastInRoute(saving.getFirst())) {
                            route.addCustomer(saving.getSecond());
                            System.out.println("Dodanie klienta " + saving.getSecond().getId() + " na koniec trasy o ID " + route.getId());
                            route.getCustomersInRoute().forEach(Customer -> System.out.print(Customer.getId() + "-"));
                            System.out.println();
                            break;
                        }
                    }
                }
            }


        }
    }

    private boolean isCustomerInRoute(Customer customer) {
        for (Route route : super.getSolution().getListOfRoutes()) {
            for (Customer c : route.getCustomersInRoute()) {
                if (customer == c) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void saveSolution() {

    }
}
