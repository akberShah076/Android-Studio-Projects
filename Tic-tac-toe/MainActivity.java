package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText newEdtPlayer1Name, newEdtPlayer2Name;
    private Button newBtnStart;
    private RatingBar newRatingBarGreen, newRatingBarRed;
    private int newTimeLimitMillis = 10000;

    private double newDividingLine = 0.5;  // New addition for the biasing trick

    static final int REQUEST_CODE_GAME = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newEdtPlayer1Name = findViewById(R.id.edtPlayer1Name);
        newEdtPlayer2Name = findViewById(R.id.edtPlayer2Name);
        newBtnStart = findViewById(R.id.btnStart);
        newRatingBarGreen = findViewById(R.id.ratingBarGreen);
        newRatingBarRed = findViewById(R.id.ratingBarRed);

        newBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // Replace 'your_menu_name' with your menu XML file name without .xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_reset) {
            newEdtPlayer1Name.setText("Player 1");
            newEdtPlayer2Name.setText("Player 2");
            newRatingBarGreen.setRating(0);
            // Resets the rating bar for Player 1
            newRatingBarRed.setRating(0);
            // Resets the rating bar for Player 2
            newBtnStart.setVisibility(View.VISIBLE);
            return true;
        } else if (itemId == R.id.action_1s) {
            newTimeLimitMillis = 1000;
            // 1s in milliseconds
            return true;
        } else if (itemId == R.id.action_2s) {
            newTimeLimitMillis = 2000;
            // 2s in milliseconds
            return true;
        } else if (itemId == R.id.action_5s) {
            newTimeLimitMillis = 5000;
            // 5s in milliseconds
            return true;
        } else if (itemId == R.id.action_10s) {
            newTimeLimitMillis = 10000;
            // 10s in milliseconds
            return true;
        } else {
            return super.onOptionsItemSelected(item);

        }
    }

    private void startGame() {
        String newPlayer1Name = newEdtPlayer1Name.getText().toString().trim();
        String newPlayer2Name = newEdtPlayer2Name.getText().toString().trim();

        if (newPlayer1Name.isEmpty() || newPlayer2Name.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter names for both players", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNewGreenStarting;
        if (new Random().nextDouble() < newDividingLine) {
            isNewGreenStarting = true;
            newDividingLine -= 0.1;
            // Adjust dividing line if green starts
        } else {
            isNewGreenStarting = false;
            newDividingLine += 0.1;
            // Adjust dividing line if red starts
        }

        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("PLAYER_1_NAME", newPlayer1Name);
        intent.putExtra("PLAYER_2_NAME", newPlayer2Name);
        intent.putExtra("TIME_LIMIT_MILLIS", newTimeLimitMillis);
        // Pass the time limit to MainActivity2
        intent.putExtra("IS_GREEN_STARTING", isNewGreenStarting);
        // Pass the starting player info

        // Starting ActivityA
        startActivityForResult(intent, REQUEST_CODE_GAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result comes from ActivityA and is OK
        if (requestCode == REQUEST_CODE_GAME && resultCode == Activity.RESULT_OK) {

            // Retrieve the game result from the returned intent
            if (data != null) {
                String gameResult = data.getStringExtra("gameResult");

                // Check game results and update rating bars accordingly
                if ("1".equals(gameResult)) { // If Player 1 won
                    newRatingBarGreen.setRating(newRatingBarGreen.getRating() + 1);
                } else if ("2".equals(gameResult)) { // If Player 2 won
                    newRatingBarRed.setRating(newRatingBarRed.getRating() + 1);
                } else if ("3".equals(gameResult)) { // Draw
                    // Do nothing or handle the draw if needed
                }

                // Check if any player reaches 5 stars
                if (newRatingBarGreen.getRating() == 5) {
                    Toast.makeText(this, "Player 1 Won!", Toast.LENGTH_SHORT).show();
                    newBtnStart.setVisibility(View.INVISIBLE);
                } else if (newRatingBarRed.getRating() == 5) {
                    Toast.makeText(this, "Player 2 Won!", Toast.LENGTH_SHORT).show();
                    newBtnStart.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
