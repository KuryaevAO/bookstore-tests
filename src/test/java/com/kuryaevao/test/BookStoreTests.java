package com.kuryaevao.test;

import org.junit.jupiter.api.Test;

import static filters.CustomLogFilter.customLogFilter;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookStoreTests {

    // 07925d03-068f-45e8-a7d2-45f208fc664c - User ID
    String token;

    @Test
    void checkBookInUsersCollection() {

        String userData = "{" +
                "  \"userName\": \"CyberDemon\"," +
                "  \"password\": \"Doom_@1337\"" +
                "}";

        String bookData = "{" +
                "  \"userId\": \"07925d03-068f-45e8-a7d2-45f208fc664c\"," +
                "  \"collectionOfIsbns\": [" +
                "    {" +
                "      \"isbn\": \"9781449325862\"" +
                "    }" +
                "  ]" +
                "}";

        String deleteBookData = "{" +
                "  \"isbn\": \"9781449325862\"," +
                "  \"userId\": \"07925d03-068f-45e8-a7d2-45f208fc664c\"" +
                "}";

        step("Generate token", () ->
                token =
                        given()
                                .filter(customLogFilter().withCustomTemplates())
                                .contentType("application/json")
                                .accept("application/json")
                                .body(userData.toString())
                                .when()
                                .log().uri()
                                .log().body()
                                .post("https://demoqa.com/Account/v1/GenerateToken")
                                .then()
                                .log().body()
                                .body("status", is("Success"))
                                .body("result", is("User authorized successfully."))
                                .extract().response().path("token")
        );

        step("Add book in User's Collection", () ->
                given()
                        .filter(customLogFilter().withCustomTemplates())
                        .contentType("application/json")
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(bookData.toString())
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .body("books", hasSize(greaterThan(0)))
        );

        step("Delete book from User's Collection", () ->
                given()
                        .filter(customLogFilter().withCustomTemplates())
                        .contentType("application/json")
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .body(deleteBookData.toString())
                        .when()
                        .log().all()
                        .delete("https://demoqa.com/BookStore/v1/Book")
                        .then()
                        .log().body()
                        .statusCode(204)
        );
    }

}