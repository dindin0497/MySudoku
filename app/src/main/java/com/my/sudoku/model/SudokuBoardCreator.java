package com.my.sudoku.model;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SudokuBoardCreator implements IBoardCreator {
	public static final String TAG=SudokuBoardCreator.class.getSimpleName();

	@Override
	public int [][] createSolution() {
		int [][]solution=new int[ROW][ROW];

		//init first row
		List<Integer> selectList=createSelectionList();

		Random random=new Random();
		for (int i=0;i<ROW;i++)
		{
			int idx=random.nextInt(selectList.size());
			solution[0][i]=selectList.get(idx);
			selectList.remove(idx);
		}

		dfs(solution, 1,0);

		return solution;
	}

	protected List<Integer> createSelectionList()
	{
		List<Integer> selectList=new ArrayList<Integer> ();
		for (int i=1;i<=ROW;i++)
			selectList.add(i);
		return selectList;
	}


	/**
	 * create the board using DFS
	 * @param solution
	 * @param row
	 * @param col
	 * @return true - if there is a work solution
	 */
	protected boolean dfs(int[][] solution, int row, int col)
	{
		if (col==ROW)
		{
			col=0;
			row++;
		}

		if (row==ROW)
			return true;
		if (solution[row][col]==0)
		{
			for (int i=1; i<=ROW; i++)
			{
				solution[row][col]=i;
				if (check(solution,row, col) && dfs(solution, row, col+1))
					return true;

				solution[row][col]=0;
			}
		}
		else
			return dfs(solution, row, col+1);

		return false;

	}

	/**
	 * verify the current number in a position
	 * @param solution
	 * @param row
	 * @param col
	 * @return true - current number is ok
	 */
	protected boolean check(int[][] solution, int row, int col)
	{
		return checkRow(solution,row, col)
				&& checkColumn(solution,row, col)
				&& checkBlock(solution,row, col);
	}

    protected boolean checkBlock(int[][] solution, int row, int col)
	{
		for (int m = 0; m < 3; m++) {
			for (int n = 0; n < 3; n++)
			{
				int x = row / 3 * 3 + m, y = col / 3 * 3 + n;
				if (x!=row && y!=col && solution[x][y]== solution[row][col])
					return false;
			}
		}

		return true;
	}
    protected boolean checkRow(int[][] solution, int row, int col)
	{
		for (int i = 0; i < ROW; ++i) {
			if (i!=col && solution[row][i]== solution[row][col])
				return false;
		}
		return true;
	}

    protected boolean checkColumn(int[][] solution, int row, int col)
	{
		for (int i = 0; i < ROW; ++i) {
			if (i!=row && solution[i][col]== solution[row][col])
				return false;
		}
		return true;
	}


	public int[][] getPlayBoard(int[][] solution, Set<Integer> set, int num)
	{
		int row=solution.length, col=solution[0].length;
		int[][] board=new int[row][col];
		int i;

		//create a position list and shuffle the order
		List<Integer> positions = new ArrayList();
		for (i = 0; i < row*col; i++)
			positions.add(i); 
		
		Collections.shuffle(positions);

		//copy the value from solution to user board
		set.clear();
		for (i = 0; i < num; i++)
		{
			int position = positions.get(i);
			int x = position / row;
			int y = position % col;
			set.add(position);
			board[x][y] = solution[x][y];
		}
		
		return board;
	}

}
