package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventsearch.R;

public class RootFragment extends Fragment {
    private final String TAG = "RootFragment";
    private ViewPager viewPager;
    static final int NUM_ITEMS = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_root, container, false);
        Log.d(TAG, "onCreateView: ");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.root_frame, new SearchFragment());
        transaction.replace(R.id.root_frame, new SearchFragment());
        transaction.commit();
        return rootView;
    }
}