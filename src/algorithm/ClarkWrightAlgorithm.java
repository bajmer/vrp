package algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class ClarkWrightAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(ClarkWrightAlgorithm.class);

    private final String name = "Clark-Wright Algorithm";
    private List<Saving> savings;

    public ClarkWrightAlgorithm(Problem problem) {
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
        savings.forEach(Saving -> logger.debug("Sorted savings: " + Saving.getFirst().getId() + "-" + Saving.getSecond().getId()));
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
                    logger.debug("Saving for customers " + first.getId() + "-" + second.getId() + ": " + saving + " km");
                }
            }
        }
    }

    private void sortSavings() {
        Collections.sort(savings, Comparator.comparingDouble(Saving::getSaving).reversed());
    }

    private void calculateSolution() {
        for (Saving saving : savings) {
            logger.debug("Savings for loop: " + saving.getFirst().getId() + "-" + saving.getSecond().getId());
//            żaden klient nie należy do trasy
            if (!isCustomerInRoute(saving.getFirst()) && !isCustomerInRoute(saving.getSecond())) {
                if ((saving.getFirst().getPackageWeight() + saving.getSecond().getPackageWeight()) <= getProblem().getVehicleCapacity()) {
                    Route route = new Route();
                    route.addCustomer(saving.getFirst());
                    route.addCustomer(saving.getSecond());
                    logger.debug("Creating new route with ID " + route.getId() + " for customers: " + saving.getFirst().getId() + "-" + saving.getSecond().getId());
                    logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                    route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                    logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());

                    if (!getSolution().getListOfRoutes().contains(route)) {
                        logger.info("Adding route \"" + route.getId() + "\" to solution.");
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
                            logger.debug("Customer with id " + saving.getFirst().getId() + " added as FIRST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;
                        } else if (route.isCustomerLastInRoute(saving.getSecond())) {
                            route.addCustomer(saving.getFirst());
                            logger.debug("Customer with id " + saving.getFirst().getId() + " added as LAST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
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
                            logger.debug("Customer with id " + saving.getSecond().getId() + " added as FIRST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;
                        } else if (route.isCustomerLastInRoute(saving.getFirst())) {
                            route.addCustomer(saving.getSecond());
                            logger.debug("Customer with id " + saving.getSecond().getId() + " added as LAST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;
                        }
                    }
                }
            }

//            sprawdzanie możliwości połączenia dwóch tras
            Route tmpRoute = null;
            for (Route routeA : getSolution().getListOfRoutes()) {
                if (tmpRoute != null) {
                    break;
                }
                if (routeA.isCustomerLastInRoute(saving.getFirst())) {
                    for (Route routeB : getSolution().getListOfRoutes()) {
                        if (routeB.isCustomerFirstInRoute(saving.getSecond())) {
                            if (routeA != routeB) {
                                if ((routeA.getCurrentPackagesWeight() + routeB.getCurrentPackagesWeight()) <= getProblem().getVehicleCapacity()) {
                                    routeA.mergeRoute(routeB);
                                    tmpRoute = routeB;
                                    logger.debug("Route \"" + routeA.getId() + "\" was merged with route \"" + routeB.getId() + "\"");
                                    logger.debug("Route \"" + routeA.getId() + "\" includes the following customers: ");
                                    routeA.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                                    logger.debug("and current packages weight for this route is " + routeA.getCurrentPackagesWeight());
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (tmpRoute != null) {
                logger.info("Removing route \"" + tmpRoute.getId() + "\" from solution because of merge.");
                getSolution().getListOfRoutes().remove(tmpRoute);
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
