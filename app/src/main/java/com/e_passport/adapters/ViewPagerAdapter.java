package com.e_passport.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.e_passport.pages.HomePage;
import com.e_passport.pages.UserPage;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomePage.newInstance();
            case 1:
                return UserPage.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
