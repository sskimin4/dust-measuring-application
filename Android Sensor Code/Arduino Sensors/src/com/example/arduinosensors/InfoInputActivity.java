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
    EditText editAge, editGender,editWeight,editHeight;
    Button btnSaveInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_info);

        editAge = (EditText) findViewById(R.id.ageInput);
        editGender = (EditText) findViewById(R.id.genderInput);
        editWeight = (EditText) findViewById(R.id.weightInput);
        editHeight = (EditText) findViewById(R.id.heightInput);

        btnSaveInfo = (Button) findViewById(R.id.saveButton);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAge = editAge.getText().toString();
                String strGender = editGender.getText().toString();
                String strWeight = editWeight.getText().toString();
                String strHeight = editHeight.getText().toString();
                UserInfo appUser = new UserInfo(strAge,strGender,strWeight,strHeight);
                Intent intent = new Intent();
                intent.putExtra("info",appUser);
                setResult(2,intent);
                finish();
            }
        });
    }
}
