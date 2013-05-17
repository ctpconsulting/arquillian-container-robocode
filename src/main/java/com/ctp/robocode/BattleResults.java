package com.ctp.robocode;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
public @interface BattleResults {

    /**
     * If expectedRank not specified, defined robot name is expected to be winner of the battle
     */
    String robot();

    int expectedRank() default 1;

}
