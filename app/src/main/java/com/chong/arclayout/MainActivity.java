package com.chong.arclayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView mListView;
    private ArcLayout mArcLayout;
    private ArcLayout mArcLayout2;
    private ImageView mIvMain;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        mListView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mData));

        initEvent();

    }

    private void initEvent() {
        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mArcLayout.isOpen()) {
                    mIvMain.setImageResource(R.drawable.shequ_img_sent);
                    mArcLayout.toggleMenu(300);
                }
            }
        });

        mArcLayout.setOnMenuItemClickListener(new ArcLayout.OnMenuItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, position + ":" + view.getTag(), Toast.LENGTH_SHORT).show();
                mIvMain.setImageResource(R.drawable.shequ_img_sent);
            }
        });
        mArcLayout.setOnMainMenuItemClickListener(new ArcLayout.OnMainMenuItemClickListener() {
            @Override
            public boolean onMainClick(View view, ArcLayout.Status status) {
                if (status == ArcLayout.Status.OPEN) { //当前展开状态，->关闭
                    mIvMain.setImageResource(R.drawable.shequ_img_sent);
                } else { // 当前关闭状态，->展开
                    mIvMain.setImageResource(R.drawable.shequ_ic_fatie_guanbi);
                }
                return true; // 需要展开，返回true
            }
        });

        mArcLayout2.setOnMainMenuItemClickListener(new ArcLayout.OnMainMenuItemClickListener() {

            @Override
            public boolean onMainClick(View view, ArcLayout.Status status) {
                Toast.makeText(MainActivity.this, "menu_left_top clicked", Toast.LENGTH_SHORT).show();
                return false; // 不需要展开，返回false
            }
        });
        mArcLayout2.setOnMenuItemClickListener(new ArcLayout.OnMenuItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, position + ":" + view.getTag(), Toast.LENGTH_SHORT).show();
                mIvMain.setImageResource(R.drawable.shequ_img_sent);
            }
        });
    }

    private void initData() {
        mData = new ArrayList<>();

        for (int i = 'A'; i < 'Z'; i++) {
            mData.add((char) i + "");
        }

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv);
        mArcLayout = (ArcLayout) findViewById(R.id.menu_right_bottom);
        mArcLayout2 = (ArcLayout) findViewById(R.id.menu_left_top);
        mIvMain = (ImageView) findViewById(R.id.iv_main);
    }

}
