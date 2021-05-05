package com.conditionmgt.notificationservice.controller;

import com.conditionmgt.notificationservice.entity.Condition;
import com.conditionmgt.notificationservice.entity.Notification;
import com.conditionmgt.notificationservice.repo.NotificationRepo;
import com.conditionmgt.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/notification/v1")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private KafkaTemplate <String,Notification > kafkaTemplate;
    private static final String TOPIC ="notification";

    @PostMapping(value = "/saveCondition",produces = { "application/json" })
    public String saveNotification(@RequestBody Condition condition){
        ObjectMapper mapper = new ObjectMapper();
        String response="";
        try {
            response= mapper.writeValueAsString(condition);
            Notification notification = new Notification();
            notification.setConditionDetails(response);
            notificationRepo.save(notification);
            createDynamicDBTable(condition);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }
    @GetMapping (value = "/getCondition",produces = { "application/json" })
    public String getCondition() throws JsonProcessingException {
        String jsn = "{ \"BloodSugar\" : { \"A1c Value\" : \"110\", \"Average Blood Glucose- mg/dl \" : \"100\", \"NewField\": \"value\" }, \"Hypertention\" : { \"systolic in mm Hg\" : \"120\", \"diastolic in mm Hg \" : \"80\"\n" +
                "\n" +
                "}\n" +
                "\n" +
                "}";


        HashMap<String, Object> map = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            //Convert Map to JSON
            Optional<Notification> notification = notificationRepo.findById(2);
            map = mapper.readValue(notification.get().getConditionDetails(), new TypeReference<HashMap<String, Object>>(){});
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapper.writeValueAsString(map);
    }
    /*@PostMapping("/saveNotification")
    public Notification saveNotification(@RequestBody Notification notification){

        return notificationService.saveNotification(notification);
    }


    @GetMapping("/publish/{msg}")
    public String postMessage(@PathVariable("msg") final String msg){
        kafkaTemplate.send(TOPIC,msg);
        return "Published";
    }

    @RequestMapping("/getNotifications")
    public List<Notification> getNotifications(){
        return notificationService.getNotification();
    }*/
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/notification";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root@321";

    public static void createDynamicDBTable(Condition condition){
        Connection conn = null;
        Statement stmt = null;
        try{

            //STEP 4: Execute a query,
            System.out.println("Creating table in given database...");
            conn = getDBConnection();
            stmt = conn.createStatement();
            Map<String, Object> map = condition.getConditionDetails();
            String tableName="";
            String sql="";
           // for(Map.Entry<String, Object> pair : map.entrySet()) {
                tableName = "Measurement";//pair.getKey();
                stmt.executeUpdate("DROP TABLE IF EXISTS" + " " + tableName);
                sql = "CREATE TABLE "+ tableName+" "+"(ID INTEGER); ";
                /*sql = "CREATE TABLE" + " " + tableName +
                        "(id INTEGER not NULL)" +
                        " PRIMARY KEY ( id ))";*/

                stmt.executeUpdate(sql);
                System.out.println("Created table in given database...");
            sql = "ALTER TABLE notification." + tableName + " "+"ADD ConditionName" +" VARCHAR(30)";
            stmt.executeUpdate(sql);
          //  }
            for(Map.Entry<String, Object> colPair : map.entrySet()) {
                LinkedHashMap<String,Object> obj = (LinkedHashMap)colPair.getValue();
                String conditionName = colPair.getKey();
                sql = "INSERT INTO notification." + tableName+" (ConditionName) values"+"(\""+conditionName+"\")";
                stmt.executeUpdate(sql);

                for(Map.Entry<String, Object> colPair1 : obj.entrySet())
                {
                    LinkedHashMap<String,Object> obj2 = (LinkedHashMap)colPair.getValue();
                    String colname = conditionName + "_" + colPair1.getKey();
                    sql = "ALTER TABLE notification." + tableName + " "+"ADD " + colname + " VARCHAR(30) ";

                    stmt.executeUpdate(sql);
                    System.out.println("Altered table with new coloums");
                }
                for(Map.Entry<String, Object> colPair1 : obj.entrySet())
                { String colname = conditionName + "_" + colPair1.getKey();
                    sql = "UPDATE notification." + tableName+" SET "+ colname+"="+"("+colPair1.getValue()+")"+"Where ConditionName ="+"\""+conditionName+"\"";
                    stmt.executeUpdate(sql);
                }

            }
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");

    }

    public static Connection getDBConnection(){
        Connection conn = null;
        Statement stmt = null;

            //STEP 2: Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Connected database successfully...");

        return conn;
        }

    @GetMapping (value = "/getConditionfromDB",produces = { "application/json" })
    public String getConditionfromDB() throws Exception{

        Connection conn = null;
        Statement stmt = null;
        conn = getDBConnection();
        stmt = conn.createStatement();
        ResultSet rs1 = stmt.executeQuery("Select * from notification.Measurement");
        // Retrieving the ResultSetMetadata object
        ResultSetMetaData rsMetaData = rs1.getMetaData();
        System.out.println(
                "List of column names in the current table: ");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode conditionDetails = mapper.createObjectNode();
        List<Map<String, Object>> rows = resultSetToList(rs1);
        String conditionKey ="";
        for (Map<String, Object> row:rows) {
            ObjectNode details = mapper.createObjectNode();
            for (Map.Entry<String, Object> rowEntry : row.entrySet()) {
                if(null != rowEntry.getKey() && rowEntry.getKey().equals("ConditionName"))
                    conditionKey= rowEntry.getValue().toString();
                System.out.print(rowEntry.getKey() + " = " + rowEntry.getValue() + ", ");
                if(null !=rowEntry.getValue() && null != rowEntry.getKey()){
                    if(null != rowEntry.getKey() && !rowEntry.getKey().equals("ConditionName")){
                        details.put(rowEntry.getKey().replace(rowEntry.getKey()+"_",""),rowEntry.getValue().toString());
                    }
                }
            }
            conditionDetails.set(conditionKey,details);
        }
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(conditionDetails);
        return json;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        while (rs.next()){
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for(int i = 1; i <= columns; ++i){
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }
}
