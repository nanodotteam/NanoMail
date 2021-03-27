package com.github.nanodot.nanomail;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class NanoMail extends JavaPlugin {

    // UUID of player with corresponding messages sent to that player
    Logger logger = this.getLogger();
    MailDataHandler mailDataHandler;
    ProfilesCache profilesCache;

    @Override
    public void onEnable() {
        logger.info("Starting up...");

        // Initialize MailDataHandler
        mailDataHandler = new MailDataHandler();

        // Registering plugin command
        PluginCommand nanoMailCommand = Bukkit.getPluginCommand("nanomail");
        if (nanoMailCommand == null) {
            logger.warning("NanoMail command is null! (Problem related to plugin.yml)");
            return;
        }
        nanoMailCommand.setExecutor(new NanoMailCommand(this));

        // Setup profiles caching
        profilesCache = new ProfilesCache();
        Bukkit.getPluginManager().registerEvents(profilesCache, this);
    }

    @Override
    public void onDisable() {
        // Save mails to disk
        mailDataHandler.saveMailsToDisk();
        profilesCache.saveCacheToDisk();
    }

    public void disableThis() {
        logger.warning("Disabling plugin (probably due to errors)");
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
