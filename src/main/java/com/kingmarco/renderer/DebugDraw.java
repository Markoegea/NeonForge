package com.kingmarco.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The DebugDraw class handles the drawing of debug lines.
 *
 * This class maintains a list of DrawLines instances and provides methods to begin a frame, draw the lines, add and remove DrawLines instances.
 */
public class DebugDraw {
    private static List<DrawLines> drawLines = new ArrayList<>();

    /**
     * Begins a frame for each DrawLines instance in the list.
     *
     * This method iterates over the DrawLines instances in the list and calls their beginFrame method.
     */
    public static void beginFrame() {
        if (drawLines.size() <= 0) return;
        for (int i=0; i < drawLines.size(); i++){
            drawLines.get(i).beginFrame();
        }
    }

    /**
     * Draws each DrawLines instance in the list.
     *
     * This method iterates over the DrawLines instances in the list and calls their draw method.
     */
    public static void draw() {
        if (drawLines.size() <= 0) return;
        for (DrawLines lines : drawLines){
            lines.draw();
        }
    }

    /**
     * Adds a DrawLines instance to the list.
     *
     * This method adds a DrawLines instance to the list and sorts the list.
     *
     * @param drawLine The DrawLines instance to be added.
     */
    public static void addDrawLines(DrawLines drawLine){
        DebugDraw.drawLines.add(drawLine);
        Collections.sort(DebugDraw.drawLines);
    }

    /**
     * Removes a DrawLines instance from the list.
     *
     * This method removes a DrawLines instance from the list and sorts the list.
     *
     * @param drawLine The DrawLines instance to be removed.
     */
    public static void removeDrawLines(DrawLines drawLine){
        DebugDraw.drawLines.remove(drawLine);
        Collections.sort(DebugDraw.drawLines);
    }

    /**
     * Removes a DrawLines instance from the list by index.
     *
     * This method removes a DrawLines instance from the list at the specified index and sorts the list.
     *
     * @param index The index of the DrawLines instance to be removed.
     */
    public static void removeDrawLines(int index){
        DebugDraw.drawLines.remove(index);
        Collections.sort(DebugDraw.drawLines);
    }
}
