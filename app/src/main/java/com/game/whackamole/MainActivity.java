package com.game.whackamole;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    //Constants (Game Settings)
    final int MAX_MOLES_SPAWNED = 3;
    final int MAX_TIME = 30;
    final int MAX_LIVES = 3;
    final int SPAWN_TIME = 1000;
    final int WINNING_SCORE = 30;

    //Variables for views
    private ImageButton[] hole;
    private TextView timeTV, scoreTV;
    private View menu, game;
    private ImageButton playBtn;
    private Button leaderBoardsBtn;
    private TextView status;
    private TextView reason;
    private TextView nameTV;

    //Game Variables
    private int[] holeType;//0-nothing,1-mole,2-bomb
    private Timer timer;
    private int time;
    private int score=0;
    private Random r;
    private int lives = 3;
    private boolean spawn = true;
    private String name;



    private AnimationDrawable correctAnimation;

    //Location
    String location =",";
    LocationManager locationManager ;

    Location currentLocation;

    public void saveScore() {

        ContentValues values = new ContentValues();
        values.put(ScoresProvider.NAME,
                name);

        values.put(ScoresProvider.SCORE,
                score);

        values.put(ScoresProvider.LOCATION,
                location);

        Uri uri = getContentResolver().insert(
                ScoresProvider.CONTENT_URI, values);




    }



    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location loc) {
            currentLocation=loc;
            location=loc.getLatitude()+","+loc.getLongitude();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);



            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {



            }else {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5,
                        0, mLocationListener);

            }




        // Set to fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set view
        setContentView(R.layout.activity_main);

        //Init variables
        r=new Random();
        holeType=new int[9];

        //Init Views
        menu=findViewById(R.id.menu);
        game=findViewById(R.id.game_layout);
        status=findViewById(R.id.statusText);
        timeTV=findViewById(R.id.timeTV);
        reason=findViewById(R.id.reasonTV);
        scoreTV=findViewById(R.id.scoreTV);

        //Set up buttons (Holes)
        hole=new ImageButton[9];
        hole[0]=findViewById(R.id.hole1);
        hole[1]=findViewById(R.id.hole2);
        hole[2]=findViewById(R.id.hole3);
        hole[3]=findViewById(R.id.hole4);
        hole[4]=findViewById(R.id.hole5);
        hole[5]=findViewById(R.id.hole6);
        hole[6]=findViewById(R.id.hole7);
        hole[7]=findViewById(R.id.hole8);
        hole[8]=findViewById(R.id.hole9);

        for(int i=0;i<9;i++){
            hole[i].setOnClickListener(this);
        }

        //Set up Play btn
        playBtn=findViewById(R.id.play_btn);
        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.setVisibility(View.GONE);
                game.setVisibility(View.VISIBLE);


                resetGame();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startGame();
                            }
                        });
                    }
                };
                timer = new Timer();
                timer.schedule(task, 1000);

            }
        });
        leaderBoardsBtn=findViewById(R.id.leaderboards_btn);
        leaderBoardsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LeaderboardsActivity.class);
                startActivity(intent);
            }
        });

        //Get the name from the Entry Activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        nameTV=findViewById(R.id.playerName);
        nameTV.setText(name);

    }


    //This functions resets the game variables
    public void resetGame(){
        for(int i=0;i<9;i++){
            holeType[i]=0;
        }
        //Inistialize game variables
        time=MAX_TIME; //Set time to 30seconds
        score=0;
        lives=MAX_LIVES;

        updateUI();
        updateTimeText();
        scoreTV.setText("Score:"+score);
    }




    //This functions removes or spawns moles
    public void spawnMoles(){

        if(spawn) {


            //pick an number between f5 and MAX_MOLES_SPAWNED
            int numberOfMoles=r.nextInt(MAX_MOLES_SPAWNED-1)+2;

            //Spawns "numberOfMoles" moles
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i=1; i<9; i++) {
                list.add(new Integer(i));
            }
            Collections.shuffle(list);
            for (int i=0; i<numberOfMoles; i++) {
               holeType[list.get(i)] = 1;
            }

            //add a bomb
            if(r.nextBoolean()){
                holeType[r.nextInt(9)] = 2;
            }



        }else{
            //Remove all moles
            for(int i=0;i<9;i++){
                holeType[i] = 0;
            }
        }
        spawn=!spawn;
        updateUI();
    }

    //Updates the UI,changes the images based on the holeHasMole variable
    public void updateUI(){
        for(int i=0;i<9;i++){
            if(holeType[i]==1){
                hole[i].setImageResource(R.drawable.hole_with_mole);

            }else if(holeType[i]==2){
                hole[i].setImageResource(R.drawable.hole_with_bomb);
            }
                else{
                hole[i].setImageResource(R.drawable.hole_without_mole);
            }
        }
    }

    //Starts the game,set up the timer
    public void startGame(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimer();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, new Date(), 1000);


        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spawnMoles();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task2, new Date(), SPAWN_TIME);

    }

    //Check if the hole has a mole or not
    public void checkHole(int i){
        if(holeType[i]==1){
            score++;

            holeType[i]=0;
            hole[i].setImageResource(R.drawable.animation);
            correctAnimation=(AnimationDrawable) hole[i].getDrawable();
            correctAnimation.run();

        }else if(holeType[i]==2){
            score-=3;
            holeType[i]=0;
            hole[i].setImageResource(R.drawable.hole_false);
        }else{
           lives--;

            updateTimeText();
            hole[i].setImageResource(R.drawable.hole_false);
        }
        checkGameOver();

        scoreTV.setText("Score:"+score);
    }

    //Updates the time text view
    public void updateTimeText(){
        timeTV.setText("Time:"+time+"sec"+"\nLives:"+lives);
    }

    //Changs the view visibility to gameover
    public void gameOver(String r){
        timer.cancel();
        menu.setVisibility(View.VISIBLE);
        status.setText("Game Over");
        reason.setText(r);

        game.setVisibility(View.GONE);
        saveScore();
    }

    //Checks if the game is over
    public void checkGameOver(){
        //Check if time is up
        if(time<=0){
            gameOver("Your time is up!\nScore:"+score);

        }
        //Check the number of lives
        if(lives<=0){
            gameOver("You have missed three points.\nScore:"+score);
        }

    }

    //Update the timer
    public void updateTimer(){
        time--;

        updateTimeText();
        checkGameOver();

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.hole1:
                checkHole(0);
                break;
            case R.id.hole2:
                checkHole(1);
                break;
            case R.id.hole3:
                checkHole(2);
                break;
            case R.id.hole4:
                checkHole(3);
                break;
            case R.id.hole5:
                checkHole(4);
                break;
            case R.id.hole6:
                checkHole(5);
                break;
            case R.id.hole7:
                checkHole(6);
                break;
            case R.id.hole8:
                checkHole(7);
                break;
            case R.id.hole9:
                checkHole(8);
                break;
        }
    }


}
