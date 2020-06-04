package com.example.schedulemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedulemanagement.bean.UserInfo;
import com.example.schedulemanagement.database.UserDBHelper;

public class LoginActivity extends AppCompatActivity {

    EditText phone_numbers,password;
    Button login,registered,forget_password;
    private UserDBHelper mHelper;
    private SharedPreferences mShared; // 声明一个共享参数对象
    UserInfo login_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone_numbers = findViewById(R.id.phone_numbers);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        registered = findViewById(R.id.registered);
        forget_password = findViewById(R.id.forget_password);
        login.setOnClickListener(new MyOnClickListener());
        registered.setOnClickListener(new MyOnClickListener());
        forget_password.setOnClickListener(new MyOnClickListener());

        // 从share.xml中获取共享参数对象
        mShared = getSharedPreferences("share", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获得用户数据库帮助器的一个实例
        mHelper = UserDBHelper.getInstance(this, 2);
        // 恢复页面，则打开数据库连接
        mHelper.openWriteLink();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停页面，则关闭数据库连接
        mHelper.closeLink();
    }

    private void showToast(String desc) {
        Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
    }
    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String phone = phone_numbers.getText().toString();
            String My_password = password.getText().toString();
            login_user = mHelper.queryByPhone(phone);
            if(v.getId()==R.id.login) {
                if(TextUtils.isEmpty(phone)||phone.length()<11||login_user == null) {
                    showToast("请输入正确的手机号码!");
                }
                else if(TextUtils.isEmpty(My_password)){
                    showToast("请输入密码!");
                }
                else if(!login_user.password.equals(My_password)){
                    showToast("请输入正确的密码!");
                }
                else {
                    SharedPreferences.Editor editor = mShared.edit(); // 获得编辑器的对象
                    editor.putString("login_user_name",login_user.username);
                    editor.putString("login_user_phone",login_user.phone_number);
                    editor.apply();
                    showToast("登录成功");
                    Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
                    startActivity(intent);
                }
            }
            if(v.getId()==R.id.forget_password) {
                Intent intent = new Intent(LoginActivity.this, Change_password.class);
                Bundle bundle = new Bundle();
                bundle.putString("from","LoginActivity");
                intent.putExtras(bundle);
                startActivity(intent);
            }
            if(v.getId()==R.id.registered) {
                Intent intent = new Intent(LoginActivity.this, RegisteredActivity.class);
                startActivity(intent);
            }
        }
    }
}