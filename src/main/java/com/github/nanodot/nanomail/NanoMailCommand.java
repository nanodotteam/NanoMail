package com.github.nanodot.nanomail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NanoMailCommand implements CommandExecutor {

    // TODO: Create proper help page
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
        } else if (args.length >= 3 && args[0].equals("send")) { // Mail send handler
            sender.sendMessage("SENDING");
            String receiverName = args[1];
            if(receiverName == null) {
                sender.sendMessage("Receiver is null");
                return true;
            }
            Player receiver = Bukkit.getPlayer(receiverName);
            if(receiver == null) {
                sender.sendMessage("Receiver is not online, trying to get offline");
                return true;
            }
            // TODO: It should find offline player UUID and add the mail :)
            UUID receiverID = receiver.getUniqueId();
            String message = args[2];
            if(message == null) {
                sender.sendMessage("Message is null");
                return true;
            }
            Mail newMail = new Mail();
            newMail.setMailID(0);
            Player player = (Player) sender;
            newMail.setAuthorUUID(player.getUniqueId());
            newMail.setContent(message);
            main.mailDataHandler.toPlayerAddMail(receiverID, newMail);
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
            Mail[] senderMails = main.mailDataHandler.getPlayerMails(senderID);
            StringBuilder response = new StringBuilder();
            response.append("Your mails:\n");
            for(Mail mail : senderMails) {
                response.append(mail.toString() + "\n");
            }
            sender.sendMessage(response.toString());
        }
        return false;
    }
}
