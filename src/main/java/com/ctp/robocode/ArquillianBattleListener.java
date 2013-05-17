package com.ctp.robocode;


import net.sf.robocode.io.Logger;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

public class ArquillianBattleListener {

    @Inject
    private Instance<RobocodeEngine> engineInstance;

    @Inject
    private Instance<RobocodeConfiguration> configurationInstance;

    @Inject
    @TestScoped
    private InstanceProducer<BattleListener> battleListenerInstance;

    public void startBattle(@Observes Before before) {

        createBattleListener();

        RobotSpecification[] specs = engineInstance.get().getLocalRepository();
        for (RobotSpecification spec : specs) {
            Logger.realOut.println("RobotSpecification: " + spec.getName());
        }

        RobocodeConfiguration configuration = configurationInstance.get();
        engineInstance.get().runBattle(
                new BattleSpecification(configuration.getNumRounds(),
                        new BattlefieldSpecification(), specs),
                configuration.getInitialPositions(), true);
    }

    public void collectResults(@Observes After after) {
        BattleListener battleListener = battleListenerInstance.get();
        engineInstance.get().removeBattleListener(battleListener);
        BattleResults battleResults = after.getTestMethod().getAnnotation(BattleResults.class);
        if (battleResults != null) {
            String name = battleResults.robot();
            if (battleResults.expectedRank() == 1 && !battleListener.isWinner(name)) {
                throw new AssertionError("Expected " + name +" to win, but real winner was " + battleListener.getWinner());
            } else if (battleResults.expectedRank() != battleListener.getRank(name)) {
                throw new AssertionError("Expected " + name +" to be on " + battleResults.expectedRank() +
                        ", but real rank was " + battleListener.getRank(name));
            }
        } else {
            throw new AssertionError("At least expected winner name should be specified. Check @BattleResults annotation.");
        }
    }

    private void createBattleListener() {
        BattleListener battleListener = new BattleListener(engineInstance.get(), configurationInstance.get());
        battleListenerInstance.set(battleListener);
        engineInstance.get().addBattleListener(battleListener);
    }
}
