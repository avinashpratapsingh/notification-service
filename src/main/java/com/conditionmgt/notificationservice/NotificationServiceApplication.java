package com.conditionmgt.notificationservice;

import com.conditionmgt.notificationservice.controller.NotificationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@SpringBootApplication
@ComponentScan(basePackages = "com.conditionmgt")
public class NotificationServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(NotificationServiceApplication.class, args);
		/*String packageName="com.cooltrickshome";
		File inputJson= new File("."+File.separator+"input.json");
		File outputPojoDirectory=new File("."+File.separator+"convertedPojo");
		outputPojoDirectory.mkdirs();
		try {
			new NotificationServiceApplication().convert2JSON(inputJson.toURI().toURL(), outputPojoDirectory, packageName, inputJson.getName().replace(".json", ""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Encountered issue while converting to pojo: "+e.getMessage());
			e.printStackTrace();
		}*/
		//NotificationController.createDynamicDBTable(notification);
	}

	public void convert2JSON(URL inputJson, File outputPojoDirectory, String packageName, String className) throws IOException{/*
		JCodeModel codeModel = new JCodeModel();
		URL source = inputJson;
		GenerationConfig config = new DefaultGenerationConfig() {
			@Override
			public boolean isGenerateBuilders() { // set config option by overriding method
				return true;
			}
			public SourceType getSourceType(){
				return SourceType.JSON;
			}
		};
		SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
		mapper.generate(codeModel, className, packageName, source);
		codeModel.build(outputPojoDirectory);
	*/}

}
