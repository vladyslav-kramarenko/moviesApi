package moviesApi.util;

import static moviesApi.util.Utilities.mapsToListOfSingletonMaps;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.domain.Sort;

public class UtilitiesTest {

    @Test
    public void testCreateSortWithValidParams() {
        String[] sortParams = {"title", "desc"};
        String[] allowedProperties = {"title", "genre", "releaseYear"};
        List<Sort.Order> result = Utilities.createSort(sortParams, allowedProperties);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getProperty(), "title");
        assertEquals(result.get(0).getDirection(), Sort.Direction.DESC);
    }

    @Test
    public void testCreateSortWithInvalidOrder() {
        String[] sortParams = {"title", "invalid_order"};
        String[] allowedProperties = {"title", "genre", "releaseYear"};
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> Utilities.createSort(sortParams, allowedProperties));
        assertEquals("Invalid sort order: invalid_order", exception.getMessage());
    }

    @Test
    public void testCreateSortWithInvalidProperty() {
        String[] sortParams = {"invalid_property", "desc"};
        String[] allowedProperties = {"title", "genre", "releaseYear"};
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> Utilities.createSort(sortParams, allowedProperties));
        assertEquals("Invalid sort property: invalid_property", exception.getMessage());
    }

    @Test
    public void testMapsToListOfSingletonMaps() {
        // Prepare input map
        Map<String, Integer> inputMap = new HashMap<>();
        inputMap.put("A", 1);
        inputMap.put("B", 2);
        inputMap.put("C", 3);

        // Call the method and get the result
        List<Map<String, Integer>> listOfSingletonMaps = mapsToListOfSingletonMaps(inputMap);

        // Assert the size of the resulting list
        assertEquals(inputMap.size(), listOfSingletonMaps.size());

        // Iterate through the input map and assert each key-value pair is in the list of singleton maps
        for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
            Map<String, Integer> singletonMap = Collections.singletonMap(entry.getKey(), entry.getValue());
            assertTrue(listOfSingletonMaps.contains(singletonMap),
                    "Expected list of singleton maps to contain map: " + singletonMap);
        }
    }
}
