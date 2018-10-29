package com.example.wuziqi;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private Button bnrenren=null;
    private Button bnrenji=null;
    private Button bnzhanji=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bnrenren=(Button)findViewById(R.id.renren);
        bnrenji=(Button)findViewById(R.id.renji);


        bnrenren.setOnClickListener(new Button.OnClickListener(){//创建监听 人人对战
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        bnrenji.setOnClickListener(new Button.OnClickListener(){//创建监听 人机对战
            @Override
            public void onClick(View v) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("请选择")
                            // 设置对话框标题

                            .setMessage("本游戏设有初级和高级电脑请选择！")
                            // 设置显示的内容

                            //右边按钮
                            .setPositiveButton("高级",new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            Intent intent=new Intent(LoginActivity.this,Main3Activity.class);
                                            startActivity(intent);

                                        }

                                    })

                            //左边按钮
                            .setNegativeButton("初级",new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,int which) {
                                            Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                                            startActivity(intent);

                                        }

                                    }).show();
                }
        });


    }
}
