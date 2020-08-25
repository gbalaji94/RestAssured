package com.rest.runner.StepDefinitions.API;

import com.rest.runner.Utilities.RestProcessor;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class BaseHook {
    Map<String, String> requestMap = new HashMap<>();
    Response response;

    @Given("Get the API Url")
    public void getAndSetTheAPIUrl() {
        requestMap.put("URL", "http://localhost:3000");
    }

    @And("Set the Authorization token")
    public void setTheAuthorizationToken() {
        requestMap.put("Authorization", "");
    }

    @Then("Construct the {string} request body with the following data")
    public void constructTheRequestBodyForWithTheFollowingData(String requestName, DataTable datatable) {
        requestMap.put("RequestName", requestName);
        Map<String, String> inputMap = datatable.asMap(String.class, String.class);
        requestMap.putAll(inputMap);
    }

    @Then("Make a Rest Call to {string} with {string} method")
    public void makeARestCallWithMethod(String path, String method) throws Throwable {
        requestMap.put("Path", path);
        requestMap.put("RequestMethod", method);
        RestProcessor restProcessor = new RestProcessor();
        response = restProcessor.processRestCall(requestMap);
    }

    @And("Verify if the status code is {string}")
    public void verifyIfTheStatusCodeIs(String statusCode) {
        Assert.assertEquals(String.valueOf(response.getStatusCode()), statusCode, "Status code mismatch");
    }

    @And("Verify if the response contains the following info")
    public void verifyItTheResponseContainsTheFollowingInfo(DataTable datatable) {
        Map<String, String> resultMap = datatable.asMap(String.class, String.class);
        for (Map.Entry<String, String> entry : resultMap.entrySet())
            Assert.assertEquals(response.jsonPath().getString(entry.getKey()), entry.getValue(), entry.getKey() + "doesnt match the expected value");
    }

}
