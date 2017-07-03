package algorithm;

import project.Route;

import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Solution {
    private int solutionID;
    private String usedAlgorithm;
    private ArrayList<Route> listOfSolutions;

    public Solution(int problemID, String usedAlgorithm) {
        this.solutionID = problemID;
        this.usedAlgorithm = usedAlgorithm;
        this.listOfSolutions = new ArrayList<Route>();
    }

    public int getSolutionID() {
        return solutionID;
    }

    public void setSolutionID(int solutionID) {
        this.solutionID = solutionID;
    }

    public ArrayList<Route> getListOfSolutions() {
        return listOfSolutions;
    }

    public void setListOfSolutions(ArrayList<Route> listOfSolutions) {
        this.listOfSolutions = listOfSolutions;
    }

    public String getUsedAlgorithm() {
        return usedAlgorithm;
    }

    public void setUsedAlgorithm(String usedAlgorithm) {
        this.usedAlgorithm = usedAlgorithm;
    }
}
