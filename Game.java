import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import java.util.Random;

/**
 * Game - Whack-a-mole game using Swing. Each button represents a hole through
 * which a mole may pop up, go down or get hit. The default button state
 * represents the "down" position (light gray background, empty text).
 * The "up" position is represented with a green background and the ":P" symbol.
 * A "hit" is represented with a "X" symbol and a red background.
 * @author Tony Padilla (apadilla)
 */
public final class Game extends JFrame {

    /**
     * Constant that represents the GUI frame width.
     */
    private static final int FRAME_WIDTH = 520;

    /**
     * Constant that represents the GUI frame height.
     */
    private static final int FRAME_HEIGHT = 240;

    /**
     * Constant that represents the columns in the text fields.
     */
    private static final int FIELD_COL = 5;

    /**
     * Constant representing the game time limit in seconds.
     */
    private static final int TIME_LIMIT = 20;

    /**
     * Constant representing the amount of buttons in the game.
     */
    private static final int NUM_BUTTONS = 20;

    /**
     * Constant representing the font size of the buttons.
     */
    private static final int FONT_SIZE = 16;

    /**
     * Constant representing the number of rows of buttons.
     */
    private static final int LAYOUT_R = 4;

    /**
     * Constant representing the number of columns of buttons.
     */
    private static final int LAYOUT_C = 5;

    /**
     * Constant representing the horizontal and vertical gaps between buttons.
     */
    private static final int GAP = 5;

    /**
     * Represents the time.
     */
    private int time = TIME_LIMIT;

    /**
     * Reference to the score text field.
     */
    private JTextField scoreField;

    /**
     * Keeps track of the game state.
     */
    private boolean isGameOver = true;

    /**
     * Constructor for the Whack-a-mole game.
     */
    public Game() {
        setTitle("Whack-a-mole GUI");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        JPanel topPane = new JPanel();
        JPanel buttonsPane =
            new JPanel(new GridLayout(LAYOUT_R, LAYOUT_C, GAP, GAP));
        JButton startButton = new JButton("Start");
        JLabel timeLabel = new JLabel("Time Left: ");
        JTextField timeField = new JTextField(FIELD_COL);
        timeField.setEditable(false);
        JLabel scoreLabel = new JLabel("Score: ");
        scoreField = new JTextField(FIELD_COL);

        Font font = new Font(Font.MONOSPACED, Font.BOLD, FONT_SIZE);
        JButton[] buttons = new JButton[NUM_BUTTONS];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("   ");
            buttons[i].setBackground(Color.LIGHT_GRAY);
            buttons[i].setFont(font);
            buttons[i].setOpaque(true);
            buttonsPane.add(buttons[i]);
        }

        startButton.addActionListener(e -> {
            isGameOver = false;
            startButton.setEnabled(false);
            Thread startThread =
                new TimerThread(timeField, startButton);
            startThread.start();
            for (JButton mole : buttons) {
                Thread buttonThread = new ButtonThread(mole);
                buttonThread.start();
                mole.addActionListener(d -> {
                    if (mole.getText().equals(" :P ") && time != 0) {
                        mole.setBackground(Color.RED);
                        mole.setText(" X ");
                        int score =
                            Integer.parseInt(scoreField.getText());
                        scoreField.setText(String.valueOf(score + 1));
                    }
                });
            }
            scoreField.setText("0");
        });

        scoreField.setEditable(false);
        topPane.add(startButton);
        topPane.add(timeLabel);
        topPane.add(timeField);
        topPane.add(scoreLabel);
        topPane.add(scoreField);
        pane.add(topPane);
        pane.add(buttonsPane);

        setContentPane(pane);
        setVisible(true);
    }

    private class TimerThread extends Thread {

        /**
         * Constant representing one second sleep time.
         */
        private static final int SLEEP_ONE_S = 1000;

        /**
         * Constant representing five second sleep time.
         */
        private static final int SLEEP_FIVE_S = 5000;

        /**
         * Reference to the start button in the GUI.
         */
        private JButton sb;

        /**
         * Reference to the timer in the GUI.
         */
        private JTextField timeField;

        /**
         * Initializer for the TimerThread.
         * @param timeTextField reference to the time field.
         * @param startButton reference to the start button.
         */
        TimerThread(JTextField timeTextField, JButton startButton) {
            sb = startButton;
            timeField = timeTextField;
        }

        /**
         * Runs the thread.
         */
        @Override
        public void run() {
            try {
                timeField.setText(String.valueOf(time));
                while (time > 0) {
                    Thread.sleep(SLEEP_ONE_S);
                    time = time - 1;
                    timeField.setText(String.valueOf(time));
                }
                isGameOver = true;
                Thread.sleep(SLEEP_FIVE_S);
                sb.setEnabled(true);
                timeField.setText("");
                time = TIME_LIMIT;
                scoreField.setText(" ");
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
    }

    private class ButtonThread extends Thread {

        /**
         * Constant representing the maximum number of seconds a button may be
         * in an "up" position.
         */
        private static final double MAX_U = 4.0;

        /**
         * Constant representing the minimum number of seconds a button may be
         * in an "up" position.
         */
        private static final double MIN_U = 0.5;

        /**
         * Constant representing the maximum number of seconds a button may be
         * in a "down" position.
         */
        private static final double MAX_D = 4.0;

        /**
         * Constant representing the minimum number of seconds a button may be
         * in a "down" position.
         */
        private static final double MIN_D = 2.0;

        /**
         * Constant representing one second.
         */
        private static final int TIME_S = 1000;

        /**
         * Reference to the accompanying button.
         */
        private JButton mole;

        /**
         * Represents the amount of seconds a button will spend in the "down"
         * position before going "up".
         */
        private Random downTime = new Random();

        /**
         * Represents the amount of seconds a button will spend in the "up"
         * position before going "down".
         */
        private Random upTime = new Random();

        /**
         * Initializer for ButtonThread.
         * @param button the accompanying button reference.
         */
        ButtonThread(JButton button) {
            mole = button;
        }

        /**
         * Runs the thread.
         */
        @Override
        public void run() {
            try {
                while (!isGameOver) {
                    if (time == 0) {
                        mole.setBackground(Color.LIGHT_GRAY);
                        mole.setText("   ");
                    } else {
                        synchronized (mole) {
                            double randomUpTime =
                                upTime.nextDouble() * (MAX_U - MIN_U) + MIN_U;
                            double randomDownTime =
                                downTime.nextDouble() * (MAX_D - MIN_D) + MIN_D;
                            mole.setBackground(Color.GREEN);
                            mole.setText(" :P ");
                            Thread.sleep((int) randomUpTime * TIME_S);
                            mole.setBackground(Color.LIGHT_GRAY);
                            mole.setText("   ");
                            Thread.sleep((int) randomDownTime * TIME_S);
                        }
                    }
                }
                mole.setBackground(Color.LIGHT_GRAY);
                mole.setText("   ");
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * Runs the whack-a-mole GUI.
     * @param args No arguments for this game.
     */
    public static void main(String[] args) {
        synchronized (Game.class) {
            new Game();
        }
    }

}





