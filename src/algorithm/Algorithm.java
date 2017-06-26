package algorithm;

/**
 * Created by Marcin on 2017-06-26.
 */
public abstract class Algorithm {

    private Problem problem;

    public Algorithm(Problem problem) {
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public abstract void runAlgorithm();
}
