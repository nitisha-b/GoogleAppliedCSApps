package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import java.lang.Math;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int userOverallScore = 0;
    private int userTurnScore = 0;
    private int compOverallScore = 0;
    private int compTurnScore = 0;

    ImageView imageView;
    TextView scoreLabel;

    Button holdButton;
    Button resetButton;
    Button rollButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);

        holdButton = (Button) findViewById(R.id.button);
        resetButton = (Button) findViewById(R.id.button3);
        rollButton = (Button) findViewById(R.id.button2);

    }

    public void onClickRoll(View view){
        int randInt = (int)(Math.random() * 6) + 1;
        String turnScoreLabel = "";

        // Display a new dice face when Roll is clicked
        changeImage(randInt);

        if(randInt == 1){

            userTurnScore = 0;
            turnScoreLabel = "Your Score: "+Integer.toString(userOverallScore)+ " Computer Score: "+
                    Integer.toString(compOverallScore);

            scoreLabel.setText(turnScoreLabel);
            rollButton.setEnabled(false);
        }
        else{
            // Update the user turn score and update the label
            userTurnScore += randInt;

            turnScoreLabel = "Your Score: "+Integer.toString(userOverallScore)+
                    " Computer Score: "+Integer.toString(compOverallScore)+" Your Turn Score: "+Integer.toString(userTurnScore);

            scoreLabel.setText(turnScoreLabel);
        }
    }

    public void onClickHold(View view){

        userOverallScore += userTurnScore;

        String holdScoreLabel = "Your Score: "+Integer.toString(userOverallScore)+ " Computer Score: "+
                Integer.toString(compOverallScore);

        scoreLabel.setText(holdScoreLabel);

        // Start computer's turn until compTurnScore <= 20
        resetTurnScores();
        computerTurn();
    }

    public void onClickReset(View view){
        resetTurnScores();
        compOverallScore = 0;
        compTurnScore = 0;

        scoreLabel.setText("Your Score: Computer Score: 0");
        holdButton.setEnabled(true);
        rollButton.setEnabled(true);
    }

    public void changeImage(int randInt){

        //String newImage = "dice"+ Integer.toString(randInt);

        if (randInt == 1) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dice1));
        }
        if (randInt == 2) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dice2));
        }
        if (randInt == 3) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dice3));
        }
        if (randInt == 4) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dice4));
        }
        if (randInt == 5) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dice5));
        }
        if (randInt == 6) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.dice6));
        }

    }

    public void computerTurn(){

        // Disable the hold and reset buttons
        holdButton.setEnabled(false);
        rollButton.setEnabled(false);

        String turnScoreLabel = "";

        int randInt = (int)(Math.random() * 6) + 1;
        changeImage(randInt);

        if (randInt == 1){

            compTurnScore = 0;

            turnScoreLabel = "Your Score: "+Integer.toString(userOverallScore)+ " Computer Score: "+
                    Integer.toString(compOverallScore);

            scoreLabel.setText(turnScoreLabel);

            holdButton.setEnabled(true);
            rollButton.setEnabled(true);

        }
        else {
            compTurnScore += randInt;

            turnScoreLabel = "Your Score: " + Integer.toString(userOverallScore) + " Computer Score: " +
                    Integer.toString(compOverallScore) + " Computer Turn Score: " + Integer.toString(compTurnScore);

            scoreLabel.setText(turnScoreLabel);

            // Set a delay by 1 second between each iteration
            if (compTurnScore < 20) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        computerTurn();
                    }
                }, 1000);
                return;
            }
        }
        compOverallScore += compTurnScore;

        resetTurnScores();
        holdButton.setEnabled(true);
        rollButton.setEnabled(true);

        String compScoreLabel = "\nComputer Holds.";
        turnScoreLabel += compScoreLabel;
        scoreLabel.setText(turnScoreLabel);

    }

    public void resetTurnScores(){
        userTurnScore = 0;
        compTurnScore = 0;
    }
}
