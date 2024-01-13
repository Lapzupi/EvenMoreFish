package com.oheers.fish.competition;

public interface CompetitionStages {


    /**
     * Run when the competition is starting
     */
    void onStarted();

    /**
     * The running process
     */
    void onRunning();

    /**
     * Run as the competition is ending.
     */
    void onEnding();

    /**
     * Run after the competition has ended.
     */
    void onEnded();
}
