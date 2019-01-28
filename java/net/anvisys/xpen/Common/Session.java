package net.anvisys.xpen.Common;

import android.content.Context;
import android.content.SharedPreferences;

import net.anvisys.xpen.Object.ActivityData;
import net.anvisys.xpen.Object.Profile;

public class Session {


    public static boolean AddUser(Context context, Profile myProfile)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UserID", myProfile.UserID);
            editor.putString("UserRole", myProfile.Role);
            editor.putString("UserLocation", myProfile.Location);
            editor.putString("OrganizationID", myProfile.OrgID);
            editor.putString("OrganizationName", myProfile.OrganizationName);
            editor.putString("regID", myProfile.REG_ID);
            editor.putString("MobileNo",myProfile.MOB_NUMBER);
            editor.putString("Name",myProfile.NAME);
            editor.putString("Email",myProfile.E_MAIL);
            editor.putString("ImageString",myProfile.strImage);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static Profile GetUser(Context context)
    {
        Profile mProfile = new Profile();
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            mProfile.UserID = prefs.getString("UserID","");
            mProfile.Role = prefs.getString("UserRole","");
            mProfile.Location = prefs.getString("UserLocation","");
            mProfile.OrgID =  prefs.getString("OrganizationID","");
            mProfile.OrganizationName =  prefs.getString("OrganizationName","");
            mProfile.MOB_NUMBER =  prefs.getString("MobileNo","");
            mProfile.NAME =  prefs.getString("Name","");
            mProfile.E_MAIL =  prefs.getString("Email","");
            mProfile.strImage =  prefs.getString("ImageString","");
            mProfile.REG_ID =  prefs.getString("regID","");

            return mProfile;
        }
        catch (Exception ex)
        {
            return null;
        }
    }


    public static boolean SetProject(Context context, ActivityData currentActivity)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("ActivityID", currentActivity.ActivityID);
            editor.putString("ActivityName", currentActivity.ActivityName);
            editor.putInt("ProjectID", currentActivity.ProjectID);
            editor.putString("ProjectName", currentActivity.ProjectName);
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static ActivityData GetProject(Context context)
    {
        ActivityData currentActivity = new ActivityData();
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            currentActivity.ActivityID = prefs.getInt("ActivityID",0);
            currentActivity.ActivityName =  prefs.getString("ActivityName","");
            currentActivity.ProjectID =  prefs.getInt("ProjectID",0);
            currentActivity.ProjectName =  prefs.getString("ProjectName","");

            return currentActivity;
        }
        catch (Exception ex)
        {
            return null;
        }
    }


    public static boolean LogOff(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            return true;

        }
        catch (Exception ex)
        {
            return false;
        }
    }


    public static boolean SetProjectSyncTime(Context context)
    {
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ProjectSyncTime", Utility.GetCurrentDateTimeUTC());
            editor.commit();
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static String GetProjectSyncTime(Context context)
    {
        String SyncTime;
        try {
            SharedPreferences prefs = context.getSharedPreferences(APP_CONST.SESSION_NAME, Context.MODE_PRIVATE);
            SyncTime = prefs.getString("ProjectSyncTime","2018-01-01T10:00:00");
            //return SyncTime;
            return "2018-01-01T10:00:00";
        }
        catch (Exception ex)
        {
            return "";
        }
    }
}
