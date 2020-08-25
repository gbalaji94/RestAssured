Feature: API Tests

  Scenario: Login and validate the User Info
    Given Get the API Url
    Then Construct the "login" request body with the following data
      | email    | kin182@testmail.com |
      | password | Test@123            |
    Then Make a Rest Call to "/signin" with "POST" method
    And Verify if the status code is "200"
    And Verify if the response contains the following info
      | user.email | kin182@testmail.com |
      | user.role  | admin               |