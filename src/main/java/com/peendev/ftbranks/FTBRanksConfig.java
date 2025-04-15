package com.peendev.ftbranks;

import dcshadow.com.moandjiezana.toml.TomlComment;
import java.util.HashMap;
import java.util.Map;

public class FTBRanksConfig {
    @TomlComment("Enable or disable the FTBRanks Discord integration")
    public boolean enabled = true;

    @TomlComment("Message format for added ranks")
    public String rankAddedMessage = "**{player}** has been promoted to **{rank}**!";

    @TomlComment("Message format for rank removal")
    public String rankRemovedMessage = "**{player}** has been demoted from **{rank}**!";

    @TomlComment("Command description for the rank management command")
    public String commandDescription = "Manage role-to-rank mappings";

    @TomlComment("Should this command only be available to admin roles?")
    public boolean adminOnly = true;

    @TomlComment("Map of Discord role IDs to FTB Ranks")
    public Map<String, String> roleToRankMappings = new HashMap<>();
} 