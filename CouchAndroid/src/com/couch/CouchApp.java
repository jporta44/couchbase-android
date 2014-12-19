package com.couch;

import java.io.IOException;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Application;
import android.util.Log;

import com.couchbase.cblite.CBLServer;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;



public class CouchApp extends Application {

    public static String TAG = "CouchApp";
    // constants
    public static final String DATABASE_NAME = "emr";
    // public static final String dDocName = "grocery-local";
    // public static final String dDocId = "_design/" + dDocName;
    // public static final String byDateViewName = "byDate";

    // couch internals
    protected static CBLServer server;
    protected static HttpClient httpClient;

    // ektorp impl
    protected CouchDbInstance dbInstance;
    protected CouchDbConnector couchDbConnector;    
    
    {
        CBLURLStreamHandlerFactory.registerSelfIgnoreError();
    }
    
    public void onCreate() {
        super.onCreate();
        startCBLite();
        startEktorp();
    }
    
    protected void startCBLite() {
        String filesDir = getFilesDir().getAbsolutePath();
        try {
            server = new CBLServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting Server", e);
        }

        // install a view definition needed by the application
        // CBLDatabase db = server.getDatabaseNamed(DATABASE_NAME);
        // CBLView view = db.getViewNamed(String.format("%s/%s", dDocName,
        // byDateViewName));
        // view.setMapReduceBlocks(new CBLViewMapBlock() {
        //
        // @Override
        // public void map(Map<String, Object> document, CBLViewMapEmitBlock
        // emitter) {
        // Object createdAt = document.get("created_at");
        // if (createdAt != null) {
        // emitter.emit(createdAt.toString(), document);
        // }
        //
        // }
        // }, null, "1.0");
    }
    

    protected void startEktorp() {
        Log.v(TAG, "starting ektorp");

        if (httpClient != null) {
            httpClient.shutdown();
        }

        httpClient = new CBLiteHttpClient(server);
        dbInstance = new StdCouchDbInstance(httpClient);
        couchDbConnector = dbInstance.createConnector(DATABASE_NAME, true);
    }    
    
    public CouchDbInstance getDbInstace () {
        return dbInstance;
    }
    
    public CouchDbConnector getDbConnector () {
        return couchDbConnector;
    }    
    
    public void deleteDb () {
        dbInstance.deleteDatabase(DATABASE_NAME);
        startEktorp();
    }    
}
