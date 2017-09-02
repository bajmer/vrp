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
public class MACSAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(MACSAlgorithm.class);
    private static final String ACS_TIME = "ACS_TIME";
    private static final String ACS_VEI = "ACS_VEI";
    private static final String MACS = "Multiple Ant Colony System";
    private static final double INITIAL_PHEROMONE_LEVEL = 0.1;
    //    private double alfa; //parametr regulujący wpływ tau (ilości feromonu), preferowana wartość to "1"
//    private double beta; //parametr regulujący wpływ ni (odwrotność odległości), preferowana wartość to 2-5
    private double gamma; //parametr określający ilość wyparowanego feromonu, zakres <0-1>, preferowana wartość to 0.5
    private int numberOfAnts; //ilość mrówek, preferowana wartość = n (ilość miast)
    private List<Ant> ants;
    private List<Solution> acsTimeSolutions;
    private List<Solution> acsVeiSolutions;
    private List<RouteSegment> acsTimeRouteSegments;
    private List<RouteSegment> acsVeiRouteSegments;
    private Solution bestMACSSolution;
    private Solution bestAcsTimeSolution;
    private Solution bestAcsVeiSolution;
    private int v; //liczba pojazdów użytych w najlepszym rozwiązaniu

    public MACSAlgorithm(Problem problem, int numberOfAnts, double alfa, double beta, double gamma) {
        super(problem, "Multiple Ant Colony System");
        this.gamma = gamma;
        this.numberOfAnts = numberOfAnts;

        this.ants = new ArrayList<>(numberOfAnts);
        this.acsTimeSolutions = new ArrayList<>();
        this.acsVeiSolutions = new ArrayList<>();
        this.bestAcsTimeSolution = new Solution(super.getProblem().getProblemID(), MACS, super.getProblem().getDepot());
        this.bestAcsVeiSolution = new Solution(super.getProblem().getProblemID(), MACS, super.getProblem().getDepot());
        Ant.setAlfa(alfa);
        Ant.setBeta(beta);

//        utworzenie danej liczby mrówek
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant());
        }

