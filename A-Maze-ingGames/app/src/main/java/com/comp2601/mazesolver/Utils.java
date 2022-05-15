package com.comp2601.mazesolver;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class Utils
{
    private static int flag;

    public static void changeToTheme(Activity activity,int count)
    {

        Log.i("THEME", String.valueOf(activity.getTheme()));

        if(count == 0)
        {
            flag = 1;
        }
        else if(count == 2)
        {
            flag = 0;
        }
        else if(count == 1)
        {
            flag = 1;
        }
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch(flag) {

            default:
            case 0:
                activity.setTheme(R.style.AppThemeDark);
                break;
            case 1:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }
}