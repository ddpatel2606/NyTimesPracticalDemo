package com.nytimespracticaldemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nytimespracticaldemo.model.MostPopularModel;
import com.nytimespracticaldemo.model.Result;
import com.nytimespracticaldemo.restclient.API;
import com.nytimespracticaldemo.restclient.ApiInterface;
import com.nytimespracticaldemo.restclient.RestClient;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    View recyclerView;
    String selectedDays="7";
    Boolean isTouch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

         recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        final List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("1 Days");
        spinnerArray.add("7 Days");
        spinnerArray.add("30 Days");

        CustomArrayAdapter adapter = new CustomArrayAdapter(this,
                R.layout.spinnerlayout, spinnerArray);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if(isTouch) {
                    selectedDays = spinnerArray.get(pos).toString().replace("Days", "").trim();
                    setupRecyclerView((RecyclerView) recyclerView);
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

    public class CustomArrayAdapter extends ArrayAdapter<String>{

        private final LayoutInflater mInflater;
        private final Context mContext;
        private final List<String> items;
        private final int mResource;

        public CustomArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                                  @NonNull List objects) {
            super(context, resource, 0, objects);

            mContext = context;
            mInflater = LayoutInflater.from(context);
            mResource = resource;
            items = objects;
        }
        @Override
        public View getDropDownView(int position, @Nullable View convertView,
                                    @NonNull ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        @Override
        public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        private View createItemView(int position, View convertView, ViewGroup parent){
            final View view = mInflater.inflate(mResource, parent, false);

            TextView offTypeTv = (TextView) view.findViewById(R.id.offer_type_txt);

            String offerData = items.get(position);

            offTypeTv.setText(offerData);

            return view;
        }
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {

        progressDialog = new ProgressDialog(ItemListActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiService = RestClient.getClient().create(ApiInterface.class);

        Call<MostPopularModel> call = apiService.getMostPopularNewsData(Integer.parseInt(selectedDays), API.NYTIMES_API_KEY);
        call.enqueue(new Callback<MostPopularModel>() {
            @Override
            public void onResponse(Call<MostPopularModel> call, Response<MostPopularModel> response) {
                Log.d(Consts.TAG, "Total number of questions fetched : " + response.body().getResults().size());

                progressDialog.dismiss();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemListActivity.this,RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                        linearLayoutManager.getOrientation());
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ItemListActivity.this,
                        response.body(), mTwoPane));
            }

            @Override
            public void onFailure(Call<MostPopularModel> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(Consts.TAG, "Got error : " + t.getLocalizedMessage());
            }
        });


    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final MostPopularModel mValues;
        private final boolean mTwoPane;

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      MostPopularModel items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mIdView.setText((position+1)+"");
            holder.mContentTitle.setText(mValues.getResults().get(position).getTitle());
            holder.mContentBy.setText(mValues.getResults().get(position).getByline());
            holder.mContentDate.setText(mValues.getResults().get(position).getPublishedDate());

            Picasso.Builder builder = new Picasso.Builder(mParentActivity);
            builder.downloader(new OkHttp3Downloader(mParentActivity));
            builder.build().load(mValues.getResults().get(position).getMedia().get(0).getMediaMetadata().get(2).getUrl())
                    .placeholder((R.drawable.ic_launcher_background))
                    .into(holder.mContentImage);


            holder.itemView.setTag(mValues.getResults().get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Result item = (Result) view.getTag();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("data", item);
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getId()+"");
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("data", item);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getId()+"");

                        context.startActivity(intent);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.getResults().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final ImageView mContentImage;
            final TextView mContentTitle;
            final TextView mContentBy;
            final TextView mContentDate;
            final RelativeLayout mainContent;


            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mainContent = (RelativeLayout) view.findViewById(R.id.mainView);
                mContentImage = (ImageView) view.findViewById(R.id.ContentImage);
                mContentTitle = (TextView) view.findViewById(R.id.contentTitle);
                mContentDate = (TextView) view.findViewById(R.id.Date);
                mContentBy = (TextView) view.findViewById(R.id.by_text);
            }
        }
    }
}
