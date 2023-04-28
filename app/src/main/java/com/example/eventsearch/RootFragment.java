package com.example.eventsearch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class RootFragment extends Fragment {
    private ViewPager viewPager;
    static final int NUM_ITEMS = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_root, container, false);
//        viewPager = rootView.findViewById(R.id.viewpager2);
//
//        SlidePagerAdapter spAdapter = new SlidePagerAdapter(getFragmentManager());
//        viewPager.setAdapter(spAdapter);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.root_frame, new SearchFragment());
        transaction.commit();
        return rootView;
    }

    public class SlidePagerAdapter extends FragmentPagerAdapter {
        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new SearchFragment();
            else
                return new ResultsFragment();
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
}