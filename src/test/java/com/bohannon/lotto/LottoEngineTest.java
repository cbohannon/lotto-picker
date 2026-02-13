package com.bohannon.lotto;

import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class LottoEngineTest {

    // ---- numberAlreadyUsed ----

    @Test
    public void testNumberAlreadyUsed_foundAtStart() {
        LottoEngine engine = new LottoEngine();
        int[] numbers = {5, 10, 15, 20, 25, 30};
        assertTrue(engine.numberAlreadyUsed(5, numbers, 6));
    }

    @Test
    public void testNumberAlreadyUsed_foundAtEnd() {
        LottoEngine engine = new LottoEngine();
        int[] numbers = {5, 10, 15, 20, 25, 30};
        assertTrue(engine.numberAlreadyUsed(30, numbers, 6));
    }

    @Test
    public void testNumberAlreadyUsed_notFound() {
        LottoEngine engine = new LottoEngine();
        int[] numbers = {5, 10, 15, 20, 25, 30};
        assertFalse(engine.numberAlreadyUsed(7, numbers, 6));
    }

    @Test
    public void testNumberAlreadyUsed_respectsCountParameter() {
        LottoEngine engine = new LottoEngine();
        int[] numbers = {5, 10, 15, 0, 0, 0};
        // 15 is at index 2, but count=2 only checks indices 0-1
        assertFalse(engine.numberAlreadyUsed(15, numbers, 2));
        assertTrue(engine.numberAlreadyUsed(15, numbers, 3));
    }

    @Test
    public void testNumberAlreadyUsed_emptyCount() {
        LottoEngine engine = new LottoEngine();
        int[] numbers = {5, 10, 15, 20, 25, 30};
        assertFalse(engine.numberAlreadyUsed(5, numbers, 0));
    }

    // ---- countMatches ----

    @Test
    public void testCountMatches_noMatches() {
        LottoEngine engine = new LottoEngine();
        int[] picks = {1, 2, 3, 4, 5, 6};
        int[] winners = {7, 8, 9, 10, 11, 12};
        assertEquals(0, engine.countMatches(picks, winners));
    }

    @Test
    public void testCountMatches_allMatch() {
        LottoEngine engine = new LottoEngine();
        int[] picks = {1, 2, 3, 4, 5, 6};
        int[] winners = {6, 5, 4, 3, 2, 1};
        assertEquals(6, engine.countMatches(picks, winners));
    }

    @Test
    public void testCountMatches_someMatch() {
        LottoEngine engine = new LottoEngine();
        int[] picks = {1, 2, 3, 4, 5, 6};
        int[] winners = {1, 2, 3, 10, 11, 12};
        assertEquals(3, engine.countMatches(picks, winners));
    }

    @Test
    public void testCountMatches_orderDoesNotMatter() {
        LottoEngine engine = new LottoEngine();
        int[] picks = {10, 20, 30, 40, 50, 1};
        int[] winners = {50, 1, 10, 7, 8, 9};
        assertEquals(3, engine.countMatches(picks, winners));
    }

    // ---- generateNumbers ----

    @Test
    public void testGenerateNumbers_returnsSixNumbers() {
        LottoEngine engine = new LottoEngine(new Random(42));
        int[] numbers = engine.generateNumbers();
        assertEquals(6, numbers.length);
    }

    @Test
    public void testGenerateNumbers_allInRange() {
        LottoEngine engine = new LottoEngine(new Random(42));
        int[] numbers = engine.generateNumbers();
        for (int n : numbers) {
            assertTrue("Number " + n + " out of range", n >= 1 && n <= 50);
        }
    }

    @Test
    public void testGenerateNumbers_noDuplicates() {
        LottoEngine engine = new LottoEngine(new Random(42));
        int[] numbers = engine.generateNumbers();
        Set<Integer> unique = new HashSet<>();
        for (int n : numbers) {
            assertTrue("Duplicate number: " + n, unique.add(n));
        }
    }

    @Test
    public void testGenerateNumbers_deterministicWithSeed() {
        LottoEngine engine1 = new LottoEngine(new Random(12345));
        LottoEngine engine2 = new LottoEngine(new Random(12345));
        assertArrayEquals(engine1.generateNumbers(), engine2.generateNumbers());
    }

    @Test
    public void testGenerateNumbers_multipleCallsAlwaysValid() {
        LottoEngine engine = new LottoEngine(new Random(99));
        for (int trial = 0; trial < 100; trial++) {
            int[] numbers = engine.generateNumbers();
            assertEquals(6, numbers.length);
            Set<Integer> unique = new HashSet<>();
            for (int n : numbers) {
                assertTrue(n >= 1 && n <= 50);
                assertTrue(unique.add(n));
            }
        }
    }

    // ---- generateQuickPick ----

    @Test
    public void testGenerateQuickPick_populatesPicks() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.generateQuickPick();
        int[] picks = engine.getPicks();
        assertNotNull(picks);
        assertEquals(6, picks.length);
        for (int p : picks) {
            assertTrue(p >= 1 && p <= 50);
        }
    }

    // ---- setPicks ----

    @Test
    public void testSetPicks_storesValues() {
        LottoEngine engine = new LottoEngine();
        int[] myPicks = {7, 14, 21, 28, 35, 42};
        engine.setPicks(myPicks);
        assertArrayEquals(myPicks, engine.getPicks());
    }

    @Test
    public void testSetPicks_defensiveCopy() {
        LottoEngine engine = new LottoEngine();
        int[] myPicks = {7, 14, 21, 28, 35, 42};
        engine.setPicks(myPicks);
        // Mutating the original should not affect the engine
        myPicks[0] = 999;
        assertEquals(7, engine.getPicks()[0]);
    }

    // ---- runOneDrawing ----

    @Test
    public void testRunOneDrawing_incrementsDrawingCount() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.setPicks(new int[]{1, 2, 3, 4, 5, 6});
        assertEquals(0, engine.getDrawingCount());
        engine.runOneDrawing();
        assertEquals(1, engine.getDrawingCount());
        engine.runOneDrawing();
        assertEquals(2, engine.getDrawingCount());
    }

    @Test
    public void testRunOneDrawing_populatesWinners() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.setPicks(new int[]{1, 2, 3, 4, 5, 6});
        engine.runOneDrawing();
        int[] winners = engine.getWinners();
        for (int w : winners) {
            assertTrue(w >= 1 && w <= 50);
        }
    }

    @Test
    public void testRunOneDrawing_returnsMatchCount() {
        // Pre-compute what the seeded random will generate, then set picks to match some
        LottoEngine probe = new LottoEngine(new Random(99));
        int[] expectedWinners = probe.generateNumbers();

        LottoEngine engine = new LottoEngine(new Random(99));
        // Set picks to exactly the winning numbers — should get 6 matches
        engine.setPicks(expectedWinners);
        int matches = engine.runOneDrawing();
        assertEquals(6, matches);
    }

    // ---- recordMatches ----

    @Test
    public void testRecordMatches_threeOfSix() {
        LottoEngine engine = new LottoEngine();
        assertFalse(engine.recordMatches(3));
        assertEquals(1, engine.getMatchesOf3());
        assertFalse(engine.recordMatches(3));
        assertEquals(2, engine.getMatchesOf3());
    }

    @Test
    public void testRecordMatches_fourOfSix() {
        LottoEngine engine = new LottoEngine();
        assertFalse(engine.recordMatches(4));
        assertEquals(1, engine.getMatchesOf4());
    }

    @Test
    public void testRecordMatches_fiveOfSix() {
        LottoEngine engine = new LottoEngine();
        assertFalse(engine.recordMatches(5));
        assertEquals(1, engine.getMatchesOf5());
    }

    @Test
    public void testRecordMatches_sixOfSix_isJackpot() {
        LottoEngine engine = new LottoEngine();
        assertTrue(engine.recordMatches(6));
        assertEquals(1, engine.getMatchesOf6());
        assertTrue(engine.isJackpotHit());
    }

    @Test
    public void testRecordMatches_zeroOneTwo_ignored() {
        LottoEngine engine = new LottoEngine();
        assertFalse(engine.recordMatches(0));
        assertFalse(engine.recordMatches(1));
        assertFalse(engine.recordMatches(2));
        assertEquals(0, engine.getMatchesOf3());
        assertEquals(0, engine.getMatchesOf4());
        assertEquals(0, engine.getMatchesOf5());
        assertEquals(0, engine.getMatchesOf6());
    }

    // ---- getYears ----

    @Test
    public void testGetYears_oneYear() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.setPicks(new int[]{1, 2, 3, 4, 5, 6});
        for (int i = 0; i < 104; i++) {
            engine.runOneDrawing();
        }
        assertEquals(1.0f, engine.getYears(), 0.001f);
    }

    @Test
    public void testGetYears_halfYear() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.setPicks(new int[]{1, 2, 3, 4, 5, 6});
        for (int i = 0; i < 52; i++) {
            engine.runOneDrawing();
        }
        assertEquals(0.5f, engine.getYears(), 0.001f);
    }

    @Test
    public void testGetYears_zeroDrawings() {
        LottoEngine engine = new LottoEngine();
        assertEquals(0.0f, engine.getYears(), 0.001f);
    }

    // ---- reset ----

    @Test
    public void testReset_clearsAllState() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.setPicks(new int[]{1, 2, 3, 4, 5, 6});
        engine.runOneDrawing();
        engine.recordMatches(3);
        engine.recordMatches(4);
        engine.recordMatches(5);

        engine.reset();

        assertEquals(0, engine.getDrawingCount());
        assertEquals(0, engine.getMatchesOf3());
        assertEquals(0, engine.getMatchesOf4());
        assertEquals(0, engine.getMatchesOf5());
        assertEquals(0, engine.getMatchesOf6());
        assertFalse(engine.isJackpotHit());
        assertEquals(0.0f, engine.getYears(), 0.001f);
    }

    // ---- Integration / stress test ----

    @Test
    public void testFullSimulation_manyDrawings() {
        LottoEngine engine = new LottoEngine(new Random(42));
        engine.setPicks(new int[]{7, 14, 21, 28, 35, 42});
        for (int i = 0; i < 10000; i++) {
            if (engine.isJackpotHit()) break;
            engine.runOneDrawing();
        }
        assertTrue(engine.getDrawingCount() > 0);
        // After many drawings we should have some 3-of-6 matches statistically
        assertTrue("Expected at least one 3-match in 10000 drawings",
                engine.getMatchesOf3() > 0);
    }
}
