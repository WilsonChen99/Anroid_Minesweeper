package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private TextView mRemainingMineView;
    private TextView mClockView;
    private ImageButton mGameModeBttn;
    private GridLayout mGridLayout;

    // Timer is ready to use on instantiation.
    // Use onClickStart and onClickEnd to use.
    private Timer mTimer;
    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the textView for timer && Set up timer
        mClockView = (TextView) findViewById(R.id.clockView);
        mTimer = new Timer(mClockView);

        // Setting up game state
        mGridLayout = (GridLayout) findViewById(R.id.gridLayout);
        mRemainingMineView = (TextView) findViewById(R.id.remainingMine);
        mGameModeBttn = (ImageButton) findViewById(R.id.gameMode);
        mGame = new Game(this, mRemainingMineView, mGridLayout, mGameModeBttn, mTimer);
    }

}