//        utworzenie tablicy segmentów trasy, zawierającej również obrócone segmenty
        List<RouteSegment> notSwappedSegments = new ArrayList<>(super.getRouteSegments().size());
        notSwappedSegments.addAll(Storage.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        List<RouteSegment> swappedSegments = new ArrayList<>(super.getRouteSegments().size());
        swappedSegments.addAll(Storage.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        for (RouteSegment rs : swappedSegments) {
            rs.swapSrcDst();
        }
        this.acsTimeRouteSegments = new ArrayList<>(super.getRouteSegments().size() * 2);
        acsTimeRouteSegments.addAll(notSwappedSegments);
        acsTimeRouteSegments.addAll(swappedSegments);
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Multiple Ant Colony System algorithm...");
//        MACS_Procedure();
//        constructNewSolution(new Ant(super.getCustomers()), ACS_TIME);
        ACS_TIME_Procedure(0);
//        saveSolution();
    }

//    private void MACS_Procedure() {
//        Solution s;
////        Initialization
//        initializeMACS();
//
//        int v; //number of active_vehicles
////        Main loop (repeat until stop criterion)
//        for (int i = 0; i < 1000; i++) {
////            set active_vehicles
//            v = 100;
////            start ACS_TIME and ACS_VEI
////            ACS_VEI_Procedure(v - 1);
//            ACS_TIME_Procedure(v);
//            while (/*ACS_Procedures are running*/) {
////                wait an improved solution s_imp from ACS_VEI or ACS_TIME
////                listener???
////                update solution
//                s = /* s_imp */;
//                if (/*active_vehicles (s_imp) < v*/) {
////                    stop ACS_Procedures
//                }
//            }
//        }
//    }

//    private void initializeMACS() {
//
//    }

    private void ACS_TIME_Procedure(int v) {
//        ustawienie początkowej ilości feromonu dla każdego segmentu
        for (RouteSegment rs : acsTimeRouteSegments) {
            rs.setMacsPheromoneLevel(INITIAL_PHEROMONE_LEVEL);
        }

        int i = 0;
        while (i < 10) {
            acsTimeSolutions.clear();
//            wyznaczenie rozwiąznia przez każdą mrówkę
            for (Ant ant : ants) {
                constructNewSolution(ant, ACS_TIME, acsTimeRouteSegments);
            }

//            find the best ant solution
            for (Solution s : acsTimeSolutions) {
//                double distanceCost = s.getTotalDistanceCost();
                Duration durationCost = s.getTotalDurationCost();
//                gdy czas rozwiązania jest mniejszy niż w najlepszym rozwiązaniu
                if (durationCost.compareTo(bestAcsTimeSolution.getTotalDurationCost()) < 0) {
                    bestAcsTimeSolution = s;
                }

//                gdy długość rozwiązania jest mniejsza niż w najlepszym rozwiązaniu
//                if (distanceCost < bestAcsTimeSolution.getTotalDistanceCost()) {
//                    bestAcsTimeSolution = s;
//                }
            }

            for (Route r : bestAcsTimeSolution.getListOfRoutes()) {
                for (RouteSegment rs : r.getRouteSegments()) {
                    rs.setMacsPartOfTheBestSolution(true);
                }
            }

//            update the best solution if it is improved
//            if solution is feasible and duration cost is less than the tmp best solution cost
//            if (bestAcsTimeSolution.getTotalDurationCost().compareTo(bestMACSSolution.getTotalDurationCost()
//                        /*&& bestAcsTimeSolution.isFeasible()*/) < 0) {
//              send s to MACSAlgorithm
//              generate event???
//            }

            logger.info("The best solution in this iteration is: " + bestAcsTimeSolution.toString());
//        perform global updating according to Equation 2
            globalPheromoneUpdate(bestAcsTimeSolution);
            i++;
        }
        Storage.getSolutionsList().add(bestAcsTimeSolution);
    }

//    private void ACS_VEI_Procedure(int v) {
////        initialize pheromone and data structure using v-1
//        acsVeiSolutions.clear();
//
////        main loop
//        while (/*warunek stopu*/) {
////            construct solution for each ant
//            for (Ant ant : ants) {
//                constructNewSolution(ant);
//            }
//
////        update the best solution if it is improved
//            for (Solution s : acsTimeSolutions) {
////            if solution is feasible and duration cost is less than the tmp best solution cost
//                if (s.isFeasible() &&
//                        s.getTotalDurationCost().compareTo(bestMACSSolution.getTotalDurationCost()) < 0) {
////              send s to MACSAlgorithm
////              generate event???
//                }
//            }
////        perform global updating according to Equation 2
//        }
//    }

    private void constructNewSolution(Ant ant, String colony, List<RouteSegment> routeSegments) {
        ant.resetUnvisitedCustomers(super.getCustomers());
        Solution antSolution = new Solution(super.getProblem().getProblemID(), MACS, super.getProblem().getDepot());

//        List<RouteSegment> routeSegments = new ArrayList<>(Storage.getRouteSegmentsList().size());
//        routeSegments.addAll(super.getRouteSegments().stream().map(RouteSegment::clone).collect(Collectors.toList()));

        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();

        //        put ant in randomly selected duplicated depot
        Route route = new Route();
        route.addCustomerAsLast(super.getProblem().getDepot());
        int tmpNodeId = route.getLastCustomerId();

//            when ant is in node i compute the set of feasible nodes
//            when feasible nodes are avaible
        while (true) {
            if (ant.updateFeasibleNodes(tmpNodeId, routeSegments, route, weightLimit, sizeLimit)) {
//                Choose probabilistically the next node j
                int nextNodeId = ant.chooseNextNode(tmpNodeId, routeSegments);
//            Add j to path, i <- j and update parameters
                for (Customer c : super.getCustomers()) {
                    if (c.getId() == nextNodeId) {
                        route.addCustomerAsLast(c);
                        ant.removeFromUnvisitedCustomers(tmpNodeId);
                        break;
                    }
                }
                for (RouteSegment rs : routeSegments) {
                    if (rs.isSegmentExist(tmpNodeId, nextNodeId)) {
                        route.addSegmentAsLast(rs);

//                    LOCAL pheromone updating (Equation 3) on added segment
//                    localPheromoneUpdate(rs);
                        break;
                    }
                }
                tmpNodeId = nextNodeId;
            } else {
                route.addCustomerAsLast(super.getProblem().getDepot());
                for (RouteSegment rs : routeSegments) {
                    if (rs.isSegmentExist(tmpNodeId, super.getProblem().getDepot().getId())) {
                        route.addSegmentAsLast(rs);
//                    LOCAL pheromone updating (Equation 3) on added segment
//                    localPheromoneUpdate(rs);
                        break;
                    }
                }
                logger.info("Znaleziona trasa: " + route.toString());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < route.getCustomersInRoute().size(); i++) {
                    Customer c = route.getCustomersInRoute().get(i);
                    sb.append(c.getId());
                    if (i != route.getCustomersInRoute().size() - 1) {
                        sb.append("->");
                    }
                }
                logger.info(sb.toString());

                ant.removeFromUnvisitedCustomers(tmpNodeId);
//                super.getRoutes().add(route);
                antSolution.getListOfRoutes().add(route);

                if (ant.getUnvisitedCustomers().size() == 0) {
                    break;
                } else {
//                    put ant in randomly selected duplicated depot
                    route = new Route();
                    route.addCustomerAsLast(super.getProblem().getDepot());
                    tmpNodeId = route.getLastCustomerId();
                }
            }
        }
        saveAntSolution(antSolution, colony);
    }

    private void localPheromoneUpdate(RouteSegment routeSegment) {
        for (RouteSegment rs : super.getRouteSegments()) {
            if (rs.equals(routeSegment)) {
//                update pheromone on segment
                double tau = rs.getMacsPheromoneLevel(); //pheromone level
                /*.....*/
                rs.setMacsPheromoneLevel(tau);
                return;
            }
        }
    }

    private void globalPheromoneUpdate(Solution bestSolution) {
//        List<RouteSegment> routeSegments;
//        if (colony.equals(ACS_TIME)) {
//            routeSegments = acsTimeRouteSegments;
//        } else {
//            routeSegments = acsVeiRouteSegments;
//        }
        for (RouteSegment rs : acsTimeRouteSegments) {
            double tau = rs.getMacsPheromoneLevel(); //poziom feromonu
            logger.info(rs.getSrc().getId() + "->" + rs.getDst().getId() + ": " + tau);
//            jeśli segment trasy należy do najlepszej trasy
            if (rs.isMacsPartOfTheBestSolution()) {
                tau = (1 - gamma) * tau + gamma / bestSolution.getTotalDistanceCost(); //parowanie feromonu + feromon najlepszego rozwiązania
            } else {
                tau = (1 - gamma) * tau; //zwykłe parowanie feromonu
            }
            rs.setMacsPheromoneLevel(tau);
        }
    }

    private void saveAntSolution(Solution solution, String colony) {
        logger.debug("Saving ant solution...");
        logger.debug(solution.getListOfRoutes().size() + " routes have been found");
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : solution.getListOfRoutes()) {
            route.setArrivalAndDepartureTimeForCustomers();
            totalDistance += route.getTotalDistance();
            totalDuration = totalDuration.plus(route.getTotalDuration());
            logger.debug(route.toString());
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
        solution.setTotalDistanceCost(totalDistance);
        solution.setTotalDurationCost(totalDuration);
        if (colony.equals(ACS_TIME)) {
            acsTimeSolutions.add(solution);
        } else {
            acsVeiSolutions.add(solution);
        }

        logger.info("Saving solution has been completed.");
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