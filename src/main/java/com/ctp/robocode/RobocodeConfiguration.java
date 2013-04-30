package com.ctp.robocode;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class RobocodeConfiguration implements ContainerConfiguration {

    private int numRounds = 1;
    private String initialPositions = null;
    private boolean experimental = true;
    private String workingDirectory = "target";
    private boolean logTurns = false;

    @Override
    public void validate() throws ConfigurationException {
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
    }

    public String getInitialPositions() {
        return initialPositions;
    }

    public void setInitialPositions(String initialPositions) {
        this.initialPositions = initialPositions;
    }

    public boolean isExperimental() {
        return experimental;
    }

    public void setExperimental(boolean experimental) {
        this.experimental = experimental;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public boolean isLogTurns() {
        return logTurns;
    }

    public void setLogTurns(boolean logTurns) {
        this.logTurns = logTurns;
    }

}
