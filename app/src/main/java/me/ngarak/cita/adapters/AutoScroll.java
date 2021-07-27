package me.ngarak.cita.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AutoScroll extends RecyclerView.OnScrollListener {
    //starting page index
    private final int startingPageIndex = 0;
    private final RecyclerView.LayoutManager layoutManager;
    //Minimum items before scrolling
    private final int visibleThreshold = 5;
    //current index of data loaded
    private int currentPage = 0;
    //total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    private boolean loading = true;

    public AutoScroll(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int lastVisibleItemPosition = 0;
        int totalItemCount = layoutManager.getItemCount();

        if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        //if total item is zero list is reseated back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

//        Log.d("TAG", ": startingPageIndex  " + startingPageIndex);
//        Log.d("TAG", ": previousTotalItemCount  " + previousTotalItemCount);

        //if it is loading, check if data set has changed. Then stop loading
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        //if it is not loading, check is threhold has met and reload more data
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, recyclerView);
            loading = true;
        }

//        Log.d("TAG", "onScrolledPage: " + currentPage);
    }

    // loads more data per page
    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
