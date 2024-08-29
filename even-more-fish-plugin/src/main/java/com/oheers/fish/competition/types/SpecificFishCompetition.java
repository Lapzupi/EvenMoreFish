package com.oheers.fish.competition.types;


import com.oheers.fish.competition.CompetitionType;

public class SpecificFishCompetition extends CompetitionNew {
    public SpecificFishCompetition(String name, int playersNeeded, CompetitionOptions options) {
        super(name, CompetitionType.SPECIFIC_FISH, playersNeeded, options);
    }

    @Override
    public void preStart(boolean adminStart) {
        super.preStart(adminStart);

        if (!chooseFish(name, adminStart)) {
            running = false;
        }
    }
}
