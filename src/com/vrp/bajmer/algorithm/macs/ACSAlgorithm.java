package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marcin on 2017-06-26.
 */
public class ACSAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(ACSAlgorithm.class);

    private static final String ACS = "Ant Colony System";
    private static final double INITIAL_PHEROMONE_LEVEL = 0.0002;
    private double ro; //parametr określający ilość wyparowanego feromonu, zakres <0-1>, preferowana wartość to 0.5
    private int m; //ilość mrówek, preferowana wartość = n (ilość miast)
    private List<Solution> antsSolutions;
    private List<RouteSegment> acsRouteSegments;
    private Solution tmpBestAcsSolution;
    private List<Integer> routeSegmentsToUpdatePheromoneLocally;

    public ACSAlgorithm(Problem problem, int m, double q0, double beta, double ro) {
        super(problem, "Ant Colony System");
        this.ro = ro;
        this.m = m;
        this.antsSolutions = new ArrayList<>();
        this.routeSegmentsToUpdatePheromoneLocally = new ArrayList<>();
        this.tmpBestAcsSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());
        Ant.setQ0(q0);
        Ant.setBeta(beta);

//        utworzenie tablicy odcinków trasy, zawierającej również obrócone odcinki
        List<RouteSegment> notSwappedSegments = new ArrayList<>(super.getRouteSegments().size());
        notSwappedSegments.addAll(Storage.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        List<RouteSegment> swappedSegments = new ArrayList<>(super.getRouteSegments().size());
        swappedSegments.addAll(Storage.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        for (RouteSegment rs : swappedSegments) {
            rs.swapSrcDst();
        }
//        this.acsRouteSegments = new List<>(super.getRouteSegments().size() * 2);
        this.acsRouteSegments = new LinkedList<>();
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
        for (RouteSegment rs : acsRouteSegments) {
            rs.setAcsPheromoneLevel(INITIAL_PHEROMONE_LEVEL); //ustawienie początkowej ilości feromonu dla każdego odcinka trasy
        }

        int iteration = 1;
        while (iteration <= 10000) {
            logger.warn("***************ITERACJA " + iteration + "********************************************************************************************");
            antsSolutions.clear();
            for (int k = 0; k < m; k++) {
                Solution antSolution = constructNewAntSolution(new Ant(super.getCustomers())); //wyznaczenie rozwiąznia przez każdą mrówkę
                saveAntSolution(antSolution);
                localPheromoneUpdate(); //lokalne aktualizowanie feromonu
            }

            for (Solution s : antsSolutions) {
                double distanceCost = s.getTotalDistanceCost();
                if (distanceCost < tmpBestAcsSolution.getTotalDistanceCost()) {
                    tmpBestAcsSolution = s; //gdy koszt rozwiązania jest mniejszy niż w najlepszym rozwiązaniu
                }
            }

            logger.warn("------------------------Najlepsze rozwiązanie po iteracji " + iteration + ": " + tmpBestAcsSolution.toString() + "-------------------------------");

            for (Route r : tmpBestAcsSolution.getListOfRoutes()) {
                for (RouteSegment rs : r.getRouteSegments()) {
                    rs.setAcsPartOfBestSolution(true);
                }
            }

            globalPheromoneUpdate(tmpBestAcsSolution); //globalne aktualizowanie feromonu
            iteration++;
        }
        Storage.getSolutionsList().add(tmpBestAcsSolution);
    }

    private Solution constructNewAntSolution(Ant ant) {
        routeSegmentsToUpdatePheromoneLocally.clear();
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();
        Solution antSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());

        Route route = new Route();
        route.addCustomerAsLast(super.getProblem().getDepot());
        int tmpNodeId = route.getLastCustomerId(); //umieszczenie mrówki w magazynie

        while (true) {
            ant.removeFromUnvisitedCustomers(tmpNodeId); //usunięcie bieżącego klienta z listy nieodwiedzonych klientów
            if (ant.updateFeasibleCustomers(tmpNodeId, acsRouteSegments, route, weightLimit, sizeLimit)) { //jeżeli lista dostępnych klientów nie jest pusta
                int nextNodeId = ant.chooseNextNode(tmpNodeId, acsRouteSegments); //wybór kolejnego klienta
                route.addCustomerAsLast(super.getCustomers().get(nextNodeId));
//                for (Customer c : super.getCustomers()) {
//                    if (c.getId() == nextNodeId) {
//                        route.addCustomerAsLast(c); //dodanie kolejnego klienta do budowanej trasy
//                        break;
//                    }
//                }

                for (RouteSegment rs : acsRouteSegments) {
                    if (rs.isSegmentExist(tmpNodeId, nextNodeId)) {
                        route.addSegmentAsLast(rs); //dodanie kolejnego odcinka do budowanej trasy
                        if (tmpNodeId != 0) {
                            routeSegmentsToUpdatePheromoneLocally.add(acsRouteSegments.indexOf(rs));
                        }
                        break;
                    }
                }
                tmpNodeId = nextNodeId;

            } else { //jeżeli lista dostępnych klientów jest pusta
                ant.removeFromUnvisitedCustomers(tmpNodeId); //usunięcie ostatniego klienta z listy nieodwiedzonych klientów
                route.addCustomerAsLast(super.getProblem().getDepot()); //dodanie magazynu do listy klienów trasy
                for (RouteSegment rs : acsRouteSegments) {
                    if (rs.isSegmentExist(tmpNodeId, super.getProblem().getDepot().getId())) {
                        route.addSegmentAsLast(rs); //dodanie odcinka ostatni klient-magazyn do trasy
                        break;
                    }
                }
                antSolution.getListOfRoutes().add(route); //dodanie trasy 0-..-i-..-0 do listy tras

                if (ant.getUnvisitedCustomers().size() == 0) {
                    break; //jeżeli nie ma więcej nieodwiedzonych klientów przerywamy pętlę (dodać warunek na nieodwiedzonych niedostępnych klientów!!)
                } else {
                    route = new Route();
                    route.addCustomerAsLast(super.getProblem().getDepot());
                    tmpNodeId = route.getLastCustomerId();
                }
            }
        }
        return antSolution;
    }

    private void localPheromoneUpdate() {
        for (int rsIndex : routeSegmentsToUpdatePheromoneLocally) {
            RouteSegment partOfAntSolution = acsRouteSegments.get(rsIndex);
            double tau = partOfAntSolution.getAcsPheromoneLevel(); //poziom feromonu
            tau = (1 - ro) * tau + ro * INITIAL_PHEROMONE_LEVEL;
            partOfAntSolution.setAcsPheromoneLevel(tau);
//            logger.warn("Aktualizowanie feromonu lokalnie: " + partOfAntSolution.getSrc().getId() + "->" + partOfAntSolution.getDst().getId()
//                    + " - poziom feromonu: " + partOfAntSolution.getAcsPheromoneLevel());
        }
    }

    private void globalPheromoneUpdate(Solution bestSolution) {
        for (RouteSegment rs : acsRouteSegments) {
            double tau = rs.getAcsPheromoneLevel();
            if (rs.isAcsPartOfBestSolution()) {
                tau = (1 - ro) * tau + 1 / bestSolution.getTotalDistanceCost();
                rs.setAcsPartOfBestSolution(false);
            } else {
                tau = (1 - ro) * tau;
            }
            rs.setAcsPheromoneLevel(tau);
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
