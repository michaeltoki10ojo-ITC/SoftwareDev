package com.example.calculatorapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This is the main class for my calculator app.
 * It controls the buttons, display, and calculations.
 */
public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String expression = "";

    /**
     * This runs when the calculator first opens.
     * It connects the display and all of the calculator buttons.
     *
     * @param savedInstanceState saves the current state of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        setupButton(R.id.button0, "0");
        setupButton(R.id.button1, "1");
        setupButton(R.id.button2, "2");
        setupButton(R.id.button3, "3");
        setupButton(R.id.button4, "4");
        setupButton(R.id.button5, "5");
        setupButton(R.id.button6, "6");
        setupButton(R.id.button7, "7");
        setupButton(R.id.button8, "8");
        setupButton(R.id.button9, "9");

        setupButton(R.id.buttonDecimal, ".");
        setupButton(R.id.buttonPlus, "+");
        setupButton(R.id.buttonMinus, "-");
        setupButton(R.id.buttonMultiply, "*");
        setupButton(R.id.buttonDivide, "/");
        setupButton(R.id.buttonOpen, "(");
        setupButton(R.id.buttonClose, ")");

        Button acButton = findViewById(R.id.buttonAC);
        Button cButton = findViewById(R.id.buttonC);
        Button equalsButton = findViewById(R.id.buttonEquals);

        acButton.setOnClickListener(v -> {
            expression = "";
            display.setText("");
        });

        cButton.setOnClickListener(v -> {
            if (!expression.isEmpty()) {
                expression = expression.substring(0, expression.length() - 1);
                display.setText(expression);
            }
        });

        equalsButton.setOnClickListener(v -> calculate());
    }

    /**
     * Sets up the buttons on the calculator.
     *
     * @param buttonId button being used
     * @param value value of the button
     */
    private void setupButton(int buttonId, String value) {

        Button button = findViewById(buttonId);

        button.setOnClickListener(v -> {
            expression += value;
            display.setText(expression);
        });
    }

    /**
     * Calculates the problem entered on the screen.
     */
    private void calculate() {

        try {

            String originalExpression = expression;

            double result = new ExpressionParser(expression).parse();

            String resultText;

            if (result == (long) result) {
                resultText = String.valueOf((long) result);
            } else {
                resultText = String.format("%.10f", result)
                        .replaceAll("0+$", "")
                        .replaceAll("\\.$", "");
            }

            display.setText(
                    originalExpression + " = " + resultText
            );

            expression = resultText;

        } catch (Exception e) {

            display.setText("Error");
            expression = "";
        }
    }

    /**
     * Reads the math problem and solves it.
     */
    private static class ExpressionParser {

        private final String text;
        private int position = -1;
        private int currentChar;

        /**
         * Creates the parser using the expression from the calculator.
         *
         * @param text the math expression being solved
         */
        ExpressionParser(String text) {
            this.text = text;
        }

        /**
         * Starts solving the problem.
         *
         * @return the final result
         */
        double parse() {

            nextChar();

            double result = parseExpression();

            if (position < text.length()) {
                throw new RuntimeException("Unexpected character");
            }

            return result;
        }

        /**
         * Moves to the next character.
         */
        private void nextChar() {

            position++;

            if (position < text.length()) {
                currentChar = text.charAt(position);
            } else {
                currentChar = -1;
            }
        }

        /**
         * Checks the next part of the math problem.
         *
         * @param charToEat character being checked
         * @return true if it matches
         */
        private boolean eat(int charToEat) {

            while (currentChar == ' ') {
                nextChar();
            }

            if (currentChar == charToEat) {
                nextChar();
                return true;
            }

            return false;
        }

        /**
         * Handles addition and subtraction.
         *
         * @return the result of the expression
         */
        private double parseExpression() {

            double value = parseTerm();

            while (true) {

                if (eat('+')) {
                    value += parseTerm();

                } else if (eat('-')) {
                    value -= parseTerm();

                } else {
                    return value;
                }
            }
        }

        /**
         * Handles multiplication and division.
         *
         * @return the result of the term
         */
        private double parseTerm() {

            double value = parseFactor();

            while (true) {

                if (eat('*')) {
                    value *= parseFactor();

                } else if (eat('/')) {
                    value /= parseFactor();

                } else {
                    return value;
                }
            }
        }

        /**
         * Handles numbers, decimals, negative numbers,
         * and parentheses.
         *
         * @return the number or value inside the parentheses
         */
        private double parseFactor() {

            if (eat('+')) {
                return parseFactor();
            }

            if (eat('-')) {
                return -parseFactor();
            }

            double value;
            int start = this.position;

            if (eat('(')) {

                value = parseExpression();

                if (!eat(')')) {
                    throw new RuntimeException("Missing parenthesis");
                }

            } else if (
                    (currentChar >= '0' && currentChar <= '9')
                            || currentChar == '.'
            ) {

                while (
                        (currentChar >= '0' && currentChar <= '9')
                                || currentChar == '.'
                ) {
                    nextChar();
                }

                value = Double.parseDouble(
                        text.substring(start, this.position)
                );

            } else {

                throw new RuntimeException("Unexpected character");
            }

            return value;
        }
    }
}