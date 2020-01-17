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

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWord = new HashMap<>();

    private int defaultLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;

        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);

            // Check if sorted word is in the hash map already
            String hashMapKey = sortLetters(word);

            if(lettersToWord.containsKey(hashMapKey)){
                // Add the words to the arraylist for that particular key
                lettersToWord.get(hashMapKey).add(word);
            }

            // If sorted word is not already a key in the hash map, make a new arraylist and
            // add the key-value pair as a new entry in the hash map
            else{
                ArrayList<String> newList = new ArrayList<>();
                newList.add(word);
                lettersToWord.put(hashMapKey, newList);
            }

            // Add all the dictionary words to a hash set
            wordSet.add(word);

            // sizeToWord Hashmap - length of words as key and the arraylist of words as value
            int wordLength = word.length();

            if(sizeToWord.containsKey(wordLength)){
                sizeToWord.get(wordLength).add(word);
            }
            else{
                ArrayList<String> keyList = new ArrayList<>();
                keyList.add(word);
                sizeToWord.put(wordLength, keyList);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {

        boolean isGoodWord = false;

        // Check if wordSet contains the word
        if(wordSet.contains(word)){

            // Check to make sure <base> is not a substring in <word>
            if(!word.contains(base)){
                isGoodWord = true;
            }
        }
        return isGoodWord;

    }

    public ArrayList<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();

        String sortedTargetWord = sortLetters(targetWord);

        for (String word : wordList) {
            String sortedWord = sortLetters(word); //Sorted words from the wordlist

            if (sortedTargetWord.equals(sortedWord) && sortedTargetWord.length() == sortedWord.length()) {
                result.add(word);
            }
        }
        return result;
    }

    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> idvArrayLists = new ArrayList<>();
        //ArrayList<String> bigArrayList = new ArrayList<>();

        for(char c = 'a'; c <= 'z'; c++){
            String newWord = word + String.valueOf(c);

            idvArrayLists = getAnagrams(newWord);
            String sortedNewWord = sortLetters(newWord);

            if(lettersToWord.containsKey(sortedNewWord)){
                for(String checkNewWord : idvArrayLists){
                    if(lettersToWord.get(sortedNewWord).contains(checkNewWord)){
                        result.add(checkNewWord);
                    }
                }

            }
        }

        return result;
    }

    public String pickGoodStarterWord() {

        Random rand = new Random();
        String pickedWord = "";

        ArrayList<String> newWordList = sizeToWord.get(defaultLength);

        if(defaultLength < MAX_WORD_LENGTH)
            defaultLength++;

        // Generate a random number from 0 to wordList.size()-1
        int randomIndex = rand.nextInt(newWordList.size());

        while(true) {

            String randomWord = newWordList.get(rand.nextInt(newWordList.size()));
            ArrayList<String> anagrams = getAnagramsWithOneMoreLetter(randomWord);

            if (anagrams.size() >= MIN_NUM_ANAGRAMS) {
                pickedWord = randomWord;
                return pickedWord;
            }
        }
    }

    public String sortLetters(String word){
        char wordToSort[] = word.toCharArray();
        Arrays.sort(wordToSort);
        String sortedWord = new String(wordToSort);

        return sortedWord;
    }
}
