package algorithm.acs;

import algorithm.Algorithm;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa implementujaca algorytm mrowkowy
 */
public class ACSAlgorithm extends Algorithm {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(ACSAlgorithm.class);

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
     * Lista rozwiazan mrowek
     */
    private final List<Solution> antsSolutions;

    /**
     * Najlepsze dotychczasowe rozwiazanie
     */
    private Solution tmpBestAcsSolution;

    /**
     * Tworzy algorytm mrowkowy na podstawie parametrow ustawionych przez uzytkownika
     * @param problem Obiekt problemu
     * @param i Ilosc iteracji algorytmu
     * @param m Ilosc mrowek
     * @param q0 Parametr okreslajacy proporcje między eksploatacja najlepszej krawedzi i eksploracja nowej
     * @param beta Parametr regulujacy wpływ ni (odwrotnosc odległosci)
     * @param ro Parametr określający ilość wyparowanego feromonu
     */
    public ACSAlgorithm(Problem problem, int i, int m, double q0, double beta, double ro) {
        super(problem, "Ant Colony System");
        this.i = i;
        this.m = m;
        this.ro = ro;
        this.antsSolutions = new ArrayList<>();
        this.tmpBestAcsSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot(), super.getProblem().isTest());
        Ant.setQ0(q0);
        Ant.setBeta(beta);
    }

    /**
     * Uruchamia dzialanie algorytmu
     */
    @Override
    public void runAlgorithm() {
        logger.info("Running the Ant Colony System algorithm...");
        ACS_Procedure();
        saveSolution();
    }

    /**
     * Zarzadza mrowkami i aktualizuje poziom feromonu
     */
    private void ACS_Procedure() {
        for (Customer c : super.getCustomers()) {
            c.getRouteSegmentsFromCustomer().clear();
        }

        for (RouteSegment rs : super.getRouteSegments()) {
            for (Customer c : super.getCustomers()) {
                if (c.equals(rs.getSrc())) {
                    c.getRouteSegmentsFromCustomer().add(rs); //przypisanie każdemu klientowi listy wychodzących z niego odcinków trasy
                    break;
                }
            }
            rs.setAcsPheromoneLevel(BigDecimal.valueOf(INITIAL_PHEROMONE_LEVEL)); //ustawienie początkowej ilości feromonu dla każdego odcinka trasy
        }

        int bestIteration = 1;
        int iteration = 1;
        while (i > 0 ? iteration <= i : iteration - bestIteration < 100) {
            antsSolutions.clear();
            for (int k = 0; k < m; k++) {
                for (Customer c : super.getCustomers()) {
                    c.setAcsChoiceProbability(BigDecimal.ZERO);
                }

                Solution antSolution = constructNewAntSolution(new Ant(super.getCustomers())); //wyznaczenie rozwiąznia przez każdą mrówkę
                saveAntSolution(antSolution);
                localPheromoneUpdate(antSolution); //lokalne aktualizowanie feromonu
            }

            for (Solution s : antsSolutions) {
                logger.info("Cost: " + (int) s.getTotalDistanceCost());
                double distanceCost = s.getTotalDistanceCost();
                if (distanceCost < tmpBestAcsSolution.getTotalDistanceCost()) {
                    tmpBestAcsSolution = s; //gdy koszt rozwiązania jest mniejszy niż w najlepszym rozwiązaniu
                    bestIteration = iteration;
                }
            }
            logger.trace("The best solution after iteration " + iteration + ": " + tmpBestAcsSolution.toString());

            for (Route r : tmpBestAcsSolution.getListOfRoutes()) {
                for (RouteSegment rs : r.getRouteSegments()) {
                    rs.setPartOfBestAcsSolution(true);
                }
            }
            globalPheromoneUpdate(tmpBestAcsSolution); //globalne aktualizowanie feromonu
            iteration++;
        }
    }

    /**
     * Symuluje tworzenie rozwiazania przez pojedyncza mrowke
     * @param ant Pojedyncza mrowka
     * @return Zwraca pojedyncze rozwiazanie uzyskane przez mrowke
     */
    private Solution constructNewAntSolution(Ant ant) {
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();
        Solution antSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot(), super.getProblem().isTest());

        Route route = new Route();
        route.addCustomerAsLast(super.getProblem().getDepot());
        Customer tmpNode = route.getLastCustomer(); //umieszczenie mrówki w magazynie

        while (true) {
            ant.removeFromUnvisitedCustomers(tmpNode.getId()); //usunięcie bieżącego klienta z listy nieodwiedzonych klientów
            if (ant.updateFeasibleCustomers(tmpNode, route, weightLimit, sizeLimit) //jeżeli lista dostępnych klientów nie jest pusta
                    && (route.getCurrentPackagesWeight() < 0.9 * weightLimit //oraz pojazd jest załadowany w mniej niż 80%
                    || (route.getCurrentPackagesWeight() > 0.9 * weightLimit && new Random().nextDouble() > 0.5))) { //lub pojazd jest załadowany w ponad 80%,
//                ale losowa liczba jest większa niż 0.5

                Customer nextNode = ant.chooseNextNode(tmpNode); //wybór kolejnego klienta
                if (nextNode == null) {
                    logger.info("TU JEST NULL! Tmp: " + tmpNode.getId() + ", Route: " + route.toString());
                }
                route.addCustomerAsLast(super.getCustomers().get(nextNode.getId())); //dodanie wybranego klienta do budowanej trasy
                for (RouteSegment rs : tmpNode.getRouteSegmentsFromCustomer()) {
                    if (rs.getDst().equals(nextNode)) {
                        route.addSegmentAsLast(rs); //dodanie kolejnego odcinka do budowanej trasy
                        if (tmpNode.getId() != 0) {
                            rs.setPartOfAntAcsSolution(true);
                        }
                        break;
                    }
                }
                tmpNode = nextNode;

            } else { //jeżeli lista dostępnych klientów jest pusta
                ant.removeFromUnvisitedCustomers(tmpNode.getId()); //usunięcie ostatniego klienta z listy nieodwiedzonych klientów
                route.addCustomerAsLast(super.getProblem().getDepot()); //dodanie magazynu do listy klienów trasy
                for (RouteSegment rs : tmpNode.getRouteSegmentsFromCustomer()) {
                    if (rs.getDst().equals(super.getProblem().getDepot())) {
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
                    tmpNode = route.getLastCustomer();
                }
            }
        }
        return antSolution;
    }

    /**
     * Aktualizuje lokalny poziom feromonu
     * @param antSolution Rozwiazanie uzyskane przez mrowke
     */
    private void localPheromoneUpdate(Solution antSolution) {
        for (Route r : antSolution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                if (rs.isPartOfAntAcsSolution()) {
                    BigDecimal tau = rs.getAcsPheromoneLevel(); //poziom feromonu
                    tau = BigDecimal.valueOf(1 - ro).multiply(tau).add(BigDecimal.valueOf(ro * INITIAL_PHEROMONE_LEVEL));
                    rs.setAcsPheromoneLevel(tau);
                    rs.setPartOfAntAcsSolution(false);
                }
            }
        }
    }

    /**
     * Aktualizuje globalny poziom feromonu
     * @param bestSolution Dotychczasowe najlepsze rozwiazanie znalezione przez mrowke
     */
    private void globalPheromoneUpdate(Solution bestSolution) {
        for (RouteSegment rs : super.getRouteSegments()) {
            BigDecimal tau = rs.getAcsPheromoneLevel();
            if (rs.isPartOfBestAcsSolution()) {
                tau = BigDecimal.valueOf(1 - ro).multiply(tau).add(BigDecimal.valueOf(1 / bestSolution.getTotalDistanceCost()));
                rs.setPartOfBestAcsSolution(false);
            } else {
                tau = BigDecimal.valueOf(1 - ro).multiply(tau);
            }
            rs.setAcsPheromoneLevel(tau);
        }
    }

    /**
     * Zapisuje rozwiazanie uzyskane przez mrowke, oblicza calkowita dlugosc i czas rozwiazania oblicza czasy przyjazdu i odjazdu do kazdego klienta
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
        antsSolutions.add(solution);
    }

    /**
     * Zapisuje najlepsze rozwiazanie znalezione przez algorytm mrowkowy
     */
    @Override
    protected void saveSolution() {
        logger.info("Saving solution...");
        for (Route route : tmpBestAcsSolution.getListOfRoutes()) {
            logger.info(route.toString());
        }
        Database.getSolutionsList().add(tmpBestAcsSolution);
        logger.info(tmpBestAcsSolution.toString());
        logger.info("Saving solution has been completed.");
    }
}
