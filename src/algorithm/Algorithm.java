package algorithm;

/**
 * Created by Marcin on 2017-06-26.
 */
public abstract class Algorithm {

    private Problem problem;
    private String algorithmName;

    public Algorithm(Problem problem) {
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public abstract void runAlgorithm();
}
