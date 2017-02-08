/** */
package com.widget.android.utils;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 转换辅助类
 *
 * @author liujia
 */
public class ConvertUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pixl2DIP(float pixl, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixl, context.getResources().getDisplayMetrics());
    }

    // 辅助方法，用于把流转换为字符串
    public static String convertStreamToString(InputStream is)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayBuffer bab = new ByteArrayBuffer(32);
        int current = 0;
        while ((current = bis.read()) != -1) {
            bab.append((byte) current);
        }
        String result = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
        return result;

    }

    // 辅助方法，用于把流转换为字符串
    public static String convertStreamToString(InputStream is, String encoding)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayBuffer bab = new ByteArrayBuffer(32);
        int current = 0;
        while ((current = bis.read()) != -1) {
            bab.append((byte) current);
        }
        String result = EncodingUtils.getString(bab.toByteArray(), encoding);
        return result;

    }

    /**
     * 日期时间转字符串
     *
     * @param date              DATE型
     * @param dateTimeFormatStr 格式
     * @return 示例：当前日期：2007年07月01日12时14分25秒。 dateTimeToStr(new
     * Date(),"yyyy-MM-dd HH:mm:ss"); 返回：2007-07-01 12:14:25
     */
    public static String dateTimeToStr(Date date, String dateTimeFormatStr) {
        String rsStr = null;
        if (dateTimeFormatStr != null) {
            SimpleDateFormat df = new SimpleDateFormat(dateTimeFormatStr);
            rsStr = df.format(date);
        } else {
            rsStr = dateTimeToStr(date, "yyyy年MM日");
        }
        return rsStr;
    }

    public static Date dateStrToTime(String dateStr, String dateTimeFormatStr) {
        SimpleDateFormat df = new SimpleDateFormat(dateTimeFormatStr);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 偷懒模板方法
    // public static void operate(final View view, final String url,
    // final int resId, final Map<String, String> params,
    // final Executable... executables) {
    // view.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // HttpRequestAsynTask httpRequestAsynTask = new HttpRequestAsynTask(
    // view.getContext()) {
    // @Override
    // protected void doPostExecute(String result) {
    // executables[0].execute(result);
    // }
    // };
    // httpRequestAsynTask.execute(url, TemplateUtil
    // .assembleFromTemplate(view.getContext(), resId, params));
    // }
    // });
    // }

    public static String converTimeFromString(String timeString) {
        String hour = timeString.substring(0, 2);
        String min = timeString.substring(2, 4);
        return hour + ":" + min;
    }

    public static String compputeDate(int beforeDay, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, beforeDay);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }

    public static String transDate(String dateStr, String orgformat,
                                   String targetFormat) throws Exception {
        SimpleDateFormat orgDateFormat = new SimpleDateFormat(orgformat);
        Date date = orgDateFormat.parse(dateStr);
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(targetFormat);
        return targetDateFormat.format(date);
    }

    public static long birthday2ageDay(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        long days = (cal.getTime().getTime() - birthDay.getTime()) / (1000 * 60 * 60 * 24);
        return days;
    }

    @SuppressWarnings("deprecation")
    public static int birthday2age(String dateStr, String format) {
        SimpleDateFormat orgDateFormat = new SimpleDateFormat(format);
        Date orgDate = null;
        try {
            orgDate = orgDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (new Date().getYear() - orgDate.getYear());
    }

    public static int birthday2age(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                } else {
                    //do nothing
                }
            } else {
                //monthNow>monthBirth
                age--;
            }
        } else {
            //monthNow<monthBirth
            //donothing
        }
        return age;
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int daysOfTwoForOneYear(Date fDate, Date oDate) {
        //首先定义一个calendar，必须使用getInstance()进行实例化
        Calendar aCalendar = Calendar.getInstance();
        //里面也可以直接插入date类型
        aCalendar.setTime(fDate);
        //计算此日期是一年中的哪一天
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        //求出两日期相隔天数
        int days = day2 - day1 + 1;
        return days;
    }

    public static int daysOfTwo(Date fDate, Date oDate) {
        long quot = 0;
        quot = oDate.getTime() - fDate.getTime();
        quot = quot / 1000 / 60 / 60 / 24;
        return (int) quot;
    }


    public static int curDateDiff(String oldDateString, String oldDateFormat,
                                  int calendarType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldDateFormat);
        try {
            Date oldDate = dateFormat.parse(oldDateString);
            Date curDate = new Date();
            long timeDiff = curDate.getTime() - oldDate.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeDiff);

            if (Calendar.YEAR == calendarType) {
                return calendar.get(Calendar.YEAR);
            } else if (Calendar.MONTH == calendarType) {
                return calendar.get(Calendar.MONTH);
            } else if (Calendar.DAY_OF_YEAR == calendarType) {
                return calendar.get(Calendar.MONTH);
            }
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return 0;
    }

    public static void changeTextColor(TextView... textViews) {
        for (TextView textView : textViews) {
            if (textView.isPressed()) {
                textView.setTextColor(Color.WHITE);
            } else {
                textView.setTextColor(Color.GRAY);
            }
        }
    }

    public static void changeInputType(final Map<String, Integer> inputMap,
                                       final EditText editText) {
        editText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
                    if (entry.getKey().equals(editText.getTag().toString())) {
                        Log.i("mytag", "键盘系列。.。。。" + entry.getKey() + "--"
                                + entry.getValue());
                        editText.setInputType(entry.getValue());
                        break;
                    }

                }

            }
        });
    }

    /**
     * 从raw中读取字符串文件
     *
     * @param context
     * @param resultXmlId
     * @return
     */
    public static String assembleRawResourceString(Context context,
                                                   int resultXmlId) {
        String result = "";
        try {
            result = ConvertUtils.convertStreamToString(context.getResources()
                    .openRawResource(resultXmlId));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * fontScale + 0.5f);
    }
}
