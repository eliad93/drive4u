package com.example.eliad.drive4u.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.eliad.drive4u.models.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeacherHomeTabAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Teacher mTeacher;

    public TeacherHomeTabAdapter(FragmentManager fm, Teacher teacher) {
        super(fm);
        mTeacher = teacher;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
