package com.rosebank.prog5121;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * Handles registration and login for Part 1 of the PROG5121 POE.
 * Validates username, password and South African cell phone number
 * formatting, registers a user, and verifies login credentials.
 *
 * Reference for the regular expression approach used in
 * checkCellPhoneNumber(): Baeldung (2023) "Java Regex" tutorial,
 * available at: https://www.baeldung.com/regular-expressions-java
 */
public class Login {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    // Tracks whether this user has successfully completed registration.
    private boolean registered = false;

    public Login(String firstName, String lastName, String username,
                  String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    // ---------- Getters ----------

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    // ---------- Validation methods ----------

    /**
     * Username must contain an underscore and be no more than
     * five characters long.
     */
    public boolean checkUserName() {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Password must be at least eight characters long and contain
     * a capital letter, a number and a special character.
     */
    // Uses regex to check for a capital letter, a number, and a special character
    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Cell phone number must start with the South African
     * international country code (+27) followed by the number,
     * which must be no more than ten characters long.
     */
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        String regex = "^\\+27[0-9]{1,10}$";
        return Pattern.matches(regex, cellPhoneNumber);
    }

    // ---------- Registration ----------

    /**
     * Validates all captured details and registers the user if every
     * condition is met. Returns the appropriate message for whichever
     * condition fails first, or a welcome message on success.
     */
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that "
                    + "your username contains an underscore and is no more "
                    + "than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that "
                    + "the password contains at least eight characters, a "
                    + "capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell number is incorrectly formatted or does not "
                    + "contain an international code; please correct the "
                    + "number and try again.";
        }
        registered = true;
        return "Welcome " + firstName + ", " + lastName
                + " it is great to see you again.";
    }

    public boolean isRegistered() {
        return registered;
    }

    // ---------- Login ----------

    /**
     * Verifies that the entered username and password match the
     * details captured at registration.
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username != null && password != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
    }

    /**
     * Returns the message shown to the user after a login attempt.
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
