# lotto-picker

A Java Swing desktop application that simulates playing the lottery, showing statistically how many drawings it takes to hit a jackpot with a fixed set of numbers.

## How It Works

Pick 6 numbers (1–54) manually or use Quick Pick for random selection, then click **Play**. The simulator runs drawings continuously at 100ms intervals, tracking how many times you match 3, 4, 5, or all 6 numbers. It stops automatically when you hit the jackpot (6 of 6), or you can stop it manually.

Results include total drawings run and an estimated elapsed time in years (assuming 156 drawings/year — Monday, Wednesday, and Saturday). The app defaults to the 6 most historically common Lotto Texas numbers: **4, 8, 15, 19, 26, 38**.

## Building and Running

**Requirements:** Java 8+, Maven 3

```bash
# Build
mvn package

# Run
java -jar target/bohannon-lotto-1.0.jar

# Run tests
mvn test
```

## Project Structure

```
src/main/java/com/bohannon/lotto/
  LottoEngine.java      # Pure game logic (no Swing dependencies)
  LottoEvent.java       # Event handling and game loop
  LottoInterface.java   # Swing GUI (entry point: main())
```
