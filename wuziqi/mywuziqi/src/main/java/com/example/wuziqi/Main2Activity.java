package com.example.wuziqi;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wuziqi.WuziqiPan;
public class Main2Activity extends AppCompatActivity implements View.OnClickListener{
    private WuziqiPanrenji wuziqiPan;
    private Button btn_reatart;
    private Button bnfanhui=null;
    private TextView txtView;
    public int recLen = 0;
    public int min=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btn_reatart = (Button) findViewById(R.id.bt_restart);
        wuziqiPan=(WuziqiPanrenji) findViewById(R.id.id_wuziqi);
        txtView = (TextView)findViewById(R.id.txttime);
        handler.postDelayed(runnable, 1000);
        btn_reatart.setOnClickListener(this);
        bnfanhui=(Button)findViewById(R.id.fanhui);
        bnfanhui.setOnClickListener(new Button.OnClickListener(){//创建监听
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_restart: {
                min = 0;
                recLen = 0;
                wuziqiPan.start();

                break;
            }
        }
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (wuziqiPan.mIsGameOver==false) //如果return掉 下次开始就不会计时了！！
                recLen++;


                if (recLen < 10) {
                    txtView.setText(min + ":0" + recLen);
                } else if (recLen < 60) {
                    txtView.setText(min + ":" + recLen);
                } else if (recLen == 60) {
                    min++;
                    recLen = 0;
                    txtView.setText(min + ":0" + recLen);
                }

                handler.postDelayed(this, 1000);

        }
    };


}
