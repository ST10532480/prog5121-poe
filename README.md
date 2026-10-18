# PROG5121 POE — Part 1: Registration and Login

## Run the app
```
mvn compile exec:java -Dexec.mainClass="com.rosebank.prog5121.LoginApp"
```
(or run `LoginApp.java` directly from your IDE)

## Run the unit tests
```
mvn test
```

## Files
- `src/main/java/com/rosebank/prog5121/Login.java` — registration/login class and validation methods
- `src/main/java/com/rosebank/prog5121/LoginApp.java` — console entry point
- `src/test/java/com/rosebank/prog5121/LoginTest.java` — JUnit 5 tests from the assignment's test table
## Moreblessing

## Testing
All methods were verified using JUnit 5 tests in LoginTest.java, covering valid and invalid username, password, and cell phone formats, plus login success and failure cases.
## Part 2: Sending Messages (QuickChat)

After a successful login the app starts QuickChat:

- Welcome message, then the user chooses how many messages to enter.
- Menu: 1) Send Messages, 2) Show recently sent messages ("Coming Soon."), 3) Quit.
- Each message has a random ten-digit ID, a recipient (international code, max ten digits), a message of up to 250 characters and a message hash (for example `00:0:HITONIGHT`).
- For each message the user can send it, disregard it, or store it in `messages.json`.
- The total number of messages sent is shown at the end.

Run the app and the tests the same way as Part 1 (`mvn test`). GitHub Actions runs the tests automatically on every push (`.github/workflows/maven.yml`).
