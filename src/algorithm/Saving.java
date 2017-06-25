package algorithm;

import project.Customer;

/**
 * Created by Marcin on 2017-06-25.
 */
public class Saving {

    double saving;
    private Customer src;
    private Customer dst;

    public Saving(Customer src, Customer dst, double saving) {
        this.src = src;
        this.dst = dst;
        this.saving = saving;
    }

    public Customer getSrc() {
        return src;
    }

    public void setSrc(Customer src) {
        this.src = src;
    }

    public Customer getDst() {
        return dst;
    }

    public void setDst(Customer dst) {
        this.dst = dst;
    }

    public double getSaving() {
        return saving;
    }

    public void setSaving(double saving) {
        this.saving = saving;
    }
}
