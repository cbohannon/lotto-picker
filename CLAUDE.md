# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**Build and package (creates a shaded/fat JAR):**
```
mvn package
```

**Run all tests:**
```
mvn test
```

**Run a single test class:**
```
mvn test -Dtest=LottoEngineTest
```

**Run a single test method:**
```
mvn test -Dtest=LottoEngineTest#testCountMatches_allMatch
```

**Run the application:**
```
java -jar target/bohannon-lotto-1.0.jar
```

## Architecture

The project is a Swing-based lottery simulator with a clean separation between game logic and UI:

- **`LottoEngine`** — Pure game logic with no Swing dependencies. Accepts an injectable `Random` for deterministic testing. Manages picks, winners, drawing counts, and match tallies. This is the only class with real unit tests.

- **`LottoEvent`** — Event handler and game loop coordinator. Implements `ActionListener`, `ItemListener`, and `Runnable`. Bridges `LottoInterface` (GUI) and `LottoEngine` (logic). The game loop runs on a background thread at a user-selected speed (0/1/10/100ms) and throttles GUI updates to at most every 50ms to keep the EDT responsive.

- **`LottoInterface`** — Swing `JFrame` that owns all GUI components. Directly exposes its fields (package-private) so `LottoEvent` can read/write them without getters.

**Data flow:** `LottoInterface` → `LottoEvent` (reads GUI fields) → `LottoEngine` (runs drawing) → `LottoEvent` (pushes results back to GUI).

**Game rules:** 6 picks from 1–54, 156 drawings/year (Mon/Wed/Sat), matches of 0/1/2 are ignored, 6-of-6 triggers jackpot and stops the loop. Default picks are the 6 most historically common Lotto Texas numbers: 4, 8, 15, 19, 26, 38.

## Key Constants (in `LottoEngine`)

| Constant | Value |
|---|---|
| `NUM_PICKS` | 6 |
| `MAX_NUMBER` | 54 |
| `DRAWINGS_PER_YEAR` | 156 |
| `DEFAULT_PICKS` | `{4, 8, 15, 19, 26, 38}` |
