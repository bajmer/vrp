package algorithm;

import project.Route;

import java.util.ArrayList;

/**
 * Created by mbala on 03.07.17.
 */
public class Solution {
    private int solutionID;
    private String usedAlgorithm;
    private ArrayList<Route> listOfRoutes;

    public Solution(int problemID, String usedAlgorithm) {
        this.solutionID = problemID;
        this.usedAlgorithm = usedAlgorithm;
        this.listOfRoutes = new ArrayList<Route>();
    }

    public int getSolutionID() {
        return solutionID;
    }

    public void setSolutionID(int solutionID) {
        this.solutionID = solutionID;
    }

    public ArrayList<Route> getListOfRoutes() {
        return listOfRoutes;
    }

    public void setListOfRoutes(ArrayList<Route> listOfRoutes) {
        this.listOfRoutes = listOfRoutes;
    }

    public String getUsedAlgorithm() {
        return usedAlgorithm;
    }

    public void setUsedAlgorithm(String usedAlgorithm) {
        this.usedAlgorithm = usedAlgorithm;
    }
}
