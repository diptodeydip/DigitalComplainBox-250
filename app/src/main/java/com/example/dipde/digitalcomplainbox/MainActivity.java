package com.example.dipde.digitalcomplainbox;


import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.Button;

import com.example.dipde.digitalcomplainbox.R;


public class MainActivity extends AppCompatActivity {
    public static String flag = "none";
    public  static String url ;
    public  static String deptflag;
    //"#1b9cad"  #4F4070 //#880e4f
    //#008B8B

    Intent intent;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this,userloginpage.class);
        Button user = (Button)findViewById(R.id.User);
        final Button ju_dge = (Button) findViewById(R.id.judge);
        ju_dge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "judge";
                v.startAnimation(buttonClick);
                startActivity(new Intent(MainActivity.this,judgecodeverify.class));
            }
        });
        user.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        v.startAnimation(buttonClick);
                        startActivity(intent);
                    }
                }
        );

        Button exit = (Button)findViewById(R.id.exit);
        exit.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        v.startAnimation(buttonClick);
                       /* moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);*/
                        finish();
                    }
                }
        );
        findViewById(R.id.solvedprb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                flag = "solves";
                startActivity(new Intent(MainActivity.this,solvedprb.class));
            }
        });
    }
}
