package com.oheers.fish.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.ImmutableList;
import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.competition.CompetitionType;
import com.oheers.fish.fishing.FishingProcessor;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.permissions.AdminPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("emfdebug")
@CommandPermission(AdminPerms.DEV_DEBUG)
public class EMFDebugCommand extends BaseCommand {
    private final ImmutableList<UUID> realPlayers = ImmutableList.of(
            UUID.fromString("31503783-760f-45dd-a809-331080747da6"),
            UUID.fromString("502be7e1-3ac0-4af3-bfa2-b2d8dfd959ab"),
            UUID.fromString("1fa14861-a8d3-4b42-a8ff-2028992f09b2"),
            UUID.fromString("4a5bac9e-dab6-400c-8483-88df48d3ae68"),
            UUID.fromString("30ba4700-87fc-43d0-b6e9-96c4e21078ed")
    );

    private final World world = Bukkit.getWorlds().get(0);
    private final ImmutableList<Location> fakeLocations = ImmutableList.of(
            new Location(world, 0D, 0D, 0D),
            new Location(world, 10D, 0D, 10D),
            new Location(world, 20D, 0D, 20D),
            new Location(world, 30D, 0D, 30D),
            new Location(world, 40D, 0D, 40D)
    );

    @Subcommand("fakecomp")
    @Description("Run a fake competition, with a preset(5) amount of players. With random winners. Players need to be online.")
    public void onFakeComp(final CommandSender sender, final CompetitionType type, final int durationInSeconds) {
        final String compId = "FAKE_COMP";

        Competition comp = new Competition(durationInSeconds, type);

        comp.setCompetitionName(compId);
        comp.setAdminStarted(true);
        comp.initRewards(null, true);
        comp.initBar(null);
        comp.initGetNumbersNeeded(null);
        comp.initStartSound(null);

        EvenMoreFish.active = comp;
        comp.begin(true);


        getFish(comp);
    }

    /*
    Gives every online player a fish every 20 seconds
     */
    public void getFish(final Competition comp) {
        EvenMoreFish.getScheduler().runTaskTimer(
                () -> {
                    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        final Fish fish = FishingProcessor.chooseNonBaitFish(onlinePlayer, chooseRandomLocation());

                        comp.applyToLeaderboard(fish, onlinePlayer);
                        EvenMoreFish.debug(fish.toString());
                    }

                }, 5L, 20L * 20
        );
    }

    private Location chooseRandomLocation() {
        int random = EvenMoreFish.getInstance().getRandom().nextInt(fakeLocations.size() - 1);
        return fakeLocations.get(random);
    }

}
