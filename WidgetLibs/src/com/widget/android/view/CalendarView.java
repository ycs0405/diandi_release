package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.widget.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * 备孕日历控件
 *
 * @author fu
 */
@SuppressLint({"NewApi", "SimpleDateFormat"})
public class CalendarView extends View {
    private final int txtColor = 0xff636363;
    private final int txtColorTo = 0xffd3d3d3;
    private final int txtGreen = 0xff8bd246;
    private final int txtWrite = 0xffffffff;
    private final int weekDayBg = 0xfff5f5f5;
    private final int selectBg = 0xffe2f3d1;
    private final int ovulationBg = 0xffff8fa7;
    private final int ovulationDayBg = 0xfffff4f5;
    private final int bestDayColor = 0xffdd7a8f;

    private int viewWidth, viewHeight;

    private int[] location = new int[2];
    private int selectXindex, selectYindex;

    float currX, currY;

    private float cellWidth, cellHeight, tabHeight;

    int row = 6;
    int toDay = 0;
    int bestDay = 12;

    private String weekS[] = {"日", "一", "二", "三", "四", "五", "六"};

    private Paint txtPaint, txtBgPaint, circlePaint, circleFillPaint, ovulationPaint, remarkLine;

    private Bitmap bestStar, men;

    int baseData[][] = new int[][]{{0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}};

    int[] ovulationData = new int[]{10, 11, 12, 13, 14, 15, 16};
    int[] menData = {2, 3, 4, 5, 6, 7, 8};

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CalendarView(Context context) {
        super(context);

        init();
    }

    private void init() {

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setAntiAlias(true);
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(sp2px(14f));

        txtBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtBgPaint.setAntiAlias(true);
        txtBgPaint.setStyle(Paint.Style.FILL);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(dip2px(1f));

        circleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleFillPaint.setAntiAlias(true);
        circleFillPaint.setStyle(Paint.Style.FILL);

        ovulationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ovulationPaint.setAntiAlias(true);
        ovulationPaint.setStyle(Paint.Style.FILL);

        remarkLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        remarkLine.setAntiAlias(true);
        remarkLine.setStyle(Paint.Style.FILL);
        remarkLine.setStrokeWidth(dip2px(0.8f));

        bestStar = BitmapFactory.decodeResource(getResources(), R.drawable.icon_best_star);
        men = BitmapFactory.decodeResource(getResources(), R.drawable.icon_men);
        createBaseData();

    }

