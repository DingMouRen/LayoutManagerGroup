package com.dingmouren.example.layoutmanagergroup;

import android.app.FragmentManagerNonConfig;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

import com.dingmouren.example.layoutmanagergroup.fragment.EchelonFragment;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTvTitle;
    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments = new ArrayList<>();//存储所有的Fragment对象
    private List<String> mManageNames = new ArrayList<>();//存储与Fragment对应的LayoutManager的名称

    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        mTvTitle = findViewById(R.id.tv_title);
        mFragmentManager = getSupportFragmentManager();

        initFragments();

    }

    private void initFragments() {
        EchelonFragment echelonFragment = new EchelonFragment();//梯形布局
        mFragments.add(echelonFragment);
        mManageNames.add("EchelonLayoutManager");

        mFragmentManager.beginTransaction()
                .replace(R.id.container_layout, mFragments.get(0))
                .commit();
        mCurrentFragment = mFragments.get(0);
        mTvTitle.setText("LayoutManagerGroup");
//        mTvTitle.setText(mManageNames.get(0));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_0:
                switchFragment(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(int position) {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .hide(mCurrentFragment)
                .show(mFragments.get(position))
                .commit();
        mCurrentFragment = mFragments.get(position);
        mTvTitle.setText(mManageNames.get(position));
    }
}
