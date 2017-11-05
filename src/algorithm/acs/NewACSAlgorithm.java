package algorithm.acs;

import algorithm.Algorithm;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa implementujaca algorytm mrowkowy
 */
public class NewACSAlgorithm extends Algorithm {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(NewACSAlgorithm.class);

    /**
     * Nazwa algorytmu
     */
    private static final String ACS = "Ant Colony System";

    /**
     * Poczatkowa wartosc feromonu
     */
    private static double initial_pheromone_level;

    /**
     * Ilosc iteracji algorytmu
     */
    private final int i;

    /**
     * ilosc mrowek
     */
    private final int m; //ilość mrówek, preferowana wartość = n (ilość miast)

    /**
     * parametr okreslajacy ilosc parujacego feromonu w zakresie 0-1
     */
    private final double ro; //parametr określający ilość wyparowanego feromonu, zakres <0-1>, preferowana wartość to 0.5

    /**
     * Najlepsze dotychczasowe rozwiazanie
     */
    private Solution bestAcsSolution;

    private Map<Integer, Double> globalPheromoneLevel;

    private List<NewAnt> ants;

    private List<Integer> segmentsInBestAcsSolution;

    private boolean nearestNeighbourSearch;

    /**
     * Tworzy algorytm mrowkowy na podstawie parametrow ustawionych przez uzytkownika
     *
     * @param problem Obiekt problemu
     * @param i       Ilosc iteracji algorytmu
     * @param m       Ilosc mrowek
     * @param q0      Parametr okreslajacy proporcje między eksploatacja najlepszej krawedzi i eksploracja nowej
     * @param beta    Parametr regulujacy wpływ ni (odwrotnosc odległosci)
     * @param ro      Parametr określający ilość wyparowanego feromonu
     */
    public NewACSAlgorithm(Problem problem, int i, int m, double q0, double beta, double ro) {
        super(problem, "Ant Colony System");
        this.i = i;
        this.m = m;
        this.ro = ro;
        this.bestAcsSolution = null;
        NewAnt.setQ0(q0);
        NewAnt.setBeta(beta);

        this.globalPheromoneLevel = new HashMap<>();
        this.ants = new ArrayList<>();
        this.segmentsInBestAcsSolution = new ArrayList<>();
    }

    /**
     * Uruchamia dzialanie algorytmu
     */
    @Override
    public void runAlgorithm() {
        logger.info("Running the Ant Colony System algorithm...");
        preInitialize();
        runNearestNeighbour();
        initializeACS();
        processACS();
        saveSolution();
    }

    private void preInitialize() {
        for (RouteSegment rs : super.getRouteSegments()) {
            for (Customer c : super.getCustomers()) {
                if (c.equals(rs.getSrc()) && c.getRouteSegmentsFromCustomer().size() < super.getCustomers().size() - 1) {
                    c.getRouteSegmentsFromCustomer().add(rs); //przypisanie każdemu klientowi listy wychodzących z niego odcinków trasy
                    break;
                }
            }
        }
    }

    private void runNearestNeighbour() {
        nearestNeighbourSearch = true;
        NewAnt nearestNeighbourAnt = new NewAnt();
        resetAnt(nearestNeighbourAnt);
        Solution nearestNeighbourSolution = findAntSolution(nearestNeighbourAnt, null);
        saveAntSolution(nearestNeighbourSolution);
        initial_pheromone_level = 1 / nearestNeighbourSolution.getTotalDistanceCost();
    }

    //    Inicjalizacja
    private void initializeACS() {
        for (RouteSegment rs : super.getRouteSegments()) {
            globalPheromoneLevel.put(rs.getId(), initial_pheromone_level); //zainicjowanie feromonu na każdym odcinku
        }

        for (int i = 0; i < m; i++) {
            ants.add(new NewAnt()); //utworzenie m mrówek
        }
    }

    //    Pętla główna ACS
    private void processACS() {
        nearestNeighbourSearch = false;
        int bestIteration = 1;
        int iteration = 1;
        while (i > 0 ? iteration <= i : iteration - bestIteration < 100) {
//            Map<Integer, Double> localPheromoneLevel = new HashMap<>(globalPheromoneLevel); //feromon lokalny
//            Map<Integer, Double> localPheromoneLevel = globalPheromoneLevel; //feromon lokalny

            for (NewAnt ant : ants) {
                resetAnt(ant);
                Solution antSolution = findAntSolution(ant, globalPheromoneLevel);
                saveAntSolution(antSolution);
                localPheromoneUpdate(antSolution, globalPheromoneLevel); //lokalna aktualizacja feromonu

                if (bestAcsSolution == null || antSolution.getTotalDistanceCost() < bestAcsSolution.getTotalDistanceCost()) {
                    bestAcsSolution = antSolution;
                    segmentsInBestAcsSolution.clear();
                    for (Route r : bestAcsSolution.getListOfRoutes()) {
                        for (RouteSegment rs : r.getRouteSegments()) {
                            segmentsInBestAcsSolution.add(rs.getId());
                        }
                    }
                    bestIteration = iteration;
                }
            }
            globalPheromoneUpdate(); //globalna aktualizacja feromonu
            iteration++;
        }
    }

