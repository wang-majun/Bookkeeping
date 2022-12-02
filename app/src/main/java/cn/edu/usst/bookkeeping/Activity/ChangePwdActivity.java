package cn.edu.usst.bookkeeping.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cn.edu.usst.bookkeeping.Bean.User;
import cn.edu.usst.bookkeeping.DAO.DBHelper;
import cn.edu.usst.bookkeeping.R;

public class ChangePwdActivity extends AppCompatActivity {
    public static final String DB_NAME = "Test.db";
    public static final String TABLE_NAME = "userinfo";
    public static final String COLUMN_USERID = "uid";
    public static final String COLUMN_USERPWD = "upwd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        final EditText edt_old_pwd =findViewById(R.id.edt_old_pwd);
        final EditText edt_new_pwd =findViewById(R.id.edt_new_pwd);

        Intent intent = getIntent();
        ArrayList<User> list = intent.getParcelableArrayListExtra("LoginUser");
        User user = list.get(0);

        //更改按键
        Button btn_change_pwd=findViewById(R.id.btn_change_pwd);
        btn_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.getUserPwd().equals(edt_old_pwd.getText().toString())) {
                    AlertDialog dialog = new AlertDialog.Builder(ChangePwdActivity.this).setTitle("原密码错误")
                            .setMessage("确定继续修改密码？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(getApplicationContext(), ChangePwdActivity.class);
                                    intent.putParcelableArrayListExtra("LoginUser", list);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(getApplicationContext(), MainActivity2.class);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                } else {
                    user.setUserPwd(edt_new_pwd.getText().toString());
                    DBHelper dbUserHelper = new DBHelper(getApplicationContext());
                    dbUserHelper.changePwd(user, edt_new_pwd.getText().toString());
                    AlertDialog dialog = new AlertDialog.Builder(ChangePwdActivity.this).setTitle("密码修改成功")
                            .setMessage("请重新登录！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
            }
        });
    }
}
