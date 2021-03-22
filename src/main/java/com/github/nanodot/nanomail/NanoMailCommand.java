package com.github.nanodot.nanomail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.UUID;

public class NanoMailCommand implements CommandExecutor {

    private final String helpPage = "Help page W.I.P.";
    private NanoMail main;

    public NanoMailCommand(NanoMail main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /nanomail - > help page
        // /nanomail send <receiver> <message> - > send a mail with message to receiver
        // /nanomail read - > shows all unread emails

        // No argument is provided
        if(args.length == 0) {
            sender.sendMessage(helpPage);
        } else if (args.length >= 1 && args[0].equals("send")) { // Mail send handler
            sender.sendMessage("SENDING");
            String receiverName = args[1];
            if(receiverName == null) {
                sender.sendMessage("Receiver is null");
                return true;
            }
            Player receiver = Bukkit.getPlayer(receiverName);
            if(receiver == null) {
                sender.sendMessage("Receiver does not exist");
                return true;
            }
            UUID receiverID = receiver.getUniqueId();
            String message = args[2];
            if(message == null) {
                sender.sendMessage("Message is null");
                return true;
            }
            LinkedList<String> receiverMessages = main.mails.get(receiverID);
            if(receiverMessages == null) {
                receiverMessages = new LinkedList<>();
            }
            String senderName = sender.getName();
            receiverMessages.add(senderName + ": " + message);
            main.mails.put(receiverID, receiverMessages);
            sender.sendMessage("Message sent!");
        } else if (args.length >= 1 && args[0].equals("read")) { // Mail read handler
            sender.sendMessage("READING");
            Player player;
            if(sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("Player only command");
                return true;
            }
            UUID senderID = player.getUniqueId();
            LinkedList<String> senderMails = main.mails.get(senderID);
            if(senderMails == null) {
                senderMails = new LinkedList<>();
            }
            StringBuilder response = new StringBuilder();
            response.append("Your mails:\n");
            for(String mail : senderMails) {
                response.append(mail + "\n");
            }
            sender.sendMessage(response.toString());
        }
        return false;
    }
}
