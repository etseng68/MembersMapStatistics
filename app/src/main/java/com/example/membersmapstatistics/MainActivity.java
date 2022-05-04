package com.example.membersmapstatistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_TAG = "SearchMap";
    protected FloatingActionButton mFab;
    protected FrameLayout mContentFrame;
    protected TabLayout mTabLayout;
    protected Toolbar mToolbar;
    protected FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        //mContentFrame = findViewById(R.id.content_frameLayout);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        //mFragmentTransaction.add(R.id.map_frameLayout, new SearchMapFragment().newInstance("uid"),
        mFragmentTransaction.add(R.id.map_frameLayout, new SearchMapFragment(), FRAGMENT_TAG);
        mFragmentTransaction.commit();
        mFab = findViewById(R.id.fab);
        mFab.setTag("age");
        mFab.setImageResource(R.drawable.ic_bubble_chart_white_24px);
        mFab.setOnClickListener(view -> {
            //startActivity(new Intent(this, FlowsActivity.class));
            SearchMapFragment fragment = (SearchMapFragment)getSupportFragmentManager().
                    findFragmentByTag(FRAGMENT_TAG);
            if(view.getTag() == "age"){
                mFab.setImageResource(R.drawable.ic_baseline_wc_white_24);
                mFab.setTag("gender");
                fragment.showRangeType(SearchMapFragment.RANGE_TYPE_AGE);
            }else{
                mFab.setImageResource(R.drawable.ic_bubble_chart_white_24px);
                mFab.setTag("age");
                fragment.showRangeType(SearchMapFragment.RANGE_TYPE_GENDER);
            }
        });
    }
    private void initToolBar(){
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mTabLayout=findViewById(R.id.tabLayout);
        mTabLayout.setVisibility(View.GONE);
    }

}