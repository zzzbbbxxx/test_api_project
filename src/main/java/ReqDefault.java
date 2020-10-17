import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ReqDefault extends ReqBase {


        @Test(description= "structure of json are correct & contains all keys,params ")
        public void testJSONisCorrect() {

                JSONObject jsonResponse = HelperReq.sendRequestGetJSON(PATH,"");

                HelperReq.validateSchema(BASE_SCHEMA,jsonResponse);

        }


        @Test(description= "value of total must be equal real count of regions")
        public void testNumberOfRegions() {

                int total = HelperReq.getValue("total");
                int count = 0;

                for (String page : Arrays.asList("?page=1", "?page=2", "?page=3"))
                        count = count + HelperReq.getJsonArray(HelperReq.sendRequestGetJSON(PATH, page),
                                        "items").length();

                Assert.assertEquals(count, total,
                        "Фактическое количество городов: " + count +
                        "...отличается от значения, возвращаемого в переменной total: "+ total);

        }


        @Test(description= "page_size default must be contains 15 regions")
        public void testDefaultPageSize() {

                org.json.JSONObject jsonObject = HelperReq.sendRequestGetJSON(PATH,"");

                Assert.assertEquals(jsonObject.getJSONArray("items").length(),
                                15,
                                "Count of regions in response by default must be equal: 15 \n");

        }



        //This function will provide the patameter data
        @DataProvider(name = "Data-Provider-Function-test11")
        public Object[][] parameterTestProvider_test11() {
                return new Object[][]{
                        {"?page=1", ""}
                };
        }


        @Test(dataProvider = "Data-Provider-Function-test11",
        description = "page by default must be equel page=1" )
        public void testDefaultPageNumber(String q1, String q2) {


                org.json.JSONObject jsonObject1 = HelperReq.sendRequestGetJSON(PATH, q1);
                org.json.JSONObject jsonObject2 = HelperReq.sendRequestGetJSON(PATH, q2);

                HelperReq.validateSchema(BASE_SCHEMA,jsonObject1);

                assertEquals(jsonObject1.toString(), jsonObject2.toString(),
                                "Page by default must be equel for response for \"page=1\"\n"
                                        + "Response for \"page=1\": " + jsonObject1 + "\n"
                                        + "Default: " + jsonObject2);

        }


        @Test(description= "regions should not be repeated for different pages")
        public void testRegionsNotRepeated() {

                List<String> listOfRegions = new ArrayList<>();
                List<String> listOfRepeatRegions = new ArrayList<>();

                for (String page : Arrays.asList("?page=1", "?page=2", "?page=3")) {

                        JSONArray tmpObj = HelperReq.getJsonArray(
                                HelperReq.sendRequestGetJSON(PATH, page), "items");

                        tmpObj.forEach(item -> {
                                String name = HelperReq.getNameOfRegion((JSONObject)item);

                                if (HelperReq.arrayContainsElem(name,listOfRegions))
                                        listOfRepeatRegions.add(name);

                                else listOfRegions.add(name);
                        });
                }

                Assert.assertEquals(listOfRepeatRegions.size(),
                        0,"Repeating regions: " + listOfRepeatRegions.toString());

        }



        //This function will provide the patameter data
        @DataProvider(name = "Data-Provider-Function_test9")
        public Object[][] parameterTestProvider_test9() {
                return new Object[][] {
                        {"?page_size=5&","page=1"},
                        {"?page_size=5&","page=2"},
                        {"?page_size=5&","page=3"},
                        {"?page_size=5&","page=4"},
                        {"?page_size=5&","page=5"},
                        {"?page_size=5&","page=6"},

                };
        }

        // country_code by default must be show all regions for all countries
        // я мог написать тест двумя способами:
        // исходя из документации - проверяя, что все страны из списка
        // ... есть в ответе
        // или исходя из знания, что в списке отсутствует "ua"
        // в первом случае будет ошибка, потому что я ищу только страны из списка и "падаю",
        // ...если появляется то, чего я не жду

        @Test(dataProvider = "Data-Provider-Function_test9",
        description = "...")
        public void testDefaultCountryCode(String q1, String q2) {

                JSONArray tmpObj = HelperReq.getJsonArray(
                        HelperReq.sendRequestGetJSON(PATH,q1+q2),
                        "items");

                HashSet<String> codeSet = new HashSet<>();

                tmpObj.forEach(item -> {
                        codeSet.add(HelperReq.getCountryCodeValue((JSONObject)item));
                });

                List<String> codeList = Arrays.asList("ru", "kg",  "kz", "cz");
                Assert.assertTrue(HelperReq.arrayContainsElemFrom(
                        codeSet,
                        codeList),
                        "Code on this page: " + codeSet + "\n" +
                                "Code by default must be in { ru, kg, kz, cz } \n ");
        }


}