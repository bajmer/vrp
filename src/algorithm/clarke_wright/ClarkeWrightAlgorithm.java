package algorithm.clarke_wright;

import algorithm.Algorithmic;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Klasa implementujaca algorytm oszczednosciowy Clarka i Wrighta
 */
public class ClarkeWrightAlgorithm implements Algorithmic {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(ClarkeWrightAlgorithm.class);

    /**
     * Nazwa algorytmu
     */
    private static final String ALGORITHM_NAME = "Clarke-Wright";

    /**
     * Rozwiazywany problem
     */
    private Problem problem;

    /**
     * Rozwiazanie, ktore zostanie znalezione przez algorytm
     */
    private Solution solution;

    /**
     * Mapa przypisujaca do kazdego odcinka "oszczednosc", ktora zostanie uzyskana przez wlaczenie tego odcinka do trasy
     */
    private Map<RouteSegment, Double> savings;

    /**
     * Tworzy obiekt klasy
     *
     * @param problem Obiekt problemu
     */
    public ClarkeWrightAlgorithm(Problem problem) {
        this.problem = problem;
        this.solution = new Solution(problem.getProblemID(), ALGORITHM_NAME, problem.getDepot(), problem.isTest());
        this.savings = new HashMap<>();
    }

    /**
     * Uruchamia dzialanie algorytmu
     */
    @Override
    public void runAlgorithm() {
        logger.info("----------------------------------------------------------------------------------------------------------------");
        logger.info("Running the Clarke-Wright algorithm...");
        createSavings();
        sortSavings();
        constructClarkeWrightSolution();
        saveSolution();
    }

    /**
     * Dla kazdego odcinka trasy tworzy oszczednosc jaka uzyska sie poprzez dolaczenie tego odcinka do rozwiazania
     */
    private void createSavings() {
        logger.info("Creating savings...");
        Customer depot = problem.getDepot();
        for (RouteSegment rs : Database.getRouteSegmentsList()) {
            Customer first = rs.getSrc();
            Customer second = rs.getDst();

            double saving = (depot.equals(first) ? 0.0 : depot.getDistances().get(first.getId()))
                    - first.getDistances().get(second.getId())
                    + (second.equals(depot) ? 0.0 : depot.getDistances().get(second.getId()));

            savings.put(rs, saving);
        }
        logger.info("Creating savings has been completed.");
    }

    /**
     * Sortuje liste odcinkow w kolejnosci od najwiekszej oszczednosci do najmniejszej
     */
    private void sortSavings() {
        logger.info("Sorting route segments by savings...");
        savings = savings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        logger.info("Sorting route segments by savings has been completed.");
    }

