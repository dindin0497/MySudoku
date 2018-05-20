package com.my.sudoku.model;

import android.util.Log;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * class implement ISudoku
 */
public class Sudoku implements ISudoku
{
    public static final String TAG=Sudoku.class.getSimpleName();

    private static final int SIZE=IBoardCreator.ROW;

    /**
     * solution board
     */
    private int[][] solution;

    /**
     * initial set number in board
     */
    private Set<Integer> initialSet=new HashSet<>();

    /**
     * step map, step -> position
     */
    private Map<Integer, List<Integer>> stepMap=new HashMap<>();

    /**
     * user board
     */
    private int[][] userBoard;

    /**
     * user stack for undo
     */
    private Stack<Integer> userStack=new Stack<>();

    private static int[] levelNum=new int[]{35,28,20};

    private OnFinishListener finishListener;

    public Sudoku()
    {

    }

    @Override
    public void initGame(int level)
    {
        if (level>=levelNum.length)
        {
            Log.e(TAG,"error level "+level);
            return;
        }

        //create first row
        long start=System.currentTimeMillis();

        IBoardCreator creator=new SudokuBoardCreator();
        solution=creator.createSolution();

        userBoard=creator.getPlayBoard(solution, initialSet, levelNum[level]);

        Log.d(TAG,"board: ");
        for (int i=0;i<SIZE;i++)
            Log.d(TAG, Arrays.toString(solution[i])+"  "+Arrays.toString(userBoard[i]));

        userStack.clear();
        updateStep();
        Log.d(TAG,"init time="+(System.currentTimeMillis()-start));
    }

    @Override
    public void reset() {
        for (int ary[]: userBoard)
            Arrays.fill(ary,0);
        for (int position: initialSet)
        {
            int x = position / SIZE;
            int y = position % SIZE;
            userBoard[x][y] = solution[x][y];
        }
        userStack.clear();
        updateStep();
    }

    @Override
    public boolean undo() {
        if (userStack.isEmpty())
            return false;
        int pos=userStack.pop();
        int x = pos / SIZE;
        int y = pos % SIZE;
        userBoard[x][y] = 0;
        updateStep();
        return true;
    }

    @Override
    public void setOnFinishListener(OnFinishListener listener) {
        finishListener=listener;
    }


    /**
     * verify the position is in initial game setting
     * @return boolean
     */
    @Override
    public boolean isInitial(int row, int col){

        return initialSet.contains(row*SIZE+col);
    }

    /**
     * get the value of a position
     */
    @Override
    public int getNumber(int row, int col) {
        return userBoard!=null?userBoard[row][col]:0;
    }

    @Override
    public String getString(int row, int col) {
        int val= getNumber(row, col);
        return val==0?"":String.valueOf(val);
    }

    @Override
    public  Set<Integer> getAvailableNumber(int row, int col) {
        Set<Integer> availableSet=new HashSet<>();
        for (int i=1;i<=SIZE;i++)
            availableSet.add(i);

        // horizontal
        for (int i = 0; i < SIZE; i++) {
            if (i ==col)
                continue;
            int t = getNumber(row, i);
            if (t != 0)
                availableSet.remove(t);
        }
        // vertical
        for (int i = 0; i < SIZE; i++) {
            if (i == row)
                continue;
            int t = getNumber(i, col);
            if (t != 0)
                availableSet.remove(t);
        }

        // block
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (i == row && j == col)
                    continue;
                int t = getNumber(i, j);
                if (t != 0)
                    availableSet.remove(t);
            }
        }

        return availableSet;
    }

    @Override
    public int getRowNumber() {
        return SIZE;
    }

    /**
     * set the value at given position
     */
    @Override
    public void setNumber(int x, int y, int value) {
        userBoard[x][y]=value;
        userStack.push(x*SIZE+y);
        updateStep();
    }

    /**
     * get the step map
     */
    @Override
    public Map<Integer, List<Integer>> getStepMap() {
        return stepMap;
    }

    /**
     * update the position with no more step
     * also check is the game finish
     */
    protected void updateStep()
    {
        stepMap.clear();

        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                if (userBoard[i][j]==0)
                {
                    Set<Integer> set= getAvailableNumber(i, j);
                    if (!stepMap.containsKey(set.size()))
                        stepMap.put(set.size(),new LinkedList<Integer>());
                    stepMap.get(set.size()).add(i*SIZE+j);
                }

            }
        }
        if (stepMap.size()==0)
            finishListener.onFinish();
    }
}
