package com.couch;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.ReplicationStatus;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import com.couchbase.client.CouchbaseClient;
import com.google.gson.Gson;

public class Main {
 
  public static void main(String[] args) throws Exception {
      
      HttpClient httpClient = new StdHttpClient.Builder()
              .url("http://192.168.1.60:4984/")
              .build();
              CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
              // if the second parameter is true, the database will be created if it doesn't exists
              CouchDbConnector db = dbInstance.createConnector("sync_gateway", true);
 
    // (Subset) of nodes in the cluster to establish a connection
//    List<URI> hosts = Arrays.asList(
//      new URI("http://192.168.1.60:8091/pools")
//    );
// 
//    // Name of the Bucket to connect to
//    String bucket = "sync_gateway";
// 
//    // Password of the bucket (empty) string if none
//    String password = "";
// 
//    // Connect to the Cluster
//    CouchbaseClient client = new CouchbaseClient(hosts, bucket, password);
//    client.set("8", "blabla");
//    //System.out.println(client.get("1"));
//    Gson gson = new Gson();

              
//***********CREATE********              
//    Patient p = new Patient();
//    p.setFirstName("Ruben");
//    p.setLastName("Da Silva");
//    p.setAge(33);
//    db.create("2",p);
    
//**********MODIFY*******    
    Patient p = db.get(Patient.class, "9");
//    db.delete(p);
    p.setLastName("Perez");
    db.update(p);
 

    
//    client.set(p1.getId(), gson.toJson(p1)).get();
//    
//    User user1 = new User("John", "Doe");
//    User user2 = new User("Matt", "Ingenthron");
//    User user3 = new User("Michael", "Nitschinger");
//     
//    client.set("user1", gson.toJson(user1)).get();
//    client.set("user2", gson.toJson(user2)).get();
//    client.set("user3", gson.toJson(user3)).get();
    
//    String designDoc = "users";
//    String viewName = "by_firstname";
//    View view = client.getView(designDoc, viewName);
//     
//    // 2: Create a Query object to customize the Query
//    Query query = new Query();
//    query.setIncludeDocs(true); // Include the full document body
//    query.setRangeStart("M"); // Start with M
//    query.setRangeEnd("M\\uefff"); // Stop before N starts
//     
//    // 3: Actually Query the View and return the results
//    ViewResponse response = client.query(view, query);
//     
//    // 4: Iterate over the Data and print out the full document
//    for (ViewRow row : response) {
//      System.out.println(row.getDocument());
//    }    
    
    //List<String> list = db.getAllDocIds();
//    for (String id : list) {
//        InputStream is = db.getAsStream("3");
//        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
//        while (s.hasNext()) {
//            System.out.println(s.next()); 
//        }
//    }
    
     //String p = (String) client.get("1");
 
    // Shutting down properly
    //client.shutdown();
  }
}
