package com.proch.practicehub;

/**
 * Keeps track of the the 12 chromatic notes and their respective frequencies.
 */
public enum Note {
    A(0),
    Bb(1),
    B(2),
    C(3),
    Db(4),
    D(5),
    Eb(6),
    E(7),
    F(8),
    Gb(-3),
    G(-2),
    Ab(-1);

    private final double frequency;

    private Note(int halfStepsAwayFromA) {
        this.frequency = 440.0 * Math.pow(2.0, halfStepsAwayFromA / 12.0);
    }

    public double getFrequency() {
        return frequency;
    }

    /**
     * Returns the frequency of the note which is an perfect pythagorean fifth above (i.e. 3:2 ratio)
     *
     * @return frequency in Hz
     */
    public double getFrequencyFifthAbove() {
        return frequency * 1.5;
    }
}
