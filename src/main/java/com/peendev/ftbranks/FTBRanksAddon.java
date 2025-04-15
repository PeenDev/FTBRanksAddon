package com.peendev.ftbranks;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.addon.DiscordIntegrationAddon;
import de.erdbeerbaerlp.dcintegration.common.addon.AddonConfigRegistry;
import de.erdbeerbaerlp.dcintegration.common.storage.CommandRegistry;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.LinkManager;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.PlayerLink;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.UUID;

public class FTBRanksAddon implements DiscordIntegrationAddon, EventListener {
    static FTBRanksConfig cfg;
    DiscordIntegration discord;

    @Override
    public void load(DiscordIntegration dc) {
        DiscordIntegration.LOGGER.info("FTBRanks Addon loaded");
        cfg = AddonConfigRegistry.loadConfig(FTBRanksConfig.class, this);
        discord = dc;
        DiscordIntegration.LOGGER.info("FTBRanks Addon configuration loaded");
        if(dc.getJDA() != null) {
            dc.getJDA().addEventListener(this);
            CommandRegistry.registerCommand(new RankCommand(this));
            DiscordIntegration.LOGGER.info("FTBRanks Discord Bot Commands Registered");
        }
    }

    @Override
    public void reload() {
        DiscordIntegration.LOGGER.info("FTBRanks Addon reloading");
        cfg = AddonConfigRegistry.loadConfig(cfg, this);
    }

    @Override
    public void unload(DiscordIntegration dc) {
        DiscordIntegration.LOGGER.info("FTBRanks Addon unloaded");
        if(dc.getJDA() != null) {
            dc.getJDA().removeEventListener(this);
        }
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof GuildMemberRoleAddEvent) {
            handleRoleAdd((GuildMemberRoleAddEvent) event);
        } else if (event instanceof GuildMemberRoleRemoveEvent) {
            handleRoleRemove((GuildMemberRoleRemoveEvent) event);
        }
    }

    private void handleRoleAdd(GuildMemberRoleAddEvent event) {
        for (Role role : event.getRoles()) {
            //FTBRanks expects lower case strings for the rank names.
            //if a rank isn't found it will fail silently.
            String rank = cfg.roleToRankMappings.get(role.getId()).toLowerCase();
            if (rank != null) {
                if(!LinkManager.isDiscordUserLinked(event.getMember().getId()))
                {
                    DiscordIntegration.LOGGER.info("Discord and Minecraft user is not linked");
                    return;
                }
                PlayerLink playerLink = LinkManager.getLink(event.getMember().getId(), null);
                String playerName = discord.getServerInterface().getNameFromUUID(UUID.fromString(playerLink.mcPlayerUUID));
                String command = String.format("ftbranks add %s %s", playerName, rank);
                discord.getServerInterface().runMCCommand(command);
            }
        }
    }

    private void handleRoleRemove(GuildMemberRoleRemoveEvent event) {
        for (Role role : event.getRoles()) {
            String rank = cfg.roleToRankMappings.get(role.getId()).toLowerCase();
            if (rank != null) {
                if(!LinkManager.isDiscordUserLinked(event.getMember().getId()))
                {
                    DiscordIntegration.LOGGER.info("Discord and Minecraft user is not linked");
                    return;
                }
                PlayerLink playerLink = LinkManager.getLink(event.getMember().getId(), null);
                String playerName = discord.getServerInterface().getNameFromUUID(UUID.fromString(playerLink.mcPlayerUUID));
                String command = String.format("ftbranks remove %s %s", playerName, rank);
                discord.getServerInterface().runMCCommand(command);
            }
        }
    }
} 