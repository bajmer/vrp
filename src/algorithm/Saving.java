package algorithm;

import project.Customer;

/**
 * Created by Marcin on 2017-06-25.
 */
public class Saving {

    double saving;
    private Customer first;
    private Customer second;

    public Saving(Customer first, Customer second, double saving) {
        this.first = first;
        this.second = second;
        this.saving = saving;
    }

    public Customer getFirst() {
        return first;
    }

    public void setFirst(Customer first) {
        this.first = first;
    }

    public Customer getSecond() {
        return second;
    }

    public void setSecond(Customer second) {
        this.second = second;
    }

    public double getSaving() {
        return saving;
    }

    public void setSaving(double saving) {
        this.saving = saving;
    }
}
