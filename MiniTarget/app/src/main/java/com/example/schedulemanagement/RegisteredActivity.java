package com.example.schedulemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedulemanagement.bean.UserInfo;
import com.example.schedulemanagement.database.UserDBHelper;
import com.example.schedulemanagement.util.ViewUtil;

public class RegisteredActivity extends AppCompatActivity {

    EditText phone_numbers,password,confirm_password,ver_code,user_name;
    Button send_code,registered,back;
    private UserDBHelper mHelper; // 声明一个用户数据库帮助器对象
    private String confirm_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        ver_code = findViewById(R.id.ver_code);
        phone_numbers = findViewById(R.id.phone_numbers);
        user_name = findViewById(R.id.user_name);

        back = findViewById(R.id.back);
        send_code = findViewById(R.id.send_code);
        registered = findViewById(R.id.registered);
        back.setOnClickListener(new MyOnClickListener());
        send_code.setOnClickListener(new MyOnClickListener());
        registered.setOnClickListener(new MyOnClickListener());
        phone_numbers.addTextChangedListener(new HideTextWatcher(phone_numbers));
        ver_code.addTextChangedListener(new HideTextWatcher(ver_code));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获得用户数据库帮助器的一个实例
        mHelper = UserDBHelper.getInstance(this, 0);
        // 恢复页面，则打开数据库连接
        mHelper.openWriteLink();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停页面，则关闭数据库连接
        mHelper.closeLink();
    }

    class MyOnClickListener implements View.OnClickListener{

        @SuppressLint("DefaultLocale")
        @Override
        public void onClick(View v) {
            String captcha = ver_code.getText().toString();
            String con_pw = confirm_password.getText().toString();
            String phone = phone_numbers.getText().toString();
            String My_password = password.getText().toString();
            if(v.getId()==R.id.registered) {
                if(TextUtils.isEmpty(phone)||phone.length()<11) {
                    showToast("请输入正确的手机号码!");
                }
                else if (TextUtils.isEmpty(captcha)) {
                    showToast("请先填写验证码");
                }
                else if(!captcha.equals(confirm_code)){
                    showToast("请输入正确的验证码");
                }
                else if (TextUtils.isEmpty(My_password)) {
                    showToast("请先填写密码");
                }
                else if (TextUtils.isEmpty(con_pw)) {
                    showToast("请先填写确认密码");
                }
                else if(!My_password.equals(con_pw)){
                    showToast("请保证两次填写的密码相同");
                }
                else {
                    UserInfo new_user = new UserInfo();
                    new_user.phone_number = phone_numbers.getText().toString();
                    new_user.password = password.getText().toString();
                    new_user.username = user_name.getText().toString();
                    if(mHelper.insert(new_user)!=-1){
                        showToast("注册完成");
                        Intent intent = new Intent(RegisteredActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        showToast("出现意料之外的错误");
                    }
                }
            }
            if(v.getId()==R.id.back){
                Intent intent = new Intent(RegisteredActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            if(v.getId()==R.id.send_code){
                // 生成六位随机数字的验证码
                confirm_code = String.format("%06d", (int) (Math.random() * 1000000 % 1000000));
                // 弹出提醒对话框，提示用户六位验证码数字
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisteredActivity.this);
                builder.setTitle("请务必记住验证码！");
                builder.setMessage("手机号" + phone + "，本次验证码是" + confirm_code + "，请输入验证码");
                builder.setPositiveButton("好的", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void showToast(String desc) {
        Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
    }

    // 定义编辑框的文本变化监听器
    private class HideTextWatcher implements TextWatcher {
        private EditText mView;
        private int mMaxLength;
        private CharSequence mStr;

        public HideTextWatcher(EditText v) {
            super();
            mView = v;
            mMaxLength = ViewUtil.getMaxLength(v);
        }

        // 在编辑框的输入文本变化前触发
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // 在编辑框的输入文本变化时触发
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mStr = s;
        }

        // 在编辑框的输入文本变化后触发
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mStr))
                return;
            // 手机号码输入达到11位，或者验证码输入达到6位，都关闭输入法软键盘
            if ((mStr.length() == 11 && mMaxLength == 11) ||
                    (mStr.length() == 6 && mMaxLength == 6)) {
                ViewUtil.hideOneInputMethod(RegisteredActivity.this, mView);
            }
        }
    }
}