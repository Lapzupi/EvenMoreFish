package com.oheers.fish.competition.types;


import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.competition.CompetitionType;
import com.oheers.fish.config.messages.ConfigMessage;
import com.oheers.fish.config.messages.Message;
import org.bukkit.event.Listener;

public abstract class CompetitionNew implements Listener {
    private final String name;
    private final CompetitionType type;
    private final int playersNeeded;
    private final CompetitionOptions options;
    protected boolean running;

    public CompetitionNew(String name, CompetitionType type, int playersNeeded, CompetitionOptions options) {
        this.name = name;
        this.type = type;
        this.playersNeeded = playersNeeded;
        this.options = options;
    }

    public void begin(boolean adminStart) {
        preStart(adminStart);
        if (!running) {
            return;
        }



    }

    private boolean enoughOnlinePlayers() {
        return EvenMoreFish.getInstance().getOnlinePlayersExcludingVanish().size() < playersNeeded;
    }

    /**
     * Here we should run checks before the competition actually starts.
     */
    public void preStart(boolean adminStart) {
        if (!adminStart && enoughOnlinePlayers()) {
            new Message(ConfigMessage.NOT_ENOUGH_PLAYERS).broadcast(true);
            running = false;
            return;
        }
        running = true;
    }


    /**
     * This runs after start()
     */
    public abstract void afterStart();

    /**
     * This runs after end()
     */
    public abstract void afterEnd();
}
