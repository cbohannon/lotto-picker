package com.bohannon.lotto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class LottoEvent implements ItemListener, ActionListener, Runnable {

    private LottoInterface gui;
    private LottoEngine engine;
    private Thread playing;

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
            gui.numbers[i].setText(null);
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
     */
    private void syncPicksFromGui() {
        int[] picks = new int[6];
        for (int i = 0; i < 6; i++) {
            picks[i] = Integer.parseInt("0" + gui.numbers[i].getText());
        }
        engine.setPicks(picks);
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
        gui.years.setText("" + engine.getYears());
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        while (playing == thisThread) {
            syncPicksFromGui();
            engine.runOneDrawing();
            syncGuiFromEngine();

            if (engine.isJackpotHit()) {
                stopPlaying();
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }
}
