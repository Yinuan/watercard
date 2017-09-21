package com.klcxkj.reshui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.klcxkj.klcxkj_waterdemo.R;

public class Main4Activity extends Activity {

    Button b7;
    Button b8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        b7 = (Button) findViewById(R.id.button7);

    }
}
