package com.example.myapplication;

import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvPingjia;
    private TextView tvPianjia;
    private TextView tvRoman;
    private List<VoiceItem> voiceItemList;
    private int voiceIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews()
    {
        Button btnPlay = this.findViewById(R.id.btnPlay);
        tvPingjia = this.findViewById(R.id.tvPingjia);
        tvPianjia = this.findViewById(R.id.tvPianjia);
        tvRoman = this.findViewById(R.id.tvRoman);
        btnPlay.setOnClickListener(new View.OnClickListener(){
            int i = 0;
            public void onClick(View v) {
                if(voiceIndex < 0){
                    voiceIndex = 0;
                }

                if(voiceIndex > voiceItemList.size() -1){
                    voiceIndex = 0;
                }
                setContent(voiceItemList.get(voiceIndex));
                voiceIndex++;
            }
        });
        voiceItemList = new LinkedList<VoiceItem>();
        VoiceItem item1 = new VoiceItem();
        item1.setPingjia("あ");
        item1.setPianjia("ア");
        item1.setRoman("a");
        voiceItemList.add(item1);

        VoiceItem item2 = new VoiceItem();
        item2.setPingjia("い");
        item2.setPianjia("イ");
        item2.setRoman("i");
        voiceItemList.add(item2);

        VoiceItem item3 = new VoiceItem();
        item3.setPingjia("う");
        item3.setPianjia("ウ");
        item3.setRoman("u");
        voiceItemList.add(item3);
    }
    private void setContent(VoiceItem item)
    {
        tvPingjia.setText("平假名："+item.getPingjia());
        tvPianjia.setText("假片名："+item.getPianjia());
        tvRoman.setText("罗马音："+item.getRoman());
    }

    private void loadXml(String file)
    {
        try {
            File path = new File(file);
            FileInputStream fis = new FileInputStream(path);

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

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
                        if ("persons".equals(tagName)) { // <persons>
                        } else if ("person".equals(tagName)) { // <person id="1">
                            id = parser.getAttributeValue(null, "id");
                        } else if ("name".equals(tagName)) { // <name>
                            name = parser.nextText();
                        }else if ("gender".equals(tagName)) { // <age>
                            gender = parser.nextText();
                        } else if ("age".equals(tagName)) { // <age>
                            age = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("person".equals(tagName)) {
//                            Log.i(TAG, "id---" + id);
//                            Log.i(TAG, "name---" + name);
//                            Log.i(TAG, "gender---" + gender);
//                            Log.i(TAG, "age---" + age);
                        }
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
    }
}
