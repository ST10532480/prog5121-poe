package com.rosebank.prog5121;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginTest.java
 *
 * Unit tests for Part 1, matching the test data and expected system
 * responses required by the assignment brief.
 */
class LoginTest {

        // ---------- Username tests ----------

        @Test
        void usernameCorrectlyFormatted_returnsWelcomeMessage() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                assertEquals("Welcome Kyle, Smith it is great to see you again.",
                                user.registerUser());
        }

        @Test
        void usernameCorrectlyFormatted_returnsTrue() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                assertTrue(user.checkUserName());
        }

        @Test
        void usernameIncorrectlyFormatted_returnsErrorMessage() {
                Login user = new Login("Kyle", "Smith", "kyle!!!!!!",
                                "Ch&&sec@ke99!", "+27838968976");
                assertEquals("Username is not correctly formatted; please ensure "
                                + "that your username contains an underscore and "
                                + "is no more than five characters in length.",
                                user.registerUser());
        }

        // ---------- Password tests ----------

        @Test
        void passwordMeetsComplexityRequirements() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                assertTrue(user.checkPasswordComplexity());
        }

        @Test
        void passwordDoesNotMeetComplexityRequirements_returnsErrorMessage() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "password", "+27838968976");
                assertEquals("Password is not correctly formatted; please ensure "
                                + "that the password contains at least eight "
                                + "characters, a capital letter, a number, and a "
                                + "special character.",
                                user.registerUser());
        }

        // ---------- Cell phone tests ----------

        @Test
        void cellPhoneCorrectlyFormatted_returnsTrue() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                assertTrue(user.checkCellPhoneNumber());
        }

        @Test
        void cellPhoneIncorrectlyFormatted_returnsErrorMessage() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "08966553");
                assertEquals("Cell number is incorrectly formatted or does not "
                                + "contain an international code; please correct "
                                + "the number and try again.",
                                user.registerUser());
        }

        // ---------- Login tests ----------

        @Test
        void loginSuccessful_returnsTrue() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                user.registerUser();
                assertTrue(user.loginUser("kyl_1", "Ch&&sec@ke99!"));
        }

        @Test
        void loginFailed_returnsFalse() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                user.registerUser();
                assertFalse(user.loginUser("kyl_1", "wrongPassword"));
        }

        @Test
        void loginSuccessful_returnsWelcomeMessage() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                user.registerUser();
                boolean success = user.loginUser("kyl_1", "Ch&&sec@ke99!");
                assertEquals("Welcome Kyle, Smith it is great to see you again.",
                                user.returnLoginStatus(success));
        }

        @Test
        void loginFailed_returnsErrorMessage() {
                Login user = new Login("Kyle", "Smith", "kyl_1",
                                "Ch&&sec@ke99!", "+27838968976");
                user.registerUser();
                boolean success = user.loginUser("kyl_1", "wrongPassword");
                assertEquals("Username or password incorrect, please try again.",
                                user.returnLoginStatus(success));
        }
        @Test void usernameWithoutUnderscore_returnsFalse() { Login user = new Login("Kyle", "Smith", "kyle1", "Ch&&sec@ke99!", "+27838968976"); assertFalse(user.checkUserName()); }
}
