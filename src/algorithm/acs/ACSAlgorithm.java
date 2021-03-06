package algorithm.acs;

import algorithm.Algorithmic;
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
public class ACSAlgorithm implements Algorithmic {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(ACSAlgorithm.class);

    /**
     * Nazwa algorytmu
     */
    private static final String ALGORITHM_NAME = "Ant Colony System";

    /**
     * Poczatkowa wartosc feromonu
     */
    private static double initialPheromoneLevel;

    /**
     * Ilosc iteracji algorytmu
     */
    private final int i;

    /**
     * Liczba mrowek
     */
    private final int m;

    /**
     * Parametr okreslajacy ilosc parujacego feromonu w zakresie 0-1
     */
    private final double ro;

    /**
     * Parametr okreslajacy proporcje miedzy eksploatacja najlepszej krawedzi i eksploracja nowej
     */
    private double q0;

    /**
     * Parametr regulujacy wpływ ni (odwrotnosc odległosci)
     */
    private int beta;

    /**
     * Rozwiazywany problem
     */
    private Problem problem;

    /**
     * Najlepsze dotychczasowe rozwiazanie
     */
    private Solution bestAcsSolution;

    /**
     * Flaga okreslajaca, czy aktywne jest przesuzkiwanie metoda najblizszego sasiada
     */
    private boolean nearestNeighbourSearch;

    /**
     * Mapa okreslajaca ilosc feromonu na odcinkach, kluczem jest ID odcinka, a wartoscia ilosc feromonu
     */
    private Map<Integer, Double> globalPheromoneLevel;

    /**
     * Lista wszystkich mrowek w kolonii
     */
    private List<Ant> ants;

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
    public ACSAlgorithm(Problem problem, int i, int m, double q0, int beta, double ro) {
        this.problem = problem;
        this.i = i;
        this.m = m;
        this.q0 = q0;
        this.beta = beta;
        this.ro = ro;
        this.bestAcsSolution = null;
        Ant.setQ0(q0);
        Ant.setBeta(beta);

        this.globalPheromoneLevel = new HashMap<>();
        this.ants = new ArrayList<>();

        for (RouteSegment rs : Database.getRouteSegmentsList()) {
            for (Customer c : Database.getCustomerList()) {
                if (c.equals(rs.getSrc()) && c.getRouteSegmentsFromCustomer().size() < Database.getCustomerList().size() - 1) {
                    c.getRouteSegmentsFromCustomer().add(rs);
                    break;
                }
            }
        }
    }

    /**
     * Uruchamia dzialanie algorytmu
     */
    @Override
    public void runAlgorithm() {
        logger.info("----------------------------------------------------------------------------------------------------------------");
        logger.info("Running the Ant Colony System algorithm with parameters:" +
                " i=" + i +
                ", m=" + m +
                ", q0=" + q0 +
                ", beta=" + beta +
                ", ro=" + ro + "...");
        runNearestNeighbour();
        initializeACS();
        findBestAcsSolution();
        saveSolution();
    }

    /**
     * Tworzy wstepne rozwiazanie dla jednej mrowki poruszajacej sie zawsze do najblizszego sasiada w celu wyznaczenia poczatkowej wartosci feromonu
     */
    private void runNearestNeighbour() {
        logger.info("Calculating the initial solution with nearest neighbour heuristics...");
        nearestNeighbourSearch = true;
        Ant nearestNeighbourAnt = new Ant();
        resetAnt(nearestNeighbourAnt);
        Solution nearestNeighbourSolution = findAntSolution(nearestNeighbourAnt);
        saveAntSolution(nearestNeighbourSolution);
        initialPheromoneLevel = 1 / nearestNeighbourSolution.getTotalDistanceCost();
        logger.info("Calculating the initial solution with nearest neighbour heuristics has been completed.");
    }

    /**
     * Inicjalizuje algorytm ACS, odklada poczatkowa wartosc feromonu na odcinkach i tworzy kolonie mrowek
     */
    private void initializeACS() {
        for (RouteSegment rs : Database.getRouteSegmentsList()) {
            globalPheromoneLevel.put(rs.getId(), initialPheromoneLevel);
        }

        for (int i = 0; i < m; i++) {
            ants.add(new Ant());
        }
    }

    /**
     * Uruchamia glowna petle algorytmu ACS
     */
    private void findBestAcsSolution() {
        logger.info("Calculating the solution...");
        nearestNeighbourSearch = false;
        int bestIteration = 1;
        int iteration = 1;
        int iterationLeft = 7500;
        while (i > 0 ? iteration <= i : iteration - bestIteration < iterationLeft) {
            for (Ant ant : ants) {
                resetAnt(ant);
                Solution antSolution = findAntSolution(ant);
                saveAntSolution(antSolution);
                localPheromoneUpdate(antSolution);

                if (bestAcsSolution == null || antSolution.getTotalDistanceCost() < bestAcsSolution.getTotalDistanceCost()) {
                    bestAcsSolution = antSolution;
                    bestIteration = iteration;
                    iterationLeft = (15000 - iteration) / 2;
                    logger.info("The best solution was found! Iteration: " + bestIteration + ", distance: " + bestAcsSolution.getTotalDistanceCost() + ". Number of iterations to end: " + iterationLeft);
                }
            }
            globalPheromoneUpdate();
            iteration++;
        }
        logger.info("Calculating the solution has been completed.");
    }

