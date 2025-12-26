package com.delivery.RouteX.util;

import org.springframework.stereotype.Component;

/**
 * Distance Calculator using Haversine Formula
 * Calculates distance between two GPS coordinates
 */
@Component
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two GPS points using Haversine formula
     *
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        // Convert latitude and longitude differences to radians
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Convert latitudes to radians
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate estimated delivery time based on distance
     * Assumes average speed of 40 km/h
     *
     * @param distanceKm Distance in kilometers
     * @return Estimated time in minutes
     */
    public int calculateEstimatedTime(double distanceKm) {
        double averageSpeedKmPerHour = 40.0;
        double hours = distanceKm / averageSpeedKmPerHour;
        return (int) Math.ceil(hours * 60); // Convert to minutes
    }

    /**
     * Format estimated time as human-readable string
     *
     * @param minutes Time in minutes
     * @return Formatted string like "45 minutes" or "1 hour 30 minutes"
     */
    public String formatEstimatedTime(int minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;

            if (remainingMinutes == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "");
            }

            return hours + " hour" + (hours > 1 ? "s" : "") + " " +
                    remainingMinutes + " minutes";
        }
    }

    /**
     * Check if a point is within a radius of another point
     *
     * @param centerLat Latitude of center point
     * @param centerLon Longitude of center point
     * @param pointLat Latitude of point to check
     * @param pointLon Longitude of point to check
     * @param radiusKm Radius in kilometers
     * @return true if point is within radius
     */
    public boolean isWithinRadius(
            double centerLat, double centerLon,
            double pointLat, double pointLon,
            double radiusKm
    ) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }
}
