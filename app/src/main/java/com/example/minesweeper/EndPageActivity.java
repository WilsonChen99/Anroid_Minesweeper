package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndPageActivity extends AppCompatActivity {
    private String mStatus;
    private String mTimeUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_page);
        Intent intent = getIntent();
        mStatus = intent.getStringExtra("status");
        mTimeUsed = intent.getStringExtra("timeUsed");
        SetText();


    }

    // This function sets the end page text information when called
    private void SetText()
    {
        TextView timeTV = findViewById(R.id.timeUsed);
        TextView statusTV = findViewById(R.id.gameStatus);
        TextView commentTV = findViewById(R.id.finalComment);

        String msg = "Used " + mTimeUsed + " seconds.";
        timeTV.setText(msg);
        msg = "You " + mStatus + ".";
        statusTV.setText(msg);
        if(mStatus.equals("won"))
        {
            msg = "Good job!";
        }
        else
        {
            msg = "Don't worry. Fight on!";
        }
        commentTV.setText(msg);
    }

    // Starts the Main Activity
    public void onClickPlayAgain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
