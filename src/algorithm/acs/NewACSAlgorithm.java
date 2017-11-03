package algorithm.acs;

import algorithm.Algorithm;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

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
    private static final double INITIAL_PHEROMONE_LEVEL = 0.001;

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

    private Map<Integer, BigDecimal> globalPheromoneLevel;

    private List<NewAnt> ants;

    private List<Integer> segmentsInBestAcsSolution;

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
        initializeACS();
        processACS();
        saveSolution();
    }

    //    Inicjalizacja
    private void initializeACS() {
        for (RouteSegment rs : super.getRouteSegments()) {
            globalPheromoneLevel.put(rs.getId(), BigDecimal.valueOf(INITIAL_PHEROMONE_LEVEL)); //zainicjowanie feromonu na każdym odcinku

            for (Customer c : super.getCustomers()) {
                if (c.equals(rs.getSrc()) && c.getRouteSegmentsFromCustomer().size() < super.getCustomers().size() - 1) {
                    c.getRouteSegmentsFromCustomer().add(rs); //przypisanie każdemu klientowi listy wychodzących z niego odcinków trasy
                    break;
                }
            }
        }

        for (int i = 0; i < m; i++) {
            ants.add(new NewAnt()); //utworzenie m mrówek
        }
    }

    //    Pętla główna ACS
    private void processACS() {

        int bestIteration = 1;
        int iteration = 1;
        while (i > 0 ? iteration <= i : iteration - bestIteration < 100) {
            Map<Integer, BigDecimal> localPheromoneLevel = new HashMap<>(globalPheromoneLevel); //feromon lokalny

            for (NewAnt ant : ants) {
                resetAnt(ant);
                Solution antSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot(), super.getProblem().isTest());//findAntSolution();
                saveAntSolution(antSolution);
                localPheromoneUpdate(antSolution, localPheromoneLevel); //lokalna aktualizacja feromonu

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
        ant.getFeasibleNodes().clear();
        ant.getUnvisitedCustomers().clear();

        for (Customer c : super.getCustomers()) {
            if (c.getId() != 0) {
                ant.getUnvisitedCustomers().add(c.getId()); //utworzenie listy nieodwiedzonych klientów (bez magazynu)
            }
        }
    }

    private void localPheromoneUpdate(Solution solution, Map<Integer, BigDecimal> localPheromoneLevel) {
        for (Route r : solution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                BigDecimal tau = localPheromoneLevel.get(rs.getId());
                tau = BigDecimal.valueOf(1 - ro).multiply(tau).add(BigDecimal.valueOf(ro * INITIAL_PHEROMONE_LEVEL));
                localPheromoneLevel.put(rs.getId(), tau);
            }
        }
    }

    private void globalPheromoneUpdate() {
        for (Map.Entry<Integer, BigDecimal> segment : globalPheromoneLevel.entrySet()) {
            BigDecimal tau = segment.getValue();
            tau = BigDecimal.valueOf(1 - ro).multiply(tau);

            if (segmentsInBestAcsSolution.contains(segment.getKey())) {
                tau = tau.add(BigDecimal.valueOf(1 / bestAcsSolution.getTotalDistanceCost()));
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