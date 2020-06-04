package com.example.schedulemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.schedulemanagement.bean.UserInfo;
import com.example.schedulemanagement.database.UserDBHelper;

public class Setup extends AppCompatActivity {
    private SharedPreferences mShared; // 声明一个共享参数对象
    private UserDBHelper mHelper;
    private String user_name;
    private String user_phone;
    EditText et_username;
    UserInfo has_login_user;
    int hour_num = 0;
    int minute_num = 0;
    int alarm_num = 0;
    boolean has_saved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        //从布局文件中获取对象
        Button plus1 = findViewById(R.id.plus1);
        Button back = findViewById(R.id.back);
        Button save = findViewById(R.id.save);
        Button cp = findViewById(R.id.change_password);
        Button exit = findViewById(R.id.exit);
        et_username = findViewById(R.id.user_name);
        //设置监听器
        plus1.setOnClickListener(new MyOnClickListener());
        back.setOnClickListener(new MyOnClickListener());
        save.setOnClickListener(new MyOnClickListener());
        cp.setOnClickListener(new MyOnClickListener());
        exit.setOnClickListener(new MyOnClickListener());
        // 从share.xml中获取共享参数对象
        mShared = getSharedPreferences("share", MODE_PRIVATE);
        // 获取共享参数中保存的数据
        hour_num = mShared.getInt("hour_num", 0);
        minute_num = mShared.getInt("minute_num", 0);
        alarm_num = mShared.getInt("alarm_num",0);
        user_name = mShared.getString("login_user_name","");
        user_phone = mShared.getString("login_user_phone","");
        et_username.setText(user_name);
        initSpinner();
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

    // 定义一个点击监听器，它实现了接口View.OnClickListener
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) { // 点击事件的处理方法
            //当点击返回按钮时
            if (v.getId() == R.id.back) {
                if (!has_saved) {
                    // 创建提醒对话框的建造器
                    AlertDialog.Builder builder = new AlertDialog.Builder(Setup.this);
                    // 给建造器设置对话框的标题文本
                    builder.setTitle("尊敬的用户");
                    // 给建造器设置对话框的信息文本
                    builder.setMessage("不保存所做的更改，直接退出吗？");
                    // 给建造器设置对话框的肯定按钮文本及其点击监听器
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Setup.this, ScheduleActivity.class);
                            startActivity(intent);
                        }
                    });
                    // 给建造器设置对话框的否定按钮文本及其点击监听器
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    // 根据建造器完成提醒对话框对象的构建
                    AlertDialog alert = builder.create();
                    // 在界面上显示提醒对话框
                    alert.show();
                }
                else{
                    Intent intent = new Intent(Setup.this, ScheduleActivity.class);
                    startActivity(intent);
                }
            }

            //当点击保存按钮时
            if(v.getId() == R.id.save){
                SharedPreferences.Editor editor = mShared.edit(); // 获得编辑器的对象
                editor.putInt("hour_num", hour_num); // 添加一个名叫hour_num的整型参数
                editor.putInt("minute_num", minute_num); // 添加一个名叫minute_num的整型参数
                editor.putInt("alarm_num", alarm_num); // 添加一个名叫alarm_num的整型参数
                editor.apply(); // 提交编辑器中的修改
                user_name = et_username.getText().toString();
                has_login_user = mHelper.queryByPhone(user_phone);
                has_login_user.username = user_name;
                if(mHelper.update(has_login_user)>=0) {
                    showToast("保存成功！");
                    has_saved = true;
                }
                else{
                    showToast("保存失败，出现意料之外的错误");
                }
            }
            //当点击退出账号按钮时
            if(v.getId() == R.id.exit){
                // 创建提醒对话框的建造器
                AlertDialog.Builder builder = new AlertDialog.Builder(Setup.this);
                // 给建造器设置对话框的标题文本
                builder.setTitle("尊敬的用户");
                // 给建造器设置对话框的信息文本
                builder.setMessage("确定要退出该账号吗？");
                // 给建造器设置对话框的肯定按钮文本及其点击监听器
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Setup.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                // 给建造器设置对话框的否定按钮文本及其点击监听器
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // 根据建造器完成提醒对话框对象的构建
                AlertDialog alert = builder.create();
                // 在界面上显示提醒对话框
                alert.show();
            }
            //点击修改密码时
            if(v.getId() == R.id.change_password)
            {
                Intent intent = new Intent(Setup.this, Change_password.class);
                Bundle bundle = new Bundle();
                bundle.putString("from","Setup");
                intent.putExtras(bundle);
                startActivity(intent);
            }
            /*
            //点击“+”时
            if(v.getId() == R.id.plus1)
            {
                功能尚未完成
            }
            */
        }
    }

    // 初始化下拉框
    private void initSpinner() {
        // 声明一个下拉列表的数组适配器
        ArrayAdapter<String> starAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, starArray);
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, minuteArray);
        ArrayAdapter<String> alarmAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, alarmArray);
        // 设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        minuteAdapter.setDropDownViewResource(R.layout.item_dropdown);
        alarmAdapter.setDropDownViewResource(R.layout.item_dropdown);
        // 从布局文件中获取下拉框
        Spinner sp = findViewById(R.id.sp_day);
        Spinner min = findViewById(R.id.sp_minute);
        Spinner alarm = findViewById(R.id.alarm);
        // 设置下拉框的标题
        sp.setPrompt("提前");
        // 设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        min.setAdapter(minuteAdapter);
        alarm.setAdapter(alarmAdapter);
        // 设置下拉框默认显示项
        sp.setSelection(hour_num);
        min.setSelection(minute_num);
        alarm.setSelection(alarm_num);
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener(1));
        min.setOnItemSelectedListener(new MySelectedListener(2));
        alarm.setOnItemSelectedListener(new MySelectedListener(3));
    }

    // 定义下拉列表需要显示的文本数组
    private String[] starArray = {"0", "1", "2", "3","4","5","6","7","8","9","10","11","12","13","14","15","16","17",
            "18","19","20","21","22","23","24"};
    private String[] minuteArray = {"0","5","10","15","20","25","30","35","40","45","50","55","59"};
    private String[] alarmArray = {"1","2","3","4"};
    // 定义一个选择监听器，它实现了接口OnItemSelectedListener

    private void showToast(String desc) {
        Toast.makeText(this, desc, Toast.LENGTH_LONG).show();
    }

    class MySelectedListener implements AdapterView.OnItemSelectedListener {
        int type;
        MySelectedListener(int i){
            type = i;
        }
        // 选择事件的处理方法，其中arg2代表选择项的序号
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            switch (type){
                case 1:hour_num = arg2;break;
                case 2:minute_num = arg2;break;
                case 3:alarm_num = arg2;break;
            }
        }

        // 未选择时的处理方法，通常无需关注
        public void onNothingSelected(AdapterView<?> arg0) {}
    }
}
