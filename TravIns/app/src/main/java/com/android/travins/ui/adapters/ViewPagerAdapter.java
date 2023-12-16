package com.android.travins.ui.adapters;

import com.android.travins.data.models.Places;
import com.android.travins.ui.fragments.OverviewFragment;
import com.android.travins.ui.fragments.ReviewFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private Places place;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new OverviewFragment(place);
            case 1:
                return new ReviewFragment(place);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void setPlaces (Places place){
        this.place = place;
    }
}
