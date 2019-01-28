package net.anvisys.xpen.Common;

import android.widget.Spinner;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {

    //static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    //static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    static final String Alternate_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


    public static String GetDateToString(Date date)
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);

            String LocalTime = sdf.format(date);
            return  LocalTime;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    public static String GetCurrentDateTimeLocal()
    {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);

            String LocalTime = sdf.format(new Date());
            return  LocalTime;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    public static String GetCurrentDateTimeUTC()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = sdf.format(new Date());
        return utcTime;
    }


    public static String ServerToLocalFormat(String date_time)
    {
        String  dTime="";
        SimpleDateFormat ldf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
        try{

            SimpleDateFormat sdf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
            Date inputDate= sdf.parse(date_time);

            dTime = ldf.format(inputDate);
        }
        catch (Exception ex)
        {
            dTime = ldf.format(new Date());
        }
        return dTime;
    }

    public static String ChangeToTimeOnly(String inDate)
    {
        String outTime ="";
        try
        {
            Date dateTime;
            try {
                SimpleDateFormat idf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }
            catch (Exception ex)
            {
                SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }

            Calendar c = Calendar.getInstance(Locale.getDefault());
            c.setTime(dateTime);
            outTime = (c.get(Calendar.HOUR_OF_DAY))+":"+(c.get(Calendar.MINUTE)); //Integer.toString(c.get(c.HOUR))+":" + Integer.toString(c.get(c.MINUTE));
        }
        catch (Exception ex)
        {

        }
        return outTime;
    }

    public static String GetDayOnly(String inDate)
    {
        String OutDate = "";
        try {
                SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            int Month = c.get(Calendar.MONTH)+1;

            int year = c.get(Calendar.YEAR);
            return Integer.toString(day);

        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }


    public static String GetMonthOnly(String inDate)
    {
        String OutDate = "";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            int Month = c.get(Calendar.MONTH)+1;

            int year = c.get(Calendar.YEAR);
            return   c.getDisplayName(Calendar.MONTH,Calendar.SHORT ,Locale.US);

        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }


    public static String GetYearOnly(String inDate)
    {
        String OutDate = "";
        try {
            SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);

            Date dateTime = idf.parse(inDate);
            Date localDate = new Date(dateTime.getTime() + TimeZone.getDefault().getRawOffset());

            Calendar c = Calendar.getInstance();
            c.setTime(localDate);
            int day =  c.get(Calendar.DAY_OF_MONTH);

            int Month = c.get(Calendar.MONTH)+1;

            int year = c.get(Calendar.YEAR);
            return  Integer.toString(year);

        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }



    public static String ChangeToDateTimeDisplayFormat(String inDate)
    {
        String OutDate = "";
        try {
            Date dateTime;
            try {
                SimpleDateFormat idf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }
            catch (Exception ex)
            {
                SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }

            Calendar c = Calendar.getInstance(Locale.getDefault());
            int CurrentYear = c.get(Calendar.YEAR);
            int CurrentDay = c.get(Calendar.DAY_OF_YEAR);

            c.setTime(dateTime);
            int day =  c.get(Calendar.DAY_OF_MONTH);
            String Month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int year = c.get(Calendar.YEAR);
            int dayOfYear = c.get(Calendar.DAY_OF_YEAR);

            String min = Integer.toString(c.get(c.MINUTE));
            if(min.length()==1)
            {
                min = "0"+ min;
            }

            String time =  c.get(c.HOUR_OF_DAY) +":" + min;

            if(CurrentDay == dayOfYear)
            {
                return time;
            }
            else if (year == CurrentYear)
            {
                return Integer.toString(day) + "" + Month; //+ "\n" + time;
            }
            else
            {
                return Integer.toString(day)  + Month +", \n" + Integer.toString(year).substring(2,4)  + " at " + time;
            }
        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static String ChangeToDateOnlyDisplayFormat(String inDate)
    {
        String OutDate = "";
        try {
            Date dateTime;
            try {
                SimpleDateFormat idf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }
            catch (Exception ex)
            {
                SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }

            Calendar c = Calendar.getInstance(Locale.getDefault());
            int CurrentYear = c.get(Calendar.YEAR);
            int CurrentDay = c.get(Calendar.DAY_OF_YEAR);

            c.setTime(dateTime);
            int day =  c.get(Calendar.DAY_OF_MONTH);
            String Month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int year = c.get(Calendar.YEAR);

            return Integer.toString(day)  + Month ;
        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static String ChangeToMonthDisplayFormat(String inDate)
    {
        String OutDate = "";
        try {
            Date dateTime;
            try {
                SimpleDateFormat idf = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }
            catch (Exception ex)
            {
                SimpleDateFormat idf = new SimpleDateFormat(Alternate_DATE_TIME_FORMAT);
                dateTime = idf.parse(inDate);
            }

            Calendar c = Calendar.getInstance(Locale.getDefault());
            int CurrentYear = c.get(Calendar.YEAR);
            int CurrentDay = c.get(Calendar.DAY_OF_YEAR);

            c.setTime(dateTime);
            int day =  c.get(Calendar.DAY_OF_MONTH);
            String Month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

            int year = c.get(Calendar.YEAR);

            return Month + "," + year;
        }
        catch (Exception ex)
        {
            int a =5;
            return "1 Jan, 2000";
        }

    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }


}

