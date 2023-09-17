package com.example.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashSet;

public class Game_ArrayList_Ver {
    // Top left textview of the main activity, prompting how many mines are there
    private TextView mRemainingMineView;
    private GridLayout mGridLayout;
    private Context mContext;
    private ImageButton mGameModeBttn;
    // Should be passed into constructor. Start when game start. Stop when victory or lose
    private Timer mTimer;
    // The amount of mines/remaining mines for this game
    private int mMines;
    private int mRemainingMines;
    // 0: game not active, 1: game active, 2: player wins, 3: player lose
    private int mStatus;
    // true: shovel; false: flag
    private boolean mGameMode;
    // Containing all the cells in the grid system
    private ArrayList<TextView> mCells;
    private HashSet<TextView> mShoveledCells;
    private HashSet<TextView> mFlaggedCells;
    // A set of cells that is randomly associated with mines at the start of the game
    private HashSet<TextView> mMineCells;
    // Custom utility class for needed functionalities
    private Util mUtil = new Util();

    // Constructor
    public Game_ArrayList_Ver(Context context, TextView remainingMineView, GridLayout gridLayout,
                              ImageButton imageBttn, Timer timer)
    {
        mContext = context;
        mRemainingMineView = remainingMineView;
        mGridLayout = gridLayout;
        mGameModeBttn = imageBttn;
        mTimer = timer;
        // Initially set game to not active
        mStatus = 0;
        mGameMode = true;
        // -1 means the game is not yet active
        mMines = -1;
        mRemainingMines = -1;
        mCells = new ArrayList<>();
        mShoveledCells = new HashSet<>();
        mFlaggedCells = new HashSet<>();
        mMineCells = new HashSet<>();
        setRemainingMineView();
        // setup the onclick event for game mode image bttn
        setGameModeBttn();
        // Set up the grid with 12(rows) x 10(cols) cells
        setGrid();
    }


    // [ On Click Events ]==========================================================================
    // Main onClick function. Call different onClick functions based on the game status.
    // GAME WIN/LOSE CAN BE POTENTIALLY CHECKED HERE!
    public void onClick(View view)
    {
        TextView clickedCell = (TextView) view;
        // If game is not active, we start the game
        if(mStatus == 0)
        {
            onClickStart();
            onClickShovel(clickedCell);
            mShoveledCells.add(clickedCell);
        }
        // If the game is active
        else if(mStatus == 1)
        {
            // mGameMode == true: shovel
            if(mGameMode)
            {
                // Check whether the clicked cell is already shoveled or flagged.
                // if yes, do nothing
                if(mShoveledCells.contains(clickedCell) || mFlaggedCells.contains(clickedCell))
                {
                    return;
                }
                // If the cell contains a mine, set the game to lose
                // Reveal all cells
                if(mMineCells.contains(clickedCell))
                {
                    mStatus = 3;
                    for(TextView tv: mCells)
                    {
                        onReveal(tv);
                    }
                    mTimer.onClickEnd();
                    return;
                }
                // If the clicked cell is not yet shoveled and valid,
                // process it and add it to the shoved list
                onClickShovel(clickedCell);
                mShoveledCells.add(clickedCell);
                // If this is the last valid cell to be shoveled, player wins
                if(mCells.size() - mShoveledCells.size() - mMines == 0)
                {
                    // Update the game status to win
                    mStatus = 2;
                    mTimer.onClickEnd();
                    // Reveal all bombs to victory icon
                    for(TextView tv: mMineCells)
                    {
                        onReveal(tv);
                    }
                }
            }
            // mGameMode == false: flag
            else
            {
                // check if the cell is already flagged. Call onClickUnFlag
                if(mFlaggedCells.contains(clickedCell))
                {
                    onClickUnFlag(clickedCell);
                }
                // if the cell is already shoveled, do nothing
                else if(mShoveledCells.contains(clickedCell))
                {
                    return;
                }
                // If the cell can be flagged, process it and add it to the flagged cell list
                // also update the remaining mine count
                else
                {
                    // Setting remaining mine count
                    mRemainingMines--;
                    setRemainingMineView();
                    // Flag cell and mark it as flagged
                    onClickFlag(clickedCell);
                    mFlaggedCells.add(clickedCell);
                }
            }
        }
    }


