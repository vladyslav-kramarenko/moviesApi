package moviesApi.service.impl;

import org.junit.Test;

import java.util.*;

import static moviesApi.service.impl.ReviewServiceImpl.getRatingGroup;
import static org.junit.Assert.assertEquals;

public class ReviewServiceImplTest {
    @Test
    public void testCreateRatingGroups() {
        List<Float[]> ratingGroupLimits = Arrays.asList(
                new Float[]{1f, 4.9f},
                new Float[]{5f, 7.9f},
                new Float[]{8f, 10f});
        Map<String, Long> expected = new HashMap<>();
        expected.put("1.0-4.9", 0L);
        expected.put("5.0-7.9", 0L);
        expected.put("8.0-10.0", 0L);
        Map<String, Long> actual = ReviewServiceImpl.createRatingGroups(ratingGroupLimits);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetRatingGroup() {
        List<Float[]> ratingGroupLimits = Arrays.asList(
                new Float[]{1f, 4.9f},
                new Float[]{5f, 7.9f},
                new Float[]{8f, 10f});

        double averageRating = 1;
        String expected = "1.0-4.9";
        String actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 3.5;
        expected = "1.0-4.9";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 4.9;
        expected = "1.0-4.9";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 5.0;
        expected = "5.0-7.9";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 6.0;
        expected = "5.0-7.9";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 7.9;
        expected = "5.0-7.9";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 8.0;
        expected = "8.0-10.0";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 9.0;
        expected = "8.0-10.0";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);

        averageRating = 10.0;
        expected = "8.0-10.0";
        actual = getRatingGroup(averageRating, ratingGroupLimits);
        assertEquals(expected, actual);
    }
}
