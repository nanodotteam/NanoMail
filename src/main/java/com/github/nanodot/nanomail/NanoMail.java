package com.github.nanodot.nanomail;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public final class NanoMail extends JavaPlugin {

    HashMap<UUID, LinkedList<String>> mails = new HashMap<>();

    @Override
    public void onEnable() {
        File file = new File("./mails");
        FileInputStream fs;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(fs);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            Object read = in.readObject();
            if(read instanceof HashMap) {
                mails = (HashMap<UUID, LinkedList<String>>) read;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PluginCommand nanoMailCommand = Bukkit.getPluginCommand("nanomail");
        if (nanoMailCommand == null) {
            return;
        }
        nanoMailCommand.setExecutor(new NanoMailCommand(this));
    }

    @Override
    public void onDisable() {
        File file = new File("./mails");
        FileOutputStream fs;
        try {
            fs = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
