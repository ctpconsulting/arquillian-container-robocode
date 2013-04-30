package com.ctp.robocode;

import java.io.File;
import java.io.IOException;

import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;

public class RobocodeContainer extends BattleAdaptor implements DeployableContainer<RobocodeConfiguration> {

    private RobocodeConfiguration configuration;

    private RobocodeEngine engine;

    @Override
    public Class<RobocodeConfiguration> getConfigurationClass() {
        return RobocodeConfiguration.class;
    }

    @Override
    public void setup(RobocodeConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void start() throws LifecycleException {
        System.setProperty("EXPERIMENTAL", configuration.isExperimental() + "");
        System.setProperty("TESTING", "true");
        System.setProperty("WORKINGDIRECTORY", configuration.getWorkingDirectory());
        engine = new RobocodeEngine(new BattleAdaptor() {
            @Override
            public void onBattleMessage(BattleMessageEvent event) {
                Logger.realOut.println(event.getMessage());
            }

            @Override
            public void onBattleError(BattleErrorEvent event) {
                Logger.realErr.println(event.getError());
            }
        });
        engine.addBattleListener(this);
        setWorkingDirectory();
    }

    @Override
    public void stop() throws LifecycleException {
        engine.removeBattleListener(this);
        File robotDb = new File(FileUtil.getCwd(), "robot.database");
        robotDb.delete();
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return ProtocolDescription.DEFAULT;
    }

    @Override
    public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
        File archiveFile = new File(RobocodeEngine.getRobotsDir(), archive.getName());
        archive.as(ZipExporter.class).exportTo(archiveFile, true);

        RobotSpecification[] specs = engine.getLocalRepository();
        for (RobotSpecification spec : specs) {
            Logger.realOut.println("RobotSpecification: " + spec.getName());
        }
        engine.runBattle(new BattleSpecification(configuration.getNumRounds(), new BattlefieldSpecification(), specs),
                configuration.getInitialPositions(), true);
        return new ProtocolMetaData();
    }

    @Override
    public void undeploy(Archive<?> archive) throws DeploymentException {
        File archiveFile = new File(RobocodeEngine.getRobotsDir(), archive.getName());
        archiveFile.delete();
    }

    @Override
    public void deploy(Descriptor descriptor) throws DeploymentException {
    }

    @Override
    public void undeploy(Descriptor descriptor) throws DeploymentException {
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        if (configuration.isLogTurns()) {
            Logger.realOut.println("turn " + event.getTurnSnapshot().getTurn());
            for (IRobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
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
            System.out.println("Rank " + bot.getRank() + ": " + specs[i].getName());
        }
    }

    private void setWorkingDirectory() {
        try {
            FileUtil.setCwd(new File("./" + configuration.getWorkingDirectory()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to set working directory", e);
        }
    }

}
