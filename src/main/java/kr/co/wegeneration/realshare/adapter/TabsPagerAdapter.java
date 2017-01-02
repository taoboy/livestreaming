package kr.co.wegeneration.realshare.adapter;

import kr.co.wegeneration.realshare.FriendFragment;
import kr.co.wegeneration.realshare.ActivityFragment;

import kr.co.wegeneration.realshare.TimeLineFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                //if(holder.tab1 == null ) holder.tab1 = new FriendFragment();
                return new FriendFragment();
            case 1:
                //if(holder.tab2 == null ) holder.tab2 = new TimeLinesFragment();
                return  new TimeLineFragment();
            case 2:
                //if(holder.tab3 == null ) holder.tab3 = new ActivityFragment();
                return new ActivityFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
