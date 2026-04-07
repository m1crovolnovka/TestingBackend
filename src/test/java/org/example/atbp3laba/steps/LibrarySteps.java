package org.example.atbp3laba.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.example.atbp3laba.CucumberSpringConfiguration;
import org.example.atbp3laba.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibrarySteps extends CucumberSpringConfiguration {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvcResponse response;
    private Book currentBook;

    @Given("the library service is available")
    public void setup() {
        webAppContextSetup(webApplicationContext);
        currentBook = null;
    }

    @And("a book with ID {int} exists in the database")
    public void aBookWithIDExistsInTheDatabase(int bookId) {
    }

    @When("I request data for book {int}")
    public void iRequestDataForBook(int bookId) {
        response = given()
                .when()
                .get("/api/books/" + bookId);

        if (response.getStatusCode() == 200) {
            currentBook = response.as(Book.class);
        } else {
            currentBook = null;
        }
    }

    @And("I send a request to calculate the fine for the retrieved book")
    public void iSendARequestToCalculateTheFine() {
        if (currentBook != null) {
            response = given()
                    .header("Content-Type", "application/json")
                    .body(currentBook)
                    .when()
                    .post("/api/calculate-fine");
        }
    }

    @Then("the API returns status code {int}")
    public void theAPIReturnsStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("the total fine should be {double}")
    public void theTotalFineShouldBe(Double expectedFine) {
        if (response.getStatusCode() == 200) {
            Double actualFine = response.as(Double.class);
            assertEquals(expectedFine, actualFine, 0.001);
        }

    }
}