package com.oheers.fish.competition.types;

import com.oheers.fish.competition.Comp;
import com.oheers.fish.competition.CompetitionType;

public class LargestFishComp extends Comp {

    public LargestFishComp(String compId) {
        super(compId);
    }

    @Override
    public CompetitionType getCompetitionType() {
        return CompetitionType.LARGEST_FISH;
    }

    @Override
    public void addToLeaderboard() {

    }

}
