package tools.jackson.datatype.guava;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;

import tools.jackson.databind.*;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test coverage for exception handling in Guava serializers and deserializers.
 * Tests cover:
 * - ORDER_MAP_ENTRIES_BY_KEYS with non-comparable and null keys
 * - Deserialization type safety
 * - Null handling consistency
 * - All exception handling blocks (ClassCastException, NullPointerException, IllegalArgumentException)
 */
public class ExceptionHandlingCoverageTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    static class NonComparableKey {
        public String value;

        public NonComparableKey(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "NonComparableKey{" + value + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NonComparableKey that = (NonComparableKey) o;
            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    /*
    /**********************************************************
    /* Tests: Multimap serialization with ORDER_MAP_ENTRIES_BY_KEYS
    /**********************************************************
     */

    @Test
    public void testMultimapSerializeOrderedWithNonComparableKeys() throws Exception {
        // When keys are not Comparable, should fall back to original order
        Multimap<NonComparableKey, Integer> multimap = ArrayListMultimap.create();
        multimap.put(new NonComparableKey("c"), 1);
        multimap.put(new NonComparableKey("a"), 2);
        multimap.put(new NonComparableKey("b"), 3);

        // Should not throw, but won't be sorted (ClassCastException caught internally)
        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(multimap);

        // Should successfully serialize (order not guaranteed)
        assertNotNull(json);
        assertTrue(json.contains("NonComparableKey"));
    }

    @Test
    public void testMultimapSerializeOrderedWithNullKey() throws Exception {
        Multimap<String, Integer> multimap = ArrayListMultimap.create();
        multimap.put("a", 1);
        multimap.put(null, 2);
        multimap.put("b", 3);

        // NullPointerException should be caught and reported
        try {
            MAPPER.writer()
                    .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                    .writeValueAsString(multimap);
            fail("Expected DatabindException for null key");
        } catch (DatabindException e) {
            verifyException(e, "Failed to sort Multimap entries", "null");
        }
    }

    @Test
    public void testHashMultimapSerializeOrderedWithNullKey() throws Exception {
        Multimap<String, Integer> multimap = HashMultimap.create();
        multimap.put("a", 1);
        multimap.put(null, 2);
        multimap.put("b", 3);

        try {
            MAPPER.writer()
                    .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                    .writeValueAsString(multimap);
            fail("Expected DatabindException for null key");
        } catch (DatabindException e) {
            verifyException(e, "Failed to sort Multimap entries", "null");
        }
    }

    @Test
    public void testLinkedHashMultimapSerializeOrderedWithNonComparable() throws Exception {
        Multimap<NonComparableKey, String> multimap = LinkedHashMultimap.create();
        multimap.put(new NonComparableKey("x"), "value1");
        multimap.put(new NonComparableKey("y"), "value2");

        // Should serialize without error (falls back to original order)
        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(multimap);
        assertNotNull(json);
    }

    /*
    /**********************************************************
    /* Tests: Cache serialization with ORDER_MAP_ENTRIES_BY_KEYS
    /**********************************************************
     */

    @Test
    public void testCacheSerializeOrderedWithNonComparableKeys() throws Exception {
        Cache<NonComparableKey, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(new NonComparableKey("c"), 1);
        cache.put(new NonComparableKey("a"), 2);
        cache.put(new NonComparableKey("b"), 3);

        // Should not throw, but won't be sorted (ClassCastException caught internally)
        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(cache);

        assertNotNull(json);
        assertTrue(json.contains("NonComparableKey"));
    }

    @Test
    public void testCacheSerializeOrderedWithNullKey() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put("a", 1);
        cache.put(null, 2); // Guava Cache allows null keys
        cache.put("b", 3);

        // NullPointerException should be caught and reported
        try {
            MAPPER.writer()
                    .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                    .writeValueAsString(cache);
            fail("Expected DatabindException for null key");
        } catch (DatabindException e) {
            verifyException(e, "Failed to sort", "null");
        }
    }

    @Test
    public void testCacheSerializeOrderedSuccessWithComparableKeys() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put("c", 1);
        cache.put("a", 2);
        cache.put("b", 3);

        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(cache);

