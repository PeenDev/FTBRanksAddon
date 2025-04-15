package com.peendev.ftbranks;

import de.erdbeerbaerlp.dcintegration.common.addon.AddonConfigRegistry;
import de.erdbeerbaerlp.dcintegration.common.discordCommands.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.io.File;

public class RankCommand extends DiscordCommand {
    private final FTBRanksAddon addon;

    protected RankCommand(FTBRanksAddon addon) {
        super("ftbranks", FTBRanksAddon.cfg.commandDescription);
        this.addon = addon;

        SubcommandData addCmd = new SubcommandData("add", "Add a role-to-rank mapping")
            .addOption(OptionType.ROLE, "role", "Discord role to map", true)
            .addOption(OptionType.STRING, "rank", "FTB Rank name", true);
            
        SubcommandData removeCmd = new SubcommandData("remove", "Remove a role-to-rank mapping")
            .addOption(OptionType.ROLE, "role", "Discord role to remove", true);
            
        SubcommandData listCmd = new SubcommandData("list", "List all role-to-rank mappings");
        
        addSubcommands(addCmd, removeCmd, listCmd);
    }

    @Override
    public boolean adminOnly() {
        return FTBRanksAddon.cfg.adminOnly;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev, ReplyCallbackAction reply) {
        String subcommand = ev.getSubcommandName();
        
        switch (subcommand) {
            case "add":
                handleAdd(ev, reply);
                break;
            case "remove":
                handleRemove(ev, reply);
                break;
            case "list":
                handleList(ev, reply);
                break;
        }
    }

    private void handleAdd(SlashCommandInteractionEvent ev, ReplyCallbackAction reply) {
        String roleId = ev.getOption("role").getAsRole().getId();
        String rank = ev.getOption("rank").getAsString();
        
        FTBRanksAddon.cfg.roleToRankMappings.put(roleId, rank);
        File configFile = new File("DiscordIntegration-Data/addons/FTBRanks Discord Integration.toml");
        AddonConfigRegistry.saveConfig(FTBRanksAddon.cfg, configFile);
        
        reply.setContent("✅ Successfully mapped role to rank: " + rank).queue();
    }

    private void handleRemove(SlashCommandInteractionEvent ev, ReplyCallbackAction reply) {
        String roleId = ev.getOption("role").getAsRole().getId();
        
        if (FTBRanksAddon.cfg.roleToRankMappings.remove(roleId) != null) {
            File configFile = new File("DiscordIntegration-Data/addons/FTBRanks Discord Integration.toml");
            AddonConfigRegistry.saveConfig(FTBRanksAddon.cfg, configFile);
            reply.setContent("✅ Successfully removed role mapping").queue();
        } else {
            reply.setContent("❌ No mapping found for this role").queue();
        }
    }

    private void handleList(SlashCommandInteractionEvent ev, ReplyCallbackAction reply) {
        if (FTBRanksAddon.cfg.roleToRankMappings.isEmpty()) {
            reply.setContent("No role-to-rank mappings configured").queue();
            return;
        }

        StringBuilder sb = new StringBuilder("**Current Role-to-Rank Mappings:**\n");
        FTBRanksAddon.cfg.roleToRankMappings.forEach((roleId, rank) -> {
            sb.append("• <@&").append(roleId).append("> → ").append(rank).append("\n");
        });
        
        reply.setContent(sb.toString()).queue();
    }
} 