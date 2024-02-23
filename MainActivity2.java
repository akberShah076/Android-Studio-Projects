package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {

    private View newBtnPlayerOneIndicator;
    private View newBtnPlayerTwoIndicator;
    private ProgressBar newProgressBarTime;
    private Button[] newButtons;
    private CountDownTimer newTimer;
    private boolean isNewPlayerOneTurn = true; // Assuming Player One (Green) starts
    private long newTimeLimitMillis; // 5 seconds (customize according to your needs)

    private TextView newTvPlayerTurn;

    private String newPlayer1Name;
    private String newPlayer2Name;

    private int[] newGameState;  // To keep track of clicked buttons
    private static final int NEW_PLAYER_ONE = 1;
    private static final int NEW_PLAYER_TWO = 2;
    private static final int NEW_EMPTY = 0;
    private boolean newGameIsActive = true;  // To keep track if the game is active

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        newBtnPlayerOneIndicator = findViewById(R.id.btnPlayerOneIndicator);
        newBtnPlayerTwoIndicator = findViewById(R.id.btnPlayerTwoIndicator);
        newProgressBarTime = findViewById(R.id.progressBarTime);
        newTvPlayerTurn = findViewById(R.id.tvPlayerTurn);
        Intent intent = getIntent();
        if (intent != null) {
            newPlayer1Name = intent.getStringExtra("PLAYER_1_NAME");
            newPlayer2Name = intent.getStringExtra("PLAYER_2_NAME");
            newTimeLimitMillis = intent.getIntExtra("TIME_LIMIT_MILLIS", 10000);
            isNewPlayerOneTurn = getIntent().getBooleanExtra("IS_GREEN_STARTING", true);  // Default to true if no value is provided

        }
        newGameState = new int[9];
        Arrays.fill(newGameState, NEW_EMPTY);
        newButtons = new Button[9];
        newButtons[0] = findViewById(R.id.button1);
        newButtons[1] = findViewById(R.id.button2);
        newButtons[2] = findViewById(R.id.button3);
        newButtons[3] = findViewById(R.id.button4);
        newButtons[4] = findViewById(R.id.button5);
        newButtons[5] = findViewById(R.id.button6);
        newButtons[6] = findViewById(R.id.button7);
        newButtons[7] = findViewById(R.id.button8);
        newButtons[8] = findViewById(R.id.button9);

        // Logic to set on click listeners for all buttons
        for(Button button : newButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGridButtonClick((Button) v);
                }
            });
        }

        startTurn();
    }

    private void startTurn() {
        // Handle logic to start a player's turn
        newBtnPlayerOneIndicator.setVisibility(isNewPlayerOneTurn ? View.VISIBLE : View.INVISIBLE);
        newBtnPlayerTwoIndicator.setVisibility(isNewPlayerOneTurn ? View.INVISIBLE : View.VISIBLE);
        newTvPlayerTurn.setText(isNewPlayerOneTurn ? newPlayer1Name : newPlayer2Name);

        newProgressBarTime.setProgress(100); // assuming max is 100

        if(newTimer != null) {
            newTimer.cancel();
        }

        newTimer = new CountDownTimer(newTimeLimitMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                newProgressBarTime.setProgress((int) (100 * millisUntilFinished / newTimeLimitMillis));
            }

            @Override
            public void onFinish() {
                newProgressBarTime.setProgress(0);
                onTimeRunOut();
            }
        }.start();
    }

    private void onGridButtonClick(Button clickedButton) {
        // Logic to handle a button click in the grid
        // Ensure the button hasn’t been clicked yet
        if (isNewButtonClickable(clickedButton)) {
            // Example: Coloring the button
            clickedButton.setBackgroundColor(isNewPlayerOneTurn ?
                    getResources().getColor(android.R.color.holo_green_light) :
                    getResources().getColor(android.R.color.holo_red_light));

            // Mark the button as clicked in your game state logic (this would depend on how you're tracking it)
            markButtonAsClicked(clickedButton);

            // Check for win
            if (isNewCheckForWin()) {
                endGame((isNewPlayerOneTurn ? "1" : "2"));
                return;
            }

            // Check for draw
            if (isNewCheckForDraw()) {
                endGame("3");
                return;
            }

            // Switch player turns
            isNewPlayerOneTurn = !isNewPlayerOneTurn;
            startTurn();
        }
    }

    private boolean isNewButtonClickable(Button clickedButton) {
        int buttonIndex = getButtonIndex(clickedButton);
        return newGameState[buttonIndex] == NEW_EMPTY;
    }

    private void markButtonAsClicked(Button clickedButton) {
        int buttonIndex = getButtonIndex(clickedButton);
        newGameState[buttonIndex] = isNewPlayerOneTurn ? NEW_PLAYER_ONE : NEW_PLAYER_TWO;
    }

    private int getButtonIndex(Button clickedButton) {
        // Iterate through all buttons and return the index of the clicked one
        for (int i = 0; i < newButtons.length; i++) {
            if (newButtons[i].getId() == clickedButton.getId()) {
                return i;
            }
        }
        return -1; // Error case: button not found (should not happen)
    }

    private boolean isNewCheckForWin() {
        int[][] winPositions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // columns
                {0, 4, 8}, {2, 4, 6}              // diagonals
        };

        for (int[] position : winPositions) {
            if (newGameState[position[0]] != NEW_EMPTY &&
                    newGameState[position[0]] == newGameState[position[1]] &&
                    newGameState[position[0]] == newGameState[position[2]]) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewCheckForDraw() {
        for (int state : newGameState) {
            if (state == NEW_EMPTY) {
                return false;  // Game is not a draw since there is an empty spot
            }
        }
        return true;  // All spots are taken, game is a draw
    }

    private void endGame(String message) {
        newGameIsActive = false;

        // Create an intent and put the message (result) in it
        Intent resultIntent = new Intent();
        resultIntent.putExtra("gameResult", message);

        // Set result and finish the activity
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
        // Optionally: Add a "Play Again" button or reset the game automatically after a delay
    }

    private void onTimeRunOut() {
        // Handle logic when time runs out
        Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show();

        // Switch player turns
        isNewPlayerOneTurn = !isNewPlayerOneTurn;
        startTurn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure timer is canceled to prevent leaks
        if(newTimer != null) {
            newTimer.cancel();
        }
    }
}
