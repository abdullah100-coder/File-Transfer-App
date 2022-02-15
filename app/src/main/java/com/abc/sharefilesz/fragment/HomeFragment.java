package com.abc.sharefilesz.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.abc.sharefilesz.activity.ConnectionManagerActivity;
import com.abc.sharefilesz.activity.ContentSharingActivity;
import com.abc.sharefilesz.activity.Activity;
import com.abc.sharefilesz.model.TitleSupport;
import com.abc.sharefilesz.R;
import com.abc.sharefilesz.adapter.SmartFragmentPagerAdapter;
import com.genonbeta.android.framework.ui.callback.SnackbarSupport;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment
        extends com.genonbeta.android.framework.app.Fragment
        implements TitleSupport, SnackbarSupport, com.genonbeta.android.framework.app.FragmentImpl, Activity.OnBackPressedListener
{
    private ViewPager mViewPager;
    private SmartFragmentPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton actionReceive = (FloatingActionButton)view.findViewById(R.id.floating_receive);

        actionReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ConnectionManagerActivity.class)
                        .putExtra(ConnectionManagerActivity.EXTRA_ACTIVITY_SUBTITLE, getString(R.string.text_receive))
                        .putExtra(ConnectionManagerActivity.EXTRA_REQUEST_TYPE, ConnectionManagerActivity.RequestType.MAKE_ACQUAINTANCE.toString()));
            }
        });

        FloatingActionButton actionSend = (FloatingActionButton)view.findViewById(R.id.floating_send);
        actionSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ContentSharingActivity.class));
            }
        });

        final BottomNavigationView bottomNavigationView = view.findViewById(R.id.layout_home_bottom_navigation_view);
        mViewPager = view.findViewById(R.id.layout_home_view_pager);
        mAdapter = new SmartFragmentPagerAdapter(getContext(), getChildFragmentManager());

        mAdapter.add(new SmartFragmentPagerAdapter.StableItem(0, TransferGroupListFragment.class, null));

        mAdapter.createTabs(bottomNavigationView);
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {

            }

            @Override
            public void onPageSelected(int i)
            {
                bottomNavigationView.setSelectedItemId(i);
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                mViewPager.setCurrentItem(menuItem.getOrder());
                return true;
            }
        });

        return view;
    }

    @Override
    public CharSequence getTitle(Context context)
    {
        return context.getString(R.string.text_home);
    }

    @Override
    public boolean onBackPressed()
    {
        Object activeItem = mAdapter.getItem(mViewPager.getCurrentItem());

        if ((activeItem instanceof Activity.OnBackPressedListener
                && ((Activity.OnBackPressedListener) activeItem).onBackPressed()))
            return true;

        if (mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(0, true);
            return true;
        }

        return false;
    }
}