    /**
     * Wyznacza najoptymalniejsza trase w oparciu o wyliczone oszczednosci
     */
    private void constructClarkeWrightSolution() {
        logger.info("Calculating the solution...");
        double weightLimit = problem.getWeightLimitPerVehicle();
        double sizeLimit = problem.getSizeLimitPerVehicle();
        for (Map.Entry<RouteSegment, Double> entry : savings.entrySet()) {
            RouteSegment segment = entry.getKey();
            logger.debug("Processing route segment: " + segment.getSrc().getId() + "-" + segment.getDst().getId());

            Customer src = segment.getSrc();
            Customer dst = segment.getDst();

            if (!isCustomerInRoute(src) && !isCustomerInRoute(dst)) { //żaden klient nie należy do trasy
                if (src.getPackageWeight() + dst.getPackageWeight() <= weightLimit //suma paczek nie przekracza limitów (masy i objętości)
                        && src.getPackageSize() + dst.getPackageSize() <= sizeLimit) {
                    Route route = new Route();
                    route.addCustomerAsFirst(src);
                    route.addCustomerAsLast(dst);
                    route.addSegmentAsLast(segment);

                    logger.debug("Creating new route with ID " + route.getId() + " for customers: " + src.getId() + "-" + dst.getId());
                    logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                    route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                    logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());

                    if (!solution.getListOfRoutes().contains(route)) {
                        logger.debug("Adding route \"" + route.getId() + "\" to solution.");
                        solution.getListOfRoutes().add(route);
                        continue;
                    }
                }
            }
//            gdy klient początkowy odcinka (src) nie należy do żadnej trasy, zaś klient końcowy jest początkiem dowolnej trasy
//            i spełnione sa warunki na dodanie klienta, wówczas dodajemy klienta src oraz odcinek na początku danej trasy
            else if (!isCustomerInRoute(src)) {
                for (Route route : solution.getListOfRoutes()) {
                    if (route.canAdd(src.getPackageWeight(), weightLimit, src.getPackageSize(), sizeLimit)) {
                        if (route.isCustomerFirst(dst)) {
                            route.addCustomerAsFirst(src);
                            route.addSegmentAsFirst(segment);

                            logger.debug("Customer with id " + src.getId() + " added on the FIRST position in route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;

                        }
                    }
                }
            }

//            gdy klient docelowy odcinka (dst) nie należy do żadnej trasy, zaś klient początkowy jest końcem dowolnej trasy
//            i spełnione sa warunki na dodanie klienta, wówczas dodajemy klienta dst oraz odcinek na końcu danej trasy
            else if (!isCustomerInRoute(dst)) {
                for (Route route : solution.getListOfRoutes()) {
                    if (route.canAdd(dst.getPackageWeight(), weightLimit, dst.getPackageSize(), sizeLimit)) {
                        if (route.isCustomerLast(src)) {
                            route.addCustomerAsLast(dst);
                            route.addSegmentAsLast(segment);

                            logger.debug("Customer with id " + dst.getId() + " added as LAST node to route " + route.getId());
                            logger.debug("Route \"" + route.getId() + "\" includes the following customers: ");
                            route.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                            logger.debug("and current packages weight for this route is " + route.getCurrentPackagesWeight());
                            break;
                        }
                    }
                }
            }

//            łączenie dwóch tras w jedną, jeśli klient początkowy i docelowy odcinka należą do różnych tras i spełnione są warunki na połączenie tras
            Route merged = null;
            Route saved = null;
            for (Route routeA : solution.getListOfRoutes()) {
                for (Route routeB : solution.getListOfRoutes()) {
                    if (routeA != routeB) {
                        boolean canMerge = false;
                        if (routeA.canAdd(routeB.getCurrentPackagesWeight(), weightLimit, routeB.getCurrentPackagesSize(), sizeLimit)) {
//                            przypadek 1 (...,...,src + dst,...,...) - klient src odcinka jest ostatnim klientem trasy A
//                            oraz klient dst jest pierwszym klientem trasy B: łączymy dwie trasy bez zmian
                            if (routeA.isCustomerLast(src) && routeB.isCustomerFirst(dst)) {
                                canMerge = true;
                            }

//                            przypadek 2 (src,...,... + dst,...,...) - klient src odcinka jest pierwszym klientem trasy A
//                            oraz klient dst jest pierwszym klientem trasy B: obracamy trasę A i łączymy dwie trasy
                            else if (routeA.isCustomerFirst(src) && routeB.isCustomerFirst(dst)) {
                                rotateRoute(routeA);
                                canMerge = true;
                            }

//                            przypadek 3 (...,...,src + ...,...,dst) - klient src odcinka jest ostatnim klientem trasy A
//                            oraz klient dst jest ostatnim klientem trasy B: obracamy trasę B i łączymy dwie trasy
                            else if (routeA.isCustomerLast(src) && routeB.isCustomerLast(dst)) { // (7) .....s   .....d
                                rotateRoute(routeB);
                                canMerge = true;
                            }

//                            łączenie tras: dodajemy do trasy A odcinek src - dst i dołączamy do trasy A trasę B
                            if (canMerge) {
                                routeA.addSegmentAsLast(segment);
                                routeA.mergeRoute(routeB);
                                saved = routeA;
                                merged = routeB;
                                break;
                            }
                        }
                    }
                }
            }
            if (merged != null) {
                logger.debug("Route \"" + merged.getId() + "\" was merged into route \"" + saved.getId() + "\"");
                logger.debug("Route \"" + saved.getId() + "\" includes now the following customers: ");
                saved.getCustomersInRoute().forEach(Customer -> logger.debug(Customer.getId() + "-"));
                logger.debug(", current packages weight is " + saved.getCurrentPackagesWeight() + "kg, current packages size is " + saved.getCurrentPackagesSize() + "m3.");

                logger.debug("Removing route \"" + merged.getId() + "\" from solution because of merge.");
                solution.getListOfRoutes().remove(merged);
            }
        }
        logger.info("Calculating the solution has been completed.");
    }

    /**
     * Obraca trasę, czyli odwraca listę klientów należących do trasy, czyści listę odcinków i dodaje odwrotne odcinki
     *
     * @param route Obracana trasa
     */
    private void rotateRoute(Route route) {
        Collections.reverse(route.getCustomersInRoute());
        route.getRouteSegments().clear();
        for (int i = 0; i < route.getCustomersInRoute().size() - 1; i++) {
            Customer source = route.getCustomersInRoute().get(i);
            Customer destination = route.getCustomersInRoute().get(i + 1);
            for (RouteSegment rs : Database.getRouteSegmentsList()) {
                if (rs.getSrc().equals(source) && rs.getDst().equals(destination)) {
                    route.getRouteSegments().add(rs);
                    break;
                }
            }
        }
    }

    /**
     * Sprawdza, czy klient nalezy do trasy
     *
     * @param customer Klient
     * @return Zwraca "false", jesli klient nie nalezy do trasy lub obiekt klienta jest magazynem, zas "true", gdy klient nalezy do trasy
     */
    private boolean isCustomerInRoute(Customer customer) {
        if (customer.equals(problem.getDepot())) {
            return false;
        }
        for (Route route : solution.getListOfRoutes()) {
            for (Customer c : route.getCustomersInRoute()) {
                if (customer == c) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Zapisuje uzyskane rozwiazanie, oblicza calkowita dlugosc i czas rozwiazania oblicza czasy przyjazdu i odjazdu do kazdego klienta
     */
    @Override
    public void saveSolution() {
        logger.info("Saving solution...");
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : solution.getListOfRoutes()) {
            route.setArrivalAndDepartureTimeForCustomers(solution.isTest());
            totalDistance += route.getTotalDistance();
            totalDuration = totalDuration.plus(route.getTotalDuration());
            logger.info(route.toString());
        }
        solution.setTotalDistanceCost(totalDistance);
        solution.setTotalDurationCost(totalDuration);
        Database.getSolutionsList().add(solution);
        logger.info(solution.toString());
        logger.info("Saving solution has been completed.");
    }
}
