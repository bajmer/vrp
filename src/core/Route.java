package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Klasa reprezentujaca trase jednego pojazdu, rozpoczynajaca sie i konczaca w magazynie
 */
public class Route {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(Route.class);

    /**
     * Numer ID
     */
    private static int routeID = 0;

    /**
     * Godzina wyjazdu z magazynu
     */
    private final LocalTime startTime;

    /**
     * Numer ID trasy
     */
    private int id;

    /**
     * Lista klientow, ktorzy naleza do trasy
     */
    private ArrayList<Customer> customersInRoute;

    /**
     * Lista odcinkow, ktore naleza do trasy
     */
    private ArrayList<RouteSegment> routeSegments;

    /**
     * Laczna dlugosc trasy
     */
    private double totalDistance;

    /**
     * Laczny czas przejazdu trasy
     */
    private Duration totalDuration;

    /**
     * Biezaca masa paczek rozwozonych po trasie
     */
    private double currentPackagesWeight;

    /**
     * Biezaca objetosc paczek rozwozonych po trasie
     */
    private double currentPackagesSize;

    /**
     * Obraz mapy z narysowana trasa
     */
    private ImageIcon imageIcon;

    /**
     * Tworzy nowa pusta trase
     */
    public Route() {
        routeID++;
        this.id = routeID;
        this.customersInRoute = new ArrayList<>();
        this.routeSegments = new ArrayList<>();
        this.totalDistance = 0.0;
        this.totalDuration = Duration.ZERO;
        this.startTime = LocalTime.of(8, 0);
        this.currentPackagesWeight = 0.0;
        this.currentPackagesSize = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Customer> getCustomersInRoute() {
        return customersInRoute;
    }

    public void setCustomersInRoute(ArrayList<Customer> customersInRoute) {
        this.customersInRoute = customersInRoute;
    }

    public ArrayList<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    public void setRouteSegments(ArrayList<RouteSegment> routeSegments) {
        this.routeSegments = routeSegments;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Duration totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getCurrentPackagesWeight() {
        return currentPackagesWeight;
    }

    public void setCurrentPackagesWeight(double currentPackagesWeight) {
        this.currentPackagesWeight = currentPackagesWeight;
    }

    public double getCurrentPackagesSize() {
        return currentPackagesSize;
    }

    public void setCurrentPackagesSize(double currentPackagesSize) {
        this.currentPackagesSize = currentPackagesSize;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    /**
     * Dodaje klienta do poczatku trasy, zwieksza biezaca mase i objetosc paczek
     * @param customer Dodawany klient
     */
    public void addCustomerAsFirst(Customer customer) {
        int firstPosition = 0;
        customersInRoute.add(firstPosition, customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
    }

    /**
     * Dodaje klienta do konca trasy, zwieksza biezaca mase i objetosc paczek
     * @param customer Dodawany klient
     */
    public void addCustomerAsLast(Customer customer) {
        customersInRoute.add(customer);
        currentPackagesWeight += customer.getPackageWeight();
        currentPackagesSize += customer.getPackageSize();
    }

    /**
     * Laczy dwie trasy w jedna, dodaje mase i objetosc paczek, a takze klientow i odcinki dolaczanej trasy. Aktualizuje dlugosc nowej trasy i czas jej przejazdu
     * @param route Dolaczana trasa
     */
    public void mergeRoute(Route route) {
        customersInRoute.addAll(route.getCustomersInRoute());
        currentPackagesWeight += route.getCurrentPackagesWeight();
        currentPackagesSize += route.getCurrentPackagesSize();
        totalDistance += route.getTotalDistance();
        totalDuration = totalDuration.plus(route.getTotalDuration());
        totalDistance = round(totalDistance);

        routeSegments.addAll(route.getRouteSegments());
    }

    /**
     * Dodaje odcinek do poczatku trasy, zwieksza dlugosc trasy i czas jej przejazdu
     * @param routeSegment Dodawany odcinek
     */
    public void addSegmentAsFirst(RouteSegment routeSegment) {
        int firstPosition = 0;
        routeSegments.add(firstPosition, routeSegment);
        totalDistance += routeSegment.getDistance();
        totalDuration = totalDuration.plus(routeSegment.getDuration()).plus(Customer.getServiceTime());
    }

    /**
     * Dodaje odcinek do konca trasy, zwieksza dlugosc trasy i czas jej przejazdu
     * @param routeSegment Dodawany odcinek
     */
    public void addSegmentAsLast(RouteSegment routeSegment) {
        routeSegments.add(routeSegment);
        totalDistance += routeSegment.getDistance();
        if (routeSegment.getDst().getId() == 0) {
            totalDuration = totalDuration.plus(routeSegment.getDuration());
        } else {
            totalDuration = totalDuration.plus(routeSegment.getDuration()).plus(Customer.getServiceTime());
        }
    }

    /**
     * Sprawdza, czy mozna dodac klienta do trasy
     * @param packageWeight Masa paczki
     * @param weightLimit Maksymalna dopuszczalna masa ladunku
     * @param packageSize Objetosc paczki
     * @param sizeLimit Maksymalna dopuszczalna objetosc ladunku
     * @return Zwraca "true" jezeli mozna dodac klienta do trasy, w przeciwnym razie "false"
     */
    public boolean canAdd(double packageWeight, double weightLimit, double packageSize, double sizeLimit) {
        return canAddWeight(packageWeight, weightLimit) && canAddSize(packageSize, sizeLimit);
    }

    /**
     * Sprawdza, czy masa paczki nie przekroczy maksymalna dopuszczalna masy ladunku
     * @param packageWeight Masa paczki
     * @param weightLimit Maksymalna dopuszczalna masa ladunku
     * @return Zwraca "true" jezeli na pojazd mozna dolozyc paczke o danej masie i nie spowoduje to przekroczenia ladownosci pojazdu, w przeciwnym razie "false"
     */
    private boolean canAddWeight(double packageWeight, double weightLimit) {
        return currentPackagesWeight + packageWeight <= weightLimit;
    }

    /**
     * Sprawdza czy paczka zmiesci sie do pojazdu
     * @param packageSize Objetosc paczki
     * @param sizeLimit Maksymalna dopuszczalna objetosc paczek
     * @return Zwraca "true" jezeli do pojazdu zmiesci sie paczka o danej objetosci, w przeciwnym razie "false"
     */
    private boolean canAddSize(double packageSize, double sizeLimit) {
        return currentPackagesSize + packageSize <= sizeLimit;
    }

    /**
     * Sprawdza, czy klient jest pierwszy w kolejnosci na trasie
     * @param customer Sprawdzany klient
     * @return Zwraca "true" jezeli klient jest pierwszy w kolejnosci na trasie, w przeciwnym razie "false"
     */
    public boolean isCustomerFirst(Customer customer) {
        return customersInRoute.get(0).equals(customer);
    }

    /**
     * Sprawdza, czy klient jest ostatni w kolejnosci na trasie
     * @param customer Sprawdzany klient
     * @return Zwraca "true" jezeli klient jest ostatni w kolejnosci na trasie, w przeciwnym razie "false"
     */
    public boolean isCustomerLast(Customer customer) {
        return customersInRoute.get(customersInRoute.size() - 1).equals(customer);
    }

    /**
     * Pobiera ostatniego w kolejnosci klienta na trasie
     * @return Zwracany klient
     */
    public Customer getLastCustomer() {
        return customersInRoute.get(customersInRoute.size() - 1);
    }

    /**
     * Zaokroagla liczbe double do jednego miejsca po przecinku
     * @param x Liczba do zaokraglenia
     * @return Zwraca zaokraglana liczbe
     */
    private double round(double x) {
        return new BigDecimal(x).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * Ustawia czasy przyjazdu i odjazdu dla klientow nalezacych do trasy
     */
    public void setArrivalAndDepartureTimeForCustomers(boolean isTest) {
        if (!isTest) {
            LocalTime departute = startTime;
            LocalTime arrival = startTime;
            for (RouteSegment rs : routeSegments) {
                Duration duration = rs.getDuration();
                if (rs.getSrc().getId() == 0) {
                    rs.setDeparture(departute);
                    arrival = arrival.plus(duration);
                    rs.setArrival(arrival);
                } else {
                    departute = arrival.plus(Customer.getServiceTime());
                    rs.setDeparture(departute);
                    arrival = departute.plus(duration);
                    rs.setArrival(arrival);
                }
            }
        }
    }

    /**
     * Wypisuje informacje o trasie
     * @return Zwraca opis trasy
     */
    @Override
    public String toString() {
        long minutes = totalDuration.toMinutes() % 60;
        String sMinutes;
        sMinutes = minutes < 10 ? "0" + Long.toString(minutes) : Long.toString(minutes);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getCustomersInRoute().size(); i++) {
            Customer c = this.getCustomersInRoute().get(i);
            sb.append(c.getId());
            if (i != this.getCustomersInRoute().size() - 1) {
                sb.append("-");
            }
        }
        return "Route " + id + ", "
                + round(totalDistance) + "km, "
                + totalDuration.toHours() + ":" + sMinutes + "h, "
                + currentPackagesWeight + "kg, "
                + currentPackagesSize + "m3, "
                + sb.toString();
    }
}
