package com.ctp.robocode;

import java.io.File;
import java.io.IOException;

import net.sf.robocode.io.FileUtil;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.context.annotation.ContainerScoped;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import robocode.control.RobocodeEngine;

public class RobocodeContainer implements DeployableContainer<RobocodeConfiguration> {

    private RobocodeConfiguration configuration;

    @Inject
    @ContainerScoped
    private InstanceProducer<RobocodeConfiguration> configurationInstance;

    @Inject
    @ContainerScoped
    private InstanceProducer<RobocodeEngine> engineInstance;

    private RobocodeEngine engine;

    @Override
    public Class<RobocodeConfiguration> getConfigurationClass() {
        return RobocodeConfiguration.class;
    }

    @Override
    public void setup(RobocodeConfiguration configuration) {
        this.configuration = configuration;
        this.configurationInstance.set(configuration);
    }

    @Override
    public void start() throws LifecycleException {
        System.setProperty("EXPERIMENTAL", configuration.isExperimental() + "");
        System.setProperty("TESTING", "true");
        System.setProperty("WORKINGDIRECTORY", configuration.getWorkingDirectory());
        engine = new RobocodeEngine();
        engineInstance.set(engine);
        setWorkingDirectory();
    }

    @Override
    public void stop() throws LifecycleException {
        File robotDb = new File(FileUtil.getCwd(), "robot.database");
        robotDb.delete();
        engine.close();
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return ProtocolDescription.DEFAULT;
    }

    @Override
    public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
        File archiveFile = new File(RobocodeEngine.getRobotsDir(), archive.getName());
        archive.as(ZipExporter.class).exportTo(archiveFile, true);
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

    private void setWorkingDirectory() {
        try {
            FileUtil.setCwd(new File("./" + configuration.getWorkingDirectory()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to set working directory", e);
        }
    }

}
