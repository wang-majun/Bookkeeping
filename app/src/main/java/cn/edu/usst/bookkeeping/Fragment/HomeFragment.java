package cn.edu.usst.bookkeeping.Fragment;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.edu.usst.bookkeeping.activity.DealRecordActivity;
import cn.edu.usst.bookkeeping.activity.ManageRecordActivity;
import cn.edu.usst.bookkeeping.activity.RecordActivity;
import cn.edu.usst.bookkeeping.activity.SearchRecordActivity;
import cn.edu.usst.bookkeeping.bean.User;
import cn.edu.usst.bookkeeping.R;

//个人中心
public class HomeFragment extends Fragment {
    ArrayList<User> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //记录收支
        ImageView btn_record = view.findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity().getApplicationContext(), RecordActivity.class);
                startActivity(intent1);
            }
        });
        //管理收支
        ImageView btn_manage_record = view.findViewById(R.id.btn_manage_record);
        btn_manage_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity().getApplicationContext(), ManageRecordActivity.class);
                startActivity(intent2);
            }
        });
        //收支查询
        ImageView btn_search_record = view.findViewById(R.id.btn_search_record);
        btn_search_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity().getApplicationContext(), SearchRecordActivity.class);
                startActivity(intent3);
            }
        });
        //收支统计
        ImageView btn_deal_record = view.findViewById(R.id.btn_deal_record);
        btn_deal_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4=new Intent(getActivity().getApplicationContext(), DealRecordActivity.class);
                startActivity(intent4);
            }
        });

        return view;
    }
}
