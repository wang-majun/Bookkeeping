package cn.edu.usst.bookkeeping.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.usst.bookkeeping.Activity.ChangePwdActivity;
import cn.edu.usst.bookkeeping.Activity.MainActivity;
import cn.edu.usst.bookkeeping.Bean.User;
import cn.edu.usst.bookkeeping.R;

//个人中心
public class UserCenterFragment extends Fragment {
    ArrayList<User> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_center, container, false);

        Intent intent = getActivity().getIntent();
        list = intent.getParcelableArrayListExtra("LoginUser");
        User user = list.get(0);
        final String userId = user.getUserId();
        TextView tv_welcome = view.findViewById(R.id.tv_welcome);
        tv_welcome.setText(userId);

        //修改密码
        TextView change_password = view.findViewById(R.id.change_password);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity().getApplicationContext(), ChangePwdActivity.class);
                intent.putParcelableArrayListExtra("LoginUser", list);
                startActivity(intent);
            }
        });

        //退出按键
        Button btn_exit = view.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("退出登录")
                        .setMessage("确定退出？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();

            }
        });
        return view;
    }
}
