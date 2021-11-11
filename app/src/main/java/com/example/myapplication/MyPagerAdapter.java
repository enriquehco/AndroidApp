package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.models.Profesores;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mFragments;

    public MyPagerAdapter(Fragment fragment) {
        super(fragment);
        //mFragments = fragments;
    }

    @NonNull
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @NonNull
    public Fragment createFragment(Profesores profesor) {
        Fragment fragment = new Fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("profesor",profesor);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return null;
    }
}
