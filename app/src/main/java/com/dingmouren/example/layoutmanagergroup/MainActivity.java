package com.dingmouren.example.layoutmanagergroup;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.dingmouren.example.layoutmanagergroup.activity.SkidRightActivity_1;
import com.dingmouren.example.layoutmanagergroup.fragment.EchelonFragment;
import com.dingmouren.example.layoutmanagergroup.fragment.PickerFragment;
import com.dingmouren.example.layoutmanagergroup.fragment.SlideFragment;

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
    private List<String> mManagerNames = new ArrayList<>();//存储与Fragment对应的LayoutManager的名称

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
        mManagerNames.add("EchelonLayoutManager");

        PickerFragment pickerFragment = new PickerFragment();//选择器布局
        mFragments.add(pickerFragment);
        mManagerNames.add("PickerLayoutManager");

        SlideFragment slideFragment = new SlideFragment();//滑动布局
        mFragments.add(slideFragment);
        mManagerNames.add("SlideLayoutManager");

        mFragmentManager.beginTransaction()
                .add(R.id.container_layout, mFragments.get(0))
                .add(R.id.container_layout,mFragments.get(1))
                .add(R.id.container_layout,mFragments.get(2))
                .hide(mFragments.get(2))
                .hide(mFragments.get(1))
                .show(mFragments.get(0))
                .commit();
        mCurrentFragment = mFragments.get(0);
        mTvTitle.setText(mManagerNames.get(0));
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
            case R.id.item_1:
                switchFragment(1);
                break;
            case R.id.item_2:
                switchFragment(2);
                break;
            case R.id.item_3:
                startActivity(new Intent(MainActivity.this, SkidRightActivity_1.class));
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
        mTvTitle.setText(mManagerNames.get(position));
    }
}
