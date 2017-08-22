package com.vrp.bajmer.algorithm;

import com.vrp.bajmer.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Marcin on 2017-06-21.
 */
public class ClarkWrightAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(ClarkWrightAlgorithm.class);

    private final String name = "Clark-Wright";
    private List<Customer> customers;
    private List<RouteSegment> routeSegments;
    private List<Route> routes;

    public ClarkWrightAlgorithm(Problem problem) {
        super(problem);
        super.setAlgorithmName(name);
        super.setSolution(new Solution(problem.getProblemID(), name, problem.getDepot()));
        customers = Storage.getCustomerList();
        routeSegments = new ArrayList<>(Storage.getRouteSegmentsList().size());
        for (RouteSegment routeSegment : Storage.getRouteSegmentsList()) {
            routeSegments.add(routeSegment.clone());
        }

        routes = super.getSolution().getListOfRoutes();
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Clark-Wright algorithm...");
        createSavings();
        sortSavings();
        searchSolution();
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

    private void searchSolution() {
        logger.info("Calculating the solution...");
        double weightLimit = getProblem().getWeightLimitPerVehicle();
        double sizeLimit = getProblem().getSizeLimitPerVehicle();
        for (RouteSegment segment : routeSegments) {
            logger.debug("Processing route segment: " + segment.getSrc().getId() + "-" + segment.getDst().getId());

            Customer src = segment.getSrc();
            Customer dst = segment.getDst();

            if (!isCustomerInRoute(src) && !isCustomerInRoute(dst)) { //żaden klient nie należy do trasy
                if (src.getPackageWeight() + dst.getPackageWeight() <= weightLimit //suma paczek nie przekracza limitów (masy i objętości)
                        && src.getPackageSize() + dst.getPackageSize() <= sizeLimit) {
                    Route route = new Route();
                    route.addCustomerAsFirst(src);
                    route.addCustomerAsLast(dst);
                    route.addSegmentAsLast(segment);

                    logger.debug("Creating new route with ID " + route.getId() + " for customers: " + src.getId() + "-" + dst.getId());
                    logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                    route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                    logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());

                    if (!routes.contains(route)) {
                        logger.debug("Adding route \"" + route.getId() + "\" to solution.");
                        routes.add(route);
                        continue;
                    }
                }
            }
//            pierwszy klient nie należy do trasy, a drugi jest brzegowym węzłem trasy
            else if (!isCustomerInRoute(src)) {
                for (Route route : routes) {
                    if (route.canAdd(src.getPackageWeight(), weightLimit, src.getPackageSize(), sizeLimit)) {
                        if (route.isCustomerFirst(dst)) {
                            route.addCustomerAsFirst(src);
                            route.addSegmentAsFirst(segment);

                            logger.debug("Customer with id " + src.getId() + " added on the FIRST position in route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;

                        } else if (route.isCustomerLast(dst)) {
                            route.addCustomerAsLast(src);
                            segment.swapSrcDst();
                            route.addSegmentAsLast(segment);

                            logger.debug("Customer with id " + src.getId() + " added on the LAST position in route " + route.getId());
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
                    if (route.canAdd(dst.getPackageWeight(), weightLimit, dst.getPackageSize(), sizeLimit)) {
                        if (route.isCustomerFirst(src)) {
                            route.addCustomerAsFirst(dst);
                            segment.swapSrcDst();
                            route.addSegmentAsFirst(segment);

                            logger.debug("Customer with id " + dst.getId() + " added as FIRST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;

                        } else if (route.isCustomerLast(src)) {
                            route.addCustomerAsLast(dst);
                            route.addSegmentAsLast(segment);

                            logger.debug("Customer with id " + dst.getId() + " added as LAST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;
                        }
                    }
                }
            }

//            obaj klienci należą do różnych tras, łączenie 2 tras w jedną
            Route merged = null;
            Route saved = null;
            for (Route routeA : routes) {
                for (Route routeB : routes) {
                    if (routeA != routeB) {
                        if (routeA.canAdd(routeB.getCurrentPackagesWeight(), weightLimit, routeB.getCurrentPackagesSize(), sizeLimit)) {
//                            węzły można połączyć na 8 sposobów:
                            if (routeA.isCustomerLast(src) && routeB.isCustomerFirst(dst)) { // (1) .....s   d.....
                                //routeA jest 1 trasą, do niej doklejamy routeB
                                routeA.addSegmentAsLast(segment);
                                routeA.mergeRoute(routeB);
                                saved = routeA;
                                merged = routeB;
                                break;

                            } else if (routeA.isCustomerLast(dst) && routeB.isCustomerFirst(src)) { // (2) .....d   s.....
                                routeA.rotate();
                                routeB.rotate();
                                routeB.addSegmentAsLast(segment);
                                routeB.mergeRoute(routeA);
                                saved = routeB;
                                merged = routeA;
                                break;

                            } else if (routeA.isCustomerFirst(src) && routeB.isCustomerLast(dst)) { // (3) s.....   .....d
                                //routeB jest 1 trasą, do niej doklejamy routeA
                                routeA.rotate();
                                routeB.rotate();
                                routeA.addSegmentAsLast(segment);
                                routeA.mergeRoute(routeB);
                                saved = routeA;
                                merged = routeB;
                                break;

                            } else if (routeA.isCustomerFirst(dst) && routeB.isCustomerLast(src)) { // (4) d.....   .....s
                                routeB.addSegmentAsLast(segment);
                                routeB.mergeRoute(routeA);
                                saved = routeB;
                                merged = routeA;
                                break;

                            } else if (routeA.isCustomerFirst(src) && routeB.isCustomerFirst(dst)) { // (5) s.....   d.....
                                //routeA jest 1 trasą (obróconą), do niej doklejamy routeB
                                routeA.rotate();
                                routeA.addSegmentAsLast(segment);
                                routeA.mergeRoute(routeB);
                                saved = routeA;
                                merged = routeB;
                                break;

                            } else if (routeA.isCustomerFirst(dst) && routeB.isCustomerFirst(src)) { // (6) d.....   s.....
                                routeB.rotate();
                                routeB.addSegmentAsLast(segment);
                                routeB.mergeRoute(routeA);
                                saved = routeB;
                                merged = routeA;
                                break;

                            } else if (routeA.isCustomerLast(src) && routeB.isCustomerLast(dst)) { // (7) .....s   .....d
                                //routeB jest 1 trasą, do niej doklejamy route A (obróconą)
                                routeB.rotate();
                                routeA.addSegmentAsLast(segment);
                                routeA.mergeRoute(routeB);
                                saved = routeA;
                                merged = routeB;
                                break;

                            } else if (routeA.isCustomerLast(dst) && routeB.isCustomerLast(src)) { // (8) .....d   .....s
                                routeA.rotate();
                                routeB.addSegmentAsLast(segment);
                                routeB.mergeRoute(routeA);
                                saved = routeB;
                                merged = routeA;
                                break;
                            }
                        }
                    }
                }
            }
            if (merged != null) {
                logger.debug("Route \"" + merged.getId() + "\" was merged into route \"" + saved.getId() + "\"");
                logger.debug("Route \"" + saved.getId() + "\" includes now the following customers: ");
                saved.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                logger.debug(", current packages weight is " + saved.getCurrentPackagesWeight() + "kg, current packages size is " + saved.getCurrentPackagesSize() + "m3.");

                logger.debug("Removing route \"" + merged.getId() + "\" from solution because of merge.");
                routes.remove(merged);
            }
        }

        addDepotNodeAsFirstAndLast();
        logger.info("Calculating the solution has been completed.");
    }

    private boolean isCustomerInRoute(Customer customer) {
        if (customer.equals(getProblem().getDepot())) {
            return false;
        }
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
            for (RouteSegment rs : routeSegments) {
                if (rs.getSrc().getId() == 0 && rs.getDst().getId() == firstCustomerID) {
                    route.addCustomerAsFirst(depot);
                    route.addSegmentAsFirst(rs);
                    break;
                }
            }
            for (RouteSegment rs : routeSegments) {
                if (rs.getSrc().getId() == 0 && rs.getDst().getId() == lastCustomerID) {
                    route.addCustomerAsLast(depot);
                    if (route.getRouteSegments().size() > 1) {
                        rs.swapSrcDst();
                        route.addSegmentAsLast(rs);
                    } else {
                        route.addSegmentAsLast(new RouteSegment(rs.getDst(), rs.getSrc(), rs.getDistance(), rs.getDuration(), rs.getGeometry()));
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void saveSolution() {
        logger.info("Saving solution...");
        logger.info(routes.size() + " routes have been found");
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : routes) {
            route.setArrivalAndDepartureTimeForCustomers();
            totalDistance += route.getTotalDistance();
            totalDuration = totalDuration.plus(route.getTotalDuration());
            logger.info(route.toString());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < route.getCustomersInRoute().size(); i++) {
                Customer c = route.getCustomersInRoute().get(i);
                sb.append(c.getId());
                if (i != route.getCustomersInRoute().size() - 1) {
                    sb.append("->");
                }
            }
            logger.info(sb.toString());
        }
        logger.info("Total distance cost: " + totalDistance + "km. Total duration cost: " + totalDuration.toHours() + ":" + totalDuration.toMinutes() % 60 + "h");
        super.getSolution().setTotalDistanceCost(totalDistance);
        super.getSolution().setTotalDurationCost(totalDuration);
        Storage.getSolutionsList().add(super.getSolution());
        logger.info("Saving solution has been completed.");
    }
}
