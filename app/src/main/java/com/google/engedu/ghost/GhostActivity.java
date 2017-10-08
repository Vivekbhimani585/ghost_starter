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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    private String wordFragment = "";
    private String cpuWord = null;
    private String userWord = null;
    String yourWord=null;
    android.os.Handler handler=new android.os.Handler();

    TextView inputText,status;
    Button challange,reset;
    int whoEndFirst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        inputText=(TextView)findViewById(R.id.ghostText);
        status=(TextView)findViewById(R.id.gameStatus) ;

        challange=(Button)findViewById(R.id.challange);
        reset=(Button)findViewById(R.id.restart);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new FastDictionary(inputStream);
        } catch (IOException e) {
            Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_SHORT).show();

        }
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        challange.setOnClickListener(this);
        reset.setOnClickListener(this);
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

    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        inputText.setText("");
        wordFragment="";
        whoEndFirst = userTurn ? 1:0;
        if (userTurn) {
            status.setText(USER_TURN);
        } else {
            status.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;

    }

    private void computerTurn() {
        status.setText(COMPUTER_TURN);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cpuWord = dictionary.getGoodWordStartingWith(wordFragment,whoEndFirst);

                if(cpuWord == "noWord"){
                    Toast.makeText(getApplicationContext(),"Computer Wins! No such Word",Toast.LENGTH_SHORT).show();
                    onStart(null);
                }
                else if(cpuWord == "sameAsPrefix"){
                    Toast.makeText(getApplicationContext(),"Computer Wins! You Ended the word",Toast.LENGTH_SHORT).show();
                    onStart(null);
                }
                else{
                    if(wordFragment.equals("")){
                        wordFragment = cpuWord.substring(0,1);
                    }else{
                        wordFragment = cpuWord.substring(0,wordFragment.length()+1);
                    }
                    inputText.setText(wordFragment);
                    Toast.makeText(getApplicationContext(),USER_TURN,Toast.LENGTH_SHORT).show();
                }
                userTurn = true;
                status.setText(USER_TURN);
            }
        },1000);


        userTurn = true;
        status.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {


        char inputKey=(char) event.getUnicodeChar();
        inputKey=Character.toLowerCase(inputKey);
        if(inputKey>='a' && inputKey<='z' )
        {
            wordFragment=String.valueOf(inputText.getText());
            wordFragment+=inputKey;
            inputText.setText(wordFragment);
            computerTurn();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Invalid Input...!!",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.challange:
                if(wordFragment.length() >= 4 ){
                     yourWord = dictionary.getAnyWordStartingWith(wordFragment);
                    if(yourWord == "noWord"){
                        Toast.makeText(getApplicationContext(),"You Wins! No such Word",Toast.LENGTH_SHORT).show();
                        onStart(null);
                    }
                    else if(yourWord == "sameAsPrefix"){
                        Toast.makeText(getApplicationContext(),"You Wins! Computer Ended the word",Toast.LENGTH_SHORT).show();
                        onStart(null);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Computer Wins! Word Exist",Toast.LENGTH_SHORT).show();
                        onStart(null);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Computer Wins! \nWord is Still Less then 4 Character",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.restart:
                onStart(null);
                break;
        }
    }
}
