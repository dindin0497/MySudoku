package com.my.sudoku.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sukoku interface
 */
public interface ISudoku {

    /**
     * init game based on level
     * @param level
     */
    void initGame(int level);

    /**
     * get row number
     * @return row number
     */
    int getRowNumber();

    /**
     * reset game to initial state
     */
    void reset();

    /**
     * undo last step
     */
    boolean undo();

    /**
     * set game finish listener
     * * @param listener
     */
    void setOnFinishListener(OnFinishListener listener);

    /**
     * check is the position in the initial list
     * @param row
     * @param col
     * @return true - tile is in the initial list
     */
    boolean isInitial(int row, int col);

    /**
     * get the text of a position
     * @param row
     * @param col
     * @return text
     */
    String getString(int row, int col);

    /**
     * get the number of a position
     * @param row
     * @param col
     * @return int
     */
    int getNumber(int row, int col);

    /**
     * set the number at a position
     * @param row
     * @param col
     * @param value
     */
    void setNumber(int row, int col, int value);

    /**
     * get available number in a position
     * @param row
     * @param col
     * @return Set of int
     */
    Set<Integer> getAvailableNumber(int row, int col);

    /**
     * get all the step left in the board
     * @return Map, step - List
     */
    Map<Integer,List<Integer>> getStepMap();


}
