package moviesApi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

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
}
