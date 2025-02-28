package com.TestDefinitionLayer;

import com.api.graphql.GraphActions;
import com.api.graphql.pojo.GraphQLQuery;
import com.api.graphql.pojo.QueryLimit;
import com.api.graphql.pojo.User;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

@Link("https://jira.cloud.com")
@Feature("GraphAPi")
public class TC004_GraphQLTest extends GraphActions {
    private final String spaceXEndpoint = "https://api.spacex.land/graphql/";

    @Severity(SeverityLevel.NORMAL)
    @Test(description = "E2E test for graphql")
    @Description("Get StarWars")
    @Story("Test Graphql")
    public void TestStarWars() {
        String query = "query Query {\n" +
                "  allFilms {\n" +
                "    films {\n" +
                "      title\n" +
                "      director\n" +
                "      releaseDate\n" +
                "      speciesConnection {\n" +
                "        species {\n" +
                "          name\n" +
                "          classification\n" +
                "          homeworld {\n" +
                "            name\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String jsonString = graphqlToJson(query);

        setBaseURI("https://swapi-graphql.netlify.app/.netlify/functions/index");
        RestAssured
                .given()
                .contentType("application/json")
                .body(jsonString)
                .when().post().then()
                .assertThat()
                .statusLine("HTTP/1.1 200 OK")
                .log()
                .body();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(description = "E2E test for graphql")
    @Description("Get Fruit Shop")
    @Story("Test Graphql")
    public void TestFruitShop() {
        String query = "query{\n" +
                "  products(id: \"7\") {\n" +
                "    name\n" +
                "    price\n" +
                "    category {\n" +
                "      name\n" +
                "    }\n" +
                "    vendor {\n" +
                "      name\n" +
                "      id\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String jsonString = graphqlToJson(query);

        setBaseURI("https://www.predic8.de/fruit-shop-graphql?");
        RestAssured
                .given()
                .contentType("application/json")
                .body(jsonString)
                .when().post().then()
                .assertThat()
                .statusLine("HTTP/1.1 200 OK")
                .log()
                .body();
    }

    @Severity(SeverityLevel.NORMAL)
    @Test(description = "E2E test for graphql")
    @Description("Get SpaceX")
    @Story("Test Graphql")
    public void TestCompanyData() {
        GraphQLQuery query = GraphQLQuery.builder().build();
        query.setQuery("{ company { name ceo coo } }");
        graphResponse(spaceXEndpoint, query, "data.company.ceo", "Elon Musk");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "E2E test for graphql")
    @Description("Get SpaceX")
    @Story("Test Graphql")
    public void TestLaunches_checkMissionName() {

        GraphQLQuery query = GraphQLQuery.builder().build();
        query.setQuery("query getLaunches($limit: Int!){ launches(limit: $limit) { mission_name } }");

        JSONObject variables = new JSONObject();
        variables.put("limit", 10);

        query.setVariables(variables.toString());
        graphResponse(spaceXEndpoint, query, "data.launches[0].mission_name", "Thaicom 6");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "E2E test for graphql")
    @Description("Get SpaceX")
    @Story("Test Graphql")
    public void TestLaunches_checkMissionName_POJO() {

        GraphQLQuery query = GraphQLQuery.builder().build();
        query.setQuery("query getLaunches($limit: Int!){ launches(limit: $limit) { mission_name } }");

        QueryLimit queryLimit = QueryLimit.builder().build();
        queryLimit.setLimit(10);

        query.setVariables(queryLimit);
        graphResponse(spaceXEndpoint, query, "data.launches[0].mission_name", "Thaicom 6");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "E2E test for graphql")
    @Description("Get SpaceX")
    @Story("Test Graphql")
    public void TestAddUser_checkReturnedData() {

        GraphQLQuery query = GraphQLQuery.builder().build();
        query.setQuery("mutation insert_users ($id: uuid!, $name: String!, $rocket: String!) { insert_users(objects: {id: $id, name: $name, rocket: $rocket}) { returning { id name rocket } } }");

        User myUser = User.builder()
                .id(UUID.randomUUID())
                .name("Bas")
                .rocket("My awesome rocket")
                .build();
        query.setVariables(myUser);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post("https://api.spacex.land/graphql/")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("data.insert_users.returning[0].id", equalTo(myUser.getId().toString()))
                .body("data.insert_users.returning[0].name", equalTo(myUser.getName()))
                .body("data.insert_users.returning[0].rocket", equalTo(myUser.getRocket()))
                .log()
                .body();
    }
}
