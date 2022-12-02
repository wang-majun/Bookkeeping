package cn.edu.usst.bookkeeping.Activity;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.usst.bookkeeping.Bean.PieModel;
import cn.edu.usst.bookkeeping.R;
import cn.edu.usst.bookkeeping.Util.ColorRandom;
import cn.edu.usst.bookkeeping.View.HistogramView;
import cn.edu.usst.bookkeeping.View.PieChartView;

//收支记录页面业务逻辑
public class DealRecordActivity extends AppCompatActivity {
    //定义spinner中的数据
    private String[] year_data = {"", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010"};
    private String[] month_data = {"", "01", "02", "03", "04", "05","06","07","08","09","10","11","12"};
    private String[] chart_data = {"", "柱状图", "饼状图"};
    private float income, outcome;
    Spinner spin_year, spin_month, spin_chart;
    //数据库
    private String selectYear, selectMonth, selectChart;
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
        //收入
        if (!TextUtils.isEmpty(selectYear) && TextUtils.isEmpty(selectMonth)) {//如果没有查询时间，有查询类型
            sql = "select * from " + TABLE_NAME + " where date like'" + selectYear + "年%' and type='收入'";
        } else {
            sql ="select * from " + TABLE_NAME + " where date like'" + selectYear + "年" + selectMonth + "月%' and type='收入'";
        }
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            float money = cursor.getFloat(cursor.getColumnIndex(COLUMN_MONEY));
            income=income+money;
        }
        //支出
        if (!TextUtils.isEmpty(selectYear) && TextUtils.isEmpty(selectMonth)) {//如果没有查询时间，有查询类型
            sql = "select * from " + TABLE_NAME + " where date like'" + selectYear + "年%' and type='支出'";
        } else {
            sql ="select * from " + TABLE_NAME + " where date like'" + selectYear + "年" + selectMonth + "月%' and type='支出'";
        }
        cursor = sqLiteDatabase.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            float money = cursor.getFloat(cursor.getColumnIndex(COLUMN_MONEY));
            outcome=outcome+money;
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
        spin_chart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectChart = chart_data[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_record);

        try {
            //打开数据库，如果是第一次会创建该数据库，模式为MODE_PRIVATE
            sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            //执行创建表的sql语句，虽然每次都调用，但只有首次才创建表

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
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, chart_data);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_chart = findViewById(R.id.spin_chart);
        spin_chart.setAdapter(adapter3);
        initClick();

        Button btn_deal=findViewById(R.id.btn_deal);
        HistogramView histogramView = findViewById(R.id.histogram_view);
        PieChartView pieChartView = findViewById(R.id.pie_chart_view);
        histogramView.setVisibility(View.GONE);
        pieChartView.setVisibility(View.GONE);
        TextView id_income = findViewById(R.id.income);
        TextView id_outcome = findViewById(R.id.outcome);
        btn_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectYear.equals("")) {
                    Toast.makeText(getApplicationContext(), "年份不能为空!", Toast.LENGTH_LONG).show();
                    return;
                } else if (selectChart.equals("")) {
                    Toast.makeText(getApplicationContext(), "图表不能为空!", Toast.LENGTH_LONG).show();
                    return;
                } else if (selectChart.equals("柱状图")) {
                    TextView chart_name = findViewById(R.id.chart_name);
                    if (selectMonth.equals(""))
                        chart_name.setText(selectYear+"年 收支柱状图");
                    else
                        chart_name.setText(selectYear+"年"+selectMonth+"月 收支柱状图");
                    histogramView.setVisibility(View.GONE);
                    pieChartView.setVisibility(View.GONE);
                    id_income.setText("");
                    id_outcome.setText("");
                    income = 0;
                    outcome = 0;
                    selectSumMoney();
                    if (income == 0 && outcome == 0) {
                        Toast.makeText(getApplicationContext(), "所在时间段没有账单", Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<Float> sums = new ArrayList<>();
                    sums.add(income);
                    sums.add(outcome);
                    List<String> names = new ArrayList<>();
                    names.add("收入");
                    names.add("支出");
                    histogramView.updateThisData(sums, names);
                    histogramView.setVisibility(View.VISIBLE);
                } else if (selectChart.equals("饼状图")) {
                    TextView chart_name = findViewById(R.id.chart_name);
                    if (selectMonth.equals(""))
                        chart_name.setText(selectYear+"年 收支饼状图");
                    else
                        chart_name.setText(selectYear+"年"+selectMonth+"月 收支饼状图");
                    histogramView.setVisibility(View.GONE);
                    pieChartView.setVisibility(View.GONE);
                    income = 0;
                    outcome = 0;
                    selectSumMoney();
                    if (income == 0 && outcome == 0) {
                        Toast.makeText(getApplicationContext(), "所在时间段没有账单", Toast.LENGTH_LONG).show();
                        return;
                    }
                    id_income.setText("收入：" + income + "元");
                    id_outcome.setText("支出：" + outcome + "元");
                    List<PieModel> pieModelList = new ArrayList<>();
                    ColorRandom colorRandom = new ColorRandom(2);
                    pieModelList.add(new PieModel((int) colorRandom.getColors().get(0), income / (income + outcome)));
                    id_income.setBackgroundColor((int) colorRandom.getColors().get(0));
                    pieModelList.add(new PieModel((int) colorRandom.getColors().get(1), outcome / (income + outcome)));
                    id_outcome.setBackgroundColor((int) colorRandom.getColors().get(1));
                    pieChartView.setData(pieModelList);
                    pieChartView.startAnima();
                    pieChartView.setVisibility(View.VISIBLE);
                    id_income.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (pieModelList.get(0).selected) {
                                pieModelList.get(0).selected = false;
                            } else {
                                pieModelList.get(0).selected = true;
                            }
                            pieChartView.setData(pieModelList);
                            pieChartView.invalidate();
                        }
                    });
                    id_outcome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (pieModelList.get(1).selected) {
                                pieModelList.get(1).selected = false;
                            } else {
                                pieModelList.get(1).selected = true;
                            }
                            pieChartView.setData(pieModelList);
                            pieChartView.invalidate();
                        }
                    });
                }
            }
        });
    }
}