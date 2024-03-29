package net.mamestagram.Module;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static net.mamestagram.Main.*;

public class Translate {

    public static String getTranslateSentence(String text, String targetLang) throws InterruptedException, IOException {

        //定数のセット
        final String url = "https://api-free.deepl.com/v2/translate";

        // UTF-8にエンコード
        String encodedText = URLEncoder.encode(text, "UTF-8");

        // リクエストボディの作成
        String requestBody = "text=" + encodedText + "&target_lang=" + targetLang;

        // Httpクライアントの作成
        HttpClient httpClient = HttpClient.newHttpClient();

        // DeeplのAPIを参考にリクエストを追加
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "DeepL-Auth-Key " + deeplAPIKEY)
                .header("User-Agent", "YourApp/1.2.3")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // リクエストを送信してレスポンスを取得
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // 翻訳語のデータをorg.jsonのAPIよりJsonに変換
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray translations = jsonResponse.getJSONArray("translations");
        JSONObject translation = translations.getJSONObject(0);

        return translation.getString("text");
    }
}
