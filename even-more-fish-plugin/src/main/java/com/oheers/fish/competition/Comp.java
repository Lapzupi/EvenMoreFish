package com.oheers.fish.competition;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.EMFCompetitionEndEvent;
import com.oheers.fish.config.messages.ConfigMessage;
import com.oheers.fish.config.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//temp name until it's working
public abstract class Comp {
    private final String compId;
    public abstract CompetitionType getCompetitionType();

    public Comp(String compId) {
        this.compId = compId;
    }

    public abstract void addToLeaderboard();

}
