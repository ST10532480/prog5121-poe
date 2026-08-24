package com.rosebank.prog5121;

import java.util.Scanner;

/**
 * LoginApp.java
 *
 * Console application for Part 1: registers a user, then lets them
 * attempt to log in using the details they registered with.
 * Console only — no GUI / JOptionPane, as required by the brief.
 */
public class LoginApp {

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

        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean loginSuccessful = user.loginUser(loginUsername, loginPassword);
        String loginStatusMessage = user.returnLoginStatus(loginSuccessful);
        System.out.println(loginStatusMessage);

        scanner.close();
    }
}
