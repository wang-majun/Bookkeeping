package cn.edu.usst.bookkeeping.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.usst.bookkeeping.R;

public class RecordActivity extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase = null;
    private int selectId = -1;
    EditText edt_money, edt_state;
    TextView tv_date;
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
            map.put("date", date);
            map.put("type", type);
            map.put("money", String.valueOf(money));
            map.put("state", state);
            list.add(map);
        }

        //创建SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                list,
                R.layout.record_item_layout0,
                new String[]{"date", "type", "money", "state"},
                new int[]{R.id.list_date, R.id.list_type, R.id.list_money, R.id.list_state});
        final ListView listView = findViewById(R.id.recordlistview);
        //绑定适配器
        listView.setAdapter(simpleAdapter);
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
        setContentView(R.layout.activity_record);
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
                new DatePickerDialog(RecordActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        //选择类型
        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_type = findViewById(R.id.spin_type);
        spin_type.setAdapter(adapter);
        initClick();

        //新增按键
        Button btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_date.getText().toString().equals("") | selectType.equals("") | edt_money.getText().toString().equals("") | edt_state.getText().toString().equals("")) {
                    Toast.makeText(RecordActivity.this, "数据不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }

                String date = tv_date.getText().toString();
                String type = selectType;
                String money = edt_money.getText().toString();
                String state = edt_state.getText().toString();
                //定义添加数据的sql语句
                String sql = "insert into " + TABLE_NAME + "(" + COLUMN_DATE + "," + COLUMN_TYPE + "," + COLUMN_MONEY + "," + COLUMN_STATE + ") " +
                        "values('" + date + "','" + type + "','" + money + "','" + state + "')";
                //执行sql语句
                sqLiteDatabase.execSQL(sql);
                Toast.makeText(getApplicationContext(), "新增数据成功!", Toast.LENGTH_LONG).show();
                //刷新显示列表
                selectData();

                //消除数据
                tv_date.setText("");
                spin_type.setAdapter(adapter);
                edt_money.setText("");
                edt_state.setText("");
            }
        });
    }
}