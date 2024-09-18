package com.example.utellme.common;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

public class OllamaUtil {

    private static final OkHttpClient client = new OkHttpClient();

    // 通过 HTTP 调用 Ollama 本地服务的辅助方法
    public String callOllamaService(String model, String prompt) throws IOException {
        String jsonBody = String.format("{\"model\": \"%s\", \"prompt\": \"%s\"}", model, prompt);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonBody
        );

        // 构建 HTTP 请求，目标为本地服务
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/generate")  // Ollama 本地服务接口
                .post(body)
                .build();

        // 执行 HTTP 请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 返回服务响应的内容
            String rawResponse = response.body().string();
            // 解析响应内容
            return parseModelResponses(rawResponse);
        }
    }

    // 解析模型的 JSON 响应，提取 response 字段并组合
    private static String parseModelResponses(String rawResponse) {
        StringBuilder fullResponse = new StringBuilder();

        // 使用 Scanner 逐行读取响应数据
        Scanner scanner = new Scanner(rawResponse);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // 提取每行的 response 字段
            JSONObject bean = JSONUtil.toBean(line, JSONObject.class);
            String responsePart = bean.get("response").toString();
            fullResponse.append(responsePart);

            // 如果 done 为 true，则表示响应结束
            if (bean.getBool("done")) {
                break;
            }
        }

        return fullResponse.toString();
    }

}
