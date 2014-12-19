package com.couch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NewRecordActivity extends Activity {
    
    public static String TAG = "NewRecordActivity";

    private Button actionBtn;
    private TextView firstName;
    private TextView lastName;
    private TextView age;
    private TextView idTv;
    
    private boolean isUpdate;
    private String id;
    private CouchApp app;
    private Patient patient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_record_activity);
        
        app = (CouchApp)getApplication();
        isUpdate = getIntent().getExtras().getBoolean("isUpdate", false);
        id= getIntent().getExtras().getString("nextId", "1");
        
        firstName = (TextView) findViewById(R.id.firstNameTv);
        lastName = (TextView) findViewById(R.id.lastNameTv);
        age = (TextView) findViewById(R.id.ageTv);
        idTv = (TextView) findViewById(R.id.idTv);
        actionBtn = (Button) findViewById(R.id.actionBtn);
        actionBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doAction();
                
            }
        });
        idTv.setText(id);
        
        if (isUpdate) {
            idTv.setEnabled(false);
            patient = app.getDbConnector().get(Patient.class,id);
            populateFields();
        } else {
            patient = new Patient();
        }
    }

    private void populateFields() {
        firstName.setText(patient.getFirstName());
        lastName.setText(patient.getLastName());
        age.setText(Integer.toString(patient.getAge()));
    }

    protected void doAction() {
        patient.setFirstName(firstName.getText().toString());
        patient.setLastName(lastName.getText().toString());
        patient.setAge(age.getText().length() >0?Integer.parseInt(age.getText().toString()):0);
        if (isUpdate) {
            app.getDbConnector().update(patient);
        } else {
            app.getDbConnector().create(idTv.getText().toString(),patient);
        }
        
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);        
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_record, menu);
        return true;
    }

}
