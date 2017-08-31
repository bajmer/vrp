package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2017-06-26.
 */
public class MACSAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(MACSAlgorithm.class);

    //    private double alfa; //parametr regulujący wpływ tau (ilości feromonu), preferowana wartość to "1"
//    private double beta; //parametr regulujący wpływ ni (odwrotność odległości), preferowana wartość to 2-5
    private double gamma; //parametr określający ilość wyparowanego feromonu, zakres <0-1>, preferowana wartość to 0.5
    private int numberOfAnts; //ilość mrówek, preferowana wartość = n (ilość miast)
    private List<Ant> ants;
    private List<Solution> acsTimeSolutions;
    private List<Solution> acsVeiSolutions;
    private Solution bestMACSSolution;
    private int v; //liczba pojazdów użytych w najlepszym rozwiązaniu

    public MACSAlgorithm(Problem problem, int numberOfAnts, double alfa, double beta, double gamma) {
        super(problem, "Multiple Ant Colony System");

//        this.alfa = alfa;
//        this.beta = beta;
        this.gamma = gamma;
        this.numberOfAnts = numberOfAnts;

        ants = new ArrayList<>(numberOfAnts);
        acsTimeSolutions = new ArrayList<>();
        acsVeiSolutions = new ArrayList<>();

        Ant.setAlfa(alfa);
        Ant.setBeta(beta);

//        utworzenie danej liczby mrówek
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant(super.getCustomers()));
        }
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Multiple Ant Colony System algorithm...");
//        MACS_Procedure();
        newActiveAnt(new Ant(super.getCustomers()));
        saveSolution();
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
//
//    private void ACS_TIME_Procedure(int v) {
////        initialize pheromone and data structure using v
//        acsTimeSolutions.clear();
//
////        main loop
//        while (/*warunek stopu*/) {
////            construct solution for each ant
//            for (Ant ant : ants) {
//                newActiveAnt(ant);
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
//            globalPheromoneUpdate();
//        }
//    }

//    private void ACS_VEI_Procedure(int v) {
////        initialize pheromone and data structure using v-1
//        acsVeiSolutions.clear();
//
////        main loop
//        while (/*warunek stopu*/) {
////            construct solution for each ant
//            for (Ant ant : ants) {
//                newActiveAnt(ant);
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

    private void newActiveAnt(Ant ant) {
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();


        for (int j = 0; j < 5; j++) {
            //        put ant in randomly selected duplicated depot
            Route route = new Route();
            route.addCustomerAsLast(super.getProblem().getDepot());
            int tmpNodeId = route.getLastCustomerId();

//            when ant is in node i compute the set of feasible nodes
//            when feasible nodes are avaible
            while (ant.updateFeasibleNodes(tmpNodeId, super.getRouteSegments(), route, weightLimit, sizeLimit)) {
//            Choose probabilistically the next node j
                int nextNodeId = ant.chooseNextNode(tmpNodeId, super.getRouteSegments());
//            Add j to path, i <- j and update parameters
                for (Customer c : super.getCustomers()) {
                    if (c.getId() == nextNodeId) {
                        route.addCustomerAsLast(c);
                        ant.removeFromUnvisitedCustomers(tmpNodeId);
                        break;
                    }
                }
                for (RouteSegment rs : super.getRouteSegments()) {
                    if (rs.isSegmentExist(tmpNodeId, nextNodeId)) {
                        route.addSegmentAsLast(rs);
//                    LOCAL pheromone updating (Equation 3) on added segment
//                    localPheromoneUpdate(rs);
                        break;
                    }
                }
                tmpNodeId = nextNodeId;
            }
            ant.removeFromUnvisitedCustomers(tmpNodeId);
            super.getRoutes().add(route);

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

        }

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

    private void globalPheromoneUpdate() {

    }

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
