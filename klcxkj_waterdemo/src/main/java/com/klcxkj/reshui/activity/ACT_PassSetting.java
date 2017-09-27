package com.klcxkj.reshui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.klcxkj.klcxkj_waterdemo.R;
import com.klcxkj.reshui.entry.BaseBo;
import com.klcxkj.reshui.entry.UserInfo;
import com.klcxkj.reshui.tools.StringConfig;
import com.klcxkj.reshui.util.AppPreference;

import org.json.JSONObject;

import java.util.HashMap;

public class ACT_PassSetting extends ACT_Network {

    private EditText pass1,pass2;
        private Button btn;
    private String AlterPass = StringConfig.BASE_URL+"tStudent/forgetLogInPwd?"; //密码
    private UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__pass_setting);
        mUserInfo = AppPreference.getInstance().getUserInfo();
        initView();
    }

        private void initView() {
            showMenu("重置密码");
            pass1 = (EditText) findViewById(R.id.password_1);
            pass2 = (EditText) findViewById(R.id.password_2);
            btn = (Button) findViewById(R.id.pass_btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  /*  if (pass1.getText().toString().length() !=6 || pass2.getText().toString().length()!=6){
                        toast("密码长度错误");
                        return;
                    }else {
                        if (!pass1.getText().toString().equals(pass2.getText().toString())){
                            toast("两次输入密码不一致");
                            return;
                        }else {
                            String pass =AppPreference.getInstance().getPassWord();
                            if (!pass.equals(pass1.getText().toString())){
                                toast("旧密码验证错误");
                                return;
                            }
                            //设置密码
                            HashMap<String,String> map =new HashMap<String, String>();
                            map.put("telPhone",mUserInfo.getTelPhone());
                            map.put("LogPwd",pass2.getText().toString());
                            sendPostRequest(AlterPass,map);
                            btn.setEnabled(false);
                        }
                    }*/
                  if (pass2.getText().toString().length()!=6){
                      toast("密码为6位数");
                      //设置密码
                      HashMap<String,String> map =new HashMap<String, String>();
                      map.put("telPhone",mUserInfo.getTelPhone());
                      map.put("LogPwd",pass2.getText().toString());
                      sendPostRequest(AlterPass,map);
                      btn.setEnabled(false);
                  }
                }
            });
        }

    @Override
    protected void handleErrorResponse(String url, VolleyError error) {
        super.handleErrorResponse(url, error);
        if(error instanceof TimeoutError){
            toast(R.string.timeout_error);
        }else{
            toast(R.string.operate_error);
        }
        btn.setEnabled(true);
    }

    @Override
    protected void handleResponse(String url, JSONObject json) {
        super.handleResponse(url, json);
        Gson gson =new Gson();
        BaseBo baseBo =gson.fromJson(json.toString(),BaseBo.class);
        if (baseBo.isSuccess()){
            toast(baseBo.getMsg());
            finish();
        }else {
            toast(baseBo.getMsg());
            btn.setEnabled(true);
        }
    }
}
