package com.oheers.fish.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.config.messages.ConfigMessage;
import com.oheers.fish.config.messages.Message;
import com.oheers.fish.config.messages.PrefixType;
import com.oheers.fish.permissions.AdminPerms;
import com.oheers.fish.permissions.UserPerms;
import com.oheers.fish.selling.SellGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


@CommandAlias("%main")
public class EMFCommand extends BaseCommand {

    @Subcommand("next")
    @Description("%desc_general_next")
    public void onNext(final CommandSender sender) {
        Message message = Competition.getNextCompetitionMessage();
        message.usePrefix(PrefixType.DEFAULT);
        message.broadcast(sender, true);
    }

    @Subcommand("toggle")
    @Description("%desc_general_toggle")
    @CommandPermission(UserPerms.TOGGLE)
    public void onToggle(final Player player) {
        if (EvenMoreFish.getInstance().getDisabledPlayers().contains(player.getUniqueId())) {
            EvenMoreFish.getInstance().getDisabledPlayers().remove(player.getUniqueId());
            new Message(ConfigMessage.TOGGLE_ON).broadcast(player, false);
            return;
        }

        EvenMoreFish.getInstance().getDisabledPlayers().add(player.getUniqueId());
        new Message(ConfigMessage.TOGGLE_OFF).broadcast(player, false);
    }



    @Default
    @HelpCommand
    public void onHelp(final CommandHelp help, final CommandSender sender) {
        new Message(ConfigMessage.HELP_GENERAL_TITLE).broadcast(sender, false);
        help.showHelp();
    }

    @Subcommand("top")
    @CommandPermission(UserPerms.TOP)
    @Description("%desc_general_top")
    public void onTop(final CommandSender sender) {
        if (!Competition.isActive()) {
            new Message(ConfigMessage.NO_COMPETITION_RUNNING).broadcast(sender, true);
            return;
        }

        if (sender instanceof Player) {
            EvenMoreFish.getInstance().getActiveCompetition().sendPlayerLeaderboard((Player) sender);
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            EvenMoreFish.getInstance().getActiveCompetition().sendConsoleLeaderboard((ConsoleCommandSender) sender);
        }
    }

    @Subcommand("shop")
    @CommandPermission(UserPerms.SHOP)
    @Description("%desc_general_shop")
    public void onShop(final CommandSender sender, @Optional final OnlinePlayer onlinePlayer) {
        if (MainConfig.getInstance().isEconomyDisabled()) {
            new Message(ConfigMessage.ECONOMY_DISABLED).broadcast(sender, false);
            return;
        }

        if (onlinePlayer == null) {
            if (!(sender instanceof Player)) {
                new Message("&cYou must specify a player when running from console.").broadcast(sender, false);
                return;
            }
            new SellGUI((Player) sender).open();
            return;
        }

        if (sender.hasPermission(AdminPerms.ADMIN)) {
            new SellGUI(onlinePlayer.player).open();
            Message message = new Message(ConfigMessage.ADMIN_OPEN_FISH_SHOP);
            message.setPlayer(onlinePlayer.player.getName());
            message.broadcast(sender, true);
        }
    }

    @Subcommand("sellall")
    @CommandPermission(UserPerms.SELL_ALL)
    @Description("%desc_general_sellall")
    public void onSellAll(final Player sender) {
        if (MainConfig.getInstance().isEconomyDisabled()) {
            new Message(ConfigMessage.ECONOMY_DISABLED).broadcast(sender, false);
            return;
        }


        SellGUI gui = new SellGUI(sender);
        gui.sell(true);
    }


}
