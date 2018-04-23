package com.examples.whywait.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.api.client.util.Data;

import java.util.ArrayList;
import java.util.List;

public class queueRemover extends Activity {

    List<Backend> guests = new ArrayList<>();
    MyAdapter adapter;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_remover_layout);


        //GET THE DATA HERE
        getData();


        //Initialize the view
        initUI();

        //Set Load more event
        setLoadEvent();

        doTheAutoRefresh();

        Log.d("Data", "Here are some guests data" + guests);

    }

    public void setLoadEvent(){
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore(){
                if(guests.size() <=20 ){
                    guests.add(null);
                    adapter.notifyItemInserted(guests.size()-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            guests.remove(guests.size()-1);
                            adapter.notifyItemRemoved(guests.size());
                            adapter.notifyDataSetChanged();
                            adapter.setLoading();

                        }
                    }, 5000);
                }else{
                    Toast.makeText(queueRemover.this, "Loading of guests completed!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void initUI(){
        RecyclerView recycler = (RecyclerView)findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(recycler, this, guests);
        recycler.setAdapter(adapter);
    }


    public void onClick(View view) {

        if(view.getId() == R.id.refresh){
            Intent intent = new Intent(getApplicationContext(), queueRemover.class);
            finish();
            startActivity(intent);
        }
        else if(view.getId() != R.id.refresh) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = findViewById(R.id.guestName);
                    if (tv.getText().toString() != null) {
                        String whereClause = "guestName = " + "'" + tv.getText().toString() + "'" + " and restName = 'India Garden'";
                        Log.d("Where Clause", "Where Clause = " + whereClause);
                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                        queryBuilder.setWhereClause(whereClause);
                        Backendless.Persistence.of(Backend.class).remove(whereClause);

                    }

                }

            }

        );

            thread.start();
        }
        adapter.notifyDataSetChanged();
        view.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), queueRemover.class);
        finish();
        startActivity(intent);


    }

    public void getData(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String whereClause = "restName = 'India Garden'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.addSortBy("created ASC");
                queryBuilder.setWhereClause(whereClause);
                List<Backend> result = Backendless.Data.of(Backend.class).find(queryBuilder);
                guests.addAll(result);
                Log.d("Result", "Result = " + result );
            }
        }){
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), queueRemover.class);
                finish();
                startActivity(intent);
            }
        }, 30000);
    }
}
