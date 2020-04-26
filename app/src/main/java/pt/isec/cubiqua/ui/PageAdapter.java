package pt.isec.cubiqua.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import pt.isec.cubiqua.MainActivity;

public class PageAdapter extends FragmentPagerAdapter {

    private int numTabs;
    private MainActivity activity;

    public PageAdapter(@NonNull FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabRecorderFragment();
            case 1:
                TabMonitorFragment mon = new TabMonitorFragment();

                return mon;
            default:
                return null;
        }
    }

    public void injectMainActivity(MainActivity act) {
        this.activity = act;
    }

    @Override
    public int getCount() {
        return this.numTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
