package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.collect.*;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for reflection-based instantiation error handling in Guava deserializers.
 * These tests target exception handling blocks for:
 * - GuavaMultimapDeserializer (InvocationTargetException, IllegalArgumentException, IllegalAccessException)
 * - RangeMapDeserializer (InvocationTargetException, IllegalArgumentException, IllegalAccessException)
 */
public class ReflectionInstantiationErrorTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests: Valid instantiation paths for Multimap types
    /**********************************************************
     */

    @Test
    public void testArrayListMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertEquals(2, multimap.get("key1").size());
        assertTrue(multimap.containsEntry("key1", 1));
        assertTrue(multimap.containsEntry("key1", 2));
    }

    @Test
    public void testHashMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        HashMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<HashMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key1", 1));
        assertTrue(multimap.containsEntry("key2", 3));
    }

    @Test
    public void testLinkedHashMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        LinkedHashMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<LinkedHashMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key1", 1));
    }

    @Test
    public void testLinkedListMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        LinkedListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<LinkedListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key1", 1));
    }

    @Test
    public void testTreeMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        TreeMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<TreeMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key1", 1));
    }

    @Test
    public void testImmutableListMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        ImmutableListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ImmutableListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key1", 1));
    }

    @Test
    public void testImmutableSetMultimapDeserialization() throws Exception {
        String json = a2q("{'key1': [1, 2], 'key2': [3, 4]}");

        ImmutableSetMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ImmutableSetMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key1", 1));
    }

    /*
    /**********************************************************
    /* Tests: Valid instantiation paths for RangeMap types
    /**********************************************************
     */

    @Test
    public void testTreeRangeMapDeserialization() throws Exception {
        String json = a2q("{'[1..5]': 'value1', '[10..15]': 'value2'}");

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("value1", rangeMap.get(3));
        assertEquals("value2", rangeMap.get(12));
    }

    @Test
    public void testImmutableRangeMapDeserialization() throws Exception {
        String json = a2q("{'[1..5]': 'value1', '[10..15]': 'value2'}");

        ImmutableRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<ImmutableRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("value1", rangeMap.get(3));
        assertEquals("value2", rangeMap.get(12));
    }

    /*
    /**********************************************************
    /* Tests: Edge cases that might trigger instantiation issues
    /**********************************************************
     */

    @Test
    public void testEmptyMultimapDeserialization() throws Exception {
        String json = "{}";

        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.isEmpty());
    }

    @Test
    public void testEmptyRangeMapDeserialization() throws Exception {
        String json = "{}";

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertTrue(rangeMap.asMapOfRanges().isEmpty());
    }

    @Test
    public void testSingleEntryMultimapDeserialization() throws Exception {
        String json = a2q("{'key': [1]}");

        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertEquals(1, multimap.size());
        assertTrue(multimap.containsEntry("key", 1));
    }

    @Test
    public void testSingleEntryRangeMapDeserialization() throws Exception {
        String json = a2q("{'[1..5]': 'value'}");

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("value", rangeMap.get(3));
    }

    /*
    /**********************************************************
    /* Tests: Multimap with multiple values per key
    /**********************************************************
     */

    @Test
    public void testMultimapWithMultipleValuesPerKey() throws Exception {
        String json = a2q("{'key': [1, 2, 3, 4, 5]}");

        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertEquals(5, multimap.get("key").size());
    }

    @Test
    public void testMultimapWithMixedValueCounts() throws Exception {
        String json = a2q("{'key1': [1], 'key2': [2, 3], 'key3': [4, 5, 6]}");

        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertEquals(1, multimap.get("key1").size());
        assertEquals(2, multimap.get("key2").size());
        assertEquals(3, multimap.get("key3").size());
    }

    /*
    /**********************************************************
    /* Tests: Complex key types
    /**********************************************************
     */

    @Test
    public void testMultimapWithIntegerKeys() throws Exception {
        String json = a2q("{'1': ['a'], '2': ['b'], '3': ['c']}");

        ArrayListMultimap<Integer, String> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<Integer, String>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry(1, "a"));
        assertTrue(multimap.containsEntry(2, "b"));
        assertTrue(multimap.containsEntry(3, "c"));
    }

    @Test
    public void testRangeMapWithDoubleRanges() throws Exception {
        String json = a2q("{'[1.0..5.0]': 'value1', '[10.0..15.0]': 'value2'}");

        TreeRangeMap<Double, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Double, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("value1", rangeMap.get(3.0));
        assertEquals("value2", rangeMap.get(12.0));
    }

    /*
    /**********************************************************
    /* Tests: Various Range formats
    /**********************************************************
     */

    @Test
    public void testRangeMapWithClosedRanges() throws Exception {
        String json = a2q("{'[1..5]': 'value1'}");

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("value1", rangeMap.get(1));
        assertEquals("value1", rangeMap.get(5));
    }

    @Test
    public void testRangeMapWithOpenRanges() throws Exception {
        String json = a2q("{'(1..5)': 'value1'}");

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertNull(rangeMap.get(1)); // Open range doesn't include endpoints
        assertEquals("value1", rangeMap.get(3));
        assertNull(rangeMap.get(5)); // Open range doesn't include endpoints
    }

    @Test
    public void testRangeMapWithMixedRanges() throws Exception {
        String json = a2q("{'[1..5)': 'value1', '(10..15]': 'value2'}");

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("value1", rangeMap.get(1));
        assertNull(rangeMap.get(5));
        assertNull(rangeMap.get(10));
        assertEquals("value2", rangeMap.get(15));
    }

    /*
    /**********************************************************
    /* Tests: Round-trip serialization/deserialization
    /**********************************************************
     */

    @Test
    public void testMultimapRoundTrip() throws Exception {
        ArrayListMultimap<String, Integer> original = ArrayListMultimap.create();
        original.put("key1", 1);
        original.put("key1", 2);
        original.put("key2", 3);

        String json = MAPPER.writeValueAsString(original);
        ArrayListMultimap<String, Integer> deserialized = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertEquals(original, deserialized);
    }

    @Test
    public void testRangeMapRoundTrip() throws Exception {
        TreeRangeMap<Integer, String> original = TreeRangeMap.create();
        original.put(Range.closed(1, 5), "value1");
        original.put(Range.closed(10, 15), "value2");

        String json = MAPPER.writeValueAsString(original);
        TreeRangeMap<Integer, String> deserialized = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertEquals(original.asMapOfRanges(), deserialized.asMapOfRanges());
    }

    /*
    /**********************************************************
    /* Tests: Type conversion edge cases
    /**********************************************************
     */

    @Test
    public void testMultimapWithStringToIntegerConversion() throws Exception {
        String json = a2q("{'key': ['1', '2', '3']}");

        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertTrue(multimap.containsEntry("key", 1));
        assertTrue(multimap.containsEntry("key", 2));
        assertTrue(multimap.containsEntry("key", 3));
    }

    @Test
    public void testRangeMapWithValidRangeFormats() throws Exception {
        // Test various valid range formats
        String json = a2q("{'[1..5]': 'closed', '(10..15)': 'open', '[20..25)': 'half-open'}");

        TreeRangeMap<Integer, String> rangeMap = MAPPER.readValue(json,
                new TypeReference<TreeRangeMap<Integer, String>>() {});

        assertNotNull(rangeMap);
        assertEquals("closed", rangeMap.get(1));
        assertEquals("open", rangeMap.get(12));
        assertEquals("half-open", rangeMap.get(20));
    }

    /*
    /**********************************************************
    /* Tests: Large collections to test scalability
    /**********************************************************
     */

    @Test
    public void testLargeMultimapDeserialization() throws Exception {
        // Build a large multimap JSON
        StringBuilder jsonBuilder = new StringBuilder("{");
        for (int i = 0; i < 100; i++) {
            if (i > 0) jsonBuilder.append(",");
            jsonBuilder.append("'key").append(i).append("':[");
            for (int j = 0; j < 10; j++) {
                if (j > 0) jsonBuilder.append(",");
                jsonBuilder.append(j);
            }
            jsonBuilder.append("]");
        }
        jsonBuilder.append("}");

        String json = a2q(jsonBuilder.toString());
        ArrayListMultimap<String, Integer> multimap = MAPPER.readValue(json,
                new TypeReference<ArrayListMultimap<String, Integer>>() {});

        assertNotNull(multimap);
        assertEquals(1000, multimap.size()); // 100 keys * 10 values
    }
}
