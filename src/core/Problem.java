package core;

/**
 * Klasa reprezentujaca problem marszrutyzacji do rozwiazania
 */
public class Problem {

    /**
     * Numer ID
     */
    private static int ID = 0;

    /**
     * Numer ID problemu
     */
    private int problemID;

    /**
     * Magazyn
     */
    private Customer depot;

    /**
     * Maksymalna masa ladunku jaka mozna zaladowac do pojazdu
     */
    private double weightLimitPerVehicle;

    /**
     * Maksymalna objetosc ladunku jaka mozna zaladowac do pojazdu
     */
    private double sizeLimitPerVehicle;

    /**
     * Tworzy obiekt klasy
     * @param weightLimitPerVehicle Maksymalna masa ladunku jaka mozna zaladowac do pojazdu
     * @param sizeLimitPerVehicle Maksymalna objetosc ladunku jaka mozna zaladowac do pojazdu
     */
    public Problem(double weightLimitPerVehicle, double sizeLimitPerVehicle) {
        ID++;
        this.problemID = ID;
        this.depot = Database.getCustomerList().get(0);
        this.weightLimitPerVehicle = weightLimitPerVehicle;
        this.sizeLimitPerVehicle = sizeLimitPerVehicle;
    }

    public int getProblemID() {
        return problemID;
    }

    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }

    public Customer getDepot() {
        return depot;
    }

    public void setDepot(Customer depot) {
        this.depot = depot;
    }

    public double getWeightLimitPerVehicle() {
        return weightLimitPerVehicle;
    }

    public void setWeightLimitPerVehicle(double weightLimitPerVehicle) {
        this.weightLimitPerVehicle = weightLimitPerVehicle;
    }

    public double getSizeLimitPerVehicle() {
        return sizeLimitPerVehicle;
    }

    public void setSizeLimitPerVehicle(double sizeLimitPerVehicle) {
        this.sizeLimitPerVehicle = sizeLimitPerVehicle;
    }
}
