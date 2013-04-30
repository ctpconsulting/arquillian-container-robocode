package com.ctp.robocode;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

public class DummyRobot extends AdvancedRobot {

    private static boolean incrementedBattles = false;

    @Override
    public String getName() {
        return "com.ctp.robocode.sittingDuck";
    }

    @Override
    public void run() {
        setBodyColor(Color.yellow);
        setGunColor(Color.yellow);

        int roundCount, battleCount;

        try {
            // Read file "count.dat" which contains 2 lines,
            // a round count, and a battle count
            try (BufferedReader reader = new BufferedReader(new FileReader(getDataFile("count.dat")))) {
                // Try to get the counts
                roundCount = Integer.parseInt(reader.readLine());
                battleCount = Integer.parseInt(reader.readLine());

            }
        } catch (IOException e) {
            // Something went wrong reading the file, reset to 0.
            roundCount = 0;
            battleCount = 0;
        } catch (NumberFormatException e) {
            // Something went wrong converting to ints, reset to 0
            roundCount = 0;
            battleCount = 0;
        }

        // Increment the # of rounds
        roundCount++;

        // If we haven't incremented # of battles already,
        // Note: Because robots are only instantiated once per battle, member variables remain valid throughout it.
        if (!incrementedBattles) {
            // Increment # of battles
            battleCount++;
            incrementedBattles = true;
        }

        try (PrintStream w = new PrintStream(new RobocodeFileOutputStream(getDataFile("count.dat")))) {
            w.println(roundCount);
            w.println(battleCount);

            // PrintStreams don't throw IOExceptions during prints, they simply set a flag.... so check it here.
            if (w.checkError()) {
                out.println("I could not write the count!");
            }
        } catch (IOException e) {
            out.println("IOException trying to write: ");
            e.printStackTrace(out);
        }
        out.println("I have been a sitting duck for " + roundCount + " rounds, in " + battleCount + " battles.");
    }

}
