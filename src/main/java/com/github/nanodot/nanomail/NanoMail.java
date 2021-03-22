package com.github.nanodot.nanomail;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public final class NanoMail extends JavaPlugin {

    // UUID of player with corresponding messages sent to that player
    HashMap<UUID, LinkedList<String>> mails = new HashMap<>();

    @Override
    public void onEnable() {
        // Temporary form of data storage, loading data
        File file = new File(this.getDataFolder().getAbsolutePath() + "/mails.txt");
        FileInputStream fs;
        ObjectInputStream in;
        try {
            fs = new FileInputStream(file);
            in = new ObjectInputStream(fs);
            Object read = in.readObject();
            if(read instanceof HashMap) {
                mails = (HashMap<UUID, LinkedList<String>>) read;
            }
            in.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Registering plugin command
        PluginCommand nanoMailCommand = Bukkit.getPluginCommand("nanomail");
        if (nanoMailCommand == null) {
            System.out.println("Command is null!");
            return;
        }
        nanoMailCommand.setExecutor(new NanoMailCommand(this));
    }

    @Override
    public void onDisable() {
        // Temporary form of data storage, saving data
        File file = new File(this.getDataFolder().getAbsolutePath() + "/mails.txt");
        FileOutputStream fs;
        try {
            fs = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                if(file.getParentFile().mkdirs()) {
                    System.out.println("Created parent file");
                }
                if(file.createNewFile()) {
                    System.out.println("Data file created");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(fs);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            out.writeObject(mails);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
