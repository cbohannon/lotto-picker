package com.bohannon.lotto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class LottoEvent implements ItemListener, ActionListener, Runnable {

    private LottoInterface gui;
    private Thread playing;

    public LottoEvent(LottoInterface in) {
        gui = in;
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
            for (int i = 0; i < 6; i ++) {
                int pick;
                do {
                    pick = (int) Math.floor(Math.random() * 50 + 1);
                } while (numberGone(pick, gui.numbers, i));
                gui.numbers[i].setText("" + pick);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                gui.numbers[i].setText(null);
            }
        }
    }

    void addOneToField(JTextField field) {
        int num = Integer.parseInt("0" + field.getText());
        num ++;
        field.setText("" + num);
    }

    boolean numberGone(int num, JTextField[] pastNums, int count) {
        for (int i = 0; i < count; i ++) {
            if (Integer.parseInt(pastNums[i].getText()) == num) {
                return true;
            }
        }
        return false;
    }

    boolean matchedOne(JTextField win, JTextField[] allPicks) {
        for (int i = 0; i < 6; i ++) {
            String winText = win.getText();
            if ( winText.equals( allPicks[i].getText() ) ) {
                return true;
            }
        }
        return false;
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        while (playing == thisThread) {
            addOneToField(gui.drawings);
            int draw = Integer.parseInt(gui.drawings.getText());
            float numYears = (float)draw / 104;
            gui.years.setText("" + numYears);

            setMatchedField(pullWinningNumbers());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    private int pullWinningNumbers() {
        int matches = 0;
        for (int i = 0; i < 6; i ++) {
            int ball;
            do {
                ball = (int) Math.floor(Math.random() * 50 + 1);
            } while (numberGone(ball, gui.winners, i));
            gui.winners[i].setText("" + ball);
            if (matchedOne(gui.winners[i], gui.numbers)) {
                matches++;
            }
        }

        return matches;
    }

    private void setMatchedField(int matches) {
        // We are ignoring 1 or 2 matches
        switch (matches) {
            case 3:
                addOneToField(gui.got3);
                break;
            case 4:
                addOneToField(gui.got4);
                break;
            case 5:
                addOneToField(gui.got5);
                break;
            case 6:
                addOneToField(gui.got6);
                gui.stop.setEnabled(false);
                gui.play.setEnabled(true);
                playing = null;
        }
    }
}