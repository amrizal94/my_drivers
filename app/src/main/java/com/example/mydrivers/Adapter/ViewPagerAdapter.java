package com.example.mydrivers.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mydrivers.Fragments.SigninFragment;
import com.example.mydrivers.Fragments.SignupFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public ViewPagerAdapter(Context c, @NonNull FragmentManager fm, int totalTabs){
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SigninFragment();
            case 1:
                return new SignupFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