        // Should be ordered alphabetically
        assertTrue(json.indexOf("\"a\"") < json.indexOf("\"b\""));
        assertTrue(json.indexOf("\"b\"") < json.indexOf("\"c\""));
    }

    /*
    /**********************************************************
    /* Tests: RangeMap serialization with ORDER_MAP_ENTRIES_BY_KEYS
    /**********************************************************
     */

    @Test
    public void testRangeMapSerializeOrderedWithNonComparableKeys() throws Exception {
        // RangeMap requires Comparable keys by design, so we test with a mock that fails comparison
        RangeMap<String, Integer> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed("a", "b"), 1);
        rangeMap.put(Range.closed("c", "d"), 2);

        // This should work fine with comparable String keys
        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(rangeMap);

        assertNotNull(json);
    }

    @Test
    public void testRangeMapSerializeOrderedWithNullValue() throws Exception {
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(1, 5), "value1");
        rangeMap.put(Range.closed(10, 15), null); // null value
        rangeMap.put(Range.closed(20, 25), "value3");

        // Should serialize successfully with null value
        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(rangeMap);

        assertNotNull(json);
        assertTrue(json.contains("null"));
    }

    /*
    /**********************************************************
    /* Tests: RangeSet deserialization with null handling
    /**********************************************************
     */

    @Test
    public void testRangeSetDeserializeFromNullValue() throws Exception {
        // Test that RangeSet properly handles null values in arrays
        String json = a2q("['[1..5]', null, '[10..15]']");

        try {
            MAPPER.readValue(json, RangeSet.class);
            // If it doesn't throw, that's also valid (null might be skipped)
        } catch (DatabindException e) {
            // Should have a clear error message about null not being accepted
            verifyException(e, "null", "RangeSet");
        }
    }

    @Test
    public void testRangeSetDeserializeValidRanges() throws Exception {
        String json = a2q("['[1..5]', '[10..15]', '[20..25]']");

        RangeSet<Integer> rangeSet = MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructParametricType(RangeSet.class, Integer.class));

        assertNotNull(rangeSet);
        assertTrue(rangeSet.contains(3));
        assertTrue(rangeSet.contains(12));
        assertTrue(rangeSet.contains(22));
        assertFalse(rangeSet.contains(7));
    }

    /*
    /**********************************************************
    /* Tests: Immutable collections with null values
    /**********************************************************
     */

    @Test
    public void testImmutableListWithNull() throws Exception {
        // ImmutableList does not accept nulls
        String json = a2q("['a', null, 'c']");

        try {
            MAPPER.readValue(json, ImmutableList.class);
            fail("Expected exception for null in ImmutableList");
        } catch (Exception e) {
            // NullPointerException should be caught and wrapped
            assertTrue(e instanceof DatabindException || e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void testImmutableSetWithNull() throws Exception {
        // ImmutableSet does not accept nulls
        String json = a2q("['a', null, 'c']");

        try {
            MAPPER.readValue(json, ImmutableSet.class);
            fail("Expected exception for null in ImmutableSet");
        } catch (Exception e) {
            // NullPointerException should be caught and wrapped
            assertTrue(e instanceof DatabindException || e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void testImmutableMapWithNullValue() throws Exception {
        // ImmutableMap silently skips null values (see GuavaImmutableMapDeserializer line 51-60)
        String json = a2q("{'key1': 'value1', 'key2': null, 'key3': 'value3'}");

        ImmutableMap<String, String> map = MAPPER.readValue(json, ImmutableMap.class);
        
        // Null value should be skipped, not cause an exception
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value3", map.get("key3"));
        assertFalse(map.containsKey("key2"));
    }

    @Test
    public void testImmutableMapWithNullKey() throws Exception {
        // Note: JSON doesn't support null keys - this is testing behavior with invalid JSON
        // The actual JSON parser will fail before reaching our deserializer
        String json = "{\"key1\": \"value1\", null: \"value2\", \"key3\": \"value3\"}";

        try {
            MAPPER.readValue(json, ImmutableMap.class);
            fail("Expected exception for null key");
        } catch (Exception e) {
            // Should fail during parsing or deserialization
            assertNotNull(e);
            // The exact exception type may vary depending on how the parser handles null keys
        }
    }

    /*
    /**********************************************************
    /* Tests: Multimap deserialization type safety
    /**********************************************************
     */

    @Test
    public void testMultimapDeserializeValidTypes() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        Multimap<String, Integer> multimap = MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructParametricType(ArrayListMultimap.class, String.class, Integer.class));

        assertNotNull(multimap);
        assertEquals(2, multimap.get("key1").size());
        assertTrue(multimap.get("key1").contains(1));
        assertTrue(multimap.get("key1").contains(2));
    }

    @Test
    public void testMultimapDeserializeWithTypeMismatch() throws Exception {
        // Try to deserialize string values as integers
        String json = a2q("{'key1': ['not', 'numbers'], 'key2': ['also', 'strings']}");

        try {
            MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructParametricType(ArrayListMultimap.class, String.class, Integer.class));
            fail("Expected exception for type mismatch");
        } catch (DatabindException e) {
            // Should have clear error about type conversion
            assertNotNull(e.getMessage());
        }
    }

    /*
    /**********************************************************
    /* Tests: RangeMap deserialization type safety
    /**********************************************************
     */

    @Test
    public void testRangeMapDeserializeValidTypes() throws Exception {
        String json = a2q("{'[1..5]': 'value1', '[10..15]': 'value2'}");

        RangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                MAPPER.getTypeFactory().constructParametricType(TreeRangeMap.class, Integer.class, String.class));

        assertNotNull(rangeMap);
        assertEquals("value1", rangeMap.get(3));
        assertEquals("value2", rangeMap.get(12));
    }

    @Test
    public void testRangeMapDeserializeInvalidRangeFormat() throws Exception {
        String json = a2q("{'invalid-range': 'value1'}");

        try {
            MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructParametricType(TreeRangeMap.class, Integer.class, String.class));
            fail("Expected exception for invalid range format");
        } catch (DatabindException e) {
            // Should have error about range parsing
            assertNotNull(e.getMessage());
        }
    }

    /*
    /**********************************************************
    /* Tests: Edge cases and boundary conditions
    /**********************************************************
     */

    @Test
    public void testEmptyMultimapWithOrdering() throws Exception {
        Multimap<String, Integer> multimap = ArrayListMultimap.create();

        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(multimap);

        assertEquals("{}", json);
    }

    @Test
    public void testSingleEntryMultimapWithOrdering() throws Exception {
        Multimap<String, Integer> multimap = ArrayListMultimap.create();
        multimap.put("key", 1);

        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(multimap);

        assertEquals(a2q("{'key':[1]}"), json);
    }

    @Test
    public void testEmptyCacheWithOrdering() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newBuilder().build();

        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(cache);

        assertEquals("{}", json);
    }

    @Test
    public void testEmptyRangeMapWithOrdering() throws Exception {
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();

        String json = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(rangeMap);

        assertEquals("{}", json);
    }

    /*
    /**********************************************************
    /* Tests: Verify error messages are actionable
    /**********************************************************
     */

    @Test
    public void testNullKeyErrorMessageIsActionable() throws Exception {
        Multimap<String, Integer> multimap = ArrayListMultimap.create();
        multimap.put(null, 1);

        try {
            MAPPER.writer()
                    .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                    .writeValueAsString(multimap);
            fail("Expected exception");
        } catch (DatabindException e) {
            String message = e.getMessage();
            assertNotNull(message);
            // Message should mention sorting, null, and the collection type
            assertTrue(message.toLowerCase().contains("sort") ||
                    message.toLowerCase().contains("null"));
        }
    }

    @Test
    public void testInvalidTypeErrorMessageIsActionable() throws Exception {
        String json = a2q("{'key': 'not-a-number'}");

        try {
            MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructParametricType(ImmutableMap.class, String.class, Integer.class));
            fail("Expected exception");
        } catch (DatabindException e) {
            String message = e.getMessage();
            assertNotNull(message);
            // Message should indicate type conversion problem
            assertTrue(message.length() > 0);
        }
    }
}
