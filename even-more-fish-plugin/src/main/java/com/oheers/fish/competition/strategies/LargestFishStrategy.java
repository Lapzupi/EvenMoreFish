package com.oheers.fish.competition.strategies;


import com.oheers.fish.competition.Competition;
import com.oheers.fish.competition.CompetitionEntry;
import com.oheers.fish.competition.CompetitionStrategy;
import com.oheers.fish.config.messages.ConfigMessage;
import com.oheers.fish.config.messages.Message;
import com.oheers.fish.fishing.items.Fish;

public class LargestFishStrategy implements CompetitionStrategy {
    @Override
    public boolean begin(Competition competition) {
        return true;
    }

    @Override
    public void applyLeaderboard() {

    }

    @Override
    public Message getSingleConsoleLeaderboardMessage(Message message, CompetitionEntry entry) {
        Fish fish = entry.getFish();
        message.setRarityColour(fish.getRarity().getColour());
        message.setLength(Float.toString(entry.getValue()));
        message.setRarity(fish.getRarity().getDisplayName());
        message.setFishCaught(fish.getDisplayName());
        message.setMessage(ConfigMessage.LEADERBOARD_LARGEST_FISH);
        return message;
    }

    @Override
    public void sendPlayerLeaderboard() {

    }
}
