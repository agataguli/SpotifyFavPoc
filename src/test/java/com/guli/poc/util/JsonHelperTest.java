package com.guli.poc.util;

import com.mashape.unirest.http.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonHelperTest {

    @Test
    public void getFromJsonNode_returnsOnjectInRequestedType() {
        // given
        JsonNode dummyJsonNode = new JsonNode("{}");

        // when
        Object result = JsonHelper.getFromJsonNode(dummyJsonNode, DummyTest.class);

        // then
        assertTrue(result instanceof DummyTest);
    }

    @Test
    public void getFromJsonNode_resultObjectHasSettedAttributeIfMatch() {
        // given
        JsonNode jsonNodeWithKeyNamedAsAttribute = new JsonNode("{\"value\":\"random\"}");

        // when
        DummyTest result = JsonHelper.getFromJsonNode(jsonNodeWithKeyNamedAsAttribute, DummyTest.class);

        // then
        assertEquals("random", result.value);
    }

    private class DummyTest {
        String value;
    }
}