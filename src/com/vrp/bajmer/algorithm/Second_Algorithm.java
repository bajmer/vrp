package com.vrp.bajmer.algorithm;

import com.vrp.bajmer.core.Problem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Marcin on 2017-06-26.
 */
public class Second_Algorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(Second_Algorithm.class);

    public Second_Algorithm(Problem problem) {
        super(problem);
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Second com.vrp.bajmer.algorithm...");
    }

    @Override
    public void saveSolution() {

    }
}
