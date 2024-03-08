package com.kingmarco.editor;

import com.kingmarco.observers.EventSystem;
import com.kingmarco.observers.events.Event;
import com.kingmarco.observers.events.EventType;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

/**
 * A class responsible to create the menu bar, the buttons to save and load the level scene.
 * */
public class MenuBar {

    /**
     * Displays an ImGui window for settings, including the buttons to save and load the scene.
     */
    public void imgui() {

        ImGui.begin("Settings", ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoTitleBar);

        ImGui.beginMenuBar();

        if(ImGui.beginMenu("File")){
            if (ImGui.menuItem("Save", "Ctrl+S")){
                EventSystem.notify(null, new Event(EventType.SaveLevel));
            }

            if (ImGui.menuItem("Load", "Ctrl+O")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }

            ImGui.endMenu();
        }

        ImGui.endMenuBar();

        ImGui.end();
    }
}
