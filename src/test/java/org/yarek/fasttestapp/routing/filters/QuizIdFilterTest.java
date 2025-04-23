package org.yarek.fasttestapp.routing.filters;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuizIdFilterTest {

    @Test
    void extractQuizId() {
        Map<String, String> uriToIdMap = new HashMap<>();
        uriToIdMap.put("/tests/1/started", "1");
        uriToIdMap.put("/tests/123/started", "123");
        uriToIdMap.put("/tests/myTest1/started", "myTest1");

        uriToIdMap.put("/tests/2/preview", "2");
        uriToIdMap.put("/tests/1xf5gs1/preview", "1xf5gs1");

        uriToIdMap.put("/tests/7/nothing", "7");
        uriToIdMap.put("/tests/someTestId/nothing", "someTestId");

        uriToIdMap.put("/tests/10/", "10");
        uriToIdMap.put("/tests/fdf41dsa31/", "fdf41dsa31");

        uriToIdMap.put("/tests/4", "4");
        uriToIdMap.put("/tests/qwsa231s", "qwsa231s");

        for (Map.Entry<String, String> entry : uriToIdMap.entrySet()) {
            assertEquals(entry.getValue(), QuizIdFilter.extractQuizId(entry.getKey()));
        }
    }
}