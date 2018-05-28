package com.example.manny.myapplication;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class PageDiffCallback extends DiffUtil.Callback {

    private final List<Results.Result> mOldResultsList;
    private final List<Results.Result> mNewResultsList;

    public PageDiffCallback(List<Results.Result> oldEmployeeList, List<Results.Result> newEmployeeList) {
        this.mOldResultsList = oldEmployeeList;
        this.mNewResultsList = newEmployeeList;
    }

    @Override
    public int getOldListSize() {
        return mOldResultsList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewResultsList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldResultsList.get(oldItemPosition).getTitle() == mNewResultsList.get(
                newItemPosition).getTitle();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Results.Result oldResult = mOldResultsList.get(oldItemPosition);
        final Results.Result newResult = mNewResultsList.get(newItemPosition);

        return oldResult.getTitle().equals(newResult.getTitle());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
