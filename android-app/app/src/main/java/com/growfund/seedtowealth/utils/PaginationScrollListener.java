package com.growfund.seedtowealth.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Pagination scroll listener for RecyclerView.
 * Triggers loading of more items when user scrolls near the bottom.
 */
public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5; // Load more when 5 items from bottom

    private LinearLayoutManager layoutManager;

    public PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Only trigger if scrolling down
        if (dy <= 0) {
            return;
        }

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        // Check if we should load more
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition + VISIBLE_THRESHOLD) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    /**
     * Called when more items should be loaded.
     * Implement this to fetch the next page of data.
     */
    protected abstract void loadMoreItems();

    /**
     * @return true if currently loading data
     */
    public abstract boolean isLoading();

    /**
     * @return true if this is the last page (no more data to load)
     */
    public abstract boolean isLastPage();
}
