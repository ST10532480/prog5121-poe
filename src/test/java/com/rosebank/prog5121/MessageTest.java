package com.rosebank.prog5121;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageTest.java
 *
 * Unit tests for Part 2, using the test data and expected system
 * responses from the assignment brief.
 *
 * Test Case 1: +27718693002, "Hi Mike, can you join us for dinner
 *              tonight?", Send
 * Test Case 2: 08575975889, "Hi Keegan, did you receive the payment?",
 *              Disregard
 */
class MessageTest {

    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    void freshSession() {
        Message.resetSession();
        Message.setStorageFile(tempDir.resolve("messages.json"));
    }

    private Message testCase1() {
        return new Message("0012345678", 0, "+27718693002", TEXT_1);
    }

    private Message testCase2() {
        return new Message("0087654321", 1, "08575975889", TEXT_2);
    }

    // ---------- Message length ----------

    @Test
    void messageUnder250Characters_returnsReadyToSend() {
        assertEquals("Message ready to send.", testCase1().checkMessageLength());
    }

    @Test
    void messageOver250Characters_returnsHowManyTooLong() {
        String tooLong = "a".repeat(260);
        Message message = new Message("0012345678", 0, "+27718693002", tooLong);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    @Test
    void messageOfExactly250Characters_isAccepted() {
        Message message = new Message("0012345678", 0, "+27718693002", "a".repeat(250));
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    // ---------- Recipient ----------

    @Test
    void recipientCorrectlyFormatted_returnsSuccess() {
        assertEquals("Cell phone number successfully captured.",
                testCase1().checkRecipientCell());
    }

    @Test
    void recipientIncorrectlyFormatted_returnsFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not "
                        + "contain an international code. Please correct the "
                        + "number and try again.",
                testCase2().checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    void messageHashIsCorrect_testCase1() {
        assertEquals("00:0:HITONIGHT", testCase1().createMessageHash());
    }

    @Test
    void remainingMessageHashesAreCorrect_testedInALoop() {
        Message[] messages = {testCase1(), testCase2()};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < messages.length; i++) {
            assertEquals(expectedHashes[i], messages[i].createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    void messageIDIsCreated_tenDigitsAndAnnounced() {
        Message message = new Message(0, "+27718693002", TEXT_1);

        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.getMessageID().matches("[0-9]{10}"));
        assertEquals("Message ID generated: " + message.getMessageID(),
                message.getMessageIDStatus());
    }

    @Test
    void messageIDLongerThanTenCharacters_isRejected() {
        Message message = new Message("12345678901", 0, "+27718693002", TEXT_1);
        assertFalse(message.checkMessageID());
    }

    // ---------- Send / disregard / store ----------

    @Test
    void userSelectsSend_returnsMessageSent() {
        assertEquals("Message successfully sent.", testCase1().sentMessage(Message.SEND));
    }

    @Test
    void userSelectsDisregard_returnsPressZeroToDelete() {
        assertEquals("Press 0 to delete the message.",
                testCase2().sentMessage(Message.DISREGARD));
    }

    @Test
    void userSelectsStore_returnsMessageStored() {
        assertEquals("Message successfully stored.", testCase1().sentMessage(Message.STORE));
    }

    @Test
    void invalidChoice_returnsInvalidOptionMessage() {
        assertEquals(Message.MSG_INVALID_CHOICE, testCase1().sentMessage(7));
    }

    @Test
    void messageWithBadRecipient_cannotBeSent() {
        // Test Case 2 has an incorrectly formatted number, so it must not send.
        assertEquals(Message.RECIPIENT_BAD, testCase2().sentMessage(Message.SEND));
        assertEquals(0, Message.returnTotalMessages());
    }

    // ---------- Totals and printing ----------

    @Test
    void totalMessagesSent_countsOnlySentMessages() {
        testCase1().sentMessage(Message.SEND);        // sent
        testCase2().sentMessage(Message.DISREGARD);   // disregarded
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessages_listsDetailsInTheRequiredOrder() {
        testCase1().sentMessage(Message.SEND);
        String nl = System.lineSeparator();

        String expected = "Message ID: 0012345678" + nl
                + "Message Hash: 00:0:HITONIGHT" + nl
                + "Recipient: +27718693002" + nl
                + "Message: " + TEXT_1 + nl + nl;

        assertEquals(expected, Message.printMessages());
    }

    @Test
    void printMessages_isEmptyBeforeAnythingIsSent() {
        assertEquals("", Message.printMessages());
    }

    // ---------- JSON storage ----------

    @Test
    void storedMessage_isWrittenToTheJsonFile() throws IOException {
        testCase1().sentMessage(Message.STORE);

        String json = Files.readString(tempDir.resolve("messages.json"),
                StandardCharsets.UTF_8);
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"+27718693002\""));
        assertTrue(json.contains("\"message\": \"" + TEXT_1 + "\""));
    }

    @Test
    void storingTwoMessages_keepsBothInOneJsonArray() throws IOException {
        testCase1().sentMessage(Message.STORE);
        new Message("0011111111", 1, "+27857597588", TEXT_2).sentMessage(Message.STORE);

        String json = Files.readString(tempDir.resolve("messages.json"),
                StandardCharsets.UTF_8);
        assertTrue(json.startsWith("["));
        assertTrue(json.contains("0012345678"));
        assertTrue(json.contains("0011111111"));
    }

    @Test
    void jsonEscapesQuotesAndNewLines() throws IOException {
        Message message = new Message("0012345678", 0, "+27718693002",
                "She said \"hi\"\nsee you");
        message.sentMessage(Message.STORE);

        String json = Files.readString(tempDir.resolve("messages.json"),
                StandardCharsets.UTF_8);
        assertTrue(json.contains("She said \\\"hi\\\"\\nsee you"));
    }
}
