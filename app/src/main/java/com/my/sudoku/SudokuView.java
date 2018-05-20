package com.my.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.my.sudoku.model.ISudoku;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sudoku view for display sudoku board
 */
public class SudokuView extends View implements KeypadDialog.IUserInput {
	private static final String TAG = SudokuView.class.getSimpleName();

	private ISudoku sudoku=null;

	private float width;
	private float height;
	private int selRow=-1;
	private int selCol=-1;
	private final Rect selRect = new Rect();
	private int rowNum;

	public SudokuView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);

	}
	public SudokuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SudokuView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	public void setSudoku(ISudoku sudoku)
	{
		this.sudoku=sudoku;
		rowNum=sudoku.getRowNumber();
	}


	public void clear()
	{
		selRow=-1;
		selCol=-1;
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
    }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = (float)w / rowNum;
		height = width;
		getRect(selRow, selCol, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + " height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void getRect(int row, int col, Rect rect) {
		rect.set((int) (col * width), (int) (row * height), (int) (col * width + width), (int) (row * height + height));
	}

	protected void drawNumber(Canvas canvas)
	{
		// Draw the numbers
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);

		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		// Centering on X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering on Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int row = 0; row < rowNum; row++) {
			for (int col = 0; col < rowNum; col++) {
				//draw number
				if(sudoku.isInitial(row, col)) {
					Rect rect = new Rect((int)(col * width), (int)(row * height), (int)((col+1)*width), (int)((row+1)*height) );
					Paint grey = new Paint();
					grey.setColor(getResources().getColor(R.color.grey));
					canvas.drawRect(rect, grey);
				}
				canvas.drawText(sudoku.getString(row, col), col * width + x, row * height + y, foreground);
			}
		}
	}
	protected void drawHint(Canvas canvas)
	{
		// Draw the hints
		// Pick a hint color based on moves left
		Paint hint = new Paint();
		int color[] = { getResources().getColor(R.color.puzzle_hint_0),
				getResources().getColor(R.color.puzzle_hint_1),
				getResources().getColor(R.color.puzzle_hint_2), };

		hint.setColor(getResources().getColor(R.color.puzzle_hint_0));

		Rect r = new Rect();

		Map<Integer, List<Integer>>  stepMap=sudoku.getStepMap();
		for (Map.Entry<Integer, List<Integer>> ent: stepMap.entrySet()) {
			int step=ent.getKey();
			List<Integer> list=ent.getValue();
			if (step<color.length)
				hint.setColor(color[step]);
			else
				hint.setColor(color[color.length-1]);
			for (int pos: list) {
				getRect(pos / rowNum, pos % rowNum, r);
				canvas.drawRect(r, hint);
			}
		}
	}
	protected void drawBoarder(Canvas canvas)
	{
		// Draw the board
		// Definte colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		dark.setStrokeWidth(5);

		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		hilite.setStrokeWidth(3);

		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));

		for (int i = 0; i <= rowNum; i++) {
			Paint pt=(i%3==0)?dark:hilite;
			canvas.drawLine(0, i * height, getWidth(), i*height, pt);
			canvas.drawLine(i * width, 0, i * width, getHeight(), pt);
		}

	}

	protected void drawSelecion(Canvas canvas)
	{
		// Draw the selection
		if (selRow!=-1 && selRow!=-1) {
			Log.d(TAG, "selRect=" + selRect);
			Paint selected = new Paint();
			selected.setColor(getResources().getColor(R.color.puzzle_selected));
			canvas.drawRect(selRect, selected);
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {

		drawNumber(canvas);

		drawHint(canvas);

		drawBoarder(canvas);

		drawSelecion(canvas);
	}



	private void select(int row, int col) {
		invalidate(selRect);
		selRow = row;
		selCol = col;
		getRect(selRow, selCol, selRect);
		invalidate(selRect);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		//Log.d(TAG, "onTouchEvent: " + event.getX() + " " + event.getY() +" "+width);

		int col=(int) (event.getX() / width), row=(int) (event.getY() / height);

        Log.d(TAG, "onTouchEvent: row=" + row + ", col=" + col);

        if(sudoku.isInitial(row,col))
            return true;

		select(row,col);

		Set<Integer> set = sudoku.getAvailableNumber(row,col);

		if (set.size()==0) {
			Toast toast = Toast.makeText(getContext(), R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		else {
			Dialog v = new KeypadDialog(getContext(), row, col, set, this);
			v.show();
		}

		return true;
	}

    @Override
    public void onInput(int row, int col, int value) {
        sudoku.setNumber(row, col, value);
        invalidate();
    }


}