package moviesApi.util;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 This class provides utility methods for the REST API.
 */
public class Utilities {
    /**
     Creates a list of sort orders based on the given parameters.
     @param sortParams an array of strings representing the sort order parameters
     @param allowedProperties an array of strings representing the allowed sort properties
     @return a list of Sort.Order objects
     @throws IllegalArgumentException if the sort order is invalid or the sort property is not allowed
     */
    public static List<Sort.Order> createSort(String[] sortParams, String[] allowedProperties) {
        List<Sort.Order> orders = new ArrayList<>();
        try {
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1].toUpperCase()) : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, sortField));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort order: " + sortParams[1]);
        }
        for (Sort.Order order : orders) {
            if (!Arrays.asList(allowedProperties).contains(order.getProperty())) {
                throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
            }
        }
        return orders;
    }
}
