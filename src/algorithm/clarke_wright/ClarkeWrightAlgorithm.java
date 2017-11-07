package algorithm.clarke_wright;

import algorithm.Algorithm;
import core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;

/**
 * Klasa implementujaca algorytm oszczednosciowy Clarka i Wrighta
 */
public class ClarkeWrightAlgorithm extends Algorithm {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(ClarkeWrightAlgorithm.class);

    /**
     * Tworzy obiekt klasy
     *
     * @param problem Obiekt problemu
     */
    public ClarkeWrightAlgorithm(Problem problem) {
        super(problem, "Clarke-Wright");
    }

    /**
     * Uruchamia dzialanie algorytmu
     */
    @Override
    public void runAlgorithm() {
        logger.info("Running the Clarke-Wright algorithm...");
        createSavings();
        sortSavings();
        searchSolution();
        saveSolution();
    }

    /**
     * Dla kazdego odcinka trasy tworzy oszczednosc jaka uzyska sie poprzez dolaczenie tego odcinka do rozwiazania
     */
    private void createSavings() {
        logger.info("Creating savings...");
        Customer depot = super.getProblem().getDepot();
        for (int i = 1; i < super.getCustomers().size(); i++) {
            for (int j = 1; j < super.getCustomers().size(); j++) {
                if (i != j) {
                    Customer first = super.getCustomers().get(i);
                    int firstID = super.getCustomers().get(i).getId();
                    int secondID = super.getCustomers().get(j).getId();
                    if ((depot.getDistances().get(firstID) != null) && (depot.getDistances().get(secondID) != null) && (first.getDistances().get(secondID) != null)) {
                        double saving = depot.getDistances().get(firstID) + depot.getDistances().get(secondID) - first.getDistances().get(secondID);
                        for (RouteSegment segment : super.getRouteSegments()) {
                            int srcID = segment.getSrc().getId();
                            int dstID = segment.getDst().getId();

                            if (srcID == firstID && dstID == secondID) {
                                segment.setClarkWrightSaving(saving);
                                break;
                            }
                        }
                        logger.debug("Saving for customers " + firstID + "-" + secondID + "= " + saving + " km");
                    }
                }
            }
        }
        logger.info("Creating savings has been completed.");
    }

    /**
     * Sortuje liste odcinkow w kolejnosci od najwiekszej oszczednosci do najmniejszej
     */
    private void sortSavings() {
        logger.info("Sorting route segments by savings...");
        super.getRouteSegments().sort(Comparator.comparingDouble(RouteSegment::getClarkWrightSaving).reversed());
        logger.info("Sorting route segments by savings has been completed.");
    }

    /**
     * Wyznacza najoptymalniejsza trase w oparciu o wyliczone oszczednosci
     */
    private void searchSolution() {
        logger.info("Calculating the solution...");
        double weightLimit = super.getProblem().getWeightLimitPerVehicle();
        double sizeLimit = super.getProblem().getSizeLimitPerVehicle();
        for (RouteSegment segment : super.getRouteSegments()) {
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

                    if (!super.getRoutes().contains(route)) {
                        logger.debug("Adding route \"" + route.getId() + "\" to solution.");
                        super.getRoutes().add(route);
                        continue;
                    }
                }
            }
//            gdy klient początkowy odcinka (src) nie należy do żadnej trasy, zaś klient końcowy jest początkiem dowolnej trasy
//            i spełnione sa warunki na dodanie klienta, wówczas dodajemy klienta src oraz odcinek na początku danej trasy
            else if (!isCustomerInRoute(src)) {
                for (Route route : super.getRoutes()) {
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
                for (Route route : super.getRoutes()) {
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
            for (Route routeA : super.getRoutes()) {
                for (Route routeB : super.getRoutes()) {
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
                super.getRoutes().remove(merged);
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
            for (RouteSegment rs : super.getRouteSegments()) {
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
        if (customer.equals(getProblem().getDepot())) {
            return false;
        }
        for (Route route : super.getRoutes()) {
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
    protected void saveSolution() {
        logger.info("Saving solution...");
        double totalDistance = 0;
        Duration totalDuration = Duration.ZERO;
        for (Route route : super.getRoutes()) {
            route.setArrivalAndDepartureTimeForCustomers(super.getSolution().isTest());
            totalDistance += route.getTotalDistance();
            totalDuration = totalDuration.plus(route.getTotalDuration());
            logger.info(route.toString());
        }
        super.getSolution().setTotalDistanceCost(totalDistance);
        super.getSolution().setTotalDurationCost(totalDuration);
        Database.getSolutionsList().add(super.getSolution());
        logger.info(super.getSolution().toString());
        logger.info("Saving solution has been completed.");
        logger.info("----------------------------------------------------------------------------------------------------------------");
    }
}
