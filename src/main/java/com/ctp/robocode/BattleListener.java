package com.ctp.robocode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.robocode.io.Logger;
import robocode.BattleResults;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;

final class BattleListener extends BattleAdaptor {

    private final RobocodeConfiguration configuration;

    private final RobocodeEngine engine;

    private final Map<String, Integer> results = new HashMap<>();

    public BattleListener(RobocodeEngine engine, RobocodeConfiguration configuration) {
        this.engine = engine;
        this.configuration = configuration;
    }

    public boolean isWinner(String robotName) {
        return 1 == getRank(robotName);
    }

    @Override
    public void onBattleMessage(BattleMessageEvent event) {
        Logger.realOut.println(event.getMessage());
    }

    @Override
    public void onBattleError(BattleErrorEvent event) {
        Logger.realErr.println(event.getError());
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        if (configuration.isLogTurns()) {
            Logger.realOut.println("turn "
                    + event.getTurnSnapshot().getTurn());
            for (IRobotSnapshot robot : event.getTurnSnapshot()
                    .getRobots()) {
                Logger.realOut.print(robot.getVeryShortName());
                Logger.realOut.print(" X:");
                Logger.realOut.print(robot.getX());
                Logger.realOut.print(" Y:");
                Logger.realOut.print(robot.getY());
                Logger.realOut.print(" V:");
                Logger.realOut.print(robot.getVelocity());
                Logger.realOut.println();
                Logger.realOut.print(robot.getOutputStreamSnapshot());
            }
        }
    }

    @Override
    public void onBattleCompleted(BattleCompletedEvent event) {
        BattleResults[] results = event.getIndexedResults();
        RobotSpecification[] specs = engine.getLocalRepository();
        for (int i = 0; i < results.length; i++) {
            BattleResults bot = results[i];
            this.results .put(bot.getTeamLeaderName(), bot.getRank());
            Logger.realOut.println("Rank " + bot.getRank() + ": "
                    + bot.getTeamLeaderName() + " | "
                    + specs[i].getName());
        }
    }

    public String getWinner() {
        for (Entry<String, Integer> entry : results.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int getRank(String robotName) {
        Integer robotResult = results.get(robotName);
        if (robotResult == null) {
            throw new IllegalStateException("Unknown Robot name " + robotName);
        }

        return robotResult;
    }
}