    /**
     * Resetuje parametry mrowki, tworzy od nowa liste nieodwiedzonych klientow
     *
     * @param ant Mrowka, ktora zostanie zresetowana
     */
    private void resetAnt(Ant ant) {
        ant.getExploitationRates().clear();
        ant.getExplorationProbabilities().clear();
        ant.getFeasibleRouteSegments().clear();
        ant.getUnvisitedCustomersID().clear();

        for (Customer c : Database.getCustomerList()) {
            if (c.getId() != 0) {
                ant.getUnvisitedCustomersID().add(c.getId());
            }
        }
    }

    /**
     * Tworzy pojedyncze rozwiazanie za pomoca mrowki
     *
     * @param ant Mrowka szukajaca rozwiazania
     * @return Zwraca jedno rozwiazanie znalezione przez jedna mrowke
     */
    private Solution findAntSolution(Ant ant) {
        double weightLimit = problem.getWeightLimitPerVehicle();
        double sizeLimit = problem.getSizeLimitPerVehicle();
        Solution antSolution = new Solution(problem.getProblemID(), ALGORITHM_NAME, problem.getDepot(), problem.isTest());

        while (ant.getUnvisitedCustomersID().size() > 0) {
            Route tmpRoute = initializeNewRoute();
            Customer tmpCustomer = tmpRoute.getLastCustomer();
            ant.updateFeasibleRouteSegments(tmpRoute, weightLimit, sizeLimit);
            while (ant.getFeasibleRouteSegments().size() > 0) {
                RouteSegment nextRouteSegment = ant.chooseNextRouteSegment(tmpCustomer, globalPheromoneLevel, nearestNeighbourSearch);
                Customer nextCustomer = nextRouteSegment.getDst();

                tmpRoute.addCustomerAsLast(nextCustomer);
                tmpRoute.addSegmentAsLast(nextRouteSegment);

                ant.removeFromUnvisitedCustomers(nextCustomer.getId());
                ant.updateFeasibleRouteSegments(tmpRoute, weightLimit, sizeLimit);
                tmpCustomer = nextCustomer;

//                if (tmpRoute.getCurrentPackagesWeight() > weightLimit * 0.8) {
//                    if (new Random().nextBoolean()) {
//                        break;
//                    }
//                }
            }

            if (tmpRoute.getTotalDistance() != 0) {
                endRoute(tmpRoute);
                antSolution.getListOfRoutes().add(tmpRoute);
            } else {
                break;
            }
        }

        return antSolution;
    }

    /**
     * Tworzy nowa trase i dodaje magazyn na poczatek
     *
     * @return Zwraca utworzona trase
     */
    private Route initializeNewRoute() {
        Route route = new Route();
        route.addCustomerAsLast(problem.getDepot());
        return route;
    }

    /**
     * Konczy biezaca trase dodajac do niej odcinek od biezacego klienta do magazynu
     *
     * @param tmpRoute Biezaca trasa
     */
    private void endRoute(Route tmpRoute) {
        Customer depot = problem.getDepot();
        for (RouteSegment rs : tmpRoute.getLastCustomer().getRouteSegmentsFromCustomer()) {
            if (rs.getDst().equals(depot)) {
                tmpRoute.addSegmentAsLast(rs); //dodanie odcinka ostatni klient-magazyn do trasy
                break;
            }
        }
        tmpRoute.addCustomerAsLast(depot); //dodanie magazynu do listy klienów trasy
    }

    /**
     * Zmniejsza ilosc feromonu na odcinkach nalezacych do trasy znalezionej przez kazda mrowke
     *
     * @param solution Znalezione przez mrowke rozwiazanie
     */
    private void localPheromoneUpdate(Solution solution) {
        for (Route r : solution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                double tau = globalPheromoneLevel.get(rs.getId());
                tau = (1 - ro) * tau + (ro * initialPheromoneLevel);
                globalPheromoneLevel.put(rs.getId(), tau);
            }
        }
    }

    /**
     * Zwieksza na koniec kazdej iteracji ilosc feromonu na odcinkach nalezacych do najlepszego globalnie rozwiazania
     */
    private void globalPheromoneUpdate() {
        for (Route r : bestAcsSolution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                double tau = globalPheromoneLevel.get(rs.getId());
                tau = (1 - ro) * tau + (1 / bestAcsSolution.getTotalDistanceCost());
                globalPheromoneLevel.put(rs.getId(), tau);
            }
        }
    }

    /**
     * Zapisuje rozwiazanie uzyskane przez mrowke, oblicza calkowita dlugosc i czas rozwiazania oblicza czasy przyjazdu i odjazdu do kazdego klienta
     *
     * @param solution Rozwiazanie znalezione przez mrowke
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
    public void saveSolution() {
        logger.info("Saving solution...");
        for (Route route : bestAcsSolution.getListOfRoutes()) {
            logger.info(route.toString());
        }
        Database.getSolutionsList().add(bestAcsSolution);
        logger.info(bestAcsSolution.toString());
        logger.info("Saving solution has been completed.");
    }

}