    private void onClickStart()
    {
        // Any block should be able to trigger the game to start
        // set the status to 1, which signals the game is active
        mStatus = 1;
        // Only start the timer when the game is first initiated
        mTimer.onClickStart();
        // On start, randomly set the mines count from 5 to 10
        mMines = mUtil.RandomNumGenerator(5, 10);
        mRemainingMines = mMines;
        setRemainingMineView();
        // Set up mines in randomly selected cells
        setMines();
    }

    private void onClickShovel(TextView tv)
    {
        tv.setBackgroundColor(Color.argb(80, 169,224,161));
    }

    private void onClickFlag(TextView tv)
    {
        tv.setBackground(mContext.getDrawable(R.drawable.icon_flag));
    }

    private void onClickUnFlag(TextView tv)
    {
        // We want to remove the cell from the clicked list
        // so that it can be flagged again if clicked again
        mFlaggedCells.remove(tv);
        tv.setBackgroundColor(Color.argb(99, 43, 61, 42));
    }

    private void onReveal(TextView tv)
    {
        if(!mShoveledCells.contains(tv))
        {
            // Case of losing: Reveal all mines associated with a bomb with bomb icon
            if(mMineCells.contains(tv) && mStatus == 3)
            {
                tv.setBackground(mContext.getDrawable(R.drawable.icon_bomb));
            }
            // Case of winning: Reveal all mines associated with a bomb with victory icon
            else if(mMineCells.contains(tv) && mStatus == 2)
            {
                tv.setBackground(mContext.getDrawable(R.drawable.icon_victory));
            }
            // SUBJECT TO CHANGE!!!!!!!!SUBJECT TO CHANGE!!!!!!!!SUBJECT TO CHANGE!!!!!!!!SUBJECT TO CHANGE!!!!!!!!
            else
            {
                tv.setBackgroundColor(Color.argb(80,244,177,91));
            }
        }
    }

    // when mGameMode = true, it should be in shovel mode
    // mGameMode = false, flag mode
    private void onClickChangeGameMode(View view)
    {
        if(mGameMode)
        {
            mGameMode = false;
            mGameModeBttn.setImageResource(R.drawable.icon_flag);
        }
        else
        {
            mGameMode = true;
            mGameModeBttn.setImageResource(R.drawable.icon_shovel);
        }
    }


    // [ Getters ] =================================================================================
    public int getGameStatus()
    {
        return mStatus;
    }

    // [ Setters ] =================================================================================
    private void setRemainingMineView()
    {
        // set the text view to "--" if the game is not yet started
        if(mRemainingMines == -1)
        {
            mRemainingMineView.setText("----");
        }
        else
        {
            String bombsStr = String.format("%d",mRemainingMines);
            mRemainingMineView.setText(bombsStr);
        }
    }

    // Image Button Setup
    // Used to set the onclick event for the image button
    private void setGameModeBttn()
    {
        mGameModeBttn.setOnClickListener(this::onClickChangeGameMode);
    }

    // Randomly sets the cells with the bombs
    private void setMines()
    {
        // Used to keep track of whether a cell already has a mine
        HashSet<Integer> numUsed = new HashSet<>();
        int rand = mUtil.RandomNumGenerator(0, mCells.size());
        for(int i = 0; i < mMines; i++)
        {
            // Make sure we don't randomly select cell that is already associated with a mine
            while(numUsed.contains(rand))
            {
                rand = mUtil.RandomNumGenerator(0, mCells.size());
            }
            // add the randomly selected cell to the mines list
            numUsed.add(rand);
            mMineCells.add(mCells.get(rand));
            mCells.get(rand).setBackgroundColor(Color.BLACK);
        }
    }


    // Grid Setup
    private void setGrid()
    {
        LayoutInflater li = LayoutInflater.from(mContext);
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 10; j++){
                TextView tv = (TextView) li.inflate(R.layout.mine_block, mGridLayout, false);
                tv.setTextColor(Color.argb(0, 87, 88, 87));
                tv.setBackgroundColor(Color.argb(99, 43, 61, 42));
                tv.setOnClickListener(this::onClick);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                mGridLayout.addView(tv, lp);

                // Adding the current text view to an array
                // So that we know the when textview is clicked later
                mCells.add(tv);
            }
        }

    }
}
