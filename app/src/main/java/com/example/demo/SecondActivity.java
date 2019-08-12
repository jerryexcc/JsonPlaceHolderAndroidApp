package com.example.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private List<Vo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        recyclerView = findViewById(R.id.recycleView);
        StringRequest request = new StringRequest(StringRequest.Method.GET, "https://my-json-server.typicode.com/jerryexcc/MyJsonPlaceHolderDemo/posts", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "response: " + response);
                JsonElement jsonParser = new JsonParser().parse(response);
                JsonArray jsonArray = jsonParser.getAsJsonArray();
                JsonObject jsonObject;
                String id, title, url;
                list = new ArrayList<>();
                for (int n = 0; n < jsonArray.size(); n++) {
                    jsonObject = jsonArray.get(n).getAsJsonObject();
                    id = jsonObject.get("id").getAsString();
                    title = jsonObject.get("title").getAsString();
                    url = jsonObject.get("author").getAsString();
                    list.add(new Vo(id, title, url));
                }
                recyclerView.setLayoutManager(new GridLayoutManager(SecondActivity.this, 4));
                recyclerView.setAdapter(new PicView(list));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "Wrong: " + error);
            }
        });

        requestQueue.add(request);
    }

    private class PicView extends RecyclerView.Adapter<PicView.Viewholder> {
        private List<Vo> myList;

        public PicView(List<Vo> myList) {
            this.myList = myList;
        }

        class Viewholder extends RecyclerView.ViewHolder {
            private TextView tvID, tvTitle, tvUrl;
            private ImageView imageView;

            public Viewholder(View itemView) {
                super(itemView);
                tvID = itemView.findViewById(R.id.tvId);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvUrl = itemView.findViewById(R.id.tvUrl);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }

        @Override
        public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_view, parent, false);
            Viewholder viewholder = new Viewholder(itemView);
            return viewholder;
        }

        @Override
        public void onBindViewHolder(final PicView.Viewholder holder, int position) {
            final Vo vo = myList.get(position);
            holder.tvID.setText(vo.getId());
            holder.tvTitle.setText(vo.getTitle());
            holder.tvUrl.setText(vo.getUrl());

            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        URL url = new URL(vo.url);
                        final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.imageView.setImageBitmap(bitmap);
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public int getItemCount() {
            return myList.size();
        }
    }
}
