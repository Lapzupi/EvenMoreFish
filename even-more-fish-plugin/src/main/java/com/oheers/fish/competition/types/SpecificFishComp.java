package com.oheers.fish.competition.types;

import com.oheers.fish.competition.Comp;
import com.oheers.fish.competition.CompetitionType;

public class SpecificFishComp extends Comp {
    public SpecificFishComp(String compId) {
        super(compId);
    }

    @Override
    public CompetitionType getCompetitionType() {
        return CompetitionType.SPECIFIC_FISH;
    }

    @Override
    public void addToLeaderboard() {

    }
}
