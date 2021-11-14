package com.example.myapplication;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.models.Profesores;
import com.example.myapplication.resources.Profesorado;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class DisplayDepartIA extends AppCompatActivity  {

    private ViewPager2 mMyViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_ia_dept);

        mMyViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tablayout);

        init();
    }

    private void init(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        Profesores[] profesorado = Profesorado.getProfesoresIA();

        for(Profesores prof : profesorado){
            ViewPagerItemFragment fragment = ViewPagerItemFragment.getInstance(prof);
            MyPagerAdapter pagerAdapter = new MyPagerAdapter(fragment);
            mMyViewPager.setAdapter(pagerAdapter);
            fragments.add(fragment);
        }
       // new TabLayoutMediator(mTabLayout, mMyViewPager).attach();
        //MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        //mMyViewPager.setAdapter(pagerAdapter);
        //mTabLayout.setupWithViewPager(mMyViewPager, true);
        //mTabLayout.setupWithViewPager(mMyViewPager,true);
    }
}
