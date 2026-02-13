package com.bohannon.lotto;

import java.util.Random;

/**
 * Pure game logic for the lottery simulator.
 * No Swing dependencies — operates entirely on primitives and int arrays.
 * Accepts an injectable Random for testability.
 */
public class LottoEngine {

    public static final int NUM_PICKS = 6;
    public static final int MAX_NUMBER = 50;
    public static final int DRAWINGS_PER_YEAR = 104;

    private final Random random;

    private int[] picks;
    private int[] winners;
    private int matchesOf3;
    private int matchesOf4;
    private int matchesOf5;
    private int matchesOf6;
    private int drawingCount;
    private boolean jackpotHit;

    public LottoEngine() {
        this(new Random());
    }

    public LottoEngine(Random random) {
        this.random = random;
        this.picks = new int[NUM_PICKS];
        this.winners = new int[NUM_PICKS];
    }

    /**
     * Check if {@code num} already exists in the first {@code count} elements of {@code numbers}.
     */
    public boolean numberAlreadyUsed(int num, int[] numbers, int count) {
        for (int i = 0; i < count; i++) {
            if (numbers[i] == num) {
                return true;
            }
        }
        return false;
    }

    /**
     * Count how many values in {@code winningNumbers} also appear in {@code userPicks}.
     */
    public int countMatches(int[] userPicks, int[] winningNumbers) {
        int matches = 0;
        for (int winner : winningNumbers) {
            for (int pick : userPicks) {
                if (winner == pick) {
                    matches++;
                    break;
                }
            }
        }
        return matches;
    }

    /**
     * Generate {@link #NUM_PICKS} unique random numbers in the range [1, {@link #MAX_NUMBER}].
     */
    public int[] generateNumbers() {
        int[] numbers = new int[NUM_PICKS];
        for (int i = 0; i < NUM_PICKS; i++) {
            int num;
            do {
                num = random.nextInt(MAX_NUMBER) + 1;
            } while (numberAlreadyUsed(num, numbers, i));
            numbers[i] = num;
        }
        return numbers;
    }

    /**
     * Generate a quick pick — fills the picks array with unique random numbers.
     */
    public void generateQuickPick() {
        this.picks = generateNumbers();
    }

    /**
     * Set the user's picks from an external source (e.g. GUI text fields).
     */
    public void setPicks(int[] userPicks) {
        this.picks = new int[NUM_PICKS];
        System.arraycopy(userPicks, 0, this.picks, 0, NUM_PICKS);
    }

    /**
     * Run one drawing: generate winners, count matches, update tallies.
     *
     * @return the number of matches for this drawing
     */
    public int runOneDrawing() {
        drawingCount++;
        this.winners = generateNumbers();
        int matches = countMatches(picks, winners);
        recordMatches(matches);
        return matches;
    }

    /**
     * Record a match count into the running totals.
     * Matches of 0, 1, or 2 are ignored (as in the original game).
     *
     * @return true if this was a jackpot (6 of 6)
     */
    public boolean recordMatches(int matches) {
        switch (matches) {
            case 3:
                matchesOf3++;
                return false;
            case 4:
                matchesOf4++;
                return false;
            case 5:
                matchesOf5++;
                return false;
            case 6:
                matchesOf6++;
                jackpotHit = true;
                return true;
            default:
                return false;
        }
    }

    /**
     * Reset all game state to initial values.
     */
    public void reset() {
        picks = new int[NUM_PICKS];
        winners = new int[NUM_PICKS];
        matchesOf3 = 0;
        matchesOf4 = 0;
        matchesOf5 = 0;
        matchesOf6 = 0;
        drawingCount = 0;
        jackpotHit = false;
    }

    // --- Getters ---

    public int[] getPicks() {
        return picks;
    }

    public int[] getWinners() {
        return winners;
    }

    public int getMatchesOf3() {
        return matchesOf3;
    }

    public int getMatchesOf4() {
        return matchesOf4;
    }

    public int getMatchesOf5() {
        return matchesOf5;
    }

    public int getMatchesOf6() {
        return matchesOf6;
    }

    public int getDrawingCount() {
        return drawingCount;
    }

    public float getYears() {
        return (float) drawingCount / DRAWINGS_PER_YEAR;
    }

    public boolean isJackpotHit() {
        return jackpotHit;
    }
}
