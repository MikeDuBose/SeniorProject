package com.examples.whywait.login;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class PopActivity extends Activity {

    public String name;
    public String guestName;
    private int partySize;
    public ArrayList<Backend> foundContacts = new ArrayList<>();
    public ArrayList<String> finalWaitTime = new ArrayList<>();
    public int total;
    public int value;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = -20;
        params.y = -20;

        getWindow().setAttributes(params);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            name = extras.getSerializable("RestaurantName").toString();
            guestName = extras.getSerializable("name").toString();
            Log.d("EXTRA EXTRA", "Your extras have passed with " + name + " " + guestName);

        }
        if (extras == null) {
            Log.d("Nope!", "No extras have been passed because you suck.");
        }

        TextView textView = findViewById(R.id.nameView);
        textView.setText(name);

        totalTime();



    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.queue:
                Bundle extras = getIntent().getExtras();

                if (extras != null) {
                    name = extras.getSerializable("RestaurantName").toString();
                    guestName = extras.getSerializable("name").toString();
                    Log.d("EXTRA EXTRA", "Your extras have passed with " + name + " " + guestName);


                    EditText editSize = findViewById(R.id.editSize);
                    String sizeString = editSize.getText().toString();
                    partySize = Integer.parseInt(sizeString);


                    Backend backend = new Backend();
                    backend.setGuestName(guestName);
                    backend.setPartySize(partySize);
                    backend.setRestName(name);




                    Backendless.Persistence.save(backend, new AsyncCallback<Backend>() {
                        public void handleResponse(Backend response) {
                            // new Contact instance has been saved
                            Log.d("SAVED!","If I'm being called, I should be saved");
                        }

                        public void handleFault(BackendlessFault fault) {
                            Log.d("Error!", "Error Fault Code : " + fault.getCode() + "Error Fault Message : " + fault.getMessage());
                            // an error has occurred, the error code can be retrieved with fault.getCode()
                        }
                    });
                }
                totalTime();
                Intent intent = new Intent(getApplicationContext(), Queue.class);
                Log.d("PartySize being passed", "total + " + total + "party size + " + partySize + "=" + (total+partySize));
                intent.putExtra("partySize", total + partySize);
                intent.putExtra("time", calcWaitTime(total));
                startActivity(intent);


/*
        String whereClause = "RestaurantName =" + "'" + name + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);


        Backendless.Data.of(Backend.class).find(queryBuilder,
                new AsyncCallback<List<Backend>>() {
                    @Override
                    public void handleResponse(List<Backend> foundContacts) {
                        if (foundContacts != null) {
                            PopActivity.this.foundContacts.add(foundContacts.get(0));
                            for (int i = 0; i < foundContacts.size(); i++) {
                                Log.d("Rest Name", "" + foundContacts.get(i).getRestaurantName());
                                Log.d("#Guests", "" + foundContacts.get(i).getNumGuests());
                                Log.d("Object ID", "" + foundContacts.get(i).getObjectId());
                            }
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                        Log.d("Error!", "Error code : " + fault.getCode());
                    }
                });*/
        break;
    }

}


    public void totalTime(){

        String whereClause = "restName =" + "'" + name + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);




        Backendless.Data.of(Backend.class).find(queryBuilder,
                new AsyncCallback<List<Backend>>() {


                    @Override
                    public void handleResponse(List<Backend> foundContacts) {
                        if (foundContacts != null) {
                            PopActivity.this.foundContacts.addAll(foundContacts);
                            for (int i = 0; i < foundContacts.size(); i++) {
                                total = total + foundContacts.get(i).getPartySize();
                                //TextView textView2 = findViewById(R.id.waitView);
                                //String stringTime = "" + total;
                                //textView2.setText(stringTime);
                                //finalWaitTime.add(total);
                                //calcWaitTime(total);



                            }

                            TextView textView2 = findViewById(R.id.waitView);
                            finalWaitTime.add(calcWaitTime(total));
                            textView2.setText(finalWaitTime.get(0));



                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                        Log.d("Error!", "Error code : " + fault.getCode());
                    }
                });



        //Log.d("DEBUG", "The total that is being passed to queue is " + finalWaitTime.get(0));

    }

    public String calcWaitTime(int time){
        String result = "";

        if(foundContacts.size() > 1 && foundContacts.size() <= 3){
            result = "Your estimated wait time is 5-10 minutes.";
        }
        if(foundContacts.size() >3 && foundContacts.size()<= 6){
            result = "Your estimated wait time is 10-15 minutes.";
        }
        if(foundContacts.size() >6 && foundContacts.size() <= 10){
            result = "Your estimated wait time is 20-30 minutes.";
        }

        Log.d("Found Contacts" , "The message result is " + result);
        return result;
    }


    public void setName(String name){
        this.name = name;

    }
    public String getName(){
        return name;
    }




}
