# notification-service

This API make use of Jackson API's @JsonAnySetter and @JsonAnyGetter method to accept dynamically changing request payload.

Use Postman or any other API testing tool and enter below given JSON as a payload and you can keep on adding new fields to the JSON which will work just fine.
This project also configured for swagger which you can use to excute the API call . Please find URL for the same - http://localhost:8081/swagger-ui.html

URL - http://localhost:8081/notification/v1/saveCondition

Sample JSON - 

{
    "BloodSugar" : {
	"A1c Value" : "110",
	"Average Blood Glucose- mg/dl " : "100",
    "NewField": "value"	
 },
 "Hypertention" : {
    "systolic in mm Hg" : "120",
    "diastolic in mm Hg " : "80"
    
 }

}


