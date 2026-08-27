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