package com.vrp.bajmer.algorithm;

import com.vrp.bajmer.core.Problem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Marcin on 2017-06-26.
 */
public class Third_Algorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(Third_Algorithm.class);

    public Third_Algorithm(Problem problem) {
        super(problem, "aaa");
    }

    @Override
    public void runAlgorithm() {
        logger.info("Running the Third com.vrp.bajmer.algorithm...");

    }

    @Override
    public void saveSolution() {

    }
}
