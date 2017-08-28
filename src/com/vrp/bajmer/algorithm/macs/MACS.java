package com.vrp.bajmer.algorithm.macs;

import com.vrp.bajmer.algorithm.Algorithm;
import com.vrp.bajmer.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2017-06-26.
 */
public class MACS extends Algorithm {

    private static final Logger logger = LogManager.getLogger(MACS.class);

    private final String name = "Multiple Ant Colony System";
    private List<Customer> extendedCustomerList = new ArrayList<>();
    private List<RouteSegment> extendedRouteSegmentsList = new ArrayList<>();
    private List<Customer> customers;
    private List<RouteSegment> routeSegments;
    private List<Route> routes;

    private double a; //param1
    private double b; //param2
    private double c; //param3
    private List<Ant> ants;
    private List<Solution> acsTimeSolutions;
    private List<Solution> acsVeiSolutions;
    private Solution bestMACSSolution;

    public MACS(Problem problem, int numberOfAnts, double a, double b, double c) {
        super(problem);
        super.setAlgorithmName(name);
        super.setSolution(new Solution(problem.getProblemID(), name, problem.getDepot()));
        customers = Storage.getCustomerList();
        routeSegments = new ArrayList<>(Storage.getRouteSegmentsList().size());
        for (RouteSegment routeSegment : Storage.getRouteSegmentsList()) {
            routeSegments.add(routeSegment.clone());
        }
        routes = super.getSolution().getListOfRoutes();

        ants = new ArrayList<>(numberOfAnts);
        acsTimeSolutions = new ArrayList<>();
        acsVeiSolutions = new ArrayList<>();

        this.a = a;
        this.b = b;
        this.c = c;

//        utworzenie danej liczby mrówek
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant());
        }
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Second com.vrp.bajmer.algorithm...");
        MACS_Procedure();
        saveSolution();
    }

    private void MACS_Procedure() {
        Solution s;
//        Initialization
        initializeMACS();

        int v; //number of active_vehicles
//        Main loop (repeat until stop criterion)
        for (int i = 0; i < 1000; i++) {
//            set active_vehicles
            v = 100;
//            start ACS_TIME and ACS_VEI
//            ACS_VEI_Procedure(v - 1);
            ACS_TIME_Procedure(v);
            while (/*ACS_Procedures are running*/) {
//                wait an improved solution s_imp from ACS_VEI or ACS_TIME
//                listener???
//                update solution
                s = /* s_imp */;
                if (/*active_vehicles (s_imp) < v*/) {
//                    stop ACS_Procedures
                }
            }
        }
    }

    private void initializeMACS() {

    }

    private void ACS_TIME_Procedure(int v) {
//        initialize pheromone and data structure using v
        acsTimeSolutions.clear();

//        main loop
        while (/*warunek stopu*/) {
//            construct solution for each ant
            for (Ant ant : ants) {
                newActiveAnt(ant);
            }

//        update the best solution if it is improved
            for (Solution s : acsTimeSolutions) {
//            if solution is feasible and duration cost is less than the tmp best solution cost
                if (s.isFeasible() &&
                        s.getTotalDurationCost().compareTo(bestMACSSolution.getTotalDurationCost()) < 0) {
//              send s to MACS
//              generate event???
                }
            }
//        perform global updating according to Equation 2
            globalPheromoneUpdate();
        }
    }

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
////              send s to MACS
////              generate event???
//                }
//            }
////        perform global updating according to Equation 2
//        }
//    }

    private void newActiveAnt(Ant ant) {
//        put ant in randomly selected duplicated depot
        Route route = new Route();
        route.addCustomerAsFirst(/*depot*/);
//        set current time = 0, load = 0

//        when feasible nodes are avaible
        while (ant.getFeasibleNodes().size() != 0) {
//            when ant is in node i compute the set of feasible nodes

//            Choose probabilistically the next node j

//            Update load and time

//            LOCAL pheromone updating (Equation 3)
            localPheromoneUpdate();
//            Add j to path, i <- j
        }

        
    }

    private Node

    private void localPheromoneUpdate() {

    }

    private void globalPheromoneUpdate() {

    }

    @Override
    public void saveSolution() {

    }
}
