package com.oheers.fish.advents;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.oheers.fish.config.messages.Message;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("emfadvent")
public class AdventCommand extends BaseCommand {

    private final AdventsMenu adventsMenu;
    private final AdventManager adventManager = AdventManager.getInstance();

    public AdventCommand() {
        this.adventsMenu = new AdventsMenu();
    }

    //Default
    @Default
    public void onGui(final Player player) {
        if (adventManager.getActiveAdvents().isEmpty()) {
            Message message = new Message("&cNo advents are currently running!");
            message.broadcast(player, true, true);
            return;
        }

        if (adventManager.getActiveAdvents().size() == 1) {
            adventManager.getActiveAdvents().get(0).adventGui().open(player);
            return;
        }

        // open advent menu
    }

    @Subcommand("list")
    public static class AdventListSubCommand {
        private final AdventManager adventManager = AdventManager.getInstance();
        public void onList(final CommandSender sender) {
            Message message = new Message(StringUtils.join(adventManager.getAdvents().stream().map(Advent::displayName).collect(Collectors.toList()), ","));
            message.broadcast(sender, true, true);
        }

        public void onListActive(final CommandSender sender) {
            Message message = new Message(StringUtils.join(adventManager.getActiveAdvents().stream().map(Advent::displayName).collect(Collectors.toList()), ","));
            message.broadcast(sender, true, true);
        }

        public void onListNotActive(final CommandSender sender) {
            Message message = new Message(StringUtils.join(adventManager.getNotActiveAdvents().stream().map(Advent::displayName).collect(Collectors.toList()), ","));
            message.broadcast(sender, true, true);
        }
    }
}
