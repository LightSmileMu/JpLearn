package com.example.myapplication;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvPingjia;
    private TextView tvPianjia;
    private TextView tvRoman;
    private List<VoiceItem> voiceItemList;
    private int voiceIndex = 0;
    private boolean isPlay = false;
    private static int delay = 0;
    private static int period = 2000;  //2s
    private static final int Update_View = 0;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private MediaPlayer player = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews()
    {
        TabHost tabHost = (TabHost) this.findViewById(android.R.id.tabhost);
        tabHost.setup();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.voice_view,tabHost.getTabContentView());
        inflater.inflate(R.layout.voice_exprise,tabHost.getTabContentView());
        tabHost.addTab(tabHost.newTabSpec("voice_view").setIndicator("观看").setContent(R.id.left));
        tabHost.addTab(tabHost.newTabSpec("voice_exprise").setIndicator("练习").setContent(R.id.right));

        final Button btnPlay = this.findViewById(R.id.btnPlay);
        tvPingjia = this.findViewById(R.id.tvPingjia);
        tvPianjia = this.findViewById(R.id.tvPianjia);
        tvRoman = this.findViewById(R.id.tvRoman);


        btnPlay.setBackgroundResource(R.mipmap.play);
        btnPlay.setOnClickListener(new View.OnClickListener(){
            int i = 0;
            public void onClick(View v) {
                if(!isPlay) {
                    startTimer();
                    btnPlay.setBackgroundResource(R.mipmap.pause);
                }
                else{
                    stopTimer();
                    btnPlay.setBackgroundResource(R.mipmap.play);
                }
                isPlay = !isPlay;
            }
        });
        final Button btnVoice = this.findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                int currentIndex = -1;
                for(int i = 0;i<voiceItemList.size();i++)
                {
                    if(("罗马音："+voiceItemList.get(i).getRoman()).equals(tvRoman.getText()) )
                    {
                        currentIndex = i;
                        break;
                    }
                }
                if(currentIndex >= 0)
                {
                    PlayVoice(voiceItemList.get(currentIndex));
                }
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Update_View:
                        if(voiceIndex < 0){
                            voiceIndex = 0;
                        }
                        if(voiceIndex > voiceItemList.size() -1){
                            voiceIndex = 0;
                        }
                        setContent(voiceItemList.get(voiceIndex));
                        voiceIndex++;
                        break;
                    default:
                        break;
                }
            }
        };

        voiceItemList = loadXml("/storage/sdcard0/data/fifty_voice.raw");
    }
    private void setContent(VoiceItem item)
    {
        tvPingjia.setText("平假名："+item.getHiragana());
        tvPianjia.setText("片假名："+item.getKatakana());
        tvRoman.setText("罗马音："+item.getRoman());
        PlayVoice(item);
    }

    private void PlayVoice(VoiceItem item)
    {
        if(item != null){
            Uri uri = Uri.parse("android.resource://com.example.myapplication/raw/"+ item.getRoman());
            if(uri != null && tvRoman.getText().length() > 0)
            {

                if(player == null || !player.isPlaying())
                {
                    player = MediaPlayer.create(this,uri);
                    player.start();
                }
            }
        }
    }

    private List<VoiceItem> loadXml(String file)
    {
        List<VoiceItem> items = new ArrayList<VoiceItem>();
        try {
            File path = new File(file);

            //FileInputStream fis = new FileInputStream(path);
            InputStream fis = this.getResources().openRawResource(R.raw.fifty_voice);
            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

            String id = null;
            String name = null;
            String gender = null;
            String age = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称
                VoiceItem item = new VoiceItem();
                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("VoiceItem".equals(tagName)) {
                            item.setId( parser.getAttributeValue(null, "id"));
                            item.setKatakana( parser.getAttributeValue(null, "katakana"));
                            item.setHiragana( parser.getAttributeValue(null, "hiragana"));
                            item.setRoman( parser.getAttributeValue(null, "roman"));
                            item.setRow( parser.getAttributeValue(null, "row"));
                            item.setColumn( parser.getAttributeValue(null, "column"));
                            item.setCategory( parser.getAttributeValue(null, "category"));
                            items.add(item);
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        break;
                    default:
                        break;
                }
                eventType = parser.next(); // 获得下一个事件类型
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{

        }
        return items;
    }

    private void startTimer(){
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    sendMessage(Update_View);
                }
            };
        }

        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, delay, period);

    }

    private void stopTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void sendMessage(int id){
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }
}
