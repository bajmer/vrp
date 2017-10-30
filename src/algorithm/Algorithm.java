package algorithm;

import core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa abstrakcyjna algorytmu
 */
public abstract class Algorithm {

    /**
     * Nazwa algorytmu
     */
    private String algorithmName;

    /**
     * Instacja problemu
     */
    private Problem problem;

    /**
     * Rozwiazanie
     */
    private Solution solution;

    /**
     * Lista klientow
     */
    private List<Customer> customers;

    /**
     * Lista odcinkow trasy
     */
    private List<RouteSegment> routeSegments;

    /**
     * Lista tras
     */
    private List<Route> routes;

    /**
     * Ustawia wartosc pol algorytmu
     * @param problem Obiekt problemu
     * @param name Nazwa algorytmu
     */
    protected Algorithm(Problem problem, String name) {
        this.problem = problem;
        this.algorithmName = name;
        this.solution = new Solution(problem.getProblemID(), name, problem.getDepot(), problem.isTest());

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

    /**
     * Metoda abstrakcyjna uruchamiajaca działanie algorytmu
     */
    public abstract void runAlgorithm();

    /**
     * Metoda abstrakcyjna zapisujaca uzyskane rozwiazanie
     */
    protected abstract void saveSolution();
}
