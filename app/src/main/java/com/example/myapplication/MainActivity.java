package com.example.myapplication;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvPingjia;
    private TextView tvPianjia;
    private TextView tvRoman;
    private List<VoiceItem> voiceItemList;
    private int viewVoiceIndex = 0;
    private int expriseVoiceIndex = 0;
    private boolean isPlay = false;
    private static final int Update_View = 0;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private MediaPlayer player = null;
    private EditText tb_exprise_hiragana;
    private EditText tb_exprise_katakana;
    private EditText tb_exprise_roman;
    private Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @SuppressLint("HandlerLeak")
    private void initViews()
    {
        initTabs();
        voiceItemList = loadXml();
        player = new MediaPlayer();
        initView();
        initExpriseView();
    }

    private void initExpriseView(){
        final Button btnCheck = this.findViewById(R.id.btn_exprise_check);
        final Button btnPrev = this.findViewById(R.id.btn_exprise_prev);
        final Button btnNext = this.findViewById(R.id.btn_exprise_next);
        final Button btnVoice = this.findViewById(R.id.btn_exprise_voice);

        tb_exprise_hiragana = this.findViewById(R.id.tb_exprise_hiragana);
        tb_exprise_katakana = this.findViewById(R.id.tb_exprise_katakana);
        tb_exprise_roman = this.findViewById(R.id.tb_exprise_roman);

        tb_exprise_hiragana.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tb_exprise_hiragana.setTextColor(Color.BLACK);
            }
        });

        tb_exprise_katakana.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tb_exprise_katakana.setTextColor(Color.BLACK);
            }
        });

        tb_exprise_roman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tb_exprise_roman.setTextColor(Color.BLACK);
            }
        });

        RadioGroup rg_exprise_mode = this.findViewById(R.id.rg_exprise_mode);

        btnCheck.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                if(tb_exprise_hiragana.getText().length() == 0){
                    sb.append("请输入平假名!\n");
                }

                if(tb_exprise_katakana.getText().length() == 0){
                    sb.append("请输入片假名!\n");
                }

                if(tb_exprise_roman.getText().length() == 0){
                    sb.append("请输入罗马音!\n");
                }

                if(sb.length() > 0){
                    sb.deleteCharAt(sb.lastIndexOf("\n"));
                    Toast toast =  Toast.makeText(MainActivity.this,sb.toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    VoiceItem item = (VoiceItem)tb_exprise_roman.getTag();
                    if(item !=null){
                        if(tb_exprise_roman.isEnabled()){
                            if(!tb_exprise_roman.getText().toString().equals(item.getRoman())){
                                tb_exprise_roman.setTextColor(Color.RED);
                            }
                            else{
                                tb_exprise_roman.setTextColor(Color.GREEN);
                            }
                        }

                        if(tb_exprise_hiragana.isEnabled()){
                            if(!tb_exprise_hiragana.getText().toString().equals(item.getHiragana())){
                                tb_exprise_hiragana.setTextColor(Color.RED);
                            }
                            else{
                                tb_exprise_hiragana.setTextColor(Color.GREEN);
                            }
                        }

                        if(tb_exprise_katakana.isEnabled()){
                            if(!tb_exprise_katakana.getText().toString().equals(item.getKatakana())){
                                tb_exprise_katakana.setTextColor(Color.RED);
                            }
                            else{
                                tb_exprise_katakana.setTextColor(Color.GREEN);
                            }
                        }
                    }
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                expriseVoiceIndex=expriseVoiceIndex-1;
                if(expriseVoiceIndex < 0){
                    expriseVoiceIndex = voiceItemList.size();
                }
                setExproseViewContent(voiceItemList.get(expriseVoiceIndex),true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                expriseVoiceIndex=expriseVoiceIndex+1;
                if(expriseVoiceIndex > voiceItemList.size()){
                    expriseVoiceIndex = 0;
                }
                setExproseViewContent(voiceItemList.get(expriseVoiceIndex),true);
            }
        });

        btnVoice.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
               VoiceItem item =(VoiceItem)tb_exprise_roman.getTag();
               if(item != null)
               {
                   playVoice(item);
               }
            }
        });

        rg_exprise_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_exprise_kana:
                        tb_exprise_hiragana.setText("");
                        tb_exprise_katakana.setText("");
                        tb_exprise_hiragana.setEnabled(true);
                        tb_exprise_katakana.setEnabled(true);
                        tb_exprise_roman.setEnabled(false);
                        break;
                    case R.id.rb_exprise_roman:
                        tb_exprise_hiragana.setEnabled(false);
                        tb_exprise_katakana.setEnabled(false);
                        tb_exprise_roman.setEnabled(true);
                        break;
                    case R.id.rb_exprise_kanaAndRoman:
                        tb_exprise_hiragana.setText("");
                        tb_exprise_katakana.setText("");
                        tb_exprise_roman.setText("");
                        tb_exprise_hiragana.setEnabled(true);
                        tb_exprise_katakana.setEnabled(true);
                        tb_exprise_roman.setEnabled(true);
                        break;
                }
                setExproseViewContent(voiceItemList.get(expriseVoiceIndex),false);
            }
        });

        setExproseViewContent(voiceItemList.get(expriseVoiceIndex),false);
    }

    private void setExproseViewContent(VoiceItem item, boolean playVoice) {
        if(tb_exprise_roman.isEnabled()){
            tb_exprise_roman.setText("");
        }
        else{
            tb_exprise_roman.setText(item.getRoman());
        }

        if(tb_exprise_hiragana.isEnabled()){
            tb_exprise_hiragana.setText("");
        }
        else{
            tb_exprise_hiragana.setText(item.getHiragana());
        }

        if(tb_exprise_katakana.isEnabled()){
            tb_exprise_katakana.setText("");
        }
        else{
            tb_exprise_katakana.setText(item.getKatakana());
        }

        tb_exprise_roman.setTag(item);
        if(playVoice){
            playVoice(item);
        }
    }

    @SuppressLint("HandlerLeak")
    private void initView() {
        btnPlay = this.findViewById(R.id.btnPlay);
        tvPingjia = this.findViewById(R.id.tvPingjia);
        tvPianjia = this.findViewById(R.id.tvPianjia);
        tvRoman = this.findViewById(R.id.tvRoman);

        btnPlay.setBackgroundResource(R.mipmap.play);
        btnPlay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                playClickHandler();
            }
        });
        final Button btnVoice = this.findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                int currentIndex = -1;
                for(int i = 0;i<voiceItemList.size();i++)
                {
                    if(("罗马音："+voiceItemList.get(i).getRoman()).contentEquals(tvRoman.getText()) )
                    {
                        currentIndex = i;
                        break;
                    }
                }
                if(currentIndex >= 0)
                {
                    playVoice(voiceItemList.get(currentIndex));
                }
            }
        });
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg != null){
                    if (msg.what == Update_View) {
                        if (viewVoiceIndex < 0) {
                            viewVoiceIndex = 0;
                        }
                        if (viewVoiceIndex > voiceItemList.size() - 1) {
                            viewVoiceIndex = 0;
                        }
                        setViewContent(voiceItemList.get(viewVoiceIndex), true);
                        viewVoiceIndex++;
                    }
                }
            }
        };
        setViewContent(voiceItemList.get(viewVoiceIndex),false);
    }

    private void playClickHandler() {
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

    private void initTabs() {
        TabHost tabHost = this.findViewById(android.R.id.tabhost);
        tabHost.setup();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.voice_view,tabHost.getTabContentView());
        inflater.inflate(R.layout.voice_exprise,tabHost.getTabContentView());
        tabHost.addTab(tabHost.newTabSpec("voice_view").setIndicator("学习").setContent(R.id.left));
        tabHost.addTab(tabHost.newTabSpec("voice_exprise").setIndicator("练习").setContent(R.id.right));
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("voice_exprise")){
                    if(isPlay){
                        playClickHandler();
                    }
                }
            }
        });
    }

    private void setViewContent(VoiceItem item, boolean playVoice) {
        if(item !=null){
            tvPingjia.setText(String.format("平假名：%s", item.getHiragana()));
            tvPianjia.setText(String.format("片假名：%s", item.getKatakana()));
            tvRoman.setText(String.format("罗马音：%s", item.getRoman()));
            if(playVoice){
                playVoice(item);
            }
        }
    }

    private void playVoice(VoiceItem item) {
        try{
            if(item != null){
                String voiceId = item.getRoman();
                if(voiceId.equals("do")){
                    voiceId= voiceId+"_1";
                }
                Uri uri = Uri.parse("android.resource://com.example.myapplication/raw/"+ voiceId);

                if(uri != null && tvRoman.getText().length() > 0)
                {
                    player.reset();
                    player.setDataSource(this,uri);
                    player.prepare();
                    player.start();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<VoiceItem> loadXml()
    {
        List<VoiceItem> items = new ArrayList<>();
        try {
            InputStream fis = this.getResources().openRawResource(R.raw.fifty_voice);
            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType(); // 获得事件类型

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

        int delay = 0;
        int period = 2000;
        if(mTimer != null){
            mTimer.schedule(mTimerTask, delay, period);
        }
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
