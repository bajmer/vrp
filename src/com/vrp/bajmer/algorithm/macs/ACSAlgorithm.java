package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marcin on 2017-06-26.
 */
public class ACSAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(ACSAlgorithm.class);
    private static final String ACS = "Ant Colony System";
    private static final double INITIAL_PHEROMONE_LEVEL = 0.1;
    private double gamma; //parametr określający ilość wyparowanego feromonu, zakres <0-1>, preferowana wartość to 0.5
    private int numberOfAnts; //ilość mrówek, preferowana wartość = n (ilość miast)
    private List<Solution> antsSolutions;
    private List<RouteSegment> acsRouteSegments;
    private Solution tmpBestSolution;

    public ACSAlgorithm(Problem problem, int numberOfAnts, double alfa, double beta, double gamma) {
        super(problem, "Ant Colony System");
        this.gamma = gamma;
        this.numberOfAnts = numberOfAnts;
        this.antsSolutions = new ArrayList<>();
        this.tmpBestSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());
        Ant.setAlfa(alfa);
        Ant.setBeta(beta);

//        utworzenie tablicy odcinków trasy, zawierającej również obrócone odcinki
        List<RouteSegment> notSwappedSegments = new ArrayList<>(super.getRouteSegments().size());
        notSwappedSegments.addAll(Storage.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        List<RouteSegment> swappedSegments = new ArrayList<>(super.getRouteSegments().size());
        swappedSegments.addAll(Storage.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        for (RouteSegment rs : swappedSegments) {
            rs.swapSrcDst();
        }
        this.acsRouteSegments = new ArrayList<>(super.getRouteSegments().size() * 2);
        acsRouteSegments.addAll(notSwappedSegments);
        acsRouteSegments.addAll(swappedSegments);
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Ant Colony System algorithm...");
        ACS_Procedure();
//        saveSolution();
    }

    private void ACS_Procedure() {
//        ustawienie początkowej ilości feromonu dla każdego odcinka trasy
        for (RouteSegment rs : acsRouteSegments) {
            rs.setMacsPheromoneLevel(INITIAL_PHEROMONE_LEVEL);
        }

        int i = 0;
        while (i < 10) {
            antsSolutions.clear();
            for (int k = 0; k < numberOfAnts; k++) {
                Solution antSolution = constructNewAntSolution(new Ant(super.getCustomers()), acsRouteSegments); //wyznaczenie rozwiąznia przez każdą mrówkę
                saveAntSolution(antSolution);
                localPheromoneUpdate(antSolution); //lokalne aktualizowanie feromonu
            }

            for (Solution s : antsSolutions) {
                Duration durationCost = s.getTotalDurationCost();
                if (durationCost.compareTo(tmpBestSolution.getTotalDurationCost()) < 0) {
                    tmpBestSolution = s; //znalezienie najlepszego rozwiązania wśród mrówek
                }
//                double distanceCost = s.getTotalDistanceCost();
//                gdy długość rozwiązania jest mniejsza niż w najlepszym rozwiązaniu
//                if (distanceCost < tmpBestSolution.getTotalDistanceCost()) {
//                    tmpBestSolution = s;
//                }
            }

            for (Route r : tmpBestSolution.getListOfRoutes()) {
                for (RouteSegment rs : r.getRouteSegments()) {
                    rs.setMacsPartOfTheBestSolution(true);
                }
            }

            logger.info("The best solution in this iteration is: " + tmpBestSolution.toString());
            globalPheromoneUpdate(tmpBestSolution); //globalne aktualizowanie feromonu
            i++;
        }
        Storage.getSolutionsList().add(tmpBestSolution);
    }

    private Solution constructNewAntSolution(Ant ant, List<RouteSegment> routeSegments) {
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();
        Solution antSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());
        Route route = new Route();
        route.addCustomerAsLast(super.getProblem().getDepot());
        int tmpNodeId = route.getLastCustomerId(); //umieszczenie mrówki w magazynie

//            when ant is in node i compute the set of feasible nodes and feasible nodes are avaible
        while (true) {
            if (ant.updateFeasibleCustomers(tmpNodeId, routeSegments, route, weightLimit, sizeLimit)) {
                int nextNodeId = ant.chooseNextNode(tmpNodeId, routeSegments); //wybór kolejnego klienta
                for (Customer c : super.getCustomers()) {
                    if (c.getId() == nextNodeId) {
                        route.addCustomerAsLast(c); //dodanie klienta do budowanej trasy
                        ant.removeFromUnvisitedCustomers(tmpNodeId); //usunięcie klienta z listy nieodwiedzonych klientów
                        break;
                    }
                }
                for (RouteSegment rs : routeSegments) {
                    if (rs.isSegmentExist(tmpNodeId, nextNodeId)) {
                        route.addSegmentAsLast(rs); //dodanie odcinka do budowanej trasy
                        break;
                    }
                }
                tmpNodeId = nextNodeId;
            } else {
                route.addCustomerAsLast(super.getProblem().getDepot());
                for (RouteSegment rs : routeSegments) {
                    if (rs.isSegmentExist(tmpNodeId, super.getProblem().getDepot().getId())) {
                        route.addSegmentAsLast(rs);
                        break;
                    }
                }
                ant.removeFromUnvisitedCustomers(tmpNodeId);
                antSolution.getListOfRoutes().add(route);

                if (ant.getUnvisitedCustomers().size() == 0) {
                    break;
                } else {
                    route = new Route();
                    route.addCustomerAsLast(super.getProblem().getDepot());
                    tmpNodeId = route.getLastCustomerId();
                }
            }
        }
        return antSolution;
    }

    private void localPheromoneUpdate(Solution solution) {
//        for (RouteSegment rs : super.getRouteSegments()) {
//            if (rs.equals(routeSegment)) {
////                update pheromone on segment
//                double tau = rs.getMacsPheromoneLevel(); //pheromone level
//                /*.....*/
//                rs.setMacsPheromoneLevel(tau);
//                return;
//            }
//        }
    }

    private void globalPheromoneUpdate(Solution bestSolution) {
        for (RouteSegment rs : acsRouteSegments) {
            double tau = rs.getMacsPheromoneLevel(); //poziom feromonu
            logger.info(rs.getSrc().getId() + "->" + rs.getDst().getId() + ": " + tau);
//            jeśli odcinek trasy należy do najlepszej trasy
            if (rs.isMacsPartOfTheBestSolution()) {
                tau = (1 - gamma) * tau + gamma / bestSolution.getTotalDistanceCost(); //parowanie feromonu + feromon najlepszego rozwiązania
            } else {
                tau = (1 - gamma) * tau; //zwykłe parowanie feromonu
            }
            rs.setMacsPheromoneLevel(tau);
        }
    }

    private void saveAntSolution(Solution solution) {
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : solution.getListOfRoutes()) {
            route.setArrivalAndDepartureTimeForCustomers();
            totalDistance += route.getTotalDistance();
            totalDuration = totalDuration.plus(route.getTotalDuration());
        }
        solution.setTotalDistanceCost(totalDistance);
        solution.setTotalDurationCost(totalDuration);
        antsSolutions.add(solution);
    }

    //    przenieść do klasy ALgorithm
    @Override
    public void saveSolution() {
        logger.info("Saving solution...");
        logger.info(super.getRoutes().size() + " routes have been found");
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : super.getRoutes()) {
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
