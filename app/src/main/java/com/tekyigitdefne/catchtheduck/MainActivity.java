package com.tekyigitdefne.catchtheduck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Array;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    TextView scoreText;
    TextView timeText;
    int score;
    SharedPreferences sp;
    ImageView duck1;
    ImageView duck2;
    ImageView duck3;
    ImageView duck4;
    ImageView duck5;
    ImageView duck6;
    ImageView duck7;
    ImageView duck8;
    ImageView duck9;
    ImageView[] images;

    Runnable runnable;
    Handler handler;
    Button startButton;

    TextView topScoreText;
    int topScore;


    boolean isPaused = false;
    CountDownTimer countDownTimer;
    long timeLeft = 10000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp = this.getSharedPreferences("com.tekyigitdefne.catchtheduck", Context.MODE_PRIVATE);
        topScore = sp.getInt("topScore", 0);

        scoreText = findViewById(R.id.textViewScore);
        timeText = findViewById(R.id.textViewTime);
        topScoreText = findViewById(R.id.textViewTopScore);
        score = 0;
        topScoreText.setText("Top Score: " + topScore);
        duck1 = findViewById(R.id.imageView1);
        duck2 = findViewById(R.id.imageView2);
        duck3 = findViewById(R.id.imageView3);
        duck4 = findViewById(R.id.imageView4);
        duck5 = findViewById(R.id.imageView5);
        duck6 = findViewById(R.id.imageView6);
        duck7 = findViewById(R.id.imageView7);
        duck8 = findViewById(R.id.imageView8);
        duck9 = findViewById(R.id.imageView9);
        images = new ImageView[] {duck1,duck2,duck3,duck4,duck5,duck6,duck7,duck8,duck9};

        startButton = findViewById(R.id.buttonStart);
        hiding();
    }


    public void start(View view){
        startButton.setVisibility(View.INVISIBLE);

        countDownTimer =new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                if (!isPaused) {
                    timeLeft = l;
                    timeText.setText("Time: " + l / 1000);
                }
            }
            @Override
            public void onFinish() {
                timeText.setText("Time: off");
                handler.removeCallbacks(runnable);
                hiding();
                saveTopScore();
                showGameOverDialog();
            }
        }.start();
        hideDucks();
    }

    public void pause(View view){
        if (countDownTimer == null || handler == null) {
            Toast.makeText(MainActivity.this, "Game hasn't started yet!", Toast.LENGTH_SHORT).show();
            return;
        }
        isPaused = true;
        countDownTimer.cancel();
        handler.removeCallbacks(runnable);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("PAUSED");
        alert.setMessage("Do you want to continue the game?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resumeGame();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Game over!", Toast.LENGTH_SHORT).show();
                finishAffinity();
                System.exit(0);
            }
        });
        alert.show();
    }

    public void resumeGame() {
        isPaused = false;
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                if (!isPaused) {
                    timeLeft = l;
                    timeText.setText("Time: " + l / 1000);
                }
            }
            @Override
            public void onFinish() {
                timeText.setText("Time: off");
                handler.removeCallbacks(runnable);
                hiding();
                saveTopScore();
                showGameOverDialog();
            }
        }.start();

        hideDucks();
    }

    public void showGameOverDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("TIME IS UP!");
        alert.setMessage("Do you want to restart the game?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this,"Game over!",Toast.LENGTH_SHORT).show();
                finishAffinity();
                System.exit(0);
            }
        });
        alert.show();
    }

    public void increaseScore(View view){
        score++;
        scoreText.setText("Score: " + score);
    }

    public void hideDucks(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                hiding();
                Random random = new Random();
                int i = random.nextInt(9);
                images[i].setVisibility(View.VISIBLE);
                handler.postDelayed(this,500);
            }
        };
        handler.post(runnable);
    }

    public void hiding(){
        for(ImageView image : images){
            image.setVisibility(View.INVISIBLE);
        }
    }

    public void saveTopScore(){
        topScore = sp.getInt("topScore",0);;
        if(score > topScore){
        sp.edit().putInt("topScore",score).apply();
        topScoreText.setText("TOP SCORE: " + score);
        }
    }


}