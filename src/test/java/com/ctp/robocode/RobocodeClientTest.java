package com.ctp.robocode;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import sample.Crazy;
import sample.Fire;

@RunWith(Arquillian.class)
public class RobocodeClientTest {

    @Deployment
    public static JavaArchive deployment() {
        return ShrinkWrap.create(JavaArchive.class, "robots.jar")
                .addClasses(
                        DummyRobot.class,
                        Crazy.class,
                        Fire.class)
                .addAsResource("com/ctp/robocode/DummyRobot.properties")
                .addAsResource("sample/Crazy.properties")
                .addAsResource("sample/Fire.properties");
    }

    @Test
    @BattleResults(robot = "sample.Fire")
    public void fire_should_win() throws Exception {
    }

    @Test
    @BattleResults(robot = "com.ctp.robocode.DummyRobot", expectedRank = 2)
    public void dummy_should_be_second() throws Exception {
    }

}
