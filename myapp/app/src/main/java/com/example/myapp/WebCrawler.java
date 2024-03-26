package com.example.myapp;

import android.util.Log;

import android.widget.ListView;

import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Request;

public class WebCrawler {
    // 定义回调接口
    public interface WebCrawlerCallback {
        void onDataFetched(List<String> names);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 修改 startCrawling 方法签名，添加 Callback 参数
    public void startCrawling(String url, String payloadKey, WebCrawlerCallback webCrawlerCallback) {
        executor.submit(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String payload = getPayload(payloadKey);
                MediaType JSON = MediaType.get("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(payload, JSON);

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Accept", "application/json, text/plain, */*")
                        .addHeader("Content-Type", "application/json;charset=UTF-8")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("WebCrawler", "Network request failed", e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseData = response.body().string();
                            List<String> names = new ArrayList<>();
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                JSONObject dataObject = jsonObject.getJSONObject("data");
                                JSONArray results = dataObject.getJSONArray("result");
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject result = results.getJSONObject(i);
                                    JSONObject data = result.getJSONObject("data");
                                    String name = data.getString("name");
                                    names.add(name);
                                }
                                // 使用回调接口返回数据
                                webCrawlerCallback.onDataFetched(names);
                            } catch (Exception e) {
                                Log.e("WebCrawler", "Error parsing JSON", e);
                            }
                        } else {
                            Log.e("WebCrawler", "HTTP request not successful");
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("WebCrawler", "Error during web crawling", e);
            }
        });
    }
    public String getPayload(String key){
        return String.format("{\"option\":{\"number\":10,\"match\":{},\"sort\":{\"top\":-1,\"stime\":-1,\"mtime\":-1,\"ctime\":-1,\"dtime\":-1},\"page\":1,\"between\":{\"max\":\"dtime\",\"min\":\"stime\",\"value\":\"now\"},\"query\":[{\"match\":{\"classId\":\"%s\",\"released\":true,\"status\":\"passed\",\"show\":true,\"hide\":{\"$ne\":\"%s\"}},\"between\":{\"max\":\"dtime\",\"min\":\"stime\",\"value\":\"now\"}}]},\"vector\":\"private\",\"static\":false}", key, key);
    }

}
