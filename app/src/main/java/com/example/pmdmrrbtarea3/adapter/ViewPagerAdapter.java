package com.example.pmdmrrbtarea3.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pmdmrrbtarea3.fragments.CapturedFragment;
import com.example.pmdmrrbtarea3.fragments.NonCapturedFragment;
import com.example.pmdmrrbtarea3.fragments.SettingsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new NonCapturedFragment();
            case 2:
                return new SettingsFragment();
            default:
                return new CapturedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;  // Número de fragments (pestañas)
    }
}
