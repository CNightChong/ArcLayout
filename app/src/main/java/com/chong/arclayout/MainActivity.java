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
            public void onItemClick(View view, int pos) {
                Toast.makeText(MainActivity.this, pos + ":" + view.getTag(), Toast.LENGTH_SHORT).show();
                mIvMain.setImageResource(R.drawable.shequ_img_sent);
            }
        });
        mArcLayout.setOnMainMenuItemClickListener(new ArcLayout.OnMainMenuItemClickListener() {
            @Override
            public void onMainClick(View view, ArcLayout.Status status) {
                if (status == ArcLayout.Status.CLOSE) {
                    mIvMain.setImageResource(R.drawable.shequ_img_sent);
                } else {
                    mIvMain.setImageResource(R.drawable.shequ_ic_fatie_guanbi);
                }
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
        mListView = (ListView) findViewById(R.id.id_listview);
        mArcLayout = (ArcLayout) findViewById(R.id.id_menu);
        mIvMain = (ImageView) findViewById(R.id.iv_main);
    }

}
