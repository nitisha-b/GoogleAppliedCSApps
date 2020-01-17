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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;


    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        int start = 0;
        int end = words.size()-1;

        Random randInt = new Random(words.size());
        prefix = prefix.toLowerCase();

        if(prefix == null){
            return words.get(randInt.nextInt());
        }
        else {
            Collections.sort(words);
            return binarySearch(prefix, words, start, end);
        }
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;

        ArrayList<String> evenWords = new ArrayList<>();
        ArrayList<String> oddWords = new ArrayList<>();
        ArrayList<String> copy = new ArrayList<>();
        copy.addAll(words);

        Random randInt = new Random(words.size());

        boolean prefixEven = (prefix.length() % 2 == 0);
        boolean isFinding = true;
        boolean wordEven = false;

        if(prefix.length() == 0) {
            return words.get(randInt.nextInt(words.size()));
        }
        else {
            Collections.sort(words);
//            while (isFinding) {
//                String word = binarySearch(prefix, copy, 0, copy.size() - 1);
//                if (word == null)
//                    isFinding = false;
//                else {
//                    if (word.length() % 2 == 0)
//                        evenWords.add(word);
//                    else
//                        oddWords.add(word);
//                    copy.remove(word);
//                }
//            }

            
        }

        if(prefixEven || oddWords.size() == 0){
            selected = evenWords.get(randInt.nextInt(evenWords.size()));
        }
        else if(!prefixEven || evenWords.size() == 0){
            selected = oddWords.get(randInt.nextInt(oddWords.size()));
        }

        return selected;
    }

    public String binarySearch(String prefix, ArrayList<String> wordList, int min, int max){

        prefix = prefix.toLowerCase();

      //  Collections.sort(words);

        while(min <= max) {

            int mid = (min + max)/2;
            String wordPref = words.get(mid);

            if (wordPref.startsWith(prefix)) {
                return words.get(mid);
            }

            else if (wordPref.compareTo(prefix) < 0) {
                min = mid + 1;
            }

            else {
                max = mid - 1;
            }
        }

        return null;
    }

}
