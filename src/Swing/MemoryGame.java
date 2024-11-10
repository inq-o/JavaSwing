package Swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class MemoryGame extends JFrame {
    private static final int SIZE = 4;  // Set game size
    private static final int GAME_TIME_LIMIT = 60; // TODO: Change value for setting difficulties
    private final JButton[] buttons = new JButton[SIZE * SIZE];
    private final Card[] cardValues = new Card[SIZE * SIZE];
    private final boolean[] cardFlipped = new boolean[SIZE * SIZE];  // Status of card is flipped on board
    private int flippedCount = 0;  // Currently flipped card count
    private int firstFlipped = -1;  // Firstly flipped card's index
    private int secondFlipped = -1; // Secondly flipped card's index
    private final Timer timer;  // Processing card flipping time
    private int timeLeft = GAME_TIME_LIMIT;
    private boolean blockClicks = false;  // Flag that blocks click
    private final JLabel titleLabel;


    public MemoryGame() {
        setTitle("Memory Game - 4X4");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        titleLabel = new JLabel("Memory Game - Time Left: " + timeLeft + " Sec", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setBackground(Color.CYAN);
        titleLabel.setOpaque(true);
        add(titleLabel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(SIZE, SIZE));

        // Generating card values
        List<Card> values = new ArrayList<>();

        for (int i = 0; i < SIZE * SIZE / 2; i++) {
            for (int j = 0; j < 2; j++) {
                Card card = new Card("" + (i + 1));
                values.add(card);
            }
        }

        Collections.shuffle(values);
        values.toArray(cardValues);

        Timer timeLimit = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                if (timeLeft <= 0) {
                    JOptionPane.showMessageDialog(MemoryGame.this,
                            "Time's up!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                updateTitle();
            }
        });


        // Generating card buttons
        for (int i = 0; i < SIZE * SIZE; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(Color.LIGHT_GRAY);
            buttons[i].addActionListener(new ButtonClickListener(i));
            cardPanel.add(buttons[i]);
        }

        add(cardPanel, BorderLayout.CENTER);

        timeLimit.start();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (firstFlipped != -1 && secondFlipped != -1) {
                    if (!cardValues[firstFlipped].name.equals(cardValues[secondFlipped].name)) {
                        // If two cards are not matched, flip all
                        buttons[firstFlipped].setText("");
                        buttons[secondFlipped].setText("");
                    }
                    cardFlipped[firstFlipped] = false;
                    cardValues[firstFlipped].isFlipped = true;
                    cardFlipped[secondFlipped] = false;
                    cardValues[secondFlipped].isFlipped = true;
                    flippedCount = 0;
                    firstFlipped = -1;
                    secondFlipped = -1;
                    blockClicks = false;  // After the cards flipped reactivate click

                    checkWin();
                }
            }
        });

        timer.setRepeats(false);  // Set timer for once
    }

    // Process when card button has clicked
    private class ButtonClickListener implements ActionListener {
        private final int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cardFlipped[index] || blockClicks) return;  // If card is already flipped or blockClicks is true, ignore action

            // Flipping card
            cardFlipped[index] = true;
            buttons[index].setText(cardValues[index].name);

            flippedCount++;

            // First card is flipped
            if (flippedCount == 1) {
                firstFlipped = index;
            }
            // Second card is flipped
            else if (flippedCount == 2) {
                secondFlipped = index;
                blockClicks = true;  // Blocks click
                timer.start();  // Check card status after 1sec
            }
        }
    }

    private void checkWin() {
        boolean allFlipped = true;

        for (Card c : cardValues) {
            if (!c.isFlipped) {
                allFlipped = false;
                break;
            }
        }

        if (allFlipped) {
            JOptionPane.showMessageDialog(this,
                    "All cards are flipped!", "Exit game", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void updateTitle() {
        titleLabel.setText("Memory Game - Time Left: " + timeLeft + " Sec");
    }

    public void run() {
        SwingUtilities.invokeLater(() -> new MemoryGame().setVisible(true));
    }
}