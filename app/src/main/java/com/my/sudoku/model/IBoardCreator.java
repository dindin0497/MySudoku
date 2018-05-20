package com.my.sudoku.model;

import java.util.Set;

/**
 * interface for creating board
 */
public interface IBoardCreator {
    int ROW=9;

    /**
     * create a solution
     */
    int[][] createSolution();

    /**
     * create a user board according to solution
     */
    int[][] getPlayBoard(int[][] solution, Set<Integer> set, int num);
}
