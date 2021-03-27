package com.github.nanodot.nanomail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NanoMailCommand implements CommandExecutor {

    private final NanoMail main;

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
            // TODO: Create proper help page
            String helpPage = "Help page W.I.P.";
            sender.sendMessage(helpPage);
        } else if (args.length >= 3 && args[0].equals("send")) { // Mail send handler
            sender.sendMessage("SENDING");
            String receiverName = args[1];

            if(receiverName == null) {
                sender.sendMessage("Receiver is null");
                return true;
            }

            Player receiver = Bukkit.getPlayer(receiverName);
            UUID receiverUUID;

            if(receiver != null) {
                receiverUUID = receiver.getUniqueId();
            } else {
                sender.sendMessage("Receiver is not online, trying to get UUID from cache");
                receiverUUID = main.profilesCache.nameUUIDCache.get(receiverName);
                if(receiverUUID == null) {
                    sender.sendMessage("That player has never played on this server (or there is some naughty bug)");
                    return true;
                }
            }

            String message = args[2];
            if(message == null) {
                sender.sendMessage("Message is null");
                return true;
            }
            Mail newMail = new Mail();
            // TODO: Implement mail ID system
            newMail.setMailID(0);
            UUID senderUUID = UUID.nameUUIDFromBytes("CONSOLE".getBytes(StandardCharsets.UTF_8));
            if(sender instanceof Player) {
                Player player = (Player) sender;
                senderUUID = player.getUniqueId();
            }
            newMail.setAuthorUUID(senderUUID);
            newMail.setContent(message);
            main.mailDataHandler.toPlayerAddMail(receiverUUID, newMail);
            sender.sendMessage("Message sent!");
        } else if (args[0].equals("read")) { // Mail read handler
            sender.sendMessage("READING");
            Player player;
            if(sender instanceof Player) {
                player = (Player) sender;
            } else {
                sender.sendMessage("Player only command");
                return true;
            }
            UUID senderID = player.getUniqueId();
            Mail[] senderMails = main.mailDataHandler.getPlayerMails(senderID);
            StringBuilder response = new StringBuilder();
            response.append("Your mails:\n");

            for(Mail mail : senderMails) {
                response.append(mail.toString()).append("\n");
            }

            sender.sendMessage(response.toString());
        }
        return false;
    }
}
