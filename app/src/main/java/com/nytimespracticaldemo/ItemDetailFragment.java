package com.nytimespracticaldemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.nytimespracticaldemo.databinding.ItemDetailBinding;
import com.nytimespracticaldemo.model.Result;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    public  Result mItem;
    ItemDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getParcelable("data");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getSource());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.item_detail,
                container,false);
        // get the root view
        View view = binding.getRoot();


        binding.itemDetail.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                Visible(binding.progressHorizontal);
                super.onPageStarted(view, url, favicon);
            }
        });

        binding.itemDetail.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int newProgress){
                // Update the progress bar with page loading progress
                binding.progressHorizontal.setProgress(newProgress);
                if(newProgress == 100){
                    // Hide the progressbar
                    Gone(binding.progressHorizontal);
                }
            }
        });

        binding.itemDetail.getSettings().setJavaScriptEnabled(true);
        binding.itemDetail.getSettings().setBuiltInZoomControls(true);
        binding.itemDetail.getSettings().setDisplayZoomControls(false);
        binding.itemDetail.loadUrl(mItem.getUrl());
        return view;
    }


    // slide the view from below itself to the current position
    public void Visible(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void Gone(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

}
