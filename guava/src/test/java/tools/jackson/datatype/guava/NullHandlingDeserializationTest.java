package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests for null handling in Guava collection deserializers.
 * These tests specifically target the exception handling blocks in:
 * - GuavaCollectionDeserializer._tryToAddNull
 * - GuavaImmutableCollectionDeserializer._tryToAddNull
 * - GuavaCacheDeserializer._tryToAddNull
 * - RangeSetDeserializer._tryToAddNull
 */
public class NullHandlingDeserializationTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests: GuavaCollectionDeserializer null handling
    /**********************************************************
     */

    @Test
    public void testTreeSetWithNullValue() throws Exception {
        // TreeSet is a Guava collection that doesn't accept null
        String json = a2q("['a', null, 'c']");

        try {
            TreeMultiset<String> set = MAPPER.readValue(json,
                    new TypeReference<TreeMultiset<String>>() {});
            fail("Expected exception for null in TreeMultiset, got: " + set);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testHashMultisetWithNullValue() throws Exception {
        // HashMultiset also doesn't accept null
        String json = a2q("['a', null, 'c']");

        try {
            HashMultiset<String> set = MAPPER.readValue(json,
                    new TypeReference<HashMultiset<String>>() {});
            fail("Expected exception for null in HashMultiset, got: " + set);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testLinkedHashMultisetWithNullValue() throws Exception {
        String json = a2q("['a', null, 'c']");

        try {
            LinkedHashMultiset<String> set = MAPPER.readValue(json,
                    new TypeReference<LinkedHashMultiset<String>>() {});
            fail("Expected exception for null in LinkedHashMultiset, got: " + set);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    /*
    /**********************************************************
    /* Tests: GuavaImmutableCollectionDeserializer null handling
    /**********************************************************
     */

    @Test
    public void testImmutableListBuilderWithNull() throws Exception {
        // ImmutableList.Builder doesn't accept null
        String json = a2q("['a', null, 'c']");

        try {
            ImmutableList<String> list = MAPPER.readValue(json,
                    new TypeReference<ImmutableList<String>>() {});
            fail("Expected exception for null in ImmutableList, got: " + list);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testImmutableSetBuilderWithNull() throws Exception {
        // ImmutableSet.Builder doesn't accept null
        String json = a2q("['a', null, 'c']");

        try {
            ImmutableSet<String> set = MAPPER.readValue(json,
                    new TypeReference<ImmutableSet<String>>() {});
            fail("Expected exception for null in ImmutableSet, got: " + set);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testImmutableSortedSetBuilderWithNull() throws Exception {
        // ImmutableSortedSet.Builder doesn't accept null
        String json = a2q("['a', null, 'c']");

        try {
            ImmutableSortedSet<String> set = MAPPER.readValue(json,
                    new TypeReference<ImmutableSortedSet<String>>() {});
            fail("Expected exception for null in ImmutableSortedSet, got: " + set);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testImmutableMultisetBuilderWithNull() throws Exception {
        // ImmutableMultiset.Builder doesn't accept null
        String json = a2q("['a', null, 'c']");

        try {
            ImmutableMultiset<String> set = MAPPER.readValue(json,
                    new TypeReference<ImmutableMultiset<String>>() {});
            fail("Expected exception for null in ImmutableMultiset, got: " + set);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    /*
    /**********************************************************
    /* Tests: GuavaCacheDeserializer null handling
    /**********************************************************
     */

    @Test
    public void testCacheDeserializeWithNullValue() throws Exception {
        // Guava Cache doesn't accept null values
        String json = a2q("{'key1': 'value1', 'key2': null, 'key3': 'value3'}");

        try {
            Cache<String, String> cache = MAPPER.readValue(json,
                    new TypeReference<Cache<String, String>>() {});
            fail("Expected exception for null value in Cache, got: " + cache);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testCacheDeserializeWithOnlyNullValue() throws Exception {
        String json = a2q("{'key': null}");

        try {
            Cache<String, String> cache = MAPPER.readValue(json,
                    new TypeReference<Cache<String, String>>() {});
            fail("Expected exception for null value in Cache, got: " + cache);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testCacheDeserializeWithMultipleNullValues() throws Exception {
        String json = a2q("{'key1': null, 'key2': null}");

        try {
            Cache<String, String> cache = MAPPER.readValue(json,
                    new TypeReference<Cache<String, String>>() {});
            fail("Expected exception for null values in Cache, got: " + cache);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    /*
    /**********************************************************
    /* Tests: RangeSetDeserializer null handling
    /**********************************************************
     */

    @Test
    public void testRangeSetWithNullElement() throws Exception {
        // RangeSet doesn't accept null
        String json = a2q("['[1..5]', null]");

        try {
            RangeSet<Integer> rangeSet = MAPPER.readValue(json,
                    new TypeReference<RangeSet<Integer>>() {});
            fail("Expected exception for null in RangeSet, got: " + rangeSet);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testImmutableRangeSetWithNullElement() throws Exception {
        String json = a2q("['[1..5]', null, '[10..15]']");

        try {
            ImmutableRangeSet<Integer> rangeSet = MAPPER.readValue(json,
                    new TypeReference<ImmutableRangeSet<Integer>>() {});
            fail("Expected exception for null in ImmutableRangeSet, got: " + rangeSet);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testTreeRangeSetWithNullElement() throws Exception {
        String json = a2q("['[1..5]', null]");

        try {
            TreeRangeSet<Integer> rangeSet = MAPPER.readValue(json,
                    new TypeReference<TreeRangeSet<Integer>>() {});
            fail("Expected exception for null in TreeRangeSet, got: " + rangeSet);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    /*
    /**********************************************************
    /* Tests: ImmutableMap null handling
    /**********************************************************
     */

    @Test
    public void testImmutableMapWithNullKey() throws Exception {
        // ImmutableMap doesn't accept null keys
        String json = a2q("{null: 'value', 'key2': 'value2'}");

        try {
            ImmutableMap<String, String> map = MAPPER.readValue(json,
                    new TypeReference<ImmutableMap<String, String>>() {});
            fail("Expected exception for null key in ImmutableMap, got: " + map);
        } catch (Exception e) {
            // Should fail with either MismatchedInputException or DatabindException
            assertNotNull(e);
        }
    }

    @Test
    public void testImmutableMapWithNullValue() throws Exception {
        // ImmutableMap doesn't accept null values
        String json = a2q("{'key1': 'value1', 'key2': null}");

        try {
            ImmutableMap<String, String> map = MAPPER.readValue(json,
                    new TypeReference<ImmutableMap<String, String>>() {});
            fail("Expected exception for null value in ImmutableMap, got: " + map);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    @Test
    public void testImmutableBiMapWithNullValue() throws Exception {
        // ImmutableBiMap doesn't accept null values
        String json = a2q("{'key1': 'value1', 'key2': null}");

        try {
            ImmutableBiMap<String, String> map = MAPPER.readValue(json,
                    new TypeReference<ImmutableBiMap<String, String>>() {});
            fail("Expected exception for null value in ImmutableBiMap, got: " + map);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept", "null");
        }
    }

    /*
    /**********************************************************
    /* Tests: Verify error messages contain helpful information
    /**********************************************************
     */

    @Test
    public void testNullValueErrorMessageContainsCollectionType() throws Exception {
        String json = a2q("['a', null, 'c']");

        try {
            ImmutableList<String> list = MAPPER.readValue(json,
                    new TypeReference<ImmutableList<String>>() {});
            fail("Expected exception");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertNotNull(message);
            // Message should mention collection type and null
            assertTrue(message.toLowerCase().contains("collection") ||
                    message.toLowerCase().contains("list") ||
                    message.toLowerCase().contains("immutable"));
            assertTrue(message.toLowerCase().contains("null"));
        }
    }

    @Test
    public void testCacheNullValueErrorMessageIsHelpful() throws Exception {
        String json = a2q("{'key': null}");

        try {
            Cache<String, String> cache = MAPPER.readValue(json,
                    new TypeReference<Cache<String, String>>() {});
            fail("Expected exception");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertNotNull(message);
            // Message should mention Cache and null
            assertTrue(message.toLowerCase().contains("cache"));
            assertTrue(message.toLowerCase().contains("null"));
        }
    }

    /*
    /**********************************************************
    /* Tests: Valid deserialization without nulls
    /**********************************************************
     */

    @Test
    public void testImmutableListWithoutNulls() throws Exception {
        String json = a2q("['a', 'b', 'c']");

        ImmutableList<String> list = MAPPER.readValue(json,
                new TypeReference<ImmutableList<String>>() {});

        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void testCacheWithoutNulls() throws Exception {
        String json = a2q("{'key1': 'value1', 'key2': 'value2'}");

        Cache<String, String> cache = MAPPER.readValue(json,
                new TypeReference<Cache<String, String>>() {});

        assertNotNull(cache);
        assertEquals("value1", cache.getIfPresent("key1"));
        assertEquals("value2", cache.getIfPresent("key2"));
    }

    @Test
    public void testRangeSetWithoutNulls() throws Exception {
        String json = a2q("['[1..5]', '[10..15]']");

        RangeSet<Integer> rangeSet = MAPPER.readValue(json,
                new TypeReference<RangeSet<Integer>>() {});

        assertNotNull(rangeSet);
        assertTrue(rangeSet.contains(3));
        assertTrue(rangeSet.contains(12));
        assertFalse(rangeSet.contains(7));
    }

    @Test
    public void testImmutableMapWithoutNulls() throws Exception {
        String json = a2q("{'key1': 'value1', 'key2': 'value2'}");

        ImmutableMap<String, String> map = MAPPER.readValue(json,
                new TypeReference<ImmutableMap<String, String>>() {});

        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }
}
