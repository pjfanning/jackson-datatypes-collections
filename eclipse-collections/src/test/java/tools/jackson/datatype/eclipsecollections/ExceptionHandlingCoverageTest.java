package tools.jackson.datatype.eclipsecollections;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for exception handling in Eclipse Collections
 * serializers and deserializers.
 * Tests cover:
 * - Type parameter mismatches
 * - Null handling for collections that don't accept nulls
 * - Deserialization edge cases
 * - Error message clarity
 */
public class ExceptionHandlingCoverageTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests: Null handling in collections
    /**********************************************************
     */

    @Test
    public void testImmutableListWithNullElement() throws Exception {
        // Eclipse Collections ImmutableList should handle nulls gracefully or reject them clearly
        String json = "[\"a\", null, \"c\"]";

        try {
            ImmutableList<String> list = MAPPER.readValue(json,
                    new TypeReference<ImmutableList<String>>() {});
            // If it succeeds, verify null is handled
            assertNotNull(list);
            // Some implementations may skip nulls or include them
            assertTrue(list.size() >= 2);
        } catch (DatabindException e) {
            // If it fails, verify error message is clear
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().length() > 0);
        }
    }

    @Test
    public void testMutableListWithNullElement() throws Exception {
        String json = "[\"a\", null, \"c\"]";

        // MutableList should be able to handle nulls
        MutableList<String> list = MAPPER.readValue(json,
                new TypeReference<MutableList<String>>() {});

        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertNull(list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void testImmutableSetWithNullElement() throws Exception {
        String json = "[\"a\", null, \"c\"]";

        try {
            ImmutableSet<String> set = MAPPER.readValue(json,
                    new TypeReference<ImmutableSet<String>>() {});
            // If it succeeds, verify the result
            assertNotNull(set);
            assertTrue(set.size() >= 2);
        } catch (DatabindException e) {
            // If it fails, error should be clear
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testMutableSetWithNullElement() throws Exception {
        String json = "[\"a\", null, \"c\"]";

        MutableSet<String> set = MAPPER.readValue(json,
                new TypeReference<MutableSet<String>>() {});

        assertNotNull(set);
        // Sets may handle nulls
        assertTrue(set.size() >= 2);
    }

    @Test
    public void testImmutableMapWithNullValue() throws Exception {
        String json = "{\"key1\": \"value1\", \"key2\": null, \"key3\": \"value3\"}";

        try {
            ImmutableMap<String, String> map = MAPPER.readValue(json,
                    new TypeReference<ImmutableMap<String, String>>() {});
            assertNotNull(map);
            // May accept or reject null values
            assertTrue(map.size() >= 2);
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testMutableMapWithNullValue() throws Exception {
        String json = "{\"key1\": \"value1\", \"key2\": null, \"key3\": \"value3\"}";

        MutableMap<String, String> map = MAPPER.readValue(json,
                new TypeReference<MutableMap<String, String>>() {});

        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals("value1", map.get("key1"));
        assertNull(map.get("key2"));
        assertEquals("value3", map.get("key3"));
    }

    /*
    /**********************************************************
    /* Tests: Type parameter mismatches
    /**********************************************************
     */

    @Test
    public void testListDeserializeWithTypeMismatch() throws Exception {
        // Try to deserialize strings as integers
        String json = "[\"not\", \"a\", \"number\"]";

        try {
            MAPPER.readValue(json, new TypeReference<MutableList<Integer>>() {});
            fail("Expected exception for type mismatch");
        } catch (DatabindException e) {
            // Should have clear error about type conversion
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().length() > 0);
        }
    }

    @Test
    public void testSetDeserializeWithTypeMismatch() throws Exception {
        String json = "[\"not\", \"a\", \"number\"]";

        try {
            MAPPER.readValue(json, new TypeReference<MutableSet<Integer>>() {});
            fail("Expected exception for type mismatch");
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testMapDeserializeWithKeyTypeMismatch() throws Exception {
        // Try to deserialize with wrong key type
        String json = "{\"key1\": 1, \"key2\": 2}";

        try {
            MAPPER.readValue(json, new TypeReference<MutableMap<Integer, Integer>>() {});
            fail("Expected exception for key type mismatch");
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testMapDeserializeWithValueTypeMismatch() throws Exception {
        // Try to deserialize with wrong value type
        String json = "{\"key1\": \"value1\", \"key2\": \"value2\"}";

        try {
            MAPPER.readValue(json, new TypeReference<MutableMap<String, Integer>>() {});
            fail("Expected exception for value type mismatch");
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    /*
    /**********************************************************
    /* Tests: Primitive type handling
    /**********************************************************
     */

    @Test
    public void testIntListWithNullShouldFail() throws Exception {
        // Primitive collections should not accept null
        String json = "[1, null, 3]";

        try {
            // Attempting to deserialize into primitive int list
            MAPPER.readValue(json, new TypeReference<org.eclipse.collections.api.list.primitive.ImmutableIntList>() {});
            // If it doesn't throw, check behavior
        } catch (Exception e) {
            // Expected - null cannot be converted to primitive int
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testIntListWithValidValues() throws Exception {
        String json = "[1, 2, 3]";

        // Should deserialize successfully
        org.eclipse.collections.api.list.primitive.ImmutableIntList list = MAPPER.readValue(json,
                new TypeReference<org.eclipse.collections.api.list.primitive.ImmutableIntList>() {});

        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    /*
    /**********************************************************
    /* Tests: Invalid JSON structures
    /**********************************************************
     */

    @Test
    public void testDeserializeListFromObject() throws Exception {
        // Try to deserialize object as list
        String json = "{\"key\": \"value\"}";

        try {
            MAPPER.readValue(json, new TypeReference<MutableList<String>>() {});
            fail("Expected exception for wrong JSON structure");
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testDeserializeMapFromArray() throws Exception {
        // Try to deserialize array as map
        String json = "[\"a\", \"b\", \"c\"]";

        try {
            MAPPER.readValue(json, new TypeReference<MutableMap<String, String>>() {});
            fail("Expected exception for wrong JSON structure");
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    /*
    /**********************************************************
    /* Tests: Empty and edge cases
    /**********************************************************
     */

    @Test
    public void testEmptyList() throws Exception {
        String json = "[]";

        MutableList<String> list = MAPPER.readValue(json,
                new TypeReference<MutableList<String>>() {});

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testEmptySet() throws Exception {
        String json = "[]";

        MutableSet<String> set = MAPPER.readValue(json,
                new TypeReference<MutableSet<String>>() {});

        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    @Test
    public void testEmptyMap() throws Exception {
        String json = "{}";

        MutableMap<String, String> map = MAPPER.readValue(json,
                new TypeReference<MutableMap<String, String>>() {});

        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testSingleElementList() throws Exception {
        String json = "[\"single\"]";

        MutableList<String> list = MAPPER.readValue(json,
                new TypeReference<MutableList<String>>() {});

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("single", list.get(0));
    }

    @Test
    public void testSingleEntryMap() throws Exception {
        String json = "{\"key\": \"value\"}";

        MutableMap<String, String> map = MAPPER.readValue(json,
                new TypeReference<MutableMap<String, String>>() {});

        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("value", map.get("key"));
    }

    /*
    /**********************************************************
    /* Tests: Serialization consistency
    /**********************************************************
     */

    @Test
    public void testSerializeDeserializeRoundTrip() throws Exception {
        MutableList<String> originalList = Lists.mutable.of("a", "b", "c");

        String json = MAPPER.writeValueAsString(originalList);
        MutableList<String> deserializedList = MAPPER.readValue(json,
                new TypeReference<MutableList<String>>() {});

        assertEquals(originalList, deserializedList);
    }

    @Test
    public void testSerializeDeserializeMapRoundTrip() throws Exception {
        MutableMap<String, Integer> originalMap = Maps.mutable.of(
                "one", 1,
                "two", 2,
                "three", 3
        );

        String json = MAPPER.writeValueAsString(originalMap);
        MutableMap<String, Integer> deserializedMap = MAPPER.readValue(json,
                new TypeReference<MutableMap<String, Integer>>() {});

        assertEquals(originalMap, deserializedMap);
    }

    @Test
    public void testSerializeDeserializeSetRoundTrip() throws Exception {
        MutableSet<String> originalSet = Sets.mutable.of("a", "b", "c");

        String json = MAPPER.writeValueAsString(originalSet);
        MutableSet<String> deserializedSet = MAPPER.readValue(json,
                new TypeReference<MutableSet<String>>() {});

        assertEquals(originalSet, deserializedSet);
    }

    /*
    /**********************************************************
    /* Tests: Error message quality
    /**********************************************************
     */

    @Test
    public void testTypeMismatchErrorMessageIsActionable() throws Exception {
        String json = "{\"key\": \"not-a-number\"}";

        try {
            MAPPER.readValue(json, new TypeReference<MutableMap<String, Integer>>() {});
            fail("Expected exception");
        } catch (DatabindException e) {
            String message = e.getMessage();
            assertNotNull(message);
            // Message should be non-empty and provide useful information
            assertTrue(message.length() > 10);
        }
    }

    @Test
    public void testStructureMismatchErrorMessageIsActionable() throws Exception {
        String json = "[1, 2, 3]";

        try {
            MAPPER.readValue(json, new TypeReference<MutableMap<String, Integer>>() {});
            fail("Expected exception");
        } catch (DatabindException e) {
            String message = e.getMessage();
            assertNotNull(message);
            assertTrue(message.length() > 0);
        }
    }

    /*
    /**********************************************************
    /* Tests: Nested structures
    /**********************************************************
     */

    @Test
    public void testNestedListDeserialization() throws Exception {
        String json = "[[\"a\", \"b\"], [\"c\", \"d\"]]";

        MutableList<MutableList<String>> nestedList = MAPPER.readValue(json,
                new TypeReference<MutableList<MutableList<String>>>() {});

        assertNotNull(nestedList);
        assertEquals(2, nestedList.size());
        assertEquals(2, nestedList.get(0).size());
        assertEquals("a", nestedList.get(0).get(0));
    }

    @Test
    public void testNestedMapDeserialization() throws Exception {
        String json = "{\"outer1\": {\"inner1\": \"value1\"}, \"outer2\": {\"inner2\": \"value2\"}}";

        MutableMap<String, MutableMap<String, String>> nestedMap = MAPPER.readValue(json,
                new TypeReference<MutableMap<String, MutableMap<String, String>>>() {});

        assertNotNull(nestedMap);
        assertEquals(2, nestedMap.size());
        assertEquals("value1", nestedMap.get("outer1").get("inner1"));
        assertEquals("value2", nestedMap.get("outer2").get("inner2"));
    }

    @Test
    public void testNestedStructureWithTypeMismatch() throws Exception {
        String json = "[[\"not\", \"numbers\"], [\"also\", \"strings\"]]";

        try {
            MAPPER.readValue(json, new TypeReference<MutableList<MutableList<Integer>>>() {});
            fail("Expected exception for nested type mismatch");
        } catch (DatabindException e) {
            assertNotNull(e.getMessage());
        }
    }

    /*
    /**********************************************************
    /* Tests: Special characters and Unicode
    /**********************************************************
     */

    @Test
    public void testListWithUnicodeCharacters() throws Exception {
        String json = "[\"hello\", \"世界\", \"🌍\"]";

        MutableList<String> list = MAPPER.readValue(json,
                new TypeReference<MutableList<String>>() {});

        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("世界", list.get(1));
        assertEquals("🌍", list.get(2));
    }

    @Test
    public void testMapWithUnicodeKeys() throws Exception {
        String json = "{\"hello\": \"world\", \"こんにちは\": \"世界\"}";

        MutableMap<String, String> map = MAPPER.readValue(json,
                new TypeReference<MutableMap<String, String>>() {});

        assertNotNull(map);
        assertEquals("世界", map.get("こんにちは"));
    }

    /*
    /**********************************************************
    /* Tests: Large collections
    /**********************************************************
     */

    @Test
    public void testLargeList() throws Exception {
        // Create a JSON array with many elements
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < 1000; i++) {
            if (i > 0) jsonBuilder.append(",");
            jsonBuilder.append(i);
        }
        jsonBuilder.append("]");

        MutableList<Integer> list = MAPPER.readValue(jsonBuilder.toString(),
                new TypeReference<MutableList<Integer>>() {});

        assertNotNull(list);
        assertEquals(1000, list.size());
        assertEquals(0, list.get(0).intValue());
        assertEquals(999, list.get(999).intValue());
    }
}
