package algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.Customer;
import project.Database;
import project.Route;
import project.RouteSegment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Marcin on 2017-06-21.
 */
public class ClarkWrightAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(ClarkWrightAlgorithm.class);

    private final String name = "Clark-Wright Algorithm";
    private List<Customer> customers;
    private List<RouteSegment> routeSegments;
    private List<Route> routes;

    public ClarkWrightAlgorithm(Problem problem) {
        super(problem);
        super.setAlgorithmName(name);
        super.setSolution(new Solution(problem.getProblemID(), name));
        customers = Database.getCustomerList();
        routeSegments = Database.getRouteSegmentsList();
        routes = getSolution().getListOfRoutes();
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Clark-Wright algorithm...");
        createSavings();
        sortSavings();
        calculateSolution();
        saveSolution();
    }

    private void createSavings() {
        logger.info("Creating savings...");
        Customer depot = super.getProblem().getDepot();
        for (int i = 1; i < customers.size(); i++) {
            for (int j = i; j < customers.size(); j++) {
                if (i != j) {
                    Customer first = customers.get(i);
                    Customer second = customers.get(j);
                    int firstID = customers.get(i).getId();
                    int secondID = customers.get(j).getId();
                    if ((depot.getDistances().get(firstID) != null) && (depot.getDistances().get(secondID) != null) && (first.getDistances().get(secondID) != null)) {
                        double saving = depot.getDistances().get(firstID) + depot.getDistances().get(secondID) - first.getDistances().get(secondID);
                        for (RouteSegment segment : routeSegments) {
                            int srcID = segment.getSrc().getId();
                            int dstID = segment.getDst().getId();

                            if (srcID == firstID && dstID == secondID) {
                                segment.setClarkWrightSaving(saving);
                                break;
                            }
                        }
                        logger.debug("Saving for customers " + firstID + "-" + secondID + "= " + saving + " km");
                    }
                }
            }
        }
        logger.info("Creating savings has been completed.");
    }

    private void sortSavings() {
        logger.info("Sorting route segments by savings...");
        Collections.sort(routeSegments, Comparator.comparingDouble(RouteSegment::getClarkWrightSaving).reversed());
        logger.info("Sorting route segments by savings has been completed.");
    }

    private void calculateSolution() {
        logger.info("Calculating the solution...");
        double maxCapacity = getProblem().getVehicleCapacity();
        for (RouteSegment segment : routeSegments) {
            Customer src = segment.getSrc();
            Customer dst = segment.getDst();
            Double distance = segment.getDistance();
            if (src.getId() != 0 && dst.getId() != 0) {
                logger.debug("Savings for loop: " + segment.getSrc().getId() + "-" + segment.getDst().getId());
//            żaden klient nie należy do trasy
                if (!isCustomerInRoute(src) && !isCustomerInRoute(dst)) {
                    if ((src.getPackageWeight() + dst.getPackageWeight()) <= maxCapacity) {
                        Route route = new Route();
                        route.addCustomerToFirstPosition(src, 0);
                        route.addCustomerToLastPosition(dst, distance);
                        logger.debug("Creating new route with ID " + route.getId() + " for customers: " + src.getId() + "-" + dst.getId());
                        logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                        route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                        logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());

                        if (!routes.contains(route)) {
                            logger.debug("Adding route \"" + route.getId() + "\" to solution.");
                            routes.add(route);
                        }
                    }
                }
//            pierwszy klient nie należy do trasy, a drugi jest brzegowym węzłem trasy
                else if (!isCustomerInRoute(src)) {
                    for (Route route : routes) {
                        if (route.canAddCustomer(src.getPackageWeight(), maxCapacity)) {
                            if (route.isCustomerOnFirstPosition(dst)) {
                                route.addCustomerToFirstPosition(src, distance);
                                logger.debug("Customer with id " + src.getId() + " added as FIRST node to route " + route.getId());
                                logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                                route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                                logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                                break;
                            } else if (route.isCustomerOnLastPosition(dst)) {
                                route.addCustomerToLastPosition(src, distance);
                                logger.debug("Customer with id " + src.getId() + " added as LAST node to route " + route.getId());
                                logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                                route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                                logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                                break;
                            }
                        }
                    }
                }
//            drugi klient nie należy do trasy, a pierwszy jest brzegowym węzłem trasy
                else if (!isCustomerInRoute(dst)) {
                    for (Route route : routes) {
                        if (route.canAddCustomer(dst.getPackageWeight(), maxCapacity)) {
                            if (route.isCustomerOnFirstPosition(src)) {
                                route.addCustomerToFirstPosition(dst, distance);
                                logger.debug("Customer with id " + dst.getId() + " added as FIRST node to route " + route.getId());
                                logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                                route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                                logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                                break;
                            } else if (route.isCustomerOnLastPosition(src)) {
                                route.addCustomerToLastPosition(dst, distance);
                                logger.debug("Customer with id " + dst.getId() + " added as LAST node to route " + route.getId());
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
                for (Route routeA : routes) {
//                if (tmpRoute != null) {
//                    break;
//                }
                    for (Route routeB : routes) {
                        if (routeA != routeB) {
                            if ((routeA.isCustomerOnLastPosition(src) && routeB.isCustomerOnFirstPosition(dst))
                                    || (routeA.isCustomerOnLastPosition(dst) && routeB.isCustomerOnFirstPosition(src))) {
                                if ((routeA.getCurrentPackagesWeight() + routeB.getCurrentPackagesWeight()) <= maxCapacity) {
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

                if (tmpRoute != null) {
                    logger.debug("Removing route \"" + tmpRoute.getId() + "\" from solution because of merge.");
                    routes.remove(tmpRoute);
                }
            }
        }
//        dodanie do tras odcinków od magazynu i do magazynu
        addDepotNodeAsFirstAndLast();
        logger.info("Calculating the solution has been completed.");
    }

    private boolean isCustomerInRoute(Customer customer) {
        for (Route route : routes) {
            for (Customer c : route.getCustomersInRoute()) {
                if (customer == c) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addDepotNodeAsFirstAndLast() {
        Customer depot = super.getProblem().getDepot();
        for (Route route : routes) {
            int firstCustomerID = route.getCustomersInRoute().get(0).getId();
            int lastCustomerID = route.getCustomersInRoute().get(route.getCustomersInRoute().size() - 1).getId();
            route.addCustomerToFirstPosition(depot, depot.getDistances().get(firstCustomerID));
            route.addCustomerToLastPosition(depot, depot.getDistances().get(lastCustomerID));
        }
    }

    @Override
    public void saveSolution() {
        logger.info("Saving solution...");
        Database.getSolutionsList().add(getSolution());
        logger.info("Wyznaczono " + routes.size() + " tras");
        for (Route route : routes) {
            logger.info("-----> Trasa nr " + routes.indexOf(route) + ": łączna długość - " + route.getTotalDistance()
                    + " km, łączna masa paczek - " + route.getCurrentPackagesWeight() + " kg");
            route.getCustomersInRoute().forEach(Customer -> logger.info(Customer.getId() + "-"));
        }
        logger.info("Saving solution has been completed.");
    }
}
