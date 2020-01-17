/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String wordFragment = "";
    SimpleDictionary simpleDictionary;

    TextView gameStatus;
    TextView ghostText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        gameStatus = findViewById(R.id.gameStatus);
        ghostText = findViewById(R.id.ghostText);

        InputStream wordsInputStream = null;
        try {
            wordsInputStream = assetManager.open("words.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            simpleDictionary = new SimpleDictionary(wordsInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again

        //String possibleLongerWord = simpleDictionary.getAnyWordStartingWith(wordFragment);
        String possibleLongerWord = simpleDictionary.getGoodWordStartingWith(wordFragment);

        Log.i("WORD", wordFragment);
        gameStatus.setText(COMPUTER_TURN);

        if(simpleDictionary.isWord(wordFragment) && wordFragment.length() >= 4){
            gameStatus.setText("Computer Wins! \n" + wordFragment+ " is a word.");
        }

        if(possibleLongerWord == null){
            gameStatus.setText("Computer Wins! \n" + wordFragment + " is not a prefix of any word.");
            return;
        }
        else{
            wordFragment += possibleLongerWord.charAt(wordFragment.length());
            ghostText.setText(wordFragment);
        }

        userTurn = true;
        gameStatus.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        char pressedKey = (char) event.getUnicodeChar();

        boolean isValidKey = Character.isLetter(pressedKey);

        if(isValidKey){
            wordFragment += pressedKey;
        }

        if(simpleDictionary.isWord(wordFragment)){
            gameStatus.setText("Is Complete Word");
        }

        ghostText.setText(wordFragment);
        gameStatus.setText(COMPUTER_TURN);

        if(isValidKey){
            userTurn = false;
            computerTurn();
        }

        return super.onKeyUp(keyCode, event);
    }

    public void onChallenge(View view){

        //String possibleLongerWord = simpleDictionary.getAnyWordStartingWith(wordFragment);
        String possibleLongerWord = simpleDictionary.getAnyWordStartingWith(wordFragment);

        Log.i("WORD", wordFragment);

        if(wordFragment.length() >= 4 && simpleDictionary.isWord(wordFragment)){
            gameStatus.setText("User Wins!");
        }
        if(possibleLongerWord != null){
            String text = "Computer Wins! \nPossible Word: "+possibleLongerWord;
            gameStatus.setText(text);
        }
        else if(possibleLongerWord == null){
            gameStatus.setText("User Wins!");
        }
    }

    public void onRestart(View view){
        gameStatus.setText(USER_TURN);
        ghostText.setText("");
        wordFragment = "";
        onStart(view);

    }
}
