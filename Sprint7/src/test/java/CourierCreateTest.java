import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
    private String login;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Can create courier")
    public void canCreateTest() {
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

    @Test
    @DisplayName ("Can not duplicate courier")
    public void cannotCreateDuplicateTest() {
        String json = getRandomCourierJson();
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(201);

        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(409);
    }

    private String getRandomCourierJson(){
        login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        String json = String.format("{\"login\": \"%s\"," +
                        "  \"password\": \"somepwd\", \"firstName\": \"Yaya\"}", login);
        return json;
    }

    @Test
    @DisplayName ("Can not create courier without login")
    public void cannotCreateCourierWithoutLoginTest() {
        File json = new File("src/test/resources/newCourierWithoutLogin.json");
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(400);
    }

    @Test
    @DisplayName ("Can not create courier without password")
    public void cannotCreateCourierWithoutPasswordTest() {
        File json = new File("src/test/resources/newCourierWithoutPassword.json");
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then().statusCode(400);
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

