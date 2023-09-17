package com.example.minesweeper;

import android.widget.TextView;

public class Cell {
    private TextView mTV;
    private int mMineAround;
    private boolean mHasMine;
    private boolean mIsFlagged;
    private boolean mIsShoveled;

    public Cell(TextView tv, boolean hasMine, boolean isFlagged, boolean isShoveled, int minesAround)
    {
        mTV = tv;
        mHasMine = hasMine;
        mIsFlagged = isFlagged;
        mIsShoveled = isShoveled;
        mMineAround = minesAround;
    }

    // Getters
    public TextView getTextView()
    {
        return mTV;
    }

    public boolean getHasMine()
    {
        return mHasMine;
    }

    public boolean getIsFlagged()
    {
        return mIsFlagged;
    }

    public boolean getIsShoveled()
    {
        return mIsShoveled;
    }

    public int getMinesAround()
    {
        return mMineAround;
    }

    // Setters
    public void setHasMine(boolean status)
    {
        mHasMine = status;
    }

    public void setIsFlagged(boolean status)
    {
        mIsFlagged = status;
    }

    public void setIsShoveled(boolean status)
    {
        mIsShoveled = status;
    }

    public void setMinesAround(int num)
    {
        mMineAround = num;
    }

}
