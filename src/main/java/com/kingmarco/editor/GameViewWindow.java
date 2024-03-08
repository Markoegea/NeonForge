package com.kingmarco.editor;

import com.kingmarco.forge.MouseListener;
import com.kingmarco.forge.Window;
import com.kingmarco.observers.EventSystem;
import com.kingmarco.observers.events.Event;
import com.kingmarco.observers.events.EventType;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

/**
 * A class responsible to create and render the game view in the ImGui UI.
 * */
public class GameViewWindow {

    private boolean isPlaying = false;
    private boolean windowIsHovered;

    /**
     * Set the ImGui items such as the play and stop buttons and the game view.
     * And set the game view position, size and distance to the {@link MouseListener} class.
     * */
    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();

        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)){
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)){
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.endMenuBar();

        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.imageButton(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        windowIsHovered = ImGui.isItemHovered();

        MouseListener.setGameViewportPos(new Vector2f(windowPos.x, windowPos.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
        MouseListener.setGameViewportDistance(new Vector2f(ImGui.getWindowPosX(), ImGui.getWindowPosY()));
        ImGui.end();
    }

    /**
     * Determines whether ImGui wants to capture the mouse.
     *
     * @return True if ImGui wants to capture the mouse, false otherwise.
     */
    public boolean getWantCaptureMouse(){
        return windowIsHovered;
    }

    /**
     * Calculates the largest available size for the viewport.
     *
     * @return The largest available size as an {@link ImVec2}.
     */
    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        return new ImVec2(windowSize.x, windowSize.y);
    }

    /**
     * Calculates the centered position for the viewport based on the specified aspect size.
     *
     * @param aspectSize The size of the aspect to center.
     * @return The centered position as an {@link ImVec2}.
     */
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }
}
