package algorithm.acs;

import core.Customer;
import core.Route;
import core.RouteSegment;

import java.math.BigDecimal;
import java.util.*;

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

    private Map<Integer, BigDecimal> explorationProbabilities;

    private Map<Integer, BigDecimal> exploitationRates;

    private static Map<Integer, BigDecimal> localPhermoneLevel;

    /**
     * Tworzy mrowke
     * @param customers Lista klientow
     */
    Ant(List<Customer> customers, Map<Integer, BigDecimal> localPhermoneLevel) {
        feasibleNodes = new ArrayList<>();
        unvisitedCustomers = new ArrayList<>();
        explorationProbabilities = new HashMap<>();
        exploitationRates = new HashMap<>();
        Ant.localPhermoneLevel = localPhermoneLevel;

        for (Customer c : customers) {
            if (c.getId() != 0) {
                unvisitedCustomers.add(c.getId()); //utworzenie listy nieodwiedzonych klientów (bez magazynu)
//                customersChoiceProbability.put(c.getId(), BigDecimal.ZERO);
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
        Customer nextNode = null;// = feasibleNodes.get(0);
        exploitationRates.clear();
        explorationProbabilities.clear();

        if (currentNode.getId() == 0) {
            //jeżeli mrówka znajduje się w magazynie wybiera losowo miasto z listy dostępnych miast
            int randomValue = new Random().nextInt(feasibleNodes.size());
            nextNode = feasibleNodes.get(randomValue);
        } else {
            //jeżeli mrówka jest w dowolnym węźle (ale nie w magazynie), wówczas wybiera kolejne miasto zgodnie z zasadami ACS
//            Customer bestExploitationCustomer = calculateProbabilityForAllFeasibleNodes(currentNode);
            calculateProbabilityForAllFeasibleNodes(currentNode);

            if (new Random().nextDouble() <= q0) {
//                nextNode = bestExploitationCustomer; //eksploatacja klienta, dla którego wartość  licznika "tau*(1/distance)^beta" jest największa
                int bestId = findBestExploitationCustomerId();
                for (Customer c : feasibleNodes) {
                    if (c.getId() == bestId) {
                        nextNode = c;
                        break;
                    }
                }

            } else {
                //wylosowanie klienta uwzględniając prawdopodobieństwo
                BigDecimal weightSum = BigDecimal.ZERO;
//                for (Customer c : feasibleNodes) {
//                    weightSum = weightSum.add(c.getAcsChoiceProbability());
//                }
                for (Customer c : feasibleNodes) {
                    if (explorationProbabilities.containsKey(c.getId())) {
                        weightSum = weightSum.add(explorationProbabilities.get(c.getId()));
                    } else {
                        System.out.println("Tu może być błąd");
                    }
                }

                BigDecimal value = BigDecimal.valueOf(new Random().nextDouble()).multiply(weightSum); //zakres 0-1 * suma wag

//                for (Customer c : feasibleNodes) {
//                    value = value.subtract(c.getAcsChoiceProbability());
//                    if (value.compareTo(BigDecimal.ZERO) <= 0) {
//                        nextNode = c;
//                        break;
//                    }
//                }

                for (Customer c : feasibleNodes) {
                    if (explorationProbabilities.containsKey(c.getId())) {
                        value = value.subtract(explorationProbabilities.get(c.getId()));
                        if (value.compareTo(BigDecimal.ZERO) <= 0) {
                            nextNode = c;
                            break;
                        }
                    }
                }
            }

            System.out.println("Blad");
        }
        System.out.println("Blad");
        return nextNode;
    }

    /**
     * Oblicza prawdopodobienstwo wybrania dostepnego klienta jako kolejnego do odwiedzenia
     * @param currentNode Klient, u ktorego obecnie znajduje sie mrowka
     * @return Zwraca klienta, który jest najlepszy do odwiedzenia
     */
    private void calculateProbabilityForAllFeasibleNodes(Customer currentNode) {
//        BigDecimal downNumber = BigDecimal.ZERO;
//        BigDecimal bestUpNumber = BigDecimal.ZERO;
//        Customer bestExploitationNode;

        BigDecimal exploitationRatesSum = BigDecimal.ZERO;
        BigDecimal bestExploitationRate = BigDecimal.ZERO;
//        Customer bestExploitationNode = null;

        for (RouteSegment rs : currentNode.getRouteSegmentsFromCustomer()) {
//            if (feasibleNodes.contains(rs.getDst())) {
//                double distance = rs.getDistance();
//                double ni = 1 / distance;
//                BigDecimal tau = rs.getAcsPheromoneLevel(); //pheromone level on segment
//                BigDecimal upNumber = tau.multiply(BigDecimal.valueOf(Math.pow(ni, beta))); //licznik
//                rs.setAcsUpNumber(upNumber);
//                if (upNumber.compareTo(bestUpNumber) > 0) {
//                    bestUpNumber = upNumber;
//                    bestExploitationNode = rs.getDst();
//                }
//                downNumber = downNumber.add(upNumber); //mianownik
//                if (downNumber.compareTo(BigDecimal.ZERO) == 0) {
//                    break;
//                }
//            }
            if (feasibleNodes.contains(rs.getDst())) {
                double distance = rs.getDistance();
                double ni = 1 / distance;
                BigDecimal tau = localPhermoneLevel.get(rs.getId());
                BigDecimal exploitationRate = tau.multiply(BigDecimal.valueOf(Math.pow(ni, beta))); //licznik
                exploitationRates.put(rs.getDst().getId(), exploitationRate);
                if (exploitationRate.compareTo(bestExploitationRate) > 0) {
                    bestExploitationRate = exploitationRate;
//                    bestExploitationNode = rs.getDst();
                }
                exploitationRatesSum = exploitationRatesSum.add(exploitationRate); //mianownik
            }
        }

//        for (RouteSegment rs : currentNode.getRouteSegmentsFromCustomer()) {
//            if (feasibleNodes.contains(rs.getDst())) {
//                BigDecimal probability = rs.getAcsUpNumber().divide(downNumber, BigDecimal.ROUND_HALF_UP); //obliczanie prawdopodobieństwa wyboru danego odcinka trasy
//                rs.getDst().setAcsChoiceProbability(probability);
//            }
//        }

        for (RouteSegment rs : currentNode.getRouteSegmentsFromCustomer()) {
            if (feasibleNodes.contains(rs.getDst())) {
                BigDecimal probability = exploitationRates.get(rs.getDst().getId()).divide(exploitationRatesSum, BigDecimal.ROUND_HALF_UP); //obliczanie prawdopodobieństwa wyboru danego odcinka trasy
                explorationProbabilities.put(rs.getDst().getId(), probability);
            }
        }

//        return bestExploitationNode;
    }

    private int findBestExploitationCustomerId() {
        Map.Entry<Integer, BigDecimal> maxEntry = null;
        for (Map.Entry<Integer, BigDecimal> entry : exploitationRates.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }
}
