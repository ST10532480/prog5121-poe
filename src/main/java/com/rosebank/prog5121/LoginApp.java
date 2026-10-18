package com.rosebank.prog5121;
// Entry point for the console app (Part 1 login, Part 2 QuickChat)

import java.util.Scanner;

/**
 * LoginApp.java
 *
 * Console application: registers a user, lets them log in, and only once
 * the login is successful starts QuickChat (Part 2) so they can send
 * messages. Console only, no GUI / JOptionPane, as required by the brief.
 */
public class LoginApp {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Registration ===");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username (must contain an underscore, "
                + "max 5 characters): ");
        String username = scanner.nextLine();

        System.out.print("Enter password (min 8 characters, a capital "
                + "letter, a number, and a special character): ");
        String password = scanner.nextLine();

        System.out.print("Enter South African cell phone number "
                + "(e.g. +27838968976): ");
        String cellPhoneNumber = scanner.nextLine();

        Login user = new Login(firstName, lastName, username, password, cellPhoneNumber);

        String registrationMessage = user.registerUser();
        System.out.println(registrationMessage);

        if (!user.isRegistered()) {
            System.out.println("Registration failed. Exiting.");
            scanner.close();
            return;
        }

        System.out.println();
        System.out.println("=== Login ===");

        // The user gets a few attempts; messages can only be sent after
        // a successful login.
        boolean loggedIn = false;
        int attempts = 0;
        while (!loggedIn && attempts < MAX_LOGIN_ATTEMPTS) {
            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine();

            loggedIn = user.loginUser(loginUsername, loginPassword);
            System.out.println(user.returnLoginStatus(loggedIn));
            attempts++;
        }

        if (loggedIn) {
            System.out.println();
            QuickChat.run(scanner);
        } else {
            System.out.println("Too many failed login attempts. Exiting.");
        }

        scanner.close();
    }
}
