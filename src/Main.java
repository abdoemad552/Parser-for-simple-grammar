
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

public class Main extends JFrame {
    private static final int WIDTH  = 500;
    private static final int HEIGHT = 400;
    private static final String TITLE = "Simple Parser";
    private static final char[] RULES = {'S', 'A'};
    private static final Font TIMES = new Font("Times New Roman", Font.PLAIN, 22);
    private static final Color ACCEPTED_COLOR = new Color(83, 187, 0);
    private static final Color REJECTED_COLOR = new Color(238, 0, 0);

    private JPanel mainPanel;
    private JPanel inputPanel;
    private JPanel buttonsPanel;
    private JPanel[] rulesPanels;
    private JLabel[][] rulesLabels;
    private JTextField[][] rulesInputs;
    private JTextArea inputArea;
    private JButton parseButton;
    private JButton exitButton;

    public Main() {
        super(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        initMainPanel();
        initInputPanel();
        initParsePanel();

        this.add(mainPanel, BorderLayout.NORTH);
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(Boolean.TRUE);
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new GridLayout(4, 2));

        rulesLabels = new JLabel[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                rulesLabels[i][j] = new JLabel(
                    "Rule " + (j + 1) + " for " + RULES[i]);
                rulesLabels[i][j].setFont(TIMES);
                rulesLabels[i][j].setForeground(Color.BLACK);
            }
        }

        rulesInputs = new JTextField[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                rulesInputs[i][j] = new JTextField(20);
                rulesInputs[i][j].setFont(TIMES);
                rulesInputs[i][j].setForeground(Color.BLACK);
                rulesInputs[i][j].addKeyListener(new KeyEventListener());
            }
        }

        rulesPanels = new JPanel[4];
        for (int i = 0; i < 4; i++) {
            rulesPanels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER));
        }

        for (int i = 0; i < 4; i++) {
            rulesPanels[i].add(rulesLabels[(2 & i) > 0 ? 1 : 0][(1 & i) > 0 ? 1 : 0]);
            rulesPanels[i].add(rulesInputs[(2 & i) > 0 ? 1 : 0][(1 & i) > 0 ? 1 : 0]);
        }

        for (int i = 0; i < 4; i++) {
            mainPanel.add(rulesPanels[i]);
        }
    }

    private void initInputPanel() {
        inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        inputArea = new JTextArea(8, 40);
        inputArea.setFont(TIMES);
        inputArea.setForeground(Color.BLACK);
        inputArea.addKeyListener(new KeyEventListener());
        inputArea.setLineWrap(Boolean.TRUE);

        inputPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(Color.BLACK, 2, Boolean.TRUE),
            "Input String",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            TIMES.deriveFont(Font.ITALIC),
            Color.BLACK
        ));

        inputPanel.add(inputArea);
    }

    private void initParsePanel() {
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        parseButton = new JButton("Parse");
        parseButton.addActionListener(new ParseButtonListener());
        parseButton.setFont(TIMES);
        parseButton.setMnemonic(KeyEvent.VK_P);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setFont(TIMES);
        exitButton.setMnemonic(KeyEvent.VK_X);

        buttonsPanel.add(parseButton);
        buttonsPanel.add(exitButton);
    }

    private class ParseButtonListener implements ActionListener {
        private static final Stack<Character> stk;
        private static String[] S;
        private static String[] A;

        static {
            stk = new Stack<>();
            S = new String[2];
            A = new String[2];
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            S[0] = rulesInputs[0][0].getText();
            S[1] = rulesInputs[0][1].getText();
            A[0] = rulesInputs[1][0].getText();
            A[1] = rulesInputs[1][1].getText();

            if (!isSimpleGrammar(S, A)) {
                reject("Not Simple Grammar"); return;
            }

            //////////////////////////////
            //  LOGIC STARTS FROM HERE  //
            //////////////////////////////

            stk.clear();
            stk.push('S');

            String inputString = inputArea.getText();
            for (int i = 0; i < inputString.length(); i++) {
                char ch = inputString.charAt(i);
                if (!stk.isEmpty()) {
                    char top = stk.peek();
                    if (top == 'S') {
                        if (ch == S[0].charAt(0)) {
                            replace(S[0]); // Replace
                            i--; // Retain
                        } else if (ch == S[1].charAt(0)) {
                            replace(S[1]); // Replace
                            i--; // Retain
                        } else {
                            reject("Rejected"); return;
                        }
                    } else if (top == 'A') {
                        if (ch == A[0].charAt(0)) {
                            replace(A[0]); // Replace
                            i--; // Retain
                        } else if (ch == A[1].charAt(0)) {
                            replace(A[1]); // Replace
                            i--; // Retain
                        } else {
                            reject("Rejected");
                            return;
                        }
                    } else if (top == ch) {
                        stk.pop(); // Pop
                        // Advance.
                    } else {
                        reject("Rejected"); return;
                    }
                } else {
                    reject("Rejected"); return;
                }
            }

            if (stk.isEmpty()) {
                accept("Accepted");
            } else {
                reject("Rejected");
            }
        }

        private boolean isSimpleGrammar(String[] S, String[] A) {
            if (
                S[0].isEmpty() || S[1].isEmpty() || A[0].isEmpty() || A[1].isEmpty() ||
                Character.isUpperCase(S[0].charAt(0)) ||
                Character.isUpperCase(S[1].charAt(0)) ||
                Character.isUpperCase(A[0].charAt(0)) ||
                Character.isUpperCase(A[1].charAt(0)) ||
                S[0].charAt(0) == S[1].charAt(0) || A[0].charAt(0) == A[1].charAt(0)
            ) {
                return false;
            }

            return true;
        }

        private void replace(String rule) {
            stk.pop();
            for (int i = rule.length() - 1; i >= 0; i--) {
                stk.push(rule.charAt(i));
            }
        }

        private void accept(String text) {
            TitledBorder border = (TitledBorder) inputPanel.getBorder();
            border.setTitle(text);
            border.setTitleColor(ACCEPTED_COLOR);
            border.setBorder(new LineBorder(ACCEPTED_COLOR, 2, Boolean.TRUE));
            inputPanel.repaint();
        }

        private void reject(String text) {
            TitledBorder border = (TitledBorder) inputPanel.getBorder();
            border.setTitle(text);
            border.setTitleColor(REJECTED_COLOR);
            border.setBorder(new LineBorder(REJECTED_COLOR, 2, Boolean.TRUE));
            inputPanel.repaint();
        }
    }

    private class KeyEventListener implements KeyListener {
        @Override public void keyReleased(KeyEvent e) {}
        @Override public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            TitledBorder border = (TitledBorder) inputPanel.getBorder();
            border.setTitle("Input String");
            border.setTitleColor(Color.BLACK);
            border.setBorder(new LineBorder(Color.BLACK, 2, Boolean.TRUE));
            inputPanel.repaint();
        }
    }

    public static void main(String[] args) { new Main(); }
}