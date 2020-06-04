package com.example.schedulemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schedulemanagement.bean.UserInfo;
import com.example.schedulemanagement.database.UserDBHelper;

public class Change_password extends AppCompatActivity {

    EditText new_password,confirm_password,et_ver_code, et_phone;
    Button send_code;
    String confirm_code;
    UserInfo change_password_user;
    Intent from_in;
    private UserDBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button back = findViewById(R.id.back);
        Button save = findViewById(R.id.save);
        send_code = findViewById(R.id.send_code);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        et_ver_code = findViewById(R.id.ver_code);
        et_phone = findViewById(R.id.phone_numbers);
        send_code.setOnClickListener(new MyOnClickListener());
        back.setOnClickListener(new MyOnClickListener());
        save.setOnClickListener(new MyOnClickListener());
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

        @SuppressLint("DefaultLocale")
        @Override
        public void onClick(View v) {
            String captcha = et_ver_code.getText().toString();
            String new_pw = new_password.getText().toString();
            String con_pw = confirm_password.getText().toString();
            String phone = et_phone.getText().toString();
            if(v.getId()==R.id.back) {
                from_in = getIntent();
                Bundle bundle = from_in.getExtras();
                assert bundle != null;
                String from = bundle.getString("from");
                if(from != null && from.equals("Setup")){
                    Intent intent = new Intent(Change_password.this, Setup.class);
                    startActivity(intent);
                }
                else if(from != null && from.equals("LoginActivity")){
                    Intent intent = new Intent(Change_password.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    showToast("发生意料之外的错误");
                }
            }
            if (v.getId()==R.id.save){
                if (TextUtils.isEmpty(captcha)||!confirm_code.equals(captcha)) {
                    showToast("请填写正确的验证码");
                    return;
                } else if (TextUtils.isEmpty(new_pw)) {
                    showToast("请先填写新的密码");
                    return;
                } else if (TextUtils.isEmpty(con_pw)) {
                    showToast("请先填写确认密码");
                    return;
                } else if(!new_pw.equals(con_pw)){
                    showToast("请保证两次填写的密码相同");
                    return;
                }
                change_password_user = new UserInfo();
                change_password_user.phone_number = phone;
                change_password_user.password = new_pw;
                if(mHelper.insert(change_password_user)!=-1){
                    showToast("保存成功");
                    Intent intent = new Intent(Change_password.this, Setup.class);
                    startActivity(intent);
                }
                else{
                    showToast("发生意料之外的错误");
                }

            }
            //发送验证码功能需要service,待完善
            if(v.getId()==R.id.send_code){
                // 生成六位随机数字的验证码
                confirm_code = String.format("%06d", (int) (Math.random() * 1000000 % 1000000));
                // 弹出提醒对话框，提示用户六位验证码数字
                AlertDialog.Builder builder = new AlertDialog.Builder(Change_password.this);
                builder.setTitle("请务必记住验证码！");
                builder.setMessage("手机号" + phone + "，本次验证码是" + et_ver_code + "，请输入验证码");
                builder.setPositiveButton("好的", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}
