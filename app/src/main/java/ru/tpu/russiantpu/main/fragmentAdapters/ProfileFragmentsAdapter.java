package ru.tpu.russiantpu.main.fragmentAdapters;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.fragments.profile.DocumentsFragment;
import ru.tpu.russiantpu.main.fragments.profile.PersonalInfoFragment;
import ru.tpu.russiantpu.main.fragments.profile.TpuPortalFragment;

public class ProfileFragmentsAdapter extends FragmentPagerAdapter {

    private final int itemsSize = 3;
    private final String[] tabTitles;

    public ProfileFragmentsAdapter(@NonNull FragmentManager fm, int behavior, Resources resources) {
        super(fm, behavior);
        this.tabTitles = getTabTitles(resources);
    }

    //получение названий вкладок через ресурсы
    //т.о., названия локализированы
    private String[] getTabTitles(Resources resources) {
        String[] titles = new String[itemsSize];
        titles[0] = resources.getString(R.string.personal_info_name);
        titles[1] = resources.getString(R.string.portal_name);
        titles[2] = resources.getString(R.string.documents_name);
        return titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new TpuPortalFragment();
            case 2:
                return new DocumentsFragment();
            default:
                return new PersonalInfoFragment();
        }
    }

    @Override
    public int getCount() {
        return itemsSize;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
