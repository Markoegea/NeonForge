package com.kingmarco.components;

import com.kingmarco.forge.Camera;
import com.kingmarco.forge.Window;
import com.kingmarco.renderer.DebugDraw;
import com.kingmarco.renderer.DrawLines;
import com.kingmarco.renderer.Line2D;
import com.kingmarco.util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Class responsible to create the grid lines in the editor mode
 * */
public class GridLines extends Component{

    private final DrawLines drawLines = new DrawLines(1);

    /**
     * Initialize the grid lines and add them to be rendered
     * */
    @Override
    public void start() {
        drawLines.start();
        DebugDraw.addDrawLines(drawLines);
    }

    /**
     * Update the lines width, height and number, based in the editor camera
     *
     * @param dt The time elapsed since the last update (in seconds).
     * */
    @Override
    public void editorUpdate(float dt) {
        Camera camera = Window.getScene().camera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((int)Math.floor(cameraPos.x / Settings.GRID_WIDTH)) * Settings.GRID_WIDTH;
        float firstY = ((int)Math.floor(cameraPos.y / Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;

        int numVertLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHorLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        float width = (projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 2;
        float height = (projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 2;

        int maxLines = Math.max(numVertLines, numHorLines);
        Vector3f color = new Vector3f(0.36f, 0.24f, 0.26f);
        for (int i=0; i < maxLines; i++){
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if( i < numVertLines){
                drawLines.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY+height), color, 1);
            }

            if (i < numHorLines){
                drawLines.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX+width, y), color, 1);
            }
        }
    }
}
