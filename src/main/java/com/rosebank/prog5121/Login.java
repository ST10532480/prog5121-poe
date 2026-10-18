package com.rosebank.prog5121;

import java.util.regex.Pattern;

public class Login {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    private boolean registered = false;

    public Login(String firstName, String lastName, String username,
                  String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

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

    
    public boolean checkUserName() {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    
    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return hasCapital && hasNumber && hasSpecial;
    }

    
    // Reference: Baeldung (2023) "Java Regex" tutorial, available at:
    // https://www.baeldung.com/regular-expressions-java
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        String regex = "^\\+27[0-9]{1,10}$";
        return Pattern.matches(regex, cellPhoneNumber);
    }

    
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

    
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username != null && password != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
    }

    
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
