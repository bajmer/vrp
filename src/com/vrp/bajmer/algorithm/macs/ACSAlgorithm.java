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
    private List<RouteSegment> globalAcsRouteSegments;
    private Solution tmpBestAcsSolution;

    public ACSAlgorithm(Problem problem, int numberOfAnts, double alfa, double beta, double gamma) {
        super(problem, "Ant Colony System");
        this.gamma = gamma;
        this.numberOfAnts = numberOfAnts;
        this.antsSolutions = new ArrayList<>();
        this.tmpBestAcsSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());
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
        this.globalAcsRouteSegments = new ArrayList<>(super.getRouteSegments().size() * 2);
        globalAcsRouteSegments.addAll(notSwappedSegments);
        globalAcsRouteSegments.addAll(swappedSegments);
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Ant Colony System algorithm...");
        ACS_Procedure();
//        saveSolution();
    }

    private void ACS_Procedure() {
//        ustawienie początkowej ilości feromonu dla każdego odcinka trasy
        for (RouteSegment rs : globalAcsRouteSegments) {
            rs.setMacsPheromoneLevel(INITIAL_PHEROMONE_LEVEL);
        }

        int i = 1;
        while (i <= 5) {
            antsSolutions.clear();
            List<RouteSegment> localAcsRouteSegments = new ArrayList<>(globalAcsRouteSegments.size());
            localAcsRouteSegments.addAll(globalAcsRouteSegments.stream().map(RouteSegment::clone).collect(Collectors.toList()));
            for (int k = 0; k < numberOfAnts; k++) {
                Solution antSolution = constructNewAntSolution(new Ant(super.getCustomers()), localAcsRouteSegments); //wyznaczenie rozwiąznia przez każdą mrówkę
                saveAntSolution(antSolution);
                logger.warn("Najlepsze rozwiązanie dla mrówki " + k + ": " + antSolution.toString());
                logger.warn("***************ITERACJA " + i + "********************************************************************************************");
                localPheromoneUpdate(localAcsRouteSegments); //lokalne aktualizowanie feromonu
            }

            for (Solution s : antsSolutions) {
                Duration durationCost = s.getTotalDurationCost();
                if (durationCost.compareTo(tmpBestAcsSolution.getTotalDurationCost()) < 0) {
                    tmpBestAcsSolution = s; //znalezienie najlepszego rozwiązania wśród mrówek
                }
//                double distanceCost = s.getTotalDistanceCost();
//                gdy długość rozwiązania jest mniejsza niż w najlepszym rozwiązaniu
//                if (distanceCost < tmpBestAcsSolution.getTotalDistanceCost()) {
//                    tmpBestAcsSolution = s;
//                }
            }

            logger.warn("Najlepsze rozwiązanie w iteracji " + i + ": " + tmpBestAcsSolution.toString());

            for (Route r : tmpBestAcsSolution.getListOfRoutes()) {
                for (RouteSegment rs : r.getRouteSegments()) {
                    rs.setPartOfTheBestACSSolution(true);
                }
            }

            //logger.info("The best solution in this iteration is: " + tmpBestAcsSolution.toString());
            globalPheromoneUpdate(tmpBestAcsSolution); //globalne aktualizowanie feromonu
            i++;
        }
        Storage.getSolutionsList().add(tmpBestAcsSolution);
    }

    private Solution constructNewAntSolution(Ant ant, List<RouteSegment> routeSegments/*lokalna kopia odcinków trasy*/) {
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
                        rs.setPartOfAntSolution(true);
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

    private void localPheromoneUpdate(List<RouteSegment> localAcsRouteSegments) {
        for (RouteSegment rs : localAcsRouteSegments) {
            double tau = rs.getMacsPheromoneLevel(); //poziom feromonu
            if (rs.isPartOfAntSolution()) {
                tau = (1 - gamma) * tau + gamma * INITIAL_PHEROMONE_LEVEL;
                rs.setMacsPheromoneLevel(tau);
                rs.setPartOfAntSolution(false);
            }
            logger.warn("Aktualizowanie feromonu lokalnie: " + rs.getSrc().getId() + "->" + rs.getDst().getId() + " - poziom feromonu: " + rs.getMacsPheromoneLevel());
        }
    }

    //    DO WERYFIKACJI!!!!
    private void globalPheromoneUpdate(Solution bestSolution) {
        for (Route r : bestSolution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                for (RouteSegment grs : globalAcsRouteSegments) {
                    double tau = grs.getMacsPheromoneLevel(); //poziom feromonu
                    if (grs.getSrc().equals(rs.getSrc()) && grs.getDst().equals(rs.getDst())) {
                        if (rs.isPartOfTheBestACSSolution()) {
                            tau = (1 - gamma) * tau + gamma / bestSolution.getTotalDistanceCost(); //parowanie feromonu + feromon najlepszego rozwiązania
                            //rs.setPartOfTheBestACSSolution(false);
                        } else {
                            tau = (1 - gamma) * tau; //zwykłe parowanie feromonu
                        }
                        logger.warn("Aktualizowanie feromonu GLOBALNIE: " + rs.getSrc().getId() + "->" + rs.getDst().getId() + " - poziom feromonu: " + tau);
                        grs.setMacsPheromoneLevel(tau);
                    }
                }
            }
        }

//        for (RouteSegment rs : globalAcsRouteSegments) {
//            double tau = rs.getMacsPheromoneLevel(); //poziom feromonu
//            logger.info("Global pheromone update: " + rs.getSrc().getId() + "->" + rs.getDst().getId() + ": " + tau);
//
//            if (rs.isPartOfTheBestACSSolution()) { //jeśli odcinek trasy należy do najlepszej trasy
//                tau = (1 - gamma) * tau + gamma / bestSolution.getTotalDistanceCost(); //parowanie feromonu + feromon najlepszego rozwiązania
//            } else {
//                tau = (1 - gamma) * tau; //zwykłe parowanie feromonu
//            }
//            rs.setMacsPheromoneLevel(tau);
//        }
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
