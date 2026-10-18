package com.rosebank.prog5121;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class QuickChat {
    public static void run(Scanner scanner) {
        System.out.println("Welcome to QuickChat.");

        try {
            int allowedMessages = askNumberOfMessages(scanner);
            int enteredMessages = 0;
            boolean running = true;

            while (running) {
                printMenu();
                int choice = readMenuChoice(scanner);

                switch (choice) {
                    case 1:
                        if (enteredMessages >= allowedMessages) {
                            System.out.println("You have already entered all "
                                    + allowedMessages + " messages.");
                        } else {
                            for (int i = enteredMessages; i < allowedMessages; i++) {
                                enterMessage(scanner, i, allowedMessages);
                            }
                            enteredMessages = allowedMessages;
                            System.out.println("Total number of messages sent: "
                                    + Message.returnTotalMessages());
                        }
                        break;
                    case 2:
                        System.out.println("Coming Soon.");
                        break;
                    case 3:
                        running = false;
                        System.out.println("Thank you for using QuickChat. Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1, 2 or 3.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("No more input available. Exiting QuickChat.");
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    
    private static int readMenuChoice(Scanner scanner) {
        System.out.print("Choose an option (1-3): ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    
    private static int askNumberOfMessages(Scanner scanner) {
        while (true) {
            System.out.print("How many messages do you wish to enter? ");
            try {
                int count = Integer.parseInt(scanner.nextLine().trim());
                if (count > 0) {
                    return count;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    private static void enterMessage(Scanner scanner, int index, int total) {
        System.out.println();
        System.out.println("--- Message " + (index + 1) + " of " + total + " ---");

        String recipient = readRecipient(scanner, index);
        String text = readMessageText(scanner, index, recipient);

        Message message = new Message(index, recipient, text);
        System.out.println(message.getMessageIDStatus());

        String result = message.sentMessage(scanner);
        System.out.println(result);

        if (Message.MSG_SENT.equals(result)) {
            System.out.println(message.getDetails());
        } else if (Message.MSG_DISREGARDED.equals(result)) {
            String answer = scanner.nextLine().trim();
            System.out.println("0".equals(answer) ? "Message deleted."
                    : "Message disregarded.");
        }
    }

    
    private static String readRecipient(Scanner scanner, int index) {
        while (true) {
            System.out.print("Enter the recipient's cell phone number "
                    + "(e.g. +27718693002): ");
            String recipient = scanner.nextLine().trim();

            Message check = new Message(index, recipient, "");
            System.out.println(check.checkRecipientCell());
            if (check.isRecipientValid()) {
                return recipient;
            }
        }
    }

    
    private static String readMessageText(Scanner scanner, int index,
                                          String recipient) {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                System.out.println("The message cannot be empty.");
                continue;
            }

            Message check = new Message(index, recipient, text);
            System.out.println(check.checkMessageLength());
            if (Message.LENGTH_OK.equals(check.checkMessageLength())) {
                return text;
            }
        }
    }
}
