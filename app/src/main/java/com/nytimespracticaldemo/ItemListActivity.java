package com.nytimespracticaldemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.nytimespracticaldemo.adapter.CustomArrayAdapter;
import com.nytimespracticaldemo.adapter.SimpleItemRecyclerViewAdapter;
import com.nytimespracticaldemo.databinding.ActivityItemListBinding;
import com.nytimespracticaldemo.model.MostPopularModel;
import com.nytimespracticaldemo.model.Result;
import com.nytimespracticaldemo.restclient.API;
import com.nytimespracticaldemo.restclient.ApiInterface;
import com.nytimespracticaldemo.restclient.RestClient;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    ProgressDialog progressDialog;
    String selectedDays="7";
    Boolean isTouch=false;
    SimpleItemRecyclerViewAdapter adapter;
    ActivityItemListBinding binding;
    MostPopularModel mostPopularModel;
    boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_list);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getTitle());
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adapter != null && adapter.selectedItemPosition != -1 && mostPopularModel != null) {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey check this Article : "+((Result)mostPopularModel.getResults().get(adapter.selectedItemPosition)).getUrl());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }


            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if(mTwoPane)
            binding.fab.setVisibility(View.VISIBLE);
        else
            binding.fab.setVisibility(View.GONE);

        setupRecyclerView();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        final List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("1 Day");
        spinnerArray.add("7 Day");
        spinnerArray.add("30 Day");

        CustomArrayAdapter adapter = new CustomArrayAdapter(this,
                R.layout.spinnerlayout, spinnerArray);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(isTouch) {
                    selectedDays = spinnerArray.get(pos).toString().replace("Day", "").trim();
                    setupRecyclerView();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouch=true;
                return false;
            }
        });

        if(selectedDays.equalsIgnoreCase("7"))
            spinner.setSelection(1);
        else if (selectedDays.equalsIgnoreCase("30"))
            spinner.setSelection(2);
        else if(selectedDays.equalsIgnoreCase("1"))
            spinner.setSelection(0);

        return true;
    }



    private void setupRecyclerView() {

        progressDialog = new ProgressDialog(ItemListActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        Call<MostPopularModel> call = apiService.getMostPopularNewsData(Integer.parseInt(selectedDays), API.NYTIMES_API_KEY);
        call.enqueue(new Callback<MostPopularModel>() {
            @Override
            public void onResponse(Call<MostPopularModel> call, Response<MostPopularModel> response) {
                Log.d(Consts.TAG, "Total number of questions fetched : " + response.body().getResults().size());

                mostPopularModel = response.body();
                progressDialog.dismiss();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemListActivity.this,RecyclerView.VERTICAL,false);
                binding.itemListView.itemList.setLayoutManager(linearLayoutManager);

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( binding.itemListView.itemList.getContext(),
                        linearLayoutManager.getOrientation());
                binding.itemListView.itemList.addItemDecoration(dividerItemDecoration);

                adapter = new SimpleItemRecyclerViewAdapter(ItemListActivity.this,
                        mostPopularModel, mTwoPane);
                binding.itemListView.itemList.setAdapter(adapter);

                if(mTwoPane)
                {
                    //Call this method which will perform click for 0'th position after computing (binding) all data...
                    postAndNotifyAdapter(new Handler(),  binding.itemListView.itemList);
                }
            }

            @Override
            public void onFailure(Call<MostPopularModel> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(Consts.TAG, "Got error : " + t.getLocalizedMessage());
            }
        });


    }

    // Select Automatic First Item while on Tablet
    protected void postAndNotifyAdapter(final Handler handler, final RecyclerView recyclerView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!recyclerView.isComputingLayout()) {
                        // This will call first item by calling "performClick()" of view.
                        ((SimpleItemRecyclerViewAdapter.MyViewHolder) recyclerView.findViewHolderForLayoutPosition(0)).itemView.performClick();
                    } else {
                        postAndNotifyAdapter(handler, recyclerView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
