import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class CourierLoginTest {
    private String login;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        String json = getRandomCourierJson();
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }
    private String getRandomCourierJson(){
        login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        String json = String.format("{\"login\": \"%s\"," +
                "  \"password\": \"somepwd\", \"firstName\": \"Yaya\"}", login);
        return json;
    }


    @Test
    @DisplayName ("Courier can log in")
    public void courierCanAuth() throws IOException {
        Path filePath = Path.of("src/test/resources/courierAlinaAuth.json");
        String json = Files.readString(filePath);
        json = String.format(json, login);
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login")
                .then().assertThat().body("id", notNullValue())
                .statusCode(200);
    }

    @Test
    @DisplayName ("Courier can not log in without login")
    public void cannotAuthWithoutLoginTest() {
        File json = new File("src/test/resources/authWithoutLogin.json");
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login")
                .then().statusCode(400);
    }

    @Test
    @DisplayName ("Courier can not log in without password")
    public void cannotAuthWithoutPasswordTest() throws IOException {
        Path filePath = Path.of("src/test/resources/authWithoutPassword.json");
        String json = Files.readString(filePath);
        json = String.format(json, login);
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login")
                .then().statusCode(400);
    }

    @Test
    @DisplayName ("Courier can not log in with incorrect data")
    public void authWithIncorrectData() throws IOException {
        Path filePath = Path.of("src/test/resources/authWithIncorrectData.json");
        String json = Files.readString(filePath);
        json = String.format(json, login);
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier/login")
                .then().statusCode(404);
    }

    @After
    public void cleanCourier() {
        if (login != null){
            RequestSpecification httpRequest = RestAssured.given();
            String loginJson = String.format("{\"login\": \"%s\",\"password\": \"somepwd\"}", login);
            Response response = httpRequest
                    .header("Content-type", "application/json")
                    .body(loginJson)
                    .post("/api/v1/courier/login");
            JsonPath jsonPathEvaluator = response.jsonPath();
            int id = jsonPathEvaluator.get("id");
            given()
                    .when()
                    .delete(String.format("/api/v1/courier/%d", id))
                    .then().statusCode(200);
        }
    }


}