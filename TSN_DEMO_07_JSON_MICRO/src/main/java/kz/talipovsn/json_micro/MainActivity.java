package kz.talipovsn.json_micro;

import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // Компонент для отображения данных

    String url = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=52.28&longitude=76.97&hourly=pm10"; // Адрес получения JSON - данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textView = findViewById(R.id.textView);

        onClick(null); // Нажмем на кнопку "Обновить"
    }

    // Кнопка "Обновить"
    public void onClick(View view) {
        textView.setText(R.string.not_data);
        String json = getHTMLData(url);
        if (json != null) {
            JSONObject _root = null;
            try {
                _root = new JSONObject(json);
                JSONObject _hourly = _root.getJSONObject("hourly");
                JSONArray _time = _hourly.getJSONArray("time");
                JSONArray _pollution = _hourly.getJSONArray("pm10");
                String firstDay = "";
                String secondDay = "";
                String thirdDay = "";
                String fourthDay = "";
                String fifthDay = "";
                int polution1 = 0;
                int polution2 = 0;
                int polution3 = 0;
                int polution4 = 0;
                int polution5 = 0;
                textView.setText("");
                for (int i = 0; i < _time.length();i++){
                    if ((i >= 0) && (i < 24)){
                        firstDay = _time.getString(0).substring(0,10);
                        polution1 += _pollution.getInt(i);
                    }
                    if ((i >= 24) && (i < 48)){
                        secondDay = _time.getString(24).substring(0,10);
                        polution2 += _pollution.getInt(i);
                    }
                    if ((i >= 48) && (i < 72)){
                        thirdDay = _time.getString(48).substring(0,10);
                        polution3 += _pollution.getInt(i);
                    }
                    if ((i >= 72) && (i < 96)){
                        fourthDay = _time.getString(72).substring(0,10);
                        polution4 += _pollution.getInt(i);
                    }
                    if (((i >= 96) && (i < _time.length()))){
                        fifthDay = _time.getString(96).substring(0,10);
                    }
                }
                textView.append("Дата: " + firstDay);
                textView.append("\n");
                textView.append("Качество воздуха: " + (polution1/24));
                textView.append("\n\n");
                textView.append("Дата: " + secondDay);
                textView.append("\n");
                textView.append("Качество воздуха: " + (polution2/24));
                textView.append("\n\n");
                textView.append("Дата: " + thirdDay);
                textView.append("\n");
                textView.append("Качество воздуха: " + (polution3/24));
                textView.append("\n\n");
                textView.append("Дата: " + fourthDay);
                textView.append("\n");
                textView.append("Качество воздуха: " + (polution4/24));
                textView.append("\n\n");
                textView.append("Дата: " + fifthDay);
                textView.append("\n");
                textView.append("Качество воздуха: ");
                textView.append("\n");
            } catch (Exception e) {
                textView.setText(R.string.error);
            }
        }
    }

    public static String getHTMLData(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data.toString();
            } else {
                return null;
            }
        } catch (Exception ignored) {
        } finally {
            conn.disconnect();
        }
        return null;
    }
}
