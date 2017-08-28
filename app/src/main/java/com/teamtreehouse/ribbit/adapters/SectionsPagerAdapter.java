package com.teamtreehouse.ribbit.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.teamtreehouse.ribbit.R;
import com.teamtreehouse.ribbit.ui.FriendsFragment;
import com.teamtreehouse.ribbit.ui.InboxFragment;

import java.util.Locale;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;
/*Make the fragments variables so that the adapter doesn't have to create fragments each time
getItem is called*/
    public FriendsFragment mFriendsFragment = new FriendsFragment();
    public InboxFragment mInboxFragment = new InboxFragment();

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

// getItem is called to instantiate the fragment for the given page.
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mInboxFragment;
            case 1:
                return mFriendsFragment;
        }
        return null;
    }

//There's only two fragments to be populated in this adapter so the return is 2.
    @Override
    public int getCount() {
        return 2;
    }

//Set the title of the tabs.
    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null; //We'll use icon instead so return null.
    }

//Set the icon of the tabs.
    public int getIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_tab_inbox;
            case 1:
                return R.drawable.ic_tab_friends;
        }
        return R.drawable.ic_tab_inbox;
    }
}





