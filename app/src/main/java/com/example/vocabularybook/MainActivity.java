package com.example.vocabularybook;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fragments.Fragment1;
import fragments.WordFragment;

public class MainActivity extends AppCompatActivity {
    TabLayout mytab;
    ViewPager viewP;
    List<String> mTitle;
    List<Fragment> mFragment;
    private PopupWindow popupWindow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mytab = findViewById(R.id.Tablayout);
        viewP = findViewById(R.id.viewP);

        mTitle = new ArrayList<>();
        mTitle.add("主页");
        mTitle.add("生词");
        mFragment = new ArrayList<>();
        mFragment.add(new Fragment1());
        mFragment.add(new WordFragment());
        viewP.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        mytab.setupWithViewPager(viewP);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddWord.class);
                startActivity(intent);
            }
        });
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(MainActivity.this, LandActivity.class);

            startActivity(intent);
            finish(); //退出当前的activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            initPopweindow();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void initPopweindow(){
        View contentView = LayoutInflater.from(this).inflate(R.layout.pophelp, null);
        popupWindow = new PopupWindow(contentView,
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        View rootview = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

}

