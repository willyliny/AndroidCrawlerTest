package com.example.myapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.os.Bundle;
import java.util.*;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private String competition = "Bi3v31Z2366";
    private String admission = "8ra11FE6317";

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> nameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview_names);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(adapter);

        Button buttonCompetition = findViewById(R.id.button_competition);
        Button buttonAdmission = findViewById(R.id.button_admission);

        // btn Competition
        buttonCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAndDisplayData(competition);
            }
        });
        // btn Admission
        buttonAdmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAndDisplayData(admission);
            }
        });
    }

    private void fetchAndDisplayData(String payloadKey) {
        // 创建 WebCrawler 实例
        WebCrawler crawler = new WebCrawler();
        // 你的 URL
        String url = "https://web.wghs.tp.edu.tw/nss/site/main/storage/5abf2d62aa93092cee58ceb4/KIAm1Ii1422/find";

        // 使用 crawler 获取数据
        crawler.startCrawling(url, payloadKey, new WebCrawler.WebCrawlerCallback() {
            @Override
            public void onDataFetched(List<String> names) {
                // 这将在数据获取完成后被调用
                // 确保操作在 UI 线程上执行
                runOnUiThread(() -> {
                    // 清除旧数据
                    nameList.clear();
                    // 添加新数据
                    nameList.addAll(names);
                    // 通知适配器数据已经改变，更新 ListView
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }
}