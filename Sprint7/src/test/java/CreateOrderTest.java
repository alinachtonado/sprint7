import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;

import static io.restassured.RestAssured.given;
import static java.lang.String.join;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final String[] colors;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }


    public CreateOrderTest(String[] colors) {
        this.colors= colors;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GRAY"}},
                {new String[]{"BLACK", "GRAY"}},
                {new String[0]}
        };
    }

    @Test
    @DisplayName("Create order")
    public void createTest(){
        String json = getJson();
        given()

                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/orders")
                .then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }

    private String getJson(){
        String json = String.format("{\"firstName\": \"Naruto\", \"lastName\": \"Uchiha\", \"address\": \"Konoha, 142 apt.\", \"metroStation\": \"4\", \"phone\": \"+7 800 355 35 35\", \"rentTime\": \"5\",\"deliveryDate\": \"2020-06-06\", \"comment\": \"Saske, come back to Konoha\", \"colors\": [\"%s\"]}", String.join("\",\"", colors));
        return json;
    }
}



