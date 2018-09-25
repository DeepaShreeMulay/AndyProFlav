package com.vritti.vrittiaccounting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.vritti.vrittiaccounting.data.AdatSoftData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin-3 on 8/29/2017.
 */

public class BalanceCashActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    String Acno,name_mar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_ledger);
        GetIntentExtras();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setIcon(icondrawable);
        getSupportActionBar().setTitle(" "+name_mar_title);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.cash);
        BitmapDrawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 120, 120, true));
        getSupportActionBar().setIcon(d);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).getIcon().setAlpha(255);
                    tabLayout.getTabAt(1).getIcon().setAlpha(100);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(0).getIcon().setAlpha(100);
                    tabLayout.getTabAt(1).getIcon().setAlpha(255);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

    }

    private void GetIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name_mar_title = (String) bundle.get("name_mar_title");
           /* Acno = (String) bundle.get("Acno");
            AdatSoftData.Selected_Acno = Acno;*/
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DatewiseBalanceFragment(), "Datewise Balance");
        adapter.addFragment(new PaymentReceiptFragment(), "Receipt & Payment");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //AdatSoftData.Selected_Acno = null;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
