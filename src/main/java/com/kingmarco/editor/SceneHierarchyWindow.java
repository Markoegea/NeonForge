package com.kingmarco.editor;

import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

/**
 * A class responsible to represents a scene hierarchy window for managing {@link GameObject}.
 * */
public class SceneHierarchyWindow {

    private static String payLoadDragDropType = "SceneHierarchy";

    /**
     * Displays the ImGui scene hierarchy window.
     */
    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject obj : gameObjects){
            if (!obj.doSerialization()){
                continue;
            }

            boolean treeNodeOpen = doTreeNode(obj, index);

            if (treeNodeOpen){
                ImGui.treePop();
            }
            index++;
        }

        ImGui.end();
    }

    /**
     * Creates a tree node for the specified GameObject.
     *
     * @param obj   The GameObject to display.
     * @param index The index of the GameObject.
     * @return True if the tree node is open, false otherwise.
     */
    private boolean doTreeNode(GameObject obj, int index){
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                obj.getName(),
                ImGuiTreeNodeFlags.DefaultOpen |
                        ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow |
                        ImGuiTreeNodeFlags.SpanAvailWidth,
                obj.getName()
        );
        ImGui.popID();

        if (ImGui.beginDragDropSource()){
            ImGui.setDragDropPayload(payLoadDragDropType, obj);
            ImGui.text(obj.getName());
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()){
            Object payloadObj = ImGui.acceptDragDropPayload(payLoadDragDropType);
            if (payloadObj != null) {
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)){
                    GameObject playerGameObj = (GameObject) payloadObj;
                    System.out.println("Payload accepted '" + playerGameObj.getName() + "'");
                }
            }
            ImGui.endDragDropTarget();
        }
        return treeNodeOpen;
    }
}
