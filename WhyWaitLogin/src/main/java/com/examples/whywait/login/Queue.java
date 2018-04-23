package com.examples.whywait.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class Queue extends Activity {

    private String guestName;
    private int partySize;
    private String restName;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 1), (int) (height * 1));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = -0;
        params.y = -0;

        getWindow().setAttributes(params);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String stringpz = extras.getSerializable("partySize").toString();
            String stringge = extras.getSerializable("time").toString();
            Log.d("Time estimate queue", "" + stringge);
            partySize = Integer.parseInt(stringpz);

        }




        TextView waitTime = findViewById(R.id.textView5);
        int time = partySize;
        Log.d("PARTY SIZE IN QUEUE IS", "PARTY SIZE IN QUEUE ISSSSSS " + partySize);
        String timeString = "" + time + " minutes.";
        waitTime.setText(timeString);

    }

    public String getGuestName(){ return guestName;}

    public void setGuestName(String guestName){ this.guestName = guestName; }

    public int getPartySize(){ return partySize;}

    public void setPartySize(int partySize){ this.partySize = partySize; }

    public String getRestName(){ return restName;}

    public void setRestName(String restName){ this.restName = restName; }

    public int waitTime(int time){
        return time * 2;
    }
    public static void findAsync(DataQueryBuilder queryBuilder, AsyncCallback<List<Queue>> callback )
    {
        Backendless.Data.of( Queue.class ).find( queryBuilder, callback );
    }
}
