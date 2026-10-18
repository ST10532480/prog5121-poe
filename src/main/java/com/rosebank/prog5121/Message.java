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

/**
 * Message.java
 *
 * Part 2 of the PROG5121 POE: a single QuickChat message together with
 * the session-wide lists of messages that were sent or stored while the
 * program is running.
 *
 * Methods required by the brief:
 *   checkMessageID(), checkRecipientCell(), createMessageHash(),
 *   sentMessage(), printMessages(), returnTotalMessages() and
 *   storeMessage() (messages are stored in a JSON file).
 */
public class Message {

    // ---------- Rules and fixed messages from the brief ----------

    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int MAX_ID_LENGTH = 10;

    // What the user can do with a message (menu options 1 to 3).
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

    // ---------- Session data (lives while the program is running) ----------

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static Path storageFile = Paths.get("messages.json");

    // ---------- One message ----------

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;

    /**
     * Creates a message with a randomly generated ten-digit message ID.
     *
     * @param messageNumber the number of this message in the session,
     *                      starting at 0 for the first message
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    /** Creates a message with a known ID (used for repeatable unit tests). */
    public Message(String messageID, int messageNumber, String recipient,
                   String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    // ---------- Getters ----------

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

    // ---------- Message ID ----------

    /** Randomly generates a ten-digit message ID (leading zeros allowed). */
    public static String generateMessageID() {
        StringBuilder id = new StringBuilder(MAX_ID_LENGTH);
        for (int i = 0; i < MAX_ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    /** The message ID must not be more than ten characters long. */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty()
                && messageID.length() <= MAX_ID_LENGTH;
    }

    /** Confirmation shown when the ID has been created. */
    public String getMessageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    // ---------- Message length ----------

    /**
     * The message must not be more than 250 characters long. Returns the
     * success message, or the error saying by how many characters the
     * message is too long.
     */
    public String checkMessageLength() {
        int length = (messageText == null) ? 0 : messageText.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return LENGTH_OK;
        }
        return "Message exceeds " + MAX_MESSAGE_LENGTH + " characters by "
                + (length - MAX_MESSAGE_LENGTH) + "; please reduce the size.";
    }

    // ---------- Recipient ----------

    /**
     * True when the recipient number starts with the international code and
     * is no more than ten characters long. This re-uses the cell phone rule
     * that was built and tested in Part 1 (Login.checkCellPhoneNumber).
     */
    public boolean isRecipientValid() {
        return new Login("", "", "", "", recipient).checkCellPhoneNumber();
    }

    /** Returns the success or failure message for the recipient number. */
    public String checkRecipientCell() {
        return isRecipientValid() ? RECIPIENT_OK : RECIPIENT_BAD;
    }

    // ---------- Message hash ----------

    /**
     * Creates the message hash: the first two digits of the message ID, a
     * colon, the message number, a colon, then the first and last words of
     * the message, all in capital letters.
     *
     * Example: ID 0012345678, message number 0 and the message "Hi Mike,
     * can you join us for dinner tonight?" gives 00:0:HITONIGHT.
     */
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

    // ---------- Send / disregard / store ----------

    /**
     * Applies the user's choice (1 = send, 2 = disregard, 3 = store) and
     * returns the message the system must show.
     */
    public String sentMessage(int choice) {
        if (choice != SEND && choice != DISREGARD && choice != STORE) {
            return MSG_INVALID_CHOICE;
        }
        if (choice == DISREGARD) {
            return MSG_DISREGARDED;
        }

        // A message that breaks the rules can be neither sent nor stored.
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

    /** Lets the user choose to send, disregard or store this message. */
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

    // ---------- Display ----------

    /** The full details of this message, in the order the brief asks for. */
    public String getDetails() {
        String nl = System.lineSeparator();
        return "Message ID: " + messageID + nl
                + "Message Hash: " + createMessageHash() + nl
                + "Recipient: " + recipient + nl
                + "Message: " + messageText;
    }

    /** Returns every message that was sent while the program is running. */
    public static String printMessages() {
        StringBuilder all = new StringBuilder();
        for (Message message : sentMessages) {
            all.append(message.getDetails()).append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        return all.toString();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // ---------- JSON storage ----------

    /**
     * Stores this message in the JSON file (messages.json by default).
     * Every stored message is kept in one JSON array.
     *
     * @return true if the file was written successfully
     */
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

    /** Builds the JSON text for a list of messages (no library needed). */
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

    /** Escapes quotes, backslashes and control characters for JSON. */
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

    // ---------- Session helpers ----------

    /** Where stored messages are written (changed in unit tests). */
    public static void setStorageFile(Path file) {
        storageFile = file;
    }

    /** Clears the sent and stored lists and restores the default file. */
    public static void resetSession() {
        sentMessages.clear();
        storedMessages.clear();
        storageFile = Paths.get("messages.json");
    }
}
