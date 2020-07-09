package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    ArrayList<phone> phoneList = new ArrayList<phone>();

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String json_str = getJsonString();
        jsonParsing(json_str);

        // tab 만들기
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout) ;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
                int pos = tab.getPosition() ;
                changeView(pos) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        }) ;

        // 첫 번째 tab
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new MyAdapter(phoneList));  // Adapter 등록

        // 두 번째 tab
        GridView gridView = (GridView)findViewById(R.id.grid_picture);
        gridView.setAdapter(new ImageAdapter(this));

        // 세 번째 tab video
        VideoView videoView = findViewById(R.id.video_view);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.summer_hate;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }


    //json -> str 변환용
    private String getJsonString()
    {
        String json = "";

        try {
            InputStream is = getAssets().open("phone_number.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return json;
    }

    // json 파싱용 클래스
    public class phone{
        private String name;
        private String phone_num;
        private String age;

        public String getName() {
            return name;
        }

        public String getPhone_num() {
            return phone_num;
        }

        public String getAge() {
            return age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhone_num(String _phone_num) {
            phone_num = _phone_num;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    // json 실제 파싱
    private void jsonParsing(String json)
    {
        try{
            JSONObject jsonObject = new JSONObject(json);

            JSONArray phoneArray = jsonObject.getJSONArray("phone_number");

            for(int i=0; i<phoneArray.length(); i++)
            {
                JSONObject movieObject = phoneArray.getJSONObject(i);

                phone phone_arr = new phone();

                phone_arr.setName(movieObject.getString("name"));
                phone_arr.setPhone_num(movieObject.getString("phone_num"));
                phone_arr.setAge(movieObject.getString("age"));

                phoneList.add(phone_arr);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // tab1 - recyclerview 어답터
    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<phone> myDataList = null;

        MyAdapter(ArrayList<phone> dataList)
        {
            myDataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성
            View view = inflater.inflate(R.layout.cardview, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position)
        {
            //ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩
            viewHolder.name.setText( myDataList.get(position).getName());
            viewHolder.phone_num.setText(myDataList.get(position).getPhone_num());
            viewHolder.age.setText(myDataList.get(position).getAge());
        }

        @Override
        public int getItemCount()
        {
            //Adapter가 관리하는 전체 데이터 개수 반환
            return myDataList.size();
        }
    }

    // tab1 - recyclerview : view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView phone_num;
        TextView age;

        ViewHolder(View itemView)
        {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone_num = itemView.findViewById(R.id.phone_num);
            age = itemView.findViewById(R.id.age);
        }
    }

    // image 전시용
    public class ImageAdapter extends BaseAdapter{
        private Context context;

        private Integer[] images = {R.drawable.pic_001,R.drawable.pic_002,R.drawable.pic_003,
                R.drawable.pic_004,R.drawable.pic_005,R.drawable.pic_006,R.drawable.pic_007,
                R.drawable.pic_008,R.drawable.pic_009,R.drawable.pic_010,R.drawable.pic_011,
                R.drawable.pic_012,R.drawable.pic_013,R.drawable.pic_014,R.drawable.pic_015,
                R.drawable.pic_016,R.drawable.pic_017,R.drawable.pic_018,R.drawable.pic_019,
                R.drawable.pic_020,};

        public ImageAdapter(Context con){
            this.context = con;
        }

        public int getCount(){
            return images.length;
        }

        public Object getItem(int pos){
            return null;
        }

        public long getItemId(int pos){
            return 0;
        }

        public View getView(int pos, View convertView, ViewGroup parent){
            ImageView imageView;

            if(convertView==null){
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(5, 5, 5, 5);
            }else{
                imageView = (ImageView)convertView;
            }
            imageView.setImageResource(images[pos]);

            return imageView;
        }
    }

    // tab 전환용
    private void changeView(int index) {
        RecyclerView textView1 = (RecyclerView) findViewById(R.id.recycler_view) ;
        GridView textView2 = (GridView) findViewById(R.id.grid_picture) ;
        VideoView textView3 = (VideoView) findViewById(R.id.video_view) ;

        switch (index) {
            case 0 :
                textView1.setVisibility(View.VISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(View.VISIBLE) ;
                textView3.setVisibility(View.INVISIBLE) ;
                break ;
            case 2 :
                textView1.setVisibility(View.INVISIBLE) ;
                textView2.setVisibility(View.INVISIBLE) ;
                textView3.setVisibility(View.VISIBLE) ;
                break ;

        }
    }
}