    private void drawWeekDays(Canvas canvas) {

        cellWidth = viewWidth / 7;
        cellHeight = cellWidth / 4 * 3;

        Rect rect = new Rect();

        float txtWidth, txtHeight;

        txtBgPaint.setColor(weekDayBg);

        Rect weekRectBg = new Rect(0, 0, viewWidth, (int) cellHeight);
        canvas.drawRect(weekRectBg, txtBgPaint);

        for (int i = 0; i < weekS.length; i++) {
            String str = weekS[i];
            if (i == 0 || i == weekS.length - 1) {
                txtPaint.setColor(txtColorTo);
            } else {
                txtPaint.setColor(txtColor);
            }
            txtPaint.getTextBounds(str, 0, str.length(), rect);

            txtWidth = Math.abs(rect.right - rect.left);
            txtHeight = Math.abs(rect.bottom - rect.top);

            canvas.drawText(str, (cellWidth - txtWidth) / 2 + i * cellWidth, (cellHeight + txtHeight - txtHeight / 2) / 2 + dip2px(1.2f), txtPaint);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Log.d("Calendar", calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "");

    }

    private void drawDay(Canvas canvas) {

        cellWidth = viewWidth / 7;
        cellHeight = cellWidth / 4 * 3;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        Date date = new Date();

        int week = getDayToWeek(format.format(date));

        calendar.setTime(date);

        Rect rect = new Rect();

        float txtWidth = 0, txtHeight = 0;


        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        toDay = calendar.get(Calendar.DATE);

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

        int lastDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        tabHeight = (row + 1) * cellHeight;

        getLayoutParams().height = (int) (tabHeight);
        requestLayout();


        int next = 0;

        int nextCount = row * 7 - (days + week);

        for (int i = 0, j = week - 1; i < row * 7; i++, j--) {

            String value = String.valueOf(i + 1);
            String lastValue = String.valueOf(lastDays - j);

            txtPaint.setTextSize(sp2px(14f));
            txtPaint.setColor(txtColor);

            if (j >= 0) {

                txtPaint.getTextBounds(lastValue, 0, lastValue.length(), rect);
                txtWidth = Math.abs(rect.right - rect.left);
                txtHeight = Math.abs(rect.bottom - rect.top);

                txtPaint.setColor(txtColorTo);
                canvas.drawText(lastValue, (cellWidth - txtWidth) / 2 + i * cellWidth, (cellHeight + txtHeight - txtHeight / 2) / 2 + dip2px(1.2f) + cellHeight, txtPaint);
            }

            if (i < days) {

                txtPaint.setColor(txtColor);

                txtPaint.getTextBounds(value, 0, value.length(), rect);
                txtWidth = Math.abs(rect.right - rect.left);
                txtHeight = Math.abs(rect.bottom - rect.top);


                if (i + 1 >= 10 && i + 1 < 10 + 7) {
                    txtPaint.setColor(txtWrite);
                }


                if (String.valueOf(toDay).equals(value) && i + 1 != bestDay && !getClickOvulation(i + 1)) {

                    drawTodayTxtGreen(canvas, i, week);

                }


                if (!String.valueOf(toDay).equals(value) && value.equals(String.valueOf(bestDay)) && baseData[selectYindex][selectXindex] != i + 1) {
                    drawBestOvulationDay(canvas, i, week);
                }

                if (value.equals(String.valueOf(toDay)) && value.equals(String.valueOf(bestDay)) && baseData[selectYindex][selectXindex] != i + 1) {
                    drawBestToDayvulationDay(canvas, i, week);
                }


                if (value.equals("10")) {

                    drawOvulationBg(canvas, i, i + 7, week);

                    if (String.valueOf(toDay).equals(value) && baseData[selectYindex][selectXindex] != i + 1) {
                        drawTodayOvulationStartBg(canvas, i, week);
                    } else {
                        drawOvulationStartCircleBg(canvas, i, week);
                    }

                    drawOvulationStopCircleBg(canvas, i, i + 6, week);

                }

                if (baseData[selectYindex][selectXindex] == i + 1) {

                    if (String.valueOf(toDay).equals(value) && i + 1 != bestDay && !getClickOvulation(i + 1)) {

                        drawTodayGreenBg(canvas, i, week);

                        txtPaint.setColor(txtWrite);
                        drawTodayTxtWrite(canvas, i, week);

                    }

                    if (value.equals(String.valueOf(bestDay))) {
                        drawBestOvulationSelect(canvas, i, week);
                    }

                    if (value.equals(String.valueOf(toDay)) && toDay == bestDay) {
                        drawBestTodayOvulationSelect(canvas, i, week);
                    }

                    if (getClickOvulation(i + 1) && i + 1 != bestDay) {
                        drawSelectTodayOvulation(canvas);
                        txtPaint.setColor(ovulationBg);
                    }
                }


                if (isMen(i + 1)) {
                    drawMenBg(canvas, i, week);
                }

                if (value.equals(String.valueOf(toDay)) && baseData[selectYindex][selectXindex] != i + 1 && i + 1 != bestDay && getClickOvulation(i + 1)) {
                    drawTodayOvulationBg(canvas, i, week);
                }


                if (String.valueOf(toDay).equals(value) && getClickOvulation(toDay)) {
                    drawTodayTxtWrite(canvas, i, week);
                }


                txtPaint.setTextSize(sp2px(14f));
                canvas.drawText(value, (cellWidth - txtWidth) / 2 + (i + week) % 7 * cellWidth,
                        (cellHeight + txtHeight - txtHeight / 2) / 2 + (i + week) / 7 * cellHeight + cellHeight + dip2px(1.2f), txtPaint);
            }

            if (i >= days && next < nextCount) {
                txtPaint.setColor(txtColorTo);
                String nextValue = String.valueOf(++next);

                txtPaint.getTextBounds(nextValue, 0, nextValue.length(), rect);
                txtWidth = Math.abs(rect.right - rect.left);
                txtHeight = Math.abs(rect.bottom - rect.top);

                canvas.drawText(nextValue,
                        (cellWidth - txtWidth) / 2 + (i + week) % 7 * cellWidth,
                        (cellHeight + txtHeight - txtHeight / 2) / 2 + (i + week) / 7 * cellHeight + cellHeight + dip2px(1.2f), txtPaint);
            }


            drawRemarkLine(canvas, i, week);
        }
    }

    private void drawTodayTxtWrite(Canvas canvas, int i, int week) {

        Rect rect = new Rect();
        float txtWidth, txtHeight;

        String toValue = "今";
        txtPaint.setTextSize(sp2px(8f));
        txtPaint.getTextBounds(toValue, 0, toValue.length(), rect);
        txtWidth = Math.abs(rect.right - rect.left);
        txtHeight = Math.abs(rect.bottom - rect.top);

        canvas.drawText(toValue, (cellWidth - txtWidth) / 2 + (i + week) % 7 * cellWidth,
                txtHeight + (i + week) / 7 * cellHeight + cellHeight + dip2px(1.5f), txtPaint);
    }

    /***
     * 绘制今天未被选中
     *
     * @param canvas
     * @param i
     * @param week
     */
    private void drawTodayTxtGreen(Canvas canvas, int i, int week) {
        Rect rect = new Rect();
        float txtWidth, txtHeight;

        txtPaint.setColor(txtGreen);
        String toValue = "今";

        txtPaint.setTextSize(sp2px(8f));
        txtPaint.getTextBounds(toValue, 0, toValue.length(), rect);
        txtWidth = Math.abs(rect.right - rect.left);
        txtHeight = Math.abs(rect.bottom - rect.top);

        canvas.drawText(toValue, (cellWidth - txtWidth) / 2 + (i + week) % 7 * cellWidth,
                txtHeight + (i + week) / 7 * cellHeight + cellHeight + dip2px(1.2f), txtPaint);
    }

    /***
     * 绘制今天被选中背景
     *
     * @param canvas
     */
    private void drawTodayGreenBg(Canvas canvas, int i, int week) {

        circleFillPaint.setColor(txtGreen);
        canvas.drawCircle(cellWidth / 2 + (i + week) % 7 * cellWidth,
                (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight, cellHeight / 2, circleFillPaint);
    }

    private void drawMenBg(Canvas canvas, int i, int week) {
        float left = cellWidth / 2 + (i + week) % 7 * cellWidth;
        float top = (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight;
        canvas.drawBitmap(men, left - men.getWidth() / 2, top + men.getHeight(), null);
    }


    private void drawRemarkLine(Canvas canvas, int i, int week) {
        int value = i + 1;
        if (baseData[selectYindex][selectXindex] == value) {//选中状态

            remarkLine.setColor(ovulationBg);

            if (value == toDay && value != bestDay && !getClickOvulation(value)) {
                //今天不是受孕日也不是最佳受孕日
                remarkLine.setColor(txtWrite);
            }
            if (value == toDay && getClickOvulation(value) && value != bestDay) {
                //今天是受孕日但不是最佳受孕日
                remarkLine.setColor(ovulationBg);
            }
            if (value == toDay && value == bestDay && getClickOvulation(value)) {
                //今天是受孕日也是最佳受孕日
                remarkLine.setColor(txtWrite);
            }
            if (value != toDay && value != bestDay && getClickOvulation(value)) {
                remarkLine.setColor(ovulationBg); // 不是今天 不是最佳受孕日，是受孕期
            }

            if (value != toDay && value == bestDay && getClickOvulation(value)) {
                remarkLine.setColor(txtWrite); // 不是今天 是最佳受孕日，是受孕期期
            }


        } else {
            if (getClickOvulation(value)) {
                remarkLine.setColor(txtWrite);
            } else {
                remarkLine.setColor(ovulationBg);
            }

        }

        float txtWidth, txtHeight;

        Rect rect = new Rect();
        txtPaint.getTextBounds("12", 0, "12".length(), rect);
        txtWidth = Math.abs(rect.right - rect.left);
        txtHeight = Math.abs(rect.bottom - rect.top);

        float left = (i + week) % 7 * cellWidth + (cellWidth - txtWidth) / 2;
        float top = (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight + txtHeight / 2 + dip2px(0.8f);

        canvas.drawLine(left, top, left + txtWidth + dip2px(2.3f), top, remarkLine);
    }

    /***
     * 绘制常规易孕期背景
     *
     * @param canvas
     * @param i
     * @param stopI
     * @param week
     */
    private void drawOvulationBg(Canvas canvas, int i, int stopI, int week) {

        ovulationPaint.setColor(ovulationBg);
        int step = 7 - getDataXY(i + 1)[0];

        boolean isRow = (i + step + week) % 7 == 0;

        int left = (int) ((i + week) % 7 * cellWidth + cellWidth / 2);
        int top = (int) ((i + week) / 7 * cellHeight + cellHeight + dip2px(2f));
        int right = (int) ((i + week) % 7 * cellWidth + step * cellWidth - (step == 7 ? cellWidth / 2 : 0));
        int bottom = (int) ((int) ((i + week) / 7 * cellHeight + cellHeight) + cellHeight - dip2px(2f));

        Rect rect = new Rect(left, top, right, bottom);

        canvas.drawRect(rect, ovulationPaint);

        if (isRow) {

            left = (int) ((i + step + week) % 7 * cellWidth);
            top = (int) ((i + step + week) / 7 * cellHeight + cellHeight + dip2px(2f));
            right = (int) ((i + step + week) % 7 * cellWidth + getDataXY(i + 1 + 7)[0] * cellWidth - cellWidth / 2);
            bottom = (int) ((int) ((i + step + week) / 7 * cellHeight + cellHeight) + cellHeight - dip2px(2f));

            Rect rect2 = new Rect(left, top, right, bottom);

            canvas.drawRect(rect2, ovulationPaint);

        }
    }

    private void drawOvulationStartCircleBg(Canvas canvas, int i, int week) {
        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(cellWidth / 2 + (i + week) % 7 * cellWidth,
                (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight, cellHeight / 2 - dip2px(2f), circleFillPaint);
    }

    private void drawOvulationStopCircleBg(Canvas canvas, int i, int stopI, int week) {
        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(cellWidth / 2 + (stopI + week) % 7 * cellWidth,
                (stopI + week) / 7 * cellHeight + cellHeight / 2 + cellHeight, cellHeight / 2 - dip2px(2f), circleFillPaint);
    }


    private void drawBestOvulationSelect(Canvas canvas, int i, int week) {
        float left = cellWidth / 2 + (i + week) % 7 * cellWidth;
        float top = (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight;

        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(left, top, cellHeight / 2 + dip2px(2f), circleFillPaint);

        circleFillPaint.setColor(bestDayColor);
        canvas.drawCircle(left, top, cellHeight / 2, circleFillPaint);

        canvas.drawBitmap(bestStar, left - bestStar.getWidth() / 2, top + bestStar.getHeight(), null);
    }

    private void drawTodayOvulationStartBg(Canvas canvas, int i, int week) {


        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(cellWidth / 2 + (i + week) % 7 * cellWidth,
                (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight, cellHeight / 2 - dip2px(2f), circleFillPaint);

        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(cellWidth / 2 + (i + week) % 7 * cellWidth,
                (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight - dip2px(4f), cellHeight / 2 - dip2px(2f), circleFillPaint);
    }

    private void drawTodayOvulationBg(Canvas canvas, int i, int week) {

        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(cellWidth / 2 + (i + week) % 7 * cellWidth,
                (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight - dip2px(4f), cellHeight / 2 - dip2px(2f), circleFillPaint);
    }

    /***
     * 今天最佳受孕选中
     *
     * @param canvas
     * @param i
     * @param week
     */
    private void drawBestTodayOvulationSelect(Canvas canvas, int i, int week) {
        float left = cellWidth / 2 + (i + week) % 7 * cellWidth;
        float top = (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight;

        circleFillPaint.setColor(ovulationBg);
        canvas.drawCircle(left, top, cellHeight / 2 + dip2px(2f), circleFillPaint);

        circleFillPaint.setColor(bestDayColor);
        canvas.drawCircle(left, top, cellHeight / 2, circleFillPaint);

        canvas.drawBitmap(bestStar, left - bestStar.getWidth() / 2, top + bestStar.getHeight(), null);
    }

    private void drawBestOvulationDay(Canvas canvas, int i, int week) {

        float left = cellWidth / 2 + (i + week) % 7 * cellWidth;
        float top = (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight;

        circleFillPaint.setColor(bestDayColor);
        canvas.drawCircle(left, top, cellHeight / 2 - dip2px(2f), circleFillPaint);

        canvas.drawBitmap(bestStar, left - bestStar.getWidth() / 2, top + bestStar.getHeight() / 3 * 2, null);
    }

    private void drawBestToDayvulationDay(Canvas canvas, int i, int week) {

        float left = cellWidth / 2 + (i + week) % 7 * cellWidth;
        float top = (i + week) / 7 * cellHeight + cellHeight / 2 + cellHeight;

        circleFillPaint.setColor(bestDayColor);
        canvas.drawCircle(left, top - dip2px(4f), cellHeight / 2 - dip2px(2f), circleFillPaint);

        canvas.drawCircle(left, top, cellHeight / 2 - dip2px(2f), circleFillPaint);

        canvas.drawBitmap(bestStar, left - bestStar.getWidth() / 2, top + bestStar.getHeight() / 3 * 2, null);
    }

    private void drawSelect(Canvas canvas) {

        if (!getClickOvulation(baseData[selectYindex][selectXindex])) {
            circleFillPaint.setColor(selectBg);
            canvas.drawCircle(selectXindex * cellWidth + cellWidth / 2, selectYindex * cellHeight + cellHeight / 2, cellHeight / 2, circleFillPaint);

            circlePaint.setColor(txtGreen);
            canvas.drawCircle(selectXindex * cellWidth + cellWidth / 2, selectYindex * cellHeight + cellHeight / 2, cellHeight / 2, circlePaint);
        }

    }


    private void drawSelectTodayOvulation(Canvas canvas) {

        circleFillPaint.setColor(ovulationDayBg);
        canvas.drawCircle(selectXindex * cellWidth + cellWidth / 2, selectYindex * cellHeight + cellHeight / 2, cellHeight / 2, circleFillPaint);

        circlePaint.setColor(ovulationBg);
        canvas.drawCircle(selectXindex * cellWidth + cellWidth / 2, selectYindex * cellHeight + cellHeight / 2, cellHeight / 2, circlePaint);
    }

    private boolean findXY(float x, float y) {
        if (location[1] <= 0) {
            getLocationInWindow(location);
        }

        if (x >= 0 && x <= viewWidth && y >= cellHeight + location[1] && y <= tabHeight + location[1]) {
            return true;
        }
        return false;
    }


    private void downState(float x, float y) {
        if (findXY(x, y)) {
            currX = x;
            currY = y;

            int tempX = (int) (x / viewWidth * 7);
            int tempY = (int) ((y - location[1]) / tabHeight * (row + 1));

            if (!isTopValid(tempX, tempY)) return;
            if (!isBottomValid(tempX, tempY)) return;

            selectXindex = tempX;
            selectYindex = tempY;

            Log.d("downState", "selectXindex = " + selectXindex + "---selectYindex = " + selectYindex);

            Log.d("downState", "viewWidth = " + viewWidth + "---tabHeight = " + tabHeight);

            Log.d("downState", "x = " + x + "---y = " + y);


            invalidate();
        }
    }

    private boolean isTopValid(int x, int y) {
        if (y == 1 && baseData[y][x] > 7) {
            return false;
        }

        return true;
    }

    private boolean isBottomValid(int x, int y) {
        if ((y == 5 || y == 6) && baseData[y][x] < 11) {
            return false;
        }

        return true;
    }

    private boolean getClickOvulation(int i) {
        for (int j = 0; j < ovulationData.length; j++) {
            if (i == ovulationData[j]) {
                return true;
            }
        }

        return false;
    }

    private int[] getDataXY(int d) {
        int[] is = new int[2];
        for (int i = 1; i < baseData.length; i++) {
            for (int j = 0; j < baseData[i].length; j++) {
                int v = baseData[i][j];
                if (d == v) {
                    if (!isTopValid(j, i)) continue;
                    if (!isBottomValid(j, i)) continue;
                    is[0] = j;
                    is[1] = i;
                    break;
                }


            }
        }
        return is;
    }

    public void createBaseData() {


        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();

        Date date = new Date();

        int week = getDayToWeek(format.format(date));

        calendar.setTime(date);

        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int toDay = calendar.get(Calendar.DATE);

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

        int lastDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        int next = 0;

        int nextCount = row * 7 - (days + week);

        for (int i = week; i > 0; i--) {
            baseData[1][week - i] = lastDays - (i - 1);
        }

        for (int i = week; i < row * 7; i++) {

            if (i <= days) {
                baseData[i / 7 + 1][i % 7] = i + 1 - week;
            }


            if (i > days && next < nextCount) {
                baseData[i / 7 + 1][i % 7] = ++next;
            }

        }

        System.out.println("----");

        for (int i = 0; i < baseData.length; i++) {
            for (int j = 0; j < baseData[i].length; j++) {
                System.out.print(baseData[i][j] + " ");
            }
            System.out.println();
        }


    }


    private void confirm(float x, float y) {

        if (findXY(x, y)) {

//			if(x>= currX && x <= currX+cellWidth && y>=currY && y <= currY+cellHeight){
//				if(outPatientCallBack != null){
            selectXindex = (int) (x / viewWidth * 7);
            selectYindex = (int) (y / tabHeight * row + 1);
//				}
//			}
        }
        invalidate();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//		if(!isClick) return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downState(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
//			confirm(event.getRawX(),event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                confirm(event.getRawX(), event.getRawY());
                break;
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getDefaultSize(getMinimumWidth(), widthMeasureSpec);
        viewHeight = getDefaultSize(getMinimumHeight(), heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawWeekDays(canvas);

        drawSelect(canvas);

        drawDay(canvas);


    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    private float sp2px(float spValue) {
        float fontScale = getResources().getDisplayMetrics().density;
        return (spValue * fontScale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public float dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }


    private int getDayToWeek(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        return w == 1 ? 7 : w - 1;
    }


    private int getDayToWeek(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = format.parse(dateStr);
            calendar.setTime(date);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            calendar.set(Calendar.DATE, 1);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int w = calendar.get(Calendar.DAY_OF_WEEK);
        return w == 1 ? 7 : w - 1;
    }


    private boolean isMen(int value) {
        for (int i = 0; i < menData.length; i++) {
            if (menData[i] == value) {
                return true;
            }
        }
        return false;
    }

}
