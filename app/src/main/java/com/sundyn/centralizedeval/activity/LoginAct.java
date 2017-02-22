package com.sundyn.centralizedeval.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.LoginBean;
import com.sundyn.centralizedeval.views.CustomProgressDialog;

/**
 * Created by Administrator on 2017/2/21.
 */

public class LoginAct extends Activity implements View.OnClickListener {


    private Context ctx;
    private EditText name, pwd;
    private CustomProgressDialog progressDialog;
    private Gson gson;
    private LoginBean loginBean;
    private Button btnLogin;
    private String userName,userPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ctx = this;
        name = (EditText) findViewById(R.id.et_name);
        pwd = (EditText) findViewById(R.id.et_password);
        gson = new Gson();
        btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideKeyborad();
        progressDialog = new CustomProgressDialog(ctx, "登陆中......");
        progressDialog.show();
        // http://192.168.100.181:8080/estime/json_wechat/login?username=23&userpwd=123456
        String URL_LOGIN = "http://192.168.100.30:8080/estime/json_wechat/login?";
        userName = name.getText().toString().trim();
        userPwd = pwd.getText().toString().trim();
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        if (userName.length() == 0 || userPwd.length() == 0) {
            Toast.makeText(ctx, "用户名和密码不能为空", Toast.LENGTH_LONG).show();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            return;
        }
        URL_LOGIN = URL_LOGIN + "username=" + userName + "&userpwd=" + userPwd;
        StringRequest stringRequest = new StringRequest(URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        String[] split = response.split(":");
                        if (split[1].contains("success")) {
                            loginBean = gson.fromJson(response, LoginBean.class);
                            Intent intent = new Intent(LoginAct.this,MainAct.class);
                            intent.putExtra("loginBean",loginBean);
                            startActivity(intent);
                            // 登录状态
//							SharedPreferences sp = ctx.getSharedPreferences("logstate",MODE_PRIVATE);
//							sp.edit().putBoolean("login",true).commit();
//							sp.edit().putString("username",userName).commit();
//							sp.edit().putString("userpwd",userPwd).commit();
                            overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                            finish();
                        } else {
                            Toast.makeText(ctx, "登录失败，用户名或密码错误",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "网络异常，请稍后重试", Toast.LENGTH_LONG).show();
                Log.i("VolleyError","VolleyErrorVolleyErrorVolleyError");
                return;
            }
        });
        // 设置超时时间10s,连接次数2次
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void hideKeyborad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(getWindow().getDecorView()
                    .getWindowToken(), 0);

    }

    private long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(ctx, "再按一次退出程序", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
