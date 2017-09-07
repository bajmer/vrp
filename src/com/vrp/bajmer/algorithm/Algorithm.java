package com.vrp.bajmer.algorithm;

import com.vrp.bajmer.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marcin on 2017-06-26.
 */
public abstract class Algorithm {

    private String algorithmName;
    private Problem problem;
    private Solution solution;

    private List<Customer> customers;
    private List<RouteSegment> routeSegments;
    private List<Route> routes;

    public Algorithm(Problem problem, String name) {
        this.problem = problem;
        this.algorithmName = name;
        this.solution = new Solution(problem.getProblemID(), name, problem.getDepot());

        this.customers = Database.getCustomerList();
        this.routeSegments = new ArrayList<>(Database.getRouteSegmentsList().size());
        this.routeSegments.addAll(Database.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));

        this.routes = this.solution.getListOfRoutes();
    }

    protected String getAlgorithmName() {
        return algorithmName;
    }

    protected void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    protected Problem getProblem() {
        return problem;
    }

    protected void setProblem(Problem problem) {
        this.problem = problem;
    }

    protected Solution getSolution() {
        return solution;
    }

    protected void setSolution(Solution solution) {
        this.solution = solution;
    }

    protected List<Customer> getCustomers() {
        return customers;
    }

    protected void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    protected List<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    protected void setRouteSegments(List<RouteSegment> routeSegments) {
        this.routeSegments = routeSegments;
    }

    protected List<Route> getRoutes() {
        return routes;
    }

    protected void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public abstract void runAlgorithm();

    protected abstract void saveSolution();
}
