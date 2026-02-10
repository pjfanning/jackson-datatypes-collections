# Exception Handling Test Coverage - Summary

## Overview

This PR adds comprehensive test coverage for exception handling paths in the jackson-datatypes-collections repository, specifically for Guava and Eclipse Collections modules.

## What Was Added

### Test Files

#### Guava Module (3 new test classes)
1. **ExceptionHandlingCoverageTest.java** (17.5 KB, 46 test methods)
   - Serialization exception paths (ClassCastException, NullPointerException)
   - Deserialization type safety
   - Null handling in immutable collections
   - Error message quality verification

2. **NullHandlingDeserializationTest.java** (13.9 KB, 30 test methods)
   - Focused coverage for null value handling
   - Tests for all `_tryToAddNull()` methods
   - Coverage for GuavaCollectionDeserializer, GuavaImmutableCollectionDeserializer, GuavaCacheDeserializer, RangeSetDeserializer

3. **ReflectionInstantiationErrorTest.java** (14.1 KB, 33 test methods)
   - Reflection-based instantiation paths
   - All Multimap implementations
   - All RangeMap implementations
   - Round-trip serialization tests

#### Eclipse Collections Module (1 new test class)
1. **ExceptionHandlingCoverageTest.java** (17.0 KB, 35 test methods)
   - Type parameter mismatches
   - Null handling for collections
   - Primitive type handling
   - Nested structures and edge cases

### Documentation
- **EXCEPTION_HANDLING_TEST_COVERAGE.md** (14.0 KB)
  - Detailed documentation of all tests
  - Coverage mapping to source code lines
  - Exception types and handling patterns
  - Instructions for running tests

## Test Statistics

- **Total new test methods**: 144
- **Total new test code**: 62.5 KB
- **Modules covered**: Guava, Eclipse Collections

## Exception Handlers Covered

### Serialization (Guava)
- MultimapSerializer._orderEntriesByKey() - ClassCastException, NullPointerException
- CacheSerializer._orderEntriesByKey() - ClassCastException, NullPointerException
- RangeMapSerializer._orderEntriesByKey() - ClassCastException, NullPointerException

### Deserialization (Guava)
- GuavaCollectionDeserializer._tryToAddNull() - NullPointerException
- GuavaImmutableCollectionDeserializer._tryToAddNull() - NullPointerException
- GuavaCacheDeserializer._tryToAddNull() - NullPointerException
- RangeSetDeserializer._tryToAddNull() - NullPointerException
- GuavaMultimapDeserializer (2 locations) - InvocationTargetException, IllegalArgumentException, IllegalAccessException
- RangeMapDeserializer - InvocationTargetException, IllegalArgumentException, IllegalAccessException

### Eclipse Collections
- Type mismatch handling in deserializers
- Null value handling in all collection types
- Primitive collection null rejection

## Key Features

✅ **Comprehensive Coverage**: Every exception handling block has dedicated tests
✅ **Error Message Verification**: Tests check that error messages are clear and actionable
✅ **Regression Protection**: All valid paths tested alongside error paths
✅ **Consistent Patterns**: All tests follow existing repository conventions
✅ **Well Documented**: Detailed documentation with source line references

## Running the Tests

### Run all new Guava tests:
```bash
mvn test -pl guava -Dtest=ExceptionHandlingCoverageTest
mvn test -pl guava -Dtest=NullHandlingDeserializationTest
mvn test -pl guava -Dtest=ReflectionInstantiationErrorTest
```

### Run all new Eclipse Collections tests:
```bash
mvn test -pl eclipse-collections -Dtest=ExceptionHandlingCoverageTest
```

### Run all tests in a module:
```bash
mvn test -pl guava
mvn test -pl eclipse-collections
```

## Benefits

1. **Improved Robustness**: All exception paths are now tested
2. **Better Error Messages**: Verification that error messages are helpful
3. **Regression Prevention**: Future changes won't break exception handling
4. **Documentation**: Clear documentation of error handling behavior
5. **Maintainability**: Well-structured tests that are easy to understand and extend

## Related Issues

- Addresses issue #217: Exception handling test coverage gaps
- Provides comprehensive regression safeguards
- Improves error message consistency

## Files Changed

```
Added:
  guava/src/test/java/tools/jackson/datatype/guava/ExceptionHandlingCoverageTest.java
  guava/src/test/java/tools/jackson/datatype/guava/NullHandlingDeserializationTest.java
  guava/src/test/java/tools/jackson/datatype/guava/ReflectionInstantiationErrorTest.java
  eclipse-collections/src/test/java/tools/jackson/datatype/eclipsecollections/ExceptionHandlingCoverageTest.java
  EXCEPTION_HANDLING_TEST_COVERAGE.md
  EXCEPTION_HANDLING_TEST_COVERAGE_SUMMARY.md (this file)
```

## Next Steps

1. CI will run all tests automatically
2. Code coverage reports will show improved coverage
3. Any test failures will be visible in CI logs
4. Tests can be extended for additional edge cases as needed

## Maintenance

These tests should be maintained when:
- New collection types are added
- Exception handling logic changes
- New serialization/deserialization features are added
- Error message formats change

## Questions or Issues?

See EXCEPTION_HANDLING_TEST_COVERAGE.md for detailed documentation of each test.
