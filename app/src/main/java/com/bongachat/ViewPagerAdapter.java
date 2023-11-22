package com.bongachat;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Feeds();
            case 1: return new chat();
            case 2: return new wallet();
            case 3: return new UserPage();
            default: return new Feeds();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
