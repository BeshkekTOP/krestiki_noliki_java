package com.example.krestiki_noliki;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isDarkTheme = false;
    private Button[] buttons = new Button[9];
    private boolean isPlayerOneTurn = true;
    private int roundCount = 0;
    private int playerOneWins = 0, playerTwoWins = 0, draws = 0;
    private TextView playerOneWinsText, playerTwoWinsText, drawsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        isDarkTheme = prefs.getBoolean("isDarkTheme", false);

        if (isDarkTheme) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_main);

        playerOneWinsText = findViewById(R.id.playerOneWins);
        playerTwoWinsText = findViewById(R.id.playerTwoWins);
        drawsText = findViewById(R.id.drawsText);

        for (int i = 0; i < 9; i++) {
            String buttonID = "button" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((Button) v).getText().toString().equals("")) {
                        return;
                    }

                    if (isPlayerOneTurn) {
                        ((Button) v).setText("X");
                    } else {
                        ((Button) v).setText("O");
                    }

                    roundCount++;

                    if (checkForWin()) {
                        if (isPlayerOneTurn) {
                            playerOneWins();
                        } else {
                            playerTwoWins();
                        }
                    } else if (roundCount == 9) {
                        draw();
                    } else {
                        isPlayerOneTurn = !isPlayerOneTurn;
                    }
                }
            });
        }

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        Button changeThemeButton = findViewById(R.id.changeThemeButton);
        changeThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme();
            }
        });

        loadStats();
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;

        SharedPreferences.Editor editor = getSharedPreferences("GamePrefs", MODE_PRIVATE).edit();
        editor.putBoolean("isDarkTheme", isDarkTheme);
        editor.apply();

        recreate();
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i * 3 + j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void playerOneWins() {
        playerOneWins++;
        updateStats();
        resetBoard();
    }

    private void playerTwoWins() {
        playerTwoWins++;
        updateStats();
        resetBoard();
    }

    private void draw() {
        draws++;
        updateStats();
        resetBoard();
    }

    private void resetBoard() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
        }
        roundCount = 0;
        isPlayerOneTurn = true;
    }

    private void resetGame() {
        playerOneWins = 0;
        playerTwoWins = 0;
        draws = 0;
        updateStats();
        resetBoard();
    }

    private void updateStats() {
        playerOneWinsText.setText("Победы X: " + playerOneWins);
        playerTwoWinsText.setText("Победы O: " + playerTwoWins);
        drawsText.setText("Ничьи: " + draws);

        SharedPreferences prefs = getSharedPreferences("GameStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("playerOneWins", playerOneWins);
        editor.putInt("playerTwoWins", playerTwoWins);
        editor.putInt("draws", draws);
        editor.apply();
    }

    private void loadStats() {
        SharedPreferences prefs = getSharedPreferences("GameStats", MODE_PRIVATE);
        playerOneWins = prefs.getInt("playerOneWins", 0);
        playerTwoWins = prefs.getInt("playerTwoWins", 0);
        draws = prefs.getInt("draws", 0);
        updateStats();
    }
}
