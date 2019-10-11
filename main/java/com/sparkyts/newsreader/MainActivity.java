package com.sparkyts.newsreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class DownloadTask extends AsyncTask<String, Void, ArrayList<String>[]>{
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Fetching Top News...!");
            pd.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String>[] news) {
            super.onPostExecute(news);

            //this will contain top-news title & urls
            final ArrayList[] topNews = news;
            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, topNews[0]);

            final ListView topNewsList = findViewById(R.id.topNewsList);
            topNewsList.setAdapter(arrayAdapter);
            topNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), viewNews.class);
                    intent.putExtra("news-url", (String)topNews[1].get(position));
                    startActivity(intent);
                }
            });

            pd.dismiss();
            pd.cancel();
        }

        @Override
        protected ArrayList<String>[] doInBackground(String... urls) {

            //This will store title and url of a specific news.
            //API USED : https://github.com/HackerNews/API
            ArrayList<String>[] news = new ArrayList[]{new ArrayList<String>(), new ArrayList<String>()};
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream is = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();
                int i;
                while((i=is.read())!=-1)
                    sb.append((char)i);

                JSONArray topLinksArray = new JSONArray(sb.toString());

                int newsLength = 20;
                if(topLinksArray.length() < newsLength) {
                    newsLength = topLinksArray.length();
                }
                for(i = 0 ; i < newsLength ; i++){
                    String newsUrlId = topLinksArray.getString(i);

                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+ newsUrlId + ".json?print=pretty");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();

                    sb.setLength(0);
                    is = urlConnection.getInputStream();
                    int j;
                    while((j=is.read())!=-1)
                        sb.append((char)j);

                    JSONObject newsObject = new JSONObject(sb.toString());

                    if(!newsObject.isNull("title") && !newsObject.isNull("url")){
                        news[0].add(newsObject.getString("title"));
                        news[1].add(newsObject.getString("url"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return news;
        }
    }
}
