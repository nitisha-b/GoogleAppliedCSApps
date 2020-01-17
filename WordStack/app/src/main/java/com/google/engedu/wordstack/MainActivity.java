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

package com.google.engedu.wordstack;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private View word1LinearLayout;
    private View word2LinearLayout;

    private int wordLength = 3;

    private Stack<LetterTile> placedTiles = new Stack<>();
    private HashMap<Integer, ArrayList<String>> wordMap = new HashMap<>();

    private TextView winText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();

                if(wordMap.containsKey(word.length())){
                    wordMap.get(word.length()).add(word);
                }
                else{
                    ArrayList<String> list = new ArrayList<>();
                    list.add(word);
                    wordMap.put(word.length(), list);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        word1LinearLayout = findViewById(R.id.word1);
        word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());

        word2LinearLayout = findViewById(R.id.word2);
        word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());

        winText = findViewById(R.id.winText);

    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);

                placedTiles.push(tile);

                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);

                    placedTiles.push(tile);

                    if (stackedLayout.empty()) {
                        getUserWords();
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {

        ArrayList<String> finalArray = wordMap.get(wordLength);
        wordLength++;
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        ((LinearLayout) word1LinearLayout).removeAllViews();
        ((LinearLayout) word2LinearLayout).removeAllViews();
        stackedLayout.clear();

        winText.setText("");
        Random randInt = new Random();
        int wordsSize = finalArray.size();

        int random1 = randInt.nextInt(wordsSize);
        int random2 = randInt.nextInt(wordsSize);

        if (random1 == random2){
            random2 = randInt.nextInt(wordsSize);
        }

        word1 = finalArray.get(random1);
        word2 = finalArray.get(random2);

        // Stacks for word1, word2 and scrambled word
        Stack<Character> word1Stack = new Stack<>();
        Stack<Character> word2Stack = new Stack<>();
        Stack<Character> scrambledWord = new Stack<>();

        for(int i = 0; i < word1.length(); i++){
            word1Stack.push(word1.charAt(i));
        }
        for(int i = 0; i < word2.length(); i++){
            word2Stack.push(word2.charAt(i));
        }

        int counter1 = 0;
        int counter2 = 0;

        while(counter1 < word1Stack.size() && counter2 < word2Stack.size()){
            int pickWord = randInt.nextInt(2);

            if(pickWord == 0){
                // pop from word1 stack and push to scrambledWord
                scrambledWord.push(word1Stack.pop());

                // increment counter1
                counter1++;
            }
            else if(pickWord == 1){
                // pop from word2 stack
                scrambledWord.push(word2Stack.pop());

                // increment counter2
                counter2++;
            }
        }

        // If the words are of different lengths
        while(!word1Stack.empty()){
            scrambledWord.push(word1Stack.pop());
        }
        while(!word2Stack.empty()){
            scrambledWord.push(word2Stack.pop());
        }

        // Converting the char stack to a string
        String finalWord = "";
        while(!scrambledWord.empty()){
            finalWord += scrambledWord.pop().toString();
        }

        // Push LetterTiles of each letter in reverse order to stackedLayout
        for(int i = finalWord.length()-1; i >= 0; i--){
            LetterTile letterTile = new LetterTile(this, finalWord.charAt(i));
            stackedLayout.push(letterTile);
        }

        messageBox.setText(finalWord + "\n" + "Word length: "+Integer.toString(wordLength-1));

        return true;
    }

    public boolean onUndo(View view) {

        if(!placedTiles.empty()){
            LetterTile pop = placedTiles.pop();

            pop.moveToViewGroup(stackedLayout);
        }
        //dragToUndo();
        return true;
    }

    public void getUserWords(){

        LinearLayout layout1 = (LinearLayout) word1LinearLayout;
        LinearLayout layout2 = (LinearLayout) word2LinearLayout;
        int count1 = layout1.getChildCount();
        int count2 = layout2.getChildCount();

        View v;
        String userWord1 = "";
        String userWord2 = "";

        for(int i=0; i<count1; i++) {
            v = layout1.getChildAt(i);
            Character letter = ((LetterTile)v).getLetter();
            userWord1 += letter.toString();
        }

        for(int i = 0; i < count2; i++){
            v = layout2.getChildAt(i);
            Character letter = ((LetterTile)v).getLetter();
            userWord2 += letter.toString();
        }

        String text;
        if(words.contains(userWord1) && words.contains(userWord2)){
            text = "You Win!";
        }
        else{
            text = "You Lose :(";
        }
        winText.setText(text);
    }

    public void dragToUndo(){
        View view = ((StackedLayout) word1LinearLayout).peek();
        ((LetterTile)view).unfreeze();
    }
}
