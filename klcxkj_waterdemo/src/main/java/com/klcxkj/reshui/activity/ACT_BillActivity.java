package com.klcxkj.reshui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.fragment.ConsumptionFragment;
import com.klcxkj.reshui.fragment.RechargeFragment;
import com.klcxkj.reshui.fragment.TransferFragment;


public class ACT_BillActivity extends AppCompatActivity {

  private View tView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__bill);
        // 判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的 //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else {
            tView =findViewById(R.id.top_menu_view);
            tView.setVisibility(View.GONE);
        }
        initview();
    }

    private void initview() {
        TextView title = (TextView) findViewById(R.id.top_title);
        title.setText("账单中心");
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.top_btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);


        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }
    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TransferFragment();
                case 1:
                    return new RechargeFragment();
                case 2:
                default:
                    return new ConsumptionFragment();

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "转账记录";
                case 1:
                    return "卡充值记录";
                case 2:
                default:
                    return "消费明细";
            }
        }
    }
}
