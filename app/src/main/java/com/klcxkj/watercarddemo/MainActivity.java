package com.klcxkj.watercarddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.klcxkj.reshui.ACT_CardCenter;
import com.klcxkj.reshui.util.AppPreference;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 登录/注册
                Intent intent =new Intent(MainActivity.this, ACT_CardCenter.class);
                intent.putExtra("tellPhoneNum","18565651433");//手机号码
                intent.putExtra("prjRecId","127");//项目ID
                startActivity(intent);

            }
        });
        findViewById(R.id.login_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreference.getInstance().deleteLoginUser();
                AppPreference.getInstance().deleteCarddevice();

                finish();
            }
        });
    }
}
