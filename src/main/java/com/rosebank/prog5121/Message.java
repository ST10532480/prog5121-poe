package com.rosebank.prog5121;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Message {
    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int MAX_ID_LENGTH = 10;

    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    public static final String LENGTH_OK = "Message ready to send.";
    public static final String RECIPIENT_OK = "Cell phone number successfully captured.";
    public static final String RECIPIENT_BAD = "Cell phone number is incorrectly "
            + "formatted or does not contain an international code. Please "
            + "correct the number and try again.";

    public static final String MSG_SENT = "Message successfully sent.";
    public static final String MSG_DISREGARDED = "Press 0 to delete the message.";
    public static final String MSG_STORED = "Message successfully stored.";
    public static final String MSG_INVALID_CHOICE = "Invalid option; please "
            + "choose 1 to send, 2 to disregard or 3 to store the message.";
    public static final String MSG_STORE_FAILED = "Message could not be "
            + "stored; please try again.";

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static Path storageFile = Paths.get("messages.json");

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;

    
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    
    public Message(String messageID, int messageNumber, String recipient,
                   String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    
    public static String generateMessageID() {
        StringBuilder id = new StringBuilder(MAX_ID_LENGTH);
        for (int i = 0; i < MAX_ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty()
                && messageID.length() <= MAX_ID_LENGTH;
    }

    
    public String getMessageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    
    public String checkMessageLength() {
        int length = (messageText == null) ? 0 : messageText.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return LENGTH_OK;
        }
        return "Message exceeds " + MAX_MESSAGE_LENGTH + " characters by "
                + (length - MAX_MESSAGE_LENGTH) + "; please reduce the size.";
    }

    
    public boolean isRecipientValid() {
        return new Login("", "", "", "", recipient).checkCellPhoneNumber();
    }

    
    public String checkRecipientCell() {
        return isRecipientValid() ? RECIPIENT_OK : RECIPIENT_BAD;
    }

    
    // Hash format: first two ID digits : message number : first and last word, e.g. 00:0:HITONIGHT
    public String createMessageHash() {
        String idPart = (messageID == null) ? ""
                : messageID.substring(0, Math.min(2, messageID.length()));

        String firstWord = "";
        String lastWord = "";
        if (messageText != null && !messageText.trim().isEmpty()) {
            String[] words = messageText.trim().split("\\s+");
            firstWord = stripPunctuation(words[0]);
            lastWord = stripPunctuation(words[words.length - 1]);
        }
        return (idPart + ":" + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase();
    }

    private static String stripPunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    
    public String sentMessage(int choice) {
        if (choice != SEND && choice != DISREGARD && choice != STORE) {
            return MSG_INVALID_CHOICE;
        }
        if (choice == DISREGARD) {
            return MSG_DISREGARDED;
        }

        if (!isRecipientValid()) {
            return RECIPIENT_BAD;
        }
        if (!LENGTH_OK.equals(checkMessageLength())) {
            return checkMessageLength();
        }

        if (choice == SEND) {
            sentMessages.add(this);
            return MSG_SENT;
        }
        return storeMessage() ? MSG_STORED : MSG_STORE_FAILED;
    }

    
    public String sentMessage(Scanner scanner) {
        int choice = 0;
        while (choice < SEND || choice > STORE) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose an option (1-3): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = 0;
            }
            if (choice < SEND || choice > STORE) {
                System.out.println("Invalid option; please enter 1, 2 or 3.");
            }
        }
        return sentMessage(choice);
    }

    
    public String getDetails() {
        String nl = System.lineSeparator();
        return "Message ID: " + messageID + nl
                + "Message Hash: " + createMessageHash() + nl
                + "Recipient: " + recipient + nl
                + "Message: " + messageText;
    }

    
    public static String printMessages() {
        StringBuilder all = new StringBuilder();
        for (Message message : sentMessages) {
            all.append(message.getDetails()).append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        return all.toString();
    }

    
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    
    public boolean storeMessage() {
        storedMessages.add(this);
        try {
            Files.writeString(storageFile, toJson(storedMessages),
                    StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            storedMessages.remove(this);
            return false;
        }
    }

    
    static String toJson(List<Message> messages) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            json.append(i == 0 ? "\n" : ",\n");
            json.append("  {\n");
            json.append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n");
            json.append("    \"messageNumber\": ").append(m.messageNumber).append(",\n");
            json.append("    \"messageHash\": \"").append(escape(m.createMessageHash())).append("\",\n");
            json.append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n");
            json.append("    \"message\": \"").append(escape(m.messageText)).append("\"\n");
            json.append("  }");
        }
        json.append(messages.isEmpty() ? "]" : "\n]").append("\n");
        return json.toString();
    }

    
    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    
    public static void setStorageFile(Path file) {
        storageFile = file;
    }

    
    public static void resetSession() {
        sentMessages.clear();
        storedMessages.clear();
        storageFile = Paths.get("messages.json");
    }
}
