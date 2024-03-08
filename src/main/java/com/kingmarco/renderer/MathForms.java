package com.kingmarco.renderer;

import org.joml.Vector2f;

/**
 * The MathForms class provides mathematical operations for game development.
 */
public class MathForms {

    /**
     * Rotates a point around a center by a specified angle.
     *
     * This method converts the angle from degrees to radians, calculates the sine and cosine of the angle, translates the point to the origin (center of rotation), calculates the rotated coordinates, and updates the point with the rotated coordinates.
     *
     * @param vert The point to be rotated.
     * @param angleDeg The angle of rotation in degrees.
     * @param center The center of rotation.
     */
    public static void rotate(Vector2f vert, float angleDeg, Vector2f center) {
        float angle = (float) Math.toRadians(angleDeg);

        // Calculate sine and cosine of the angle
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        // Translate the point to the origin (center of rotation)
        float translatedX = vert.x - center.x;
        float translatedY = vert.y - center.y;

        // Calculate the rotated coordinates
        vert.x = (((translatedX * cos) - (translatedY * sin)) + center.x);
        vert.y = (((translatedX * sin)  + (translatedY * cos)) + center.y);
    }
}
