package eu.ha3.presencefootsteps.util;

import java.util.Random;

import eu.ha3.presencefootsteps.sound.Options;

public class Period implements Options {

    public long min;
    public long max;

    public Period(long value) {
        this(value, value);
    }

    public Period(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public void copy(Period from) {
        min = from.min;
        max = from.max;
    }

    public void set(long value) {
        set(value, value);
    }

    public void set(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public float random(Random rand) {
        return MathUtil.randAB(rand, min, max);
    }

    public float on(float value) {
        return MathUtil.between(min, max, value);
    }

    @Override
    public boolean containsKey(String option) {
        return "delay_min".equals(option)
            || "delay_max".equals(option);
    }

    @Override
    public float get(String option) {
        return "delay_min".equals(option) ? min
             : "delay_max".equals(option) ? max
             : 0;
    }
}
