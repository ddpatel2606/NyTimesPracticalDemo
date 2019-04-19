package com.nytimespracticaldemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.nytimespracticaldemo.ItemDetailActivity;
import com.nytimespracticaldemo.ItemDetailFragment;
import com.nytimespracticaldemo.ItemListActivity;
import com.nytimespracticaldemo.R;
import com.nytimespracticaldemo.databinding.ItemListContentBinding;
import com.nytimespracticaldemo.model.MostPopularModel;
import com.nytimespracticaldemo.model.Result;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.MyViewHolder> {

    private final ItemListActivity mParentActivity;
    private final MostPopularModel mValues;
    private final boolean mTwoPane;
    public int selectedItemPosition=-1;

    public SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                         MostPopularModel items,
                                         boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemListContentBinding binding = DataBindingUtil.inflate(layoutInflater,
                R.layout.item_list_content, parent, false);

        return new MyViewHolder(binding);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.binding.idTextView.setText((position + 1) + "");
        holder.binding.contentTitleTextView.setText(mValues.getResults().get(position).getTitle());
        holder.binding.byTextView.setText(mValues.getResults().get(position).getByline());
        holder.binding.dateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_date_range_black_24dp,0,0,0);
        holder.binding.dateTextView.setGravity(Gravity.CENTER_VERTICAL);
        holder.binding.dateTextView.setText(mValues.getResults().get(position).getPublishedDate());

        // loading album cover using Glide library
        Glide.with(mParentActivity).load(mValues.getResults().
                get(position).getMedia().get(0).getMediaMetadata().get(2).getUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.ContentImageView);

        holder.itemView.setTag(mValues.getResults().get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedItemPosition= position;
                Result item = (Result) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("data", item);
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getId() + "");
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra("data", item);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getId() + "");

                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.getResults().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final ItemListContentBinding binding;

        public MyViewHolder(ItemListContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
