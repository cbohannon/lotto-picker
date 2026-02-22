package com.bohannon.lotto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class LottoEvent implements ItemListener, ActionListener, Runnable {

    private static final int[] SPEED_MS = {100, 10, 1, 0};
    private static final int GUI_UPDATE_INTERVAL_MS = 50;

    private LottoInterface gui;
    private LottoEngine engine;
    private volatile Thread playing;

    public LottoEvent(LottoInterface in) {
        gui = in;
        engine = new LottoEngine();
    }

    public LottoEvent(LottoInterface in, LottoEngine engine) {
        gui = in;
        this.engine = engine;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        switch (command) {
            case "Play":
                startPlaying();
                break;
            case "Stop":
                stopPlaying();
                break;
            case "Reset":
                clearAllFields();
                break;
            default:
                exitApplication();
                break;
        }
    }

    void startPlaying() {
        playing = new Thread(this);
        playing.start();
        gui.play.setEnabled(false);
        gui.stop.setEnabled(true);
        gui.reset.setEnabled(false);
        gui.quickpick.setEnabled(false);
        gui.personal.setEnabled(false);
    }

    void stopPlaying() {
        gui.stop.setEnabled(false);
        gui.play.setEnabled(true);
        gui.reset.setEnabled(true);
        gui.quickpick.setEnabled(true);
        gui.personal.setEnabled(true);
        playing = null;
    }

    void clearAllFields() {
        engine.reset();
        for (int i = 0; i < 6; i++) {
            gui.numbers[i].setText(String.valueOf(LottoEngine.DEFAULT_PICKS[i]));
            gui.winners[i].setText(null);
        }
        gui.got3.setText("0");
        gui.got4.setText("0");
        gui.got5.setText("0");
        gui.got6.setText("0");
        gui.drawings.setText("0");
        gui.years.setText("0");
    }

    void exitApplication() {
        if (playing != null) {
            playing = null;
        }

        gui.dispose();
        System.exit(0);
    }

    public void itemStateChanged(ItemEvent event) {
        Object item = event.getItem();
        if (item == gui.quickpick) {
            engine.generateQuickPick();
            int[] picks = engine.getPicks();
            for (int i = 0; i < 6; i++) {
                gui.numbers[i].setText("" + picks[i]);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                gui.numbers[i].setText(null);
            }
        }
    }

    /**
     * Read the current user picks from the GUI text fields into the engine.
     * Returns false and shows an error dialog if any field is invalid.
     */
    private boolean syncPicksFromGui() {
        int[] picks = new int[6];
        for (int i = 0; i < 6; i++) {
            int value;
            try {
                value = Integer.parseInt(gui.numbers[i].getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(gui,
                        "Pick #" + (i + 1) + " is not a valid number.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (value < 1 || value > LottoEngine.MAX_NUMBER) {
                JOptionPane.showMessageDialog(gui,
                        "Pick #" + (i + 1) + " must be between 1 and " + LottoEngine.MAX_NUMBER + ".",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            picks[i] = value;
        }
        engine.setPicks(picks);
        return true;
    }

    /**
     * Push all engine state back to the GUI text fields.
     */
    private void syncGuiFromEngine() {
        int[] winners = engine.getWinners();
        for (int i = 0; i < 6; i++) {
            gui.winners[i].setText("" + winners[i]);
        }
        gui.got3.setText("" + engine.getMatchesOf3());
        gui.got4.setText("" + engine.getMatchesOf4());
        gui.got5.setText("" + engine.getMatchesOf5());
        gui.got6.setText("" + engine.getMatchesOf6());
        gui.drawings.setText("" + engine.getDrawingCount());
        gui.years.setText(String.format("%.1f", engine.getYears()));
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        long lastGuiUpdate = 0;
        while (playing == thisThread) {
            if (!syncPicksFromGui()) {
                SwingUtilities.invokeLater(this::stopPlaying);
                break;
            }
            engine.runOneDrawing();

            long now = System.currentTimeMillis();
            if (now - lastGuiUpdate >= GUI_UPDATE_INTERVAL_MS) {
                SwingUtilities.invokeLater(this::syncGuiFromEngine);
                lastGuiUpdate = now;
            }

            if (engine.isJackpotHit()) {
                SwingUtilities.invokeLater(this::syncGuiFromEngine);
                SwingUtilities.invokeLater(this::stopPlaying);
                break;
            }

            int sleepMs = SPEED_MS[gui.speedCombo.getSelectedIndex()];
            if (sleepMs > 0) {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }
}
