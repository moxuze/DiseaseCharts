package com.moxtar_1s.android.disease_charts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.moxtar_1s.android.disease_charts.china.ChinaFragment;
import com.moxtar_1s.android.disease_charts.global.GlobalFragment;
import com.moxtar_1s.android.disease_charts.news.NewsData;
import com.moxtar_1s.android.disease_charts.news.NewsFragment;
import com.moxtar_1s.android.disease_charts.rumors.RumorsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NewsFragment.OnListFragmentInteractionListener {
    ViewPager2 viewPager2;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.view_pager2);
        tabLayout = findViewById(R.id.tab_layout);

        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(ChinaFragment.newInstance());
        fragments.add(GlobalFragment.newInstance());
        fragments.add(NewsFragment.newInstance());
        fragments.add(RumorsFragment.newInstance());

        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }
            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });

        // 预加载页面
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(getString(R.string.tab_china));
                        break;
                    case 1:
                        tab.setText(getString(R.string.tab_global));
                        break;
                    case 2:
                        tab.setText(getString(R.string.tab_news));
                        break;
                    case 3:
                        tab.setText(getString(R.string.tab_rumors));
                        break;
                    default:
                        tab.setText("?");
                }
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onListFragmentInteraction(NewsData.NewsItem item) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(item.sourceUrl));
        startActivity(intent);
    }
}
