package com.example.minesweeper;

import android.graphics.Color;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Util
{
    private boolean[] visited = null;
    private int mShoveledCount = 0;

    // Didn't use search algorithm as it's just one level search.
    public int findMineAround(ArrayList<TextView> grid, HashMap<TextView, Cell> map, TextView currTV) {
        int currTvIndex = grid.indexOf(currTV);
        // valid row index is from 0 to 11 (total of 12 rows);
        int currRow = currTvIndex/10;
        // valid col index is from 0 to 9 (total of 10 cols);
        int currCol = currTvIndex%10;


        int numMinesAround = 0;

        // Check top row
        if(currRow > 0)
        {
            // Check direct top cell
            int topCellRow = currRow-1;
            int topCellLoc = topCellRow * 10 + currCol;
            if(map.get(grid.get(topCellLoc)).getHasMine())
            {
                numMinesAround ++;
            }

            // Check top right cell
            if(currCol < 9 && map.get(grid.get(topCellLoc+1)).getHasMine())
            {
                numMinesAround ++;
            }
            // Check top left cell
            if(currCol > 0 && map.get(grid.get(topCellLoc-1)).getHasMine())
            {
                numMinesAround ++;
            }
        }

        // Check curr row
        if(currCol < 9 && map.get(grid.get(currTvIndex+1)).getHasMine())
        {
            numMinesAround ++;
        }
        if(currCol > 0 && map.get(grid.get(currTvIndex-1)).getHasMine())
        {
            numMinesAround ++;
        }

        // Check bot row
        if(currRow < 11)
        {
            // Check direct bot cell
            int botCellRow = currRow + 1;
            int botCellLoc = botCellRow * 10 + currCol;
            if(map.get(grid.get(botCellLoc)).getHasMine())
            {
                numMinesAround ++;
            }

            // Check bot right cell
            if(currCol < 9 && map.get(grid.get(botCellLoc + 1)).getHasMine())
            {
                numMinesAround ++;
            }

            // Check bot left cell
            if(currCol > 0 && map.get(grid.get(botCellLoc - 1)).getHasMine())
            {
                numMinesAround ++;
            }
        }

        return numMinesAround;
    }

    public void BFSReveal(ArrayList<TextView> grid, HashMap<TextView, Cell> map, TextView currTV, Game game)
    {
        // If visited array is not yet initialized,
        // Construct it with all false values, indicating all cells are not yet explored
        if(visited == null)
        {
            visited = new boolean[grid.size()];
        }


        // queue used for BFS
        LinkedList<Integer> queue = new LinkedList<>();

        int currCellIndex = grid.indexOf(currTV);
        // mark the current location as explored
        visited[currCellIndex] = true;
        queue.add(currCellIndex);

        // Perform BFS
        while(queue.size() != 0) {
            int i = queue.poll();

            Cell currCell = map.get(grid.get(i));
            // First check if the cell has a flag because we want to correctly update the
            // remaining flag count when reveal it
            if(currCell.getIsFlagged())
            {
                game.mRemainingMines++;
                game.setRemainingMineView();
            }
            // Stop exploring at this level if the curr cell is a mine
            if(currCell.getHasMine())
            {
                continue;
            }
            // Stop exploring if the curr cell contains a minesAround number larger than 0
            // Reveal it before stopping.
            else if(currCell.getMinesAround() != 0)
            {
                // Reveal curr cell
                mShoveledCount++;
                currCell.setIsShoveled(true);
                currCell.getTextView().setText(String.format("%d", currCell.getMinesAround()));
                currCell.getTextView().setTextColor(Color.BLACK);
                currCell.getTextView().setBackgroundColor(Color.argb(80, 169,224,161));
                continue;
            }
            else
            {
                mShoveledCount++;
                currCell.setIsShoveled(true);
                currCell.getTextView().setBackgroundColor(Color.argb(80, 169,224,161));
            }




            int currCellRow = i/10;
            int currCellCol = i%10;

            // Explore top cell if valid
            if(currCellRow > 0)
            {
                int topCellRow = currCellRow - 1;
                // Calculate the top cell index in the array
                int topCellLoc = topCellRow * 10 + currCellCol;
                if(!visited[topCellLoc])
                {
                    visited[topCellLoc] = true;
                    queue.add(topCellLoc);
                }
            }

            // Explore right cell if valid
            if(currCellCol < 9)
            {
                int rightCellLoc = i + 1;
                if(!visited[rightCellLoc])
                {
                    visited[rightCellLoc] = true;
                    queue.add(rightCellLoc);
                }
            }

            // Explore bot cell if valid
            if(currCellRow < 11)
            {
                int botCellRow = currCellRow + 1;
                int botCellLoc = botCellRow * 10 + currCellCol;
                if(!visited[botCellLoc])
                {
                    visited[botCellLoc] = true;
                    queue.add(botCellLoc);
                }
            }

            // Explore left cell if valid
            if(currCellCol > 0)
            {
                int leftCellLoc = i - 1;
                if(!visited[leftCellLoc])
                {
                    visited[leftCellLoc] = true;
                    queue.add(leftCellLoc);
                }
            }
        }
    }

    public int GetShoveledCount()
    {
        return mShoveledCount;
    }

    public int RandomNumGenerator(int min, int max)
    {
        return (int)(Math.random() * (max-min) + min);
    }

}
