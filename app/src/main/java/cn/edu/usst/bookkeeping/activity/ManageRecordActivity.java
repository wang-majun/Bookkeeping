package cn.edu.usst.bookkeeping.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.usst.bookkeeping.R;

public class ManageRecordActivity extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase = null;
    private int selectId = -1;
    EditText edt_money, edt_state;
    TextView tv_date, tv_test;
    private String[] type_data = {"", "收入", "支出"};
    Spinner spin_type;
    private String selectType = "";
    private int mYear, mMonth, mDay;

    private static final String DATABASE_NAME = "Test.db";
    private static final String TABLE_NAME = "record";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_MONEY = "money";
    private static final String COLUMN_STATE = "state";

    //创建表
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME
            + "(" + COLUMN_ID + " integer primary key autoincrement," + COLUMN_DATE + " text," + COLUMN_TYPE
            + " text," + COLUMN_MONEY + " float," + COLUMN_STATE + " text)";

    //自定义的查询方法
    private void selectData() {
        //遍历整个表
        String sql = "select * from " + TABLE_NAME ;
        //把查询数据封装到Cursor
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //用while循环遍历Cursor，再把它分别放到map中，最后统一存入list中，便于调用
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
            float money = cursor.getFloat(cursor.getColumnIndex(COLUMN_MONEY));
            String state = cursor.getString(cursor.getColumnIndex(COLUMN_STATE));

            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(id));
            map.put("date", date);
            map.put("type", type);
            map.put("money", String.valueOf(money));
            map.put("state", state);
            list.add(map);
        }

        //创建SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                list,
                R.layout.record_item_layout,
                new String[]{"id", "date", "type", "money", "state"},
                new int[]{R.id.list_id, R.id.list_date, R.id.list_type, R.id.list_money, R.id.list_state});
        final ListView listView = findViewById(R.id.recordlistview);
        //绑定适配器
        listView.setAdapter(simpleAdapter);
        //设置ListView单击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView tempList = (ListView) parent;


                View mView = tempList.getChildAt(position);
                TextView list_id = mView.findViewById(R.id.list_id);
                TextView list_date = mView.findViewById(R.id.list_date);
                TextView list_type = mView.findViewById(R.id.list_type);
                TextView list_money = mView.findViewById(R.id.list_money);
                TextView list_state = mView.findViewById(R.id.list_state);

                String rid = list_id.getText().toString();
                String date = list_date.getText().toString();
                String type = list_type.getText().toString();
                String money = list_money.getText().toString();
                String state = list_state.getText().toString();

                tv_test.setText(rid);
                tv_date.setText(date);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageRecordActivity.this, android.R.layout.simple_spinner_item, type_data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_type.setAdapter(adapter);
                if (type.equals("收入"))
                    spin_type.setSelection(1);
                else
                    spin_type.setSelection(2);
                edt_money.setText(money);
                edt_state.setText(state);
                selectId = Integer.parseInt(rid);
            }
        });
    }

    private void initClick() {
        //类别事件
        spin_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectType = type_data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //日期选择器对话框监听
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("年").append("0").
                            append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                } else {
                    days = new StringBuffer().append(mYear).append("年").append("0").
                            append(mMonth + 1).append("月").append(mDay).append("日").toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("年").
                            append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                } else {
                    days = new StringBuffer().append(mYear).append("年").
                            append(mMonth + 1).append("月").append(mDay).append("日").toString();
                }

            }
            tv_date.setText(days);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_record);
        try {
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            sqLiteDatabase.execSQL(CREATE_TABLE);
            //执行查询
            selectData();
        } catch (SQLException e) {
            Toast.makeText(this, "数据库异常!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        tv_date = findViewById(R.id.tv_date);
        tv_test = findViewById(R.id.tv_test);
        edt_money = findViewById(R.id.edt_money);
        edt_state = findViewById(R.id.edt_state);

        //选择日期按键
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        Button btn_date = findViewById(R.id.btn_date);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ManageRecordActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        //选择类型
        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_type = findViewById(R.id.spin_type);
        spin_type.setAdapter(adapter);
        initClick();

        //修改按键
        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //无选择提示
                if (selectId == -1) {
                    Toast.makeText(getApplicationContext(), "请选择要修改的行!", Toast.LENGTH_LONG).show();
                    return;
                }
                //判断是否有空数据
                if (tv_date.getText().toString().equals("") | selectType.equals("") | edt_money.getText().toString().equals("") | edt_state.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "数据不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }

                String date = tv_date.getText().toString();
                String type = selectType;
                String money = edt_money.getText().toString();
                String state = edt_state.getText().toString();
                //定义修改数据的sql语句
                String sql = "update " + TABLE_NAME + " set " + COLUMN_DATE + "='" + date + "',type='" + type + "',money='" + money + "',state='" + state + "' where id=" + selectId;
                //执行sql语句
                sqLiteDatabase.execSQL(sql);
                Toast.makeText(getApplicationContext(), "修改数据成功!", Toast.LENGTH_LONG).show();
                //刷新显示列表
                selectData();
                selectId = -1;
                //消除数据
                tv_test.setText("");
                tv_date.setText("");
                spin_type.setAdapter(adapter);
                edt_money.setText("");
                edt_state.setText("");
            }
        });
        //删除按键
        Button btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //无选择提示
                if (selectId == -1) {
                    Toast.makeText(ManageRecordActivity.this, "请选择要删除的行!", Toast.LENGTH_LONG).show();
                    return;
                }

                //定义删除对话框
                AlertDialog dialog = new AlertDialog.Builder(ManageRecordActivity.this).setTitle("删除操作")
                        .setMessage("确定删除？此操作不可逆，请慎重选择！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //定义删除的sql语句
                                String sql = "delete from " + TABLE_NAME + " where id=" + selectId;
                                //执行sql语句
                                sqLiteDatabase.execSQL(sql);
                                //刷新显示列表
                                Toast.makeText(getApplicationContext(), "删除数据成功!", Toast.LENGTH_LONG).show();
                                selectData();
                                selectId = -1;
                                //清除数据
                                tv_test.setText("");
                                tv_date.setText("");
                                spin_type.setAdapter(adapter);
                                edt_money.setText("");
                                edt_state.setText("");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();
            }
        });
    }
}