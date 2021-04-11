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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/notification";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root@321";

    public static void createDynamicDBTable(Condition condition){
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query,
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            Map<String, Object> map = condition.getConditionDetails();
            String tableName="";
            String sql="";
            for(Map.Entry<String, Object> pair : map.entrySet()) {
                tableName = pair.getKey();
                stmt.executeUpdate("DROP TABLE IF EXISTS" + " " + tableName);
                sql = "CREATE TABLE "+ tableName+" "+"(ID INTEGER); ";
                /*sql = "CREATE TABLE" + " " + tableName +
                        "(id INTEGER not NULL)" +
                        " PRIMARY KEY ( id ))";*/

                stmt.executeUpdate(sql);
                System.out.println("Created table in given database...");
            }
            for(Map.Entry<String, Object> colPair : map.entrySet()) {
               // for(int i= 0 ; i<colPair.getValue();i++){
                LinkedHashMap<String,Object> obj = (LinkedHashMap)colPair.getValue();
                String table = colPair.getKey();
                for(Map.Entry<String, Object> colPair1 : obj.entrySet())
                {
                    LinkedHashMap<String,Object> obj2 = (LinkedHashMap)colPair.getValue();
                   // for(Map.Entry<String, Object> colPair2 : obj2.entrySet()){
                    //colPair1.getValue();
                    String colname = table + "_" + colPair1.getKey();
                    sql = "ALTER TABLE notification." + table + " "+"ADD " + colname + " VARCHAR(30)";

                    stmt.executeUpdate(sql);
                    System.out.println("Altered table with new coloums");
                    //}
                }
                for(Map.Entry<String, Object> colPair1 : obj.entrySet())
                { String colname = table + "_" + colPair1.getKey();

                sql = "INSERT INTO notification." + table+"("+colname+") values"+"("+colPair1.getValue()+")";
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

}
