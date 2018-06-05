package com.example.arduinosensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class breathActivity extends Activity {
    RelativeLayout View3;
    float acu;
    Intent intent;
    String sum;
    TextView bre_sum;
    TextView ciga;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breath);
        bre_sum=(TextView) findViewById(R.id.tete);
        ciga=(TextView) findViewById(R.id.cigar);
        View3 = (RelativeLayout) findViewById(R.id.bre);
        View3.setBackgroundResource(R.drawable.smoke);
        intent=getIntent();
        sum = intent.getStringExtra("sum");
        bre_sum.setText(" 흡입량: " + sum + "㎍");
        acu= Float.parseFloat(sum);
        acu= acu* 84/58;
        ciga.setText("담배 흡연 시간: " + String.format("%.2f", acu) +"분");
    }
}
