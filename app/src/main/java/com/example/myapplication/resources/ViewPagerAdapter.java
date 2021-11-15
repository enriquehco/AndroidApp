package com.example.myapplication.resources;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Page1;
import com.example.myapplication.Page2;
import com.example.myapplication.Page3;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Page1();
            case 1:
                return new Page2();
            default:
                return new Page3();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
