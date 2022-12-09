package cn.edu.usst.bookkeeping.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.usst.bookkeeping.R;

//收支记录页面业务逻辑
public class SearchRecordActivity extends AppCompatActivity {
    //定义spinner中的数据
    private String[] year_data = {"", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010"};
    private String[] month_data = {"", "01", "02", "03", "04", "05","06","07","08","09","10","11","12"};
    private String[] type_data = {"", "收入", "支出"};
    Spinner spin_year, spin_month, spin_type;
    ListView listView;
    TextView tv_show;
    float sum=0;
    //数据库
    private String selectYear, selectMonth, selectType;
    private static final String DATABASE_NAME = "Test.db";
    private static final String TABLE_NAME = "record";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_MONEY = "money";
    private static final String COLUMN_STATE = "state";
    private SQLiteDatabase sqLiteDatabase = null;

    private void selectSumMoney() {
        //自定义查询的sql语句
        String sql;
        //如果查询年份和查询类型都为空，则查询整个表
        if (TextUtils.isEmpty(selectYear) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME;
            //如果有查询年份，没有查询月份，没有查询类型，查询指定内容
        } else if (!TextUtils.isEmpty(selectYear) && TextUtils.isEmpty(selectMonth) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where date like'" + selectYear + "年%'";
            //如果有查询年份和月份，没有查询类型，查询指定内容
        } else if (!TextUtils.isEmpty(selectYear) && !TextUtils.isEmpty(selectMonth) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where date like'" + selectYear + "年" + selectMonth + "月%'";
            //如果没有查询时间，有查询类型，查询指定内容
        } else if (TextUtils.isEmpty(selectYear) && !TextUtils.isEmpty(selectType)) {//如果没有查询时间，有查询类型
            sql = "select * from " + TABLE_NAME + " where type='" + selectType+"'";
        } else {//否则，查询条件都不为空，查询指定内容
            sql ="select * from " + TABLE_NAME + " where date like'" + selectYear + "年" + selectMonth + "月%' and type='" + selectType+"'";
        }
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        while (cursor.moveToNext()) {

            float money = cursor.getFloat(cursor.getColumnIndex(COLUMN_MONEY));
            sum=sum+money;

            //list.add(map);
        }
        String money2=String.valueOf(sum);
        tv_show.setText(money2);
        sum=0;
    }
    //选择数据
    private void selectData() {
        //自定义查询的sql语句
        String sql;
        //如果查询年份和查询类型都为空，则查询整个表
        if (TextUtils.isEmpty(selectYear) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME;
            //如果有查询年份，没有查询月份，没有查询类型，查询指定内容
        } else if (!TextUtils.isEmpty(selectYear) && TextUtils.isEmpty(selectMonth) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where date like'" + selectYear + "年%'";
            //如果有查询年份和月份，没有查询类型，查询指定内容
        } else if (!TextUtils.isEmpty(selectYear) && !TextUtils.isEmpty(selectMonth) && TextUtils.isEmpty(selectType)) {
            sql = "select * from " + TABLE_NAME + " where date like'" + selectYear + "年" + selectMonth + "月%'";
            //如果没有查询时间，有查询类型，查询指定内容
        } else if (TextUtils.isEmpty(selectYear) && !TextUtils.isEmpty(selectType)) {//如果没有查询时间，有查询类型
            sql = "select * from " + TABLE_NAME + " where type='" + selectType+"'";
        } else {//否则，查询条件都不为空，查询指定内容
            sql ="select * from " + TABLE_NAME + " where date like'" + selectYear + "年" + selectMonth + "月%' and type='" + selectType+"'";
        }
        //将查询到的数据封装到Cursor
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (cursor.getCount() == 0) {
            //查无数据则显示空列表
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(null);
            Toast.makeText(getApplicationContext(), "无数据", Toast.LENGTH_SHORT).show();
        } else {
            //查有数据则显示列表
            listView.setVisibility(View.VISIBLE);
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
            listView.setAdapter(simpleAdapter);

        }
    }

    //时间和类别spinner点击事件
    private void initClick() {
        //年月事件
        spin_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectYear = year_data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spin_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMonth = month_data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectData();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);
        tv_show=findViewById(R.id.tv_show);
        try {
            //打开数据库，如果是第一次会创建该数据库，模式为MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            //执行创建表的sql语句，虽然每次都调用，但只有首次才创建表

            //执行查询
            listView = findViewById(R.id.searchlistview);//绑定列表
            selectData();
        } catch (SQLException e) {
            Toast.makeText(this, "数据库异常!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter1 = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, year_data);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_year = findViewById(R.id.spin_year);
        spin_year.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, month_data);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_month = findViewById(R.id.spin_month);
        spin_month.setAdapter(adapter2);
        ArrayAdapter<String> adapter3 = new
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_data);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_type = findViewById(R.id.spin_type);
        spin_type.setAdapter(adapter3);
        initClick();
        Button btn_calc=findViewById(R.id.btn_calc);
        btn_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSumMoney();
            }
        });
    }
}