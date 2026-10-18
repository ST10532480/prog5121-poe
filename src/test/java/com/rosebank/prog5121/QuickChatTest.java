package com.rosebank.prog5121;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QuickChatTest.java
 *
 * Runs the QuickChat menu with scripted keyboard input and checks what the
 * user sees on the console.
 */
class QuickChatTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Message.resetSession();
        Message.setStorageFile(tempDir.resolve("messages.json"));
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /** Runs QuickChat with the given lines typed by the user. */
    private String runWith(String... lines) {
        QuickChat.run(new Scanner(String.join("\n", lines) + "\n"));
        return captured.toString();
    }

    @Test
    void showsWelcomeMessageAndMenu() {
        String output = runWith("1", "3");

        assertTrue(output.contains("Welcome to QuickChat."));
        assertTrue(output.contains("1) Send Messages"));
        assertTrue(output.contains("2) Show recently sent messages"));
        assertTrue(output.contains("3) Quit"));
    }

    @Test
    void optionTwo_showsComingSoon() {
        String output = runWith("1", "2", "3");
        assertTrue(output.contains("Coming Soon."));
    }

    @Test
    void invalidMenuInput_isHandledAndMenuKeepsRunning() {
        String output = runWith("1", "abc", "9", "3");

        assertEquals(2, countOccurrences(output, "Invalid option. Please enter 1, 2 or 3."));
        assertTrue(output.contains("Thank you for using QuickChat. Goodbye."));
    }

    @Test
    void sendOneMessage_showsDetailsAndTotal() {
        String output = runWith(
                "1",                                   // one message
                "1",                                   // Send Messages
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                "1",                                   // Send Message
                "3");                                  // Quit

        assertTrue(output.contains("Cell phone number successfully captured."));
        assertTrue(output.contains("Message ready to send."));
        assertTrue(output.contains("Message ID generated: "));
        assertTrue(output.contains("Message successfully sent."));
        assertTrue(output.contains("Message Hash: "));
        assertTrue(output.contains("HITONIGHT"));
        assertTrue(output.contains("Recipient: +27718693002"));
        assertTrue(output.contains("Message: Hi Mike, can you join us for dinner tonight?"));
        assertTrue(output.contains("Total number of messages sent: 1"));
    }

    @Test
    void twoMessages_oneSentOneDisregarded_totalIsOne() {
        String output = runWith(
                "2",                                   // two messages
                "1",                                   // Send Messages
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                "1",                                   // Send
                "+27857597588",
                "Hi Keegan, did you receive the payment?",
                "2",                                   // Disregard
                "0",                                   // delete it
                "3");                                  // Quit

        assertTrue(output.contains("Press 0 to delete the message."));
        assertTrue(output.contains("Message deleted."));
        assertTrue(output.contains("Total number of messages sent: 1"));
    }

    @Test
    void storedMessage_isReportedAsStored() {
        String output = runWith(
                "1", "1",
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                "3",                                   // Store Message
                "3");                                  // Quit

        assertTrue(output.contains("Message successfully stored."));
        assertTrue(output.contains("Total number of messages sent: 0"));
    }

    @Test
    void badRecipient_isRejectedAndAskedAgain() {
        String output = runWith(
                "1", "1",
                "08575975889",                         // no international code
                "+27857597588",
                "Hi Keegan, did you receive the payment?",
                "1", "3");

        assertTrue(output.contains("Cell phone number is incorrectly formatted or "
                + "does not contain an international code. Please correct the "
                + "number and try again."));
        assertTrue(output.contains("Message successfully sent."));
    }

    @Test
    void messageOver250Characters_isRejectedAndAskedAgain() {
        String output = runWith(
                "1", "1",
                "+27718693002",
                "a".repeat(251),
                "Hi Mike, can you join us for dinner tonight?",
                "1", "3");

        assertTrue(output.contains("Message exceeds 250 characters by 1; "
                + "please reduce the size."));
        assertTrue(output.contains("Message successfully sent."));
    }

    @Test
    void onlyTheChosenNumberOfMessagesCanBeEntered() {
        String output = runWith(
                "1",                                   // only one message allowed
                "1",
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?",
                "1",
                "1",                                   // try to send more
                "3");

        assertTrue(output.contains("You have already entered all 1 messages."));
    }

    private static int countOccurrences(String text, String part) {
        int count = 0;
        int index = text.indexOf(part);
        while (index != -1) {
            count++;
            index = text.indexOf(part, index + part.length());
        }
        return count;
    }
}
