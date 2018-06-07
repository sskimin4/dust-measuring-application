package com.example.arduinosensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class InfoInputActivity extends Activity {
    EditText editAge, editGender, editWeight, editHeight;
    Button btnSaveInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_info);

        String age = getIntent().getStringExtra("age");
        String weight = getIntent().getStringExtra("weight");
        String gender = getIntent().getStringExtra("gender");
        String height = getIntent().getStringExtra("height");

        editAge = (EditText) findViewById(R.id.ageInput);
        editGender = (EditText) findViewById(R.id.genderInput);
        editWeight = (EditText) findViewById(R.id.weightInput);
        editHeight = (EditText) findViewById(R.id.heightInput);

        if (!age.isEmpty()) editAge.setText(age);
        if (!weight.isEmpty()) editWeight.setText(weight);
        if (!gender.isEmpty()) editGender.setText(gender);
        if (!height.isEmpty()) editHeight.setText(height);

        btnSaveInfo = (Button) findViewById(R.id.saveButton);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAge = editAge.getText().toString();
                String strGender = editGender.getText().toString();
                String strWeight = editWeight.getText().toString();
                String strHeight = editHeight.getText().toString();
                UserInfo appUser = new UserInfo(strAge, strGender, strWeight, strHeight);
                Intent intent = new Intent();
                intent.putExtra("info", appUser);
                setResult(2, intent);
                finish();
            }
        });
    }
}
