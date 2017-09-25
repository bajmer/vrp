package algorithm.macs;

import algorithm.Algorithm;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ACSAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(ACSAlgorithm.class);

    private static final String ACS = "Ant Colony System";
    private static final double INITIAL_PHEROMONE_LEVEL = 0.001;
    private final int i; //ilość iteracji algorytmu
    private final int m; //ilość mrówek, preferowana wartość = n (ilość miast)
    private final double ro; //parametr określający ilość wyparowanego feromonu, zakres <0-1>, preferowana wartość to 0.5
    private final List<Solution> antsSolutions;
    private final List<RouteSegment> acsRouteSegments;
    private Solution tmpBestAcsSolution;

    public ACSAlgorithm(Problem problem, int i, int m, double q0, double beta, double ro) {
        super(problem, "Ant Colony System");
        this.i = i;
        this.m = m;
        this.ro = ro;
        this.antsSolutions = new ArrayList<>();
        this.tmpBestAcsSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());
        Ant.setQ0(q0);
        Ant.setBeta(beta);

//        utworzenie tablicy odcinków trasy, zawierającej również obrócone odcinki
        List<RouteSegment> notSwappedSegments = new ArrayList<>(super.getRouteSegments().size());
        notSwappedSegments.addAll(Database.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        List<RouteSegment> swappedSegments = new ArrayList<>(super.getRouteSegments().size());
        swappedSegments.addAll(Database.getRouteSegmentsList().stream().map(RouteSegment::clone).collect(Collectors.toList()));
        for (RouteSegment rs : swappedSegments) {
            rs.swapSrcDst();
        }
        this.acsRouteSegments = new LinkedList<>();
        acsRouteSegments.addAll(notSwappedSegments);
        acsRouteSegments.addAll(swappedSegments);
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Ant Colony System algorithm...");
        ACS_Procedure();
        saveSolution();
    }

    private void ACS_Procedure() {
        for (RouteSegment rs : acsRouteSegments) {
            for (Customer c : super.getCustomers()) {
                if (c.equals(rs.getSrc())) {
                    c.getRouteSegmentsFromCustomer().add(rs); //przypisanie każdemu klientowi listy wychodzących z niego odcinków trasy
                    break;
                }
            }
            rs.setAcsPheromoneLevel(INITIAL_PHEROMONE_LEVEL); //ustawienie początkowej ilości feromonu dla każdego odcinka trasy
        }

        int iteration = 1;
        while (iteration <= i) {
            antsSolutions.clear();
            for (int k = 0; k < m; k++) {
                Solution antSolution = constructNewAntSolution(new Ant(super.getCustomers())); //wyznaczenie rozwiąznia przez każdą mrówkę
                saveAntSolution(antSolution);
                localPheromoneUpdate(antSolution); //lokalne aktualizowanie feromonu
            }

            for (Solution s : antsSolutions) {
                double distanceCost = s.getTotalDistanceCost();
                if (distanceCost < tmpBestAcsSolution.getTotalDistanceCost()) {
                    tmpBestAcsSolution = s; //gdy koszt rozwiązania jest mniejszy niż w najlepszym rozwiązaniu
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

    private Solution constructNewAntSolution(Ant ant) {
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();
        Solution antSolution = new Solution(super.getProblem().getProblemID(), ACS, super.getProblem().getDepot());

        Route route = new Route();
        route.addCustomerAsLast(super.getProblem().getDepot());
        Customer tmpNode = route.getLastCustomer(); //umieszczenie mrówki w magazynie

        while (true) {
            ant.removeFromUnvisitedCustomers(tmpNode.getId()); //usunięcie bieżącego klienta z listy nieodwiedzonych klientów
            if (ant.updateFeasibleCustomers(tmpNode, route, weightLimit, sizeLimit)) { //jeżeli lista dostępnych klientów nie jest pusta
                Customer nextNode = ant.chooseNextNode(tmpNode); //wybór kolejnego klienta

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

    private void localPheromoneUpdate(Solution antSolution) {
        for (Route r : antSolution.getListOfRoutes()) {
            for (RouteSegment rs : r.getRouteSegments()) {
                if (rs.isPartOfAntAcsSolution()) {
                    double tau = rs.getAcsPheromoneLevel(); //poziom feromonu
                    tau = (1 - ro) * tau + ro * INITIAL_PHEROMONE_LEVEL;
                    rs.setAcsPheromoneLevel(tau);
                    rs.setPartOfAntAcsSolution(false);
                }
            }
        }
    }

    private void globalPheromoneUpdate(Solution bestSolution) {
        for (RouteSegment rs : acsRouteSegments) {
            double tau = rs.getAcsPheromoneLevel();
            if (rs.isPartOfBestAcsSolution()) {
                tau = (1 - ro) * tau + 1 / bestSolution.getTotalDistanceCost();
                rs.setPartOfBestAcsSolution(false);
            } else {
                tau = (1 - ro) * tau;
            }

            if (tau > 1E-100) {
                rs.setAcsPheromoneLevel(tau);
            } else {
                return;
            }
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
