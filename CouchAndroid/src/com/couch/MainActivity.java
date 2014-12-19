package com.couch;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;
import org.ektorp.ReplicationCommand;
import org.ektorp.ReplicationStatus;
import org.ektorp.UpdateConflictException;
import org.ektorp.android.util.ChangesFeedAsyncTask;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static String TAG = "MainActivity";

    protected ReplicationCommand pushReplicationCommand;
    protected ReplicationCommand pullReplicationCommand;

    private ListView recordList;
    private Button newBtn;
    private Button sendBtn;
    private Button receiveBtn;
    private Button cleanDbBtn;
    private ProgressDialog pd;

    private CouchApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (CouchApp) getApplication();
        recordList = (ListView) findViewById(R.id.listView);
        recordList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
                Patient p = (Patient) parent.getItemAtPosition(position);
                updateRecord(p);
            }
        });

        recordList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position, long id) {
                final Patient p = (Patient) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                AlertDialog alert = builder.setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                deleteRecord(p);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();

                alert.show();

                return true;
            }
        });
        newBtn = (Button) findViewById(R.id.btnNew);
        newBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                insertNewRecord();
            }
        });
        sendBtn = (Button) findViewById(R.id.btnSendData);
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        receiveBtn = (Button) findViewById(R.id.btnReceiveData);
        receiveBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                receiveData();
            }

        });
        cleanDbBtn = (Button) findViewById(R.id.btnCleanDb);
        cleanDbBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanDb();
            }
        });
        populateList();
        
        //Start Replication right away, no need to push buttons and it's CONTINUOUS
        
        receiveData();
        sendData();
        
        //Listen to Changes
        ChangesCommand changesCmd = new ChangesCommand.Builder().continuous(true).build();
        ExampleChangesFeedAsyncTask couchChangesAsyncTask = new ExampleChangesFeedAsyncTask(app.getDbConnector(), changesCmd);
        couchChangesAsyncTask.execute();
    }

    protected void deleteRecord(Patient p) {
        app.getDbConnector().delete(p);
        populateList();
    }

    protected void updateRecord(Patient p) {
        Intent newRecordIntent = new Intent(this.getApplicationContext(), NewRecordActivity.class);
        newRecordIntent.putExtra("isUpdate", true);
        newRecordIntent.putExtra("nextId", p.getId());
        startActivityForResult(newRecordIntent, 1);

    }

    protected void cleanDb() {
        app.deleteDb();
        populateList();

    }

    protected void receiveData()  {
        pullReplicationCommand = new ReplicationCommand.Builder()
                .source("http://192.168.1.60:4984/sync_gateway")
                .target(CouchApp.DATABASE_NAME)
                .continuous(true)
                .build();
        
        ReplicationStatus status = app.getDbInstace().replicate(pullReplicationCommand);
    }

    protected void sendData() {

        pushReplicationCommand = new ReplicationCommand.Builder()
                .source(CouchApp.DATABASE_NAME)
                .target("http://192.168.1.60:4984/sync_gateway")
                .continuous(true)
                .build();
        app.getDbInstace().replicate(pushReplicationCommand);
    }

//    private void showPd() {
//        pd = ProgressDialog.show(this, "", "Loading. Please wait...", true);
//    }
//
//    private void dismissPd() {
//        if (pd != null) {
//            pd.dismiss();
//        }
//    }

    protected void insertNewRecord() {
        Intent newRecordIntent = new Intent(this.getApplicationContext(), NewRecordActivity.class);
        newRecordIntent.putExtra("isUpdate", false);
        int id = 0;
        List<String> list;
        try {
            list = app.getDbConnector().getAllDocIds();
            for (String storedId : list) {
                int value = Integer.valueOf(storedId);
                if (value > id) {
                    id = value;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        newRecordIntent.putExtra("nextId", Integer.toString(++id));
        startActivityForResult(newRecordIntent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        populateList();
    }

    private void populateList() {
        try {
            List<Patient> list = new ArrayList<Patient>();
            for (String id : app.getDbConnector().getAllDocIds()) {
                Patient patient = app.getDbConnector().get(Patient.class, id);
                list.add(patient);
            }
            PatientAdapter adapter = new PatientAdapter(app.getApplicationContext(),
                    R.layout.record_list_item, list);
            recordList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PatientAdapter extends ArrayAdapter<Patient> {
        public PatientAdapter(Context context, int textViewResourceId, List<Patient> objects) {
            super(context, textViewResourceId, objects);
        }

    }
    
    class ExampleChangesFeedAsyncTask extends ChangesFeedAsyncTask {

        public ExampleChangesFeedAsyncTask(CouchDbConnector couchDbConnector, ChangesCommand changesCommand) {
            super(couchDbConnector, changesCommand);
        }

        @Override
        protected void handleDocumentChange(DocumentChange change) {
            populateList();
        }

        @Override
        protected void onDbAccessException(DbAccessException dbAccessException) {
            Log.e(TAG, "DbAccessException following changes feed", dbAccessException);
        }

    }
}
