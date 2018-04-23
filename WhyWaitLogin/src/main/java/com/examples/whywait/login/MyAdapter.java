package com.examples.whywait.login;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

class LoadingViewHolder extends RecyclerView.ViewHolder{

    public ProgressBar progressBar;

    public LoadingViewHolder(View guestView){
        super(guestView);
        progressBar = (ProgressBar) guestView.findViewById(R.id.progressBar);
    }

}

class GuestViewHolder extends RecyclerView.ViewHolder{
    public TextView guestName, partySize, timeInLine;
    public GuestViewHolder(View guestView){
        super(guestView);
        guestName = (TextView) guestView.findViewById(R.id.guestName);
        partySize = (TextView) guestView.findViewById(R.id.partySize);
        timeInLine = (TextView) guestView.findViewById(R.id.timeInLine);
    }
}



public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING=1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<Backend> guests;
    int visibleThreshold = 5;
    int lastVisibleGuest,totalGuestCount;

    public MyAdapter(RecyclerView recyclerView, Activity activity, List<Backend> guests){
        this.activity = activity;
        this.guests = guests;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener((new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalGuestCount = linearLayoutManager.getItemCount();
                lastVisibleGuest = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalGuestCount <= (lastVisibleGuest+visibleThreshold)){
                    if(loadMore != null)
                        loadMore.onLoadMore();
                }
                isLoading = true;
            }
        }));

    }

    @Override
    public int getItemViewType(int position) {
        return guests.get(position) == null? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore){
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(activity).inflate(R.layout.activity_queue_remover, parent, false);
            return new GuestViewHolder(view);
        }
        else if(viewType == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(activity).inflate(R.layout.guest_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof GuestViewHolder){
            Backend backend = guests.get(position);
            GuestViewHolder viewHolder = (GuestViewHolder) holder;
            viewHolder.guestName.setText(guests.get(position).getGuestName());
            viewHolder.partySize.setText(String.valueOf(guests.get(position).getPartySize()));
            viewHolder.timeInLine.setText(guests.get(position).getCreated().toString());
        }
        else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    public void setLoading(){
        isLoading = false;
    }
}
