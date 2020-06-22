package com.bohannon.lotto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LottoInterface extends JFrame {
    LottoEvent lotto = new LottoEvent(this);

    // set up row 1
    JPanel row1 = new JPanel();
    ButtonGroup option = new ButtonGroup();
    JCheckBox quickpick = new JCheckBox("Quick Pick", false);
    JCheckBox personal = new JCheckBox("Personal", true);

    // set up row 2
    JPanel row2 = new JPanel();
    JLabel numbersLabel = new JLabel("Your picks: ", JLabel.RIGHT);
    JTextField[] numbers = new JTextField[6];
    JLabel winnersLabel = new JLabel("Winners: ", JLabel.RIGHT);
    JTextField[] winners = new JTextField[6];

    // set up row 3
    JPanel row3 = new JPanel();
    JButton stop = new JButton("Stop");
    JButton play = new JButton("Play");
    JButton reset = new JButton("Reset");
    JButton exit = new JButton("Exit");

    // set up row 4
    JPanel row4 = new JPanel();
    JLabel got3Label = new JLabel("3 of 6: ", JLabel.RIGHT);
    JTextField got3 = new JTextField("0");
    JLabel got4Label = new JLabel("4 of 6: ", JLabel.RIGHT);
    JTextField got4 = new JTextField("0");
    JLabel got5Label = new JLabel("5 of 6: ", JLabel.RIGHT);
    JTextField got5 = new JTextField("0");
    JLabel got6Label = new JLabel("6 of 6: ", JLabel.RIGHT);
    JTextField got6 = new JTextField("0", 10);
    JLabel drawingsLabel = new JLabel("Drawings: ", JLabel.RIGHT);
    JTextField drawings = new JTextField("0");
    JLabel yearsLabel = new JLabel("Years: ", JLabel.RIGHT);
    JTextField years = new JTextField();

    public LottoInterface() {
        super();

        setTitle("Bohannon Lotto Picks");
        setSize(550, 270);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GridLayout layout = new GridLayout(5, 1, 10, 10);
        setLayout(layout);

        // Add listeners
        quickpick.addItemListener(lotto);
        personal.addItemListener(lotto);
        stop.addActionListener(lotto);
        play.addActionListener(lotto);
        reset.addActionListener(lotto);
        exit.addActionListener(lotto);

        FlowLayout layout1 = new FlowLayout(FlowLayout.CENTER, 10, 10);

        option.add(quickpick);
        option.add(personal);
        row1.setLayout(layout1);
        row1.add(quickpick);
        row1.add(personal);

        add(row1);

        GridLayout layout2 = new GridLayout(2, 7, 10, 10);

        row2.setLayout(layout2);
        row2.add(numbersLabel);

        for (int i = 0; i < 6; i ++) {
            numbers[i] = new JTextField();
            numbers[i].setText(String.valueOf(i + 1)); // set the default numbers
            row2.add(numbers[i]);
        }

        row2.add(winnersLabel);

        for (int i = 0; i < 6; i ++) {
            winners[i] = new JTextField();
            winners[i].setEditable(false);
            row2.add(winners[i]);
        }

        add(row2);

        FlowLayout layout3 = new FlowLayout(FlowLayout.CENTER, 10, 10);

        row3.setLayout(layout3);
        stop.setEnabled(false);
        row3.add(stop);
        row3.add(play);
        row3.add(reset);
        row3.add(exit);

        add(row3);

        GridLayout layout4 = new GridLayout(2, 3, 20, 10);

        row4.setLayout(layout4);
        row4.add(got3Label);
        got3.setEditable(false);
        row4.add(got3);
        row4.add(got4Label);
        got4.setEditable(false);
        row4.add(got4);
        row4.add(got5Label);
        got5.setEditable(false);
        row4.add(got5);
        row4.add(got6Label);
        got6.setEditable(false);
        row4.add(got6);
        row4.add(drawingsLabel);
        drawings.setEditable(false);
        row4.add(drawings);
        row4.add(yearsLabel);
        years.setEditable(false);
        row4.add(years);

        add(row4);

        setWindowLocation();
        setVisible(true);
    }

    public void setWindowLocation() {
        // get the screen size
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        // create and pack the elements
        pack();
        // set the location
        Dimension size = getPreferredSize();
        setLocation((int) ((bounds.width / 2) - (size.getWidth() / 2)), (int) ((bounds.height / 2) - (size.getHeight() / 2)));
    }

    public static void main(String[] arguments) {
        final LottoInterface FRAME = new LottoInterface();

        FRAME.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FRAME.dispose();
                System.exit(0);
            }
        });
    }
}