    private void resetAnt(NewAnt ant) {
        ant.getExploitationRates().clear();
        ant.getExplorationProbabilities().clear();
        ant.getFeasibleRouteSegments().clear();
        ant.getUnvisitedCustomersID().clear();

        for (Customer c : super.getCustomers()) {
            if (c.getId() != 0) {
                ant.getUnvisitedCustomersID().add(c.getId()); //utworzenie listy nieodwiedzonych klientów (bez magazynu)
            }
        }
    }

    private Solution findAntSolution(NewAnt ant, Map<Integer, Double> localPhermoneLevel) {
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();
        Solution antSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot(), super.getProblem().isTest());

        while (ant.getUnvisitedCustomersID().size() > 0) {
            Route tmpRoute = initializeNewRoute();
            Customer tmpCustomer = tmpRoute.getLastCustomer();
            ant.updateFeasibleRouteSegments(tmpCustomer, tmpRoute, weightLimit, sizeLimit);
            while (ant.getFeasibleRouteSegments().size() > 0) {
                RouteSegment nextRouteSegment = ant.chooseNextRouteSegment(tmpCustomer, localPhermoneLevel, nearestNeighbourSearch);
                Customer nextCustomer = nextRouteSegment.getDst();

                tmpRoute.addCustomerAsLast(nextCustomer); //dodanie wybranego klienta do budowanej trasy
                tmpRoute.addSegmentAsLast(nextRouteSegment); //dodanie następnego odcinka do trasy

                ant.removeFromUnvisitedCustomers(nextCustomer.getId());
                ant.updateFeasibleRouteSegments(nextCustomer, tmpRoute, weightLimit, sizeLimit);
                tmpCustomer = nextCustomer;

//                if (tmpRoute.getCurrentPackagesWeight() > weightLimit * 0.7) {
//                    if (new Random().nextBoolean()) {
//                        break;
//                    }
//                }
            }

            if (tmpRoute.getTotalDistance() != 0) {
                endRoute(tmpCustomer, tmpRoute); //powrót do magazynu
                antSolution.getListOfRoutes().add(tmpRoute); //dodanie trasy do listy tras
            } else {
                break;
            }
        }

        return antSolution;
    }

    private Route initializeNewRoute() {
        Route route = new Route();
        route.addCustomerAsLast(super.getProblem().getDepot());
        return route; //umieszczenie mrówki w magazynie
    }

    private void endRoute(Customer tmpCustomer, Route tmpRoute) {
        Customer depot = super.getProblem().getDepot();
        tmpRoute.addCustomerAsLast(depot); //dodanie magazynu do listy klienów trasy
        for (RouteSegment rs : tmpCustomer.getRouteSegmentsFromCustomer()) {
            if (rs.getDst().equals(depot)) {
                tmpRoute.addSegmentAsLast(rs); //dodanie odcinka ostatni klient-magazyn do trasy
                break;
            }
        }
    }

    private void localPheromoneUpdate(Solution solution, Map<Integer, Double> localPheromoneLevel) {
        for (Route r : solution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                double tau = localPheromoneLevel.get(rs.getId());
                tau = (1 - ro) * tau + (ro * initial_pheromone_level);
                localPheromoneLevel.put(rs.getId(), tau);
            }
        }
    }

    private void globalPheromoneUpdate() {
        for (Map.Entry<Integer, Double> segment : globalPheromoneLevel.entrySet()) {
            double tau = segment.getValue();
            tau = (1 - ro) * tau;

            if (segmentsInBestAcsSolution.contains(segment.getKey())) {
                tau = tau + (1 / bestAcsSolution.getTotalDistanceCost());
            }

            if (tau < initial_pheromone_level) {
                tau = initial_pheromone_level;
            }
            globalPheromoneLevel.put(segment.getKey(), tau);
        }
    }

    /**
     * Zapisuje rozwiazanie uzyskane przez mrowke, oblicza calkowita dlugosc i czas rozwiazania oblicza czasy przyjazdu i odjazdu do kazdego klienta
     *
     * @param solution Znalezione rozwiazanie
     */
    private void saveAntSolution(Solution solution) {
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : solution.getListOfRoutes()) {
            route.setArrivalAndDepartureTimeForCustomers(solution.isTest());
            totalDistance += route.getTotalDistance();
            totalDuration = totalDuration.plus(route.getTotalDuration());
        }
        solution.setTotalDistanceCost(totalDistance);
        solution.setTotalDurationCost(totalDuration);
    }

    /**
     * Zapisuje najlepsze rozwiazanie znalezione przez algorytm mrowkowy
     */
    @Override
    protected void saveSolution() {
        logger.info("Saving solution...");
        for (Route route : bestAcsSolution.getListOfRoutes()) {
            logger.info(route.toString());
        }
        Database.getSolutionsList().add(bestAcsSolution);
        logger.info(bestAcsSolution.toString());
        logger.info("Saving solution has been completed.");
    }

}