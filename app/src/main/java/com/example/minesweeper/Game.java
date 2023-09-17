package com.example.minesweeper;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.gridlayout.widget.GridLayout;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Game {
    // Top left textview of the main activity, prompting how many mines are there
    final private TextView mRemainingMineView;
    final private GridLayout mGridLayout;
    final private Context mContext;
    final private ImageButton mGameModeBttn;
    // Should be passed into constructor. Start when game start. Stop when victory or lose
    final private Timer mTimer;
    // The amount of mines/remaining mines for this game
    private int mMines;
    public int mRemainingMines;
    // 0: game not active, 1: game active, 2: player wins, 3: player lose
    private int mStatus;
    // true: shovel; false: flag
    private boolean mGameMode;
    // Containing all the cells in the grid system for all operations
    final private HashMap<TextView, Cell> mCells;
    // Containing all the textview in the arraylist, used to do search in order
    final private ArrayList<TextView> mCellsArr;
    // Custom utility class for needed functionalities
    private final Util mUtil = new Util();

    // Constructor
    public Game(Context context, TextView remainingMineView, GridLayout gridLayout,
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
        mRemainingMines = 0;
        mCells = new HashMap<>();
        mCellsArr = new ArrayList<>();
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
        Cell clickedCell = mCells.get((TextView) view);
        // If game is not active, we start the game
        if(mStatus == 0)
        {
            onClickStart();
            // Have correct first click behavior based on game mode(shovel or flag)
            if(mGameMode)
            {
                onClickShovel(clickedCell);
            }
            else
            {
                onClickFlag(clickedCell);
            }

        }
        // If the game is active
        else if(mStatus == 1)
        {
            // mGameMode == true: shovel
            if(mGameMode)
            {
                // Check whether the clicked cell is already shoveled or flagged.
                // if yes, do nothing
                if(clickedCell.getIsShoveled() || clickedCell.getIsFlagged())
                {
                    return;
                }

                // If the cell contains a mine, set the game to lose
                // Reveal all cells
                if(clickedCell.getHasMine())
                {
                    mStatus = 3;
                    for(Cell cell: mCells.values())
                    {
                        onReveal(cell);
                    }
                    mTimer.onClickEnd();
                    return;
                }

                // If the clicked cell is not yet shoveled and valid, process it
                onClickShovel(clickedCell);

                // If this is the last valid cell to be shoveled, player wins
                if(mCells.size() - mUtil.GetShoveledCount() - mMines == 0)
                {
                    // Update the game status to win
                    mStatus = 2;
                    mTimer.onClickEnd();
                    // Reveal all bombs to victory icon
                    for(Cell cell: mCells.values()) {
                        onReveal(cell);
                    }
                }
            }
            // mGameMode == false: flag
            else
            {
                // Call onClickUnFlag already flagged
                if(clickedCell.getIsFlagged())
                {
                    onClickUnFlag(clickedCell);
                }
                // if the cell is already shoveled, do nothing
                else if(clickedCell.getIsShoveled())
                {
                    return;
                }
                // If the cell can be flagged, process it and add it to the flagged cell list
                // also update the remaining mine count
                else
                {
                    // Flag cell and mark it as flagged
                    onClickFlag(clickedCell);
                }
            }
        }
        // mGameStatus == 3 || mGameStatus == 4
        // The game has ended
        // use onClickEnd() function to start the end page activity
        // And send necessary information
        else
        {
            onClickEnd();
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
        // mMines = mUtil.RandomNumGenerator(8, 12);
        // For this lab statically set the number of mines to 4.
        mMines = 4;
        mRemainingMines = mMines;
        setRemainingMineView();
        // Set up mines in randomly selected cells
        setMines();
    }

    // Called when the game has ended
    // This function starts the end page activity
    // And sends text information
    private void onClickEnd()
    {
        String status = "";
        if(mStatus == 2)
        {
            status = "won";
        }
        else if(mStatus == 3)
        {
            status = "lost";
        }
        String timeUsed = mTimer.getTime();
        Intent intent = new Intent(mContext, EndPageActivity.class);
        intent.putExtra("status", status);
        intent.putExtra("timeUsed", timeUsed);
        mContext.startActivity(intent);
    }

    private void onClickShovel(Cell cell)
    {
        mUtil.BFSReveal(mCellsArr, mCells, cell.getTextView(), this);
    }

    private void onClickFlag(Cell cell)
    {
        mRemainingMines--;
        setRemainingMineView();
        cell.setIsFlagged(true);
        cell.getTextView().setBackground(mContext.getDrawable(R.drawable.icon_flag));
        cell.setIsFlagged(true);

    }

    private void onClickUnFlag(Cell cell)
    {

        // Since we are unflagging, add one back to the remaining count
        mRemainingMines ++;
        setRemainingMineView();
        // We want to remove the cell from the clicked list
        // so that it can be flagged again if clicked again
        cell.setIsFlagged(false);
        cell.getTextView().setBackgroundColor(Color.argb(100, 38, 103, 25));
    }

    // Used to reveal all cells on win or lose
    @SuppressLint("UseCompatLoadingForDrawables")
    private void onReveal(Cell cell)
    {
        if(!cell.getIsShoveled())
        {
            // Case of losing: Reveal all mines associated with a bomb with bomb icon
            if(cell.getHasMine() && mStatus == 3)
            {
                cell.getTextView().setBackground(mContext.getDrawable(R.drawable.icon_bomb));
            }
            // Case of winning: Reveal all mines associated with a bomb with victory icon
            else if(cell.getHasMine() && mStatus == 2)
            {
                cell.getTextView().setBackground(mContext.getDrawable(R.drawable.icon_victory));
            }
            // Otherwise reveal the empty cell
            // (If we lose there's a chance that there's an unclicked cell)
            else
            {
                // Check if the cell is flagged, we want to displace the flag with an X
                if(cell.getIsFlagged())
                {
                    cell.getTextView().setBackground(mContext.getDrawable(R.drawable.icon_redx));
                }
                // Check if the cell has a number
                else if(cell.getMinesAround() != 0)
                {
                    cell.getTextView().setText(String.format("%d", cell.getMinesAround()));
                    cell.getTextView().setBackgroundColor(Color.argb(80, 169,224,161));
                }
                else
                {
                    cell.getTextView().setBackgroundColor(Color.argb(80, 169,224,161));
                }
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

    // [ Setters ] =================================================================================
    public void setRemainingMineView()
    {
        // set the text view to "--" if the game is not yet started
        if(mStatus == 0)
        {
            mRemainingMineView.setText("----");
        }
        // set the text view to corresponding # of remaining mines
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
        Set<TextView> keys = mCells.keySet();
        ArrayList<TextView> keysArr = new ArrayList<>(keys);

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
            mCells.get(keysArr.get(rand)).setHasMine(true);
        }

        // Update all cells with the minesAround number
        for(Cell cell: mCells.values())
        {
            if(!cell.getHasMine())
            {
                int minesAround = mUtil.findMineAround(mCellsArr, mCells, cell.getTextView());
                cell.setMinesAround(minesAround);
            }
        }
    }


    // Grid Setup
    private void setGrid()
    {
        LayoutInflater li = LayoutInflater.from(mContext);
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 10; j++){
                TextView tv = (TextView) li.inflate(R.layout.mine_block, mGridLayout, false);
                //tv.setTextColor(Color.argb(0, 87, 88, 87));
                tv.setBackgroundColor(Color.argb(100, 38, 103, 25));
                tv.setOnClickListener(this::onClick);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                mGridLayout.addView(tv, lp);

                // Adding the current text view to the map for later operations
                mCells.put(tv, new Cell(tv, false, false, false, 0));
                // Adding the current text view to an array as well, so that we can use the order
                // to keep track of which tv is clicked (used to perform searching)
                mCellsArr.add(tv);
            }
        }

    }
}
