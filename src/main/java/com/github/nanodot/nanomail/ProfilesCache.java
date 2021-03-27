package com.github.nanodot.nanomail;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ProfilesCache implements Listener {
    private final NanoMail main = NanoMail.getPlugin(NanoMail.class);

    HashMap<String, UUID> nameUUIDCache = new HashMap<>();
    HashMap<UUID, String> UUIDNameCache = new HashMap<>();

    File profilesCacheFile = new File(main.getDataFolder().getAbsolutePath(), "playerCache.yml");
    private final Logger logger = main.logger;
    private final Yaml yaml = new Yaml();

    public ProfilesCache() {
        // TODO: Load cache data from a file to HashMaps
        if (!profilesCacheFile.exists()) {
            // No file, nothing to load
            return;
        }
        FileInputStream profilesCacheInputStream;
        try {
            profilesCacheInputStream = new FileInputStream(profilesCacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.warning("Cache file not found (strange)");
            return;
        }
        HashMap<String, Object> loadedProfiles = yaml.load(profilesCacheInputStream);
        for(Map.Entry<String, Object> profile : loadedProfiles.entrySet()) {
            String playerName = profile.getKey();
            UUID playerUUID = (UUID) profile.getValue();

            nameUUIDCache.put(playerName, playerUUID);
            UUIDNameCache.put(playerUUID, playerName);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String playerName = e.getPlayer().getName();
        UUID playerUUID = e.getPlayer().getUniqueId();

        // Using .put() so old values will get replaced
        nameUUIDCache.put(playerName, playerUUID);
        UUIDNameCache.put(playerUUID, playerName);
    }

    public void saveCacheToDisk() {
        // TODO: Save cache data to disk
        try {
            if(main.getDataFolder().mkdirs()) {
                logger.info("Successfully created plugin data folder");
            } else {
                logger.info("Nice! Plugin data folder already exists");
            }
        } catch(SecurityException e) {
            // TODO: Change responsibility of disabling to the main class just by throwing Exception
            e.printStackTrace();
            logger.warning("Error creating plugin data folder (SecurityException)");
            main.disableThis();
            return;
        }

        try {
            if(profilesCacheFile.createNewFile()) {
                logger.info("Created new cache file");
            } else {
                logger.info("Awesome, cache file already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Error creating cache file");
            main.disableThis();
            return;
        }

        PrintWriter printWriter;

        try {
            printWriter = new PrintWriter(profilesCacheFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            logger.warning("Can't create PrinterWriter for profilesCacheFile");
            return;
        }

        yaml.dump(nameUUIDCache, printWriter);
        logger.info("Successfully saved cache data");
    }
}
