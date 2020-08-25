package com.rest.runner.Utilities;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;

public class RestProcessor {

    /*
    * Supported Request Methods are POST, PUT, GET, DELETE. If you wish to add any other method update in row 27.
    * Update the type of Authorization Token, in my case its a Bearer token.
    *
    *
    * */

    public Response processRestCall(Map<String, String> inputMap) throws Throwable {
        String requestMethod = inputMap.get("RequestMethod").toUpperCase();
        String[] supportedAPIMethod = {"POST", "PUT", "GET", "DELETE"};
        if (!Arrays.asList(supportedAPIMethod).contains(requestMethod)) {
            throw new Exception("Unsupported API Method");
        }

        String requestBody, fileContent = null;
        Response response = null;
        RestAssured.baseURI = inputMap.get("URL") + inputMap.get("Path");
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", ContentType.JSON);

        if(inputMap.containsKey("Authorization")){
            request.header("Authorization", "Bearer " + inputMap.get("Authorization"));
        }

        if (requestMethod.equalsIgnoreCase("POST") || requestMethod.equalsIgnoreCase("PUT")) {
            String filePath = "src/test/resources/requests/" + inputMap.get("RequestName") + ".json";
            try (FileReader reader = new FileReader(filePath)) {
                JSONParser jsonParser = new JSONParser();
                fileContent = jsonParser.parse(reader).toString(); //JSON File is read and parsed
            } catch (FileNotFoundException fileNotFound) {
                throw new Exception("JSON file is missing in the directory");
            } catch (ParseException parseException) {
                throw new Exception("Request has invalid JSON syntax");
            }

            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(fileContent);
            requestBody = template.apply(inputMap); //JSON Request is substituted here

            if (requestBody.length() <= 0) {
                throw new Exception("Request Body is empty for " + requestMethod + " requests.");
            }
            request.body(requestBody);
        }

        switch (requestMethod) {
            case "GET": {
                response = request.get();
                break;
            }
            case "PUT": {
                response = request.put();
                break;
            }
            case "POST": {
                response = request.post();
                break;
            }
            case "DELETE": {
                response = request.delete();
                break;
            }
        }

        return response;
    }
}
