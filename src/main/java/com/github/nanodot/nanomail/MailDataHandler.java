package com.github.nanodot.nanomail;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class MailDataHandler {
    private HashMap<UUID, MailBox> mailData = new HashMap<>();
    private final NanoMail main = NanoMail.getPlugin(NanoMail.class);
    File mailDataFolder = new File(main.getDataFolder().getAbsolutePath() + "/mails/");
    private final Logger logger = main.logger;
    private final Yaml yaml = new Yaml(new CustomClassLoaderConstructor(com.github.nanodot.nanomail.MailBox.class.getClassLoader()));

    public MailDataHandler() {
        // Here check and setup of files should happen
        // Set some class' global variables
        // After files are checked it should iterate over mails/<PlayerUUID>.yml files
        // and load it to Map<String, Object>, String is "mails" and Object is array of mails
        // and then convert maps to HashMap<UUID, LinkedList<Mail>>

        // Get or create data folder if it does not exist
        try {
            if(mailDataFolder.mkdirs()) {
                logger.info("Successfully created mails data folder");
            } else {
                logger.info("Nice! Mails data folder already exists");
            }
        } catch(SecurityException e) {
            // TODO: Change responsibility of disabling to the main class just by throwing Exception
            e.printStackTrace();
            logger.warning("Error creating mails data folder (SecurityException)");
            main.disableThis();
            return;
        }

        // Get all mail users mails (mailboxes)
        File[] mailDataFiles = mailDataFolder.listFiles();

        if(mailDataFiles == null) {
            // There is no mails at the moment
            // so we can skip the rest of this constructor code
            return;
        }

        // File structure will look like this
        // File name: Player's UUID
        // lastID = 1
        // mails:
        //   0:
        //     authorUUID: "xxxx-xxxx-xxxx-xxxx"
        //     content: "Test of NanoMail"
        //   1:
        //     authorUUID: "xxxx-xxxx-xxxx-xxxx"
        //     content: "Hey! What's up?"
        // ...

        for(File oneUserMails : mailDataFiles) {
            String playerUUIDFileName = oneUserMails.getName();
            // Remove .yml from the end of file name
            String playerUUIDString = playerUUIDFileName.substring(0, playerUUIDFileName.length() - 5);
            UUID playerUUID = UUID.fromString(playerUUIDString);
            FileInputStream mailDataInputStream;

            try {
                mailDataInputStream = new FileInputStream(oneUserMails);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.warning("Mail data file was not found (strange behaviour)!");
                main.disableThis();
                return;
            }

            MailBox playerMails = yaml.loadAs(mailDataInputStream, com.github.nanodot.nanomail.MailBox.class);
            //logger.info("\nMailbox " + playerUUIDString + "\n" + playerMails.toString());

            mailData.put(playerUUID, playerMails);
        }
    }

    public Mail[] getPlayerMails(UUID playerUUID) {
        MailBox playerMails = mailData.get(playerUUID);

        // Handle the case when no MailBox exist
        if(playerMails == null) {
            return new Mail[0];
        }

        return playerMails.getMails().toArray(new Mail[0]);
    }

    public void toPlayerAddMail(UUID playerUUID, Mail mailToAdd) {
        MailBox playerMailBox = mailData.get(playerUUID);

        // Handle the case when no MailBox exist
        if(playerMailBox == null) {
            playerMailBox = new MailBox();
            playerMailBox.setLastMailID(0);
            playerMailBox.setMails(new LinkedList<>());
            mailData.put(playerUUID, playerMailBox);
        }

        playerMailBox.getMails().add(mailToAdd);
    }

    public void setPlayerMails(UUID playerUUID, Mail[] mailsToSet) {
        MailBox playerMailBox = mailData.get(playerUUID);

        // Handle the case when no MailBox exist
        if(playerMailBox == null) {
            playerMailBox = new MailBox();
            playerMailBox.setLastMailID(0);
        }

        playerMailBox.setMails((LinkedList<Mail>) Arrays.asList(mailsToSet));
    }

    public void saveMailsToDisk() {
        // For every existing MailBox
        for(Map.Entry<UUID, MailBox> mailDataEntry : mailData.entrySet()) {
            // If <UUID>.yml file does not exist create it
            // Create a new map and fill it with MailBox values
            // Dump the map to <UUID>.yml file using SnakeYAML (overwriting it)
            String UUIDString = mailDataEntry.getKey().toString();
            String mailBoxFileName = UUIDString + ".yml";

            File mailBoxFile = new File(mailDataFolder, mailBoxFileName);
            try {
                if (mailBoxFile.createNewFile()) {
                    logger.info("Created new file " + mailBoxFileName + " for storing user data");
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.warning("Cannot create file " + mailBoxFileName + " for storing user data");
                continue;
            }

            MailBox mailBox = mailDataEntry.getValue();

            // We don't have to create a map, we can use some magic
            // Map<String, Object> mailDataToDump = new HashMap<>();

            PrintWriter printWriter;

            try {
                printWriter = new PrintWriter(mailBoxFile);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
                logger.warning("Cannot create PrintWriter (FileNotFound)");
                continue;
            }

            yaml.dump(mailBox, printWriter);
            logger.info("Successfully saved player mails");
        }
    }
}
