package algorithm.acs;

import core.Customer;
import core.Route;
import core.RouteSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa reprezentujaca pojedyncza mrowke w algorytmie mrowkowym
 */
class Ant {

    /**
     * Parametr okreslajacy proporcje miedzy eksploatacja najlepszej krawedzi i eksploracja nowej
     */
    private static double q0;

    /**
     * Parametr regulujacy wpływ ni (odwrotnosc odległosci)
     */
    private static double beta; //preferowana wartość to 2-5

    /**
     * Lista nieodwiedzonych jeszcze klientow
     */
    private List<Integer> unvisitedCustomers;

    /**
     * Lista dostepnych klientow, do ktorych moze isc mrowka w najblizszym kroku
     */
    private List<Customer> feasibleNodes;

    /**
     * Tworzy mrowke
     * @param customers Lista klientow
     */
    Ant(List<Customer> customers) {
        feasibleNodes = new ArrayList<>();
        unvisitedCustomers = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getId() != 0) {
                unvisitedCustomers.add(c.getId()); //utworzenie listy nieodwiedzonych klientów (bez magazynu)
            }
        }
    }

    static double getQ0() {
        return q0;
    }

    static void setQ0(double q0) {
        Ant.q0 = q0;
    }

    static double getBeta() {
        return beta;
    }

    static void setBeta(double beta) {
        Ant.beta = beta;
    }

    List<Integer> getUnvisitedCustomers() {
        return unvisitedCustomers;
    }

    public void setUnvisitedCustomers(List<Integer> unvisitedCustomers) {
        this.unvisitedCustomers = unvisitedCustomers;
    }

    public List<Customer> getFeasibleNodes() {
        return feasibleNodes;
    }

    public void setFeasibleNodes(List<Customer> feasibleNodes) {
        this.feasibleNodes = feasibleNodes;
    }

    /**
     * Aktualizuje liste mozliwych do odwiedzenia klientow
     * @param tmpNode Klient, u ktorego obecnie znajduje sie mrowka
     * @param route Trasa tworzona przez mrowke
     * @param weightLimit Maksymalna dopuszczalna masa ladunku
     * @param sizeLimit Maksymalna dopuszczalna objetosc ladunku
     * @return Zwraca "true" jesli sa klienci, ktorych mozna w danym kroku, w przeciwnym razie "false"
     */
    boolean updateFeasibleCustomers(Customer tmpNode, Route route, double weightLimit, double sizeLimit) {
        feasibleNodes.clear();
        for (RouteSegment rsFromCustomer : tmpNode.getRouteSegmentsFromCustomer()) {
            Customer dst = rsFromCustomer.getDst();
            if (unvisitedCustomers.contains(dst.getId())) { //jeśli klient jest na liście nieodwiedzonych klientów
                if (route.canAdd(dst.getPackageWeight(), weightLimit, dst.getPackageSize(), sizeLimit)) { //jeśli klient może zostać dodany do trasy (dopisać warunki czasowe dla VRPTW)
                    feasibleNodes.add(dst);
                }
            }
        }
        return feasibleNodes.size() != 0;
    }

    /**
     * Usuwa klienta z listy nieodwiedzonych klientow
     * @param idToRemove Id klienta, ktorego nalezy usunac
     */
    void removeFromUnvisitedCustomers(int idToRemove) {
        for (int i = 0; i < unvisitedCustomers.size(); i++) {
            int id = unvisitedCustomers.get(i);
            if (id == idToRemove) {
                unvisitedCustomers.remove(i);
                return;
            }
        }
    }

    /**
     * Wybiera klienta, ktory zostanie obecnie odwiedzony
     * @param currentNode Klient, u ktorego obecnie znajduje sie mrowka
     * @return Zwraca kolejnego do odwiedzenia klienta
     */
    Customer chooseNextNode(Customer currentNode) {
        Customer nextNode = null;
        if (currentNode.getId() == 0) {
            //jeżeli mrówka jest w magazynie wybiera losowo miasto z listy dostępnych miast
            int randomValue = new Random().nextInt(feasibleNodes.size());
            nextNode = feasibleNodes.get(randomValue);
        } else {
            //jeżeli mrówka jest w dowolnym węźle (ale nie w magazynie), wówczas wybiera kolejne miasto zgodnie z zasadami ACS
            Customer bestExploitationCustomer = calculateProbabilityForAllFeasibleNodes(currentNode);
            if (new Random().nextDouble() <= q0) {
                nextNode = bestExploitationCustomer; //eksploatacja klienta, dla którego wartość  licznika "tau*(1/distance)^beta" jest największa
            } else {
                //wylosowanie klienta uwzględniając prawdopodobieństwo
                double weightSum = 0;
                for (Customer c : feasibleNodes) {
                    weightSum += c.getAcsChoiceProbability();
                }
                double value = new Random().nextDouble() * weightSum; //zakres 0-1 * suma wag

                for (Customer c : feasibleNodes) {
                    value -= c.getAcsChoiceProbability();
                    if (value <= 0) {
                        nextNode = c;
                        break;
                    }
                }
            }
        }
        return nextNode;
    }

    /**
     * Oblicza prawdopodobienstwo wybrania dostepnego klienta jako kolejnego do odwiedzenia
     * @param currentNode Klient, u ktorego obecnie znajduje sie mrowka
     * @return Zwraca klienta, który jest najlepszy do odwiedzenia
     */
    private Customer calculateProbabilityForAllFeasibleNodes(Customer currentNode) {
        double downNumber = 0;
        double bestUpNumber = 0;
        Customer bestExploitationNode = null;

        for (RouteSegment rs : currentNode.getRouteSegmentsFromCustomer()) {
            if (feasibleNodes.contains(rs.getDst())) {
                double distance = rs.getDistance();
                double ni = 1 / distance;
                double tau = rs.getAcsPheromoneLevel(); //pheromone level on segment
                double upNumber = tau * Math.pow(ni, beta); //licznik
                rs.setAcsUpNumber(upNumber);
                if (upNumber > bestUpNumber) {
                    bestUpNumber = upNumber;
                    bestExploitationNode = rs.getDst();
                }
                downNumber += upNumber; //mianownik
                if (downNumber == 0) {
                    break;
                }
            }
        }

        for (RouteSegment rs : currentNode.getRouteSegmentsFromCustomer()) {
            if (feasibleNodes.contains(rs.getDst())) {
                double probability = rs.getAcsUpNumber() / downNumber; //obliczanie prawdopodobieństwa wyboru danego odcinka trasy
                rs.getDst().setAcsChoiceProbability(probability);
            }
        }
        return bestExploitationNode;
    }
}
