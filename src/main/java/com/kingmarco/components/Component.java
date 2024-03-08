package com.kingmarco.components;

import com.kingmarco.editor.JImGui;
import com.kingmarco.forge.GameObject;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Abstract class used to define the behavior and necessary methods for a component
 * @author marko
 */
public abstract class Component {

    private static int ID_COUNTER = 0;
    private int uid = -1;
    public transient GameObject gameObject = null;

    /**
     * Called once at the beginning of the program
     */
    public void start() {

    }

    /**
     * Called through each frame in game mode of the program until exit
     */
    public void update(float dt){

    }

    /**
     * Called through each frame in editor mode of the program until exit
     */
    public void editorUpdate(float dt) {

    }

    /**
     * Called when a collision begins between this object and another object.
     *
     * @param collingObject The GameObject representing the other colliding object.
     * @param contact         The Contact information about the collision.
     * @param hitNormal       The surface normal vector at the point of collision.
     */
    public void beginCollision(GameObject collingObject, Contact contact, Vector2f hitNormal) {

    }

    /**
     * Called when a collision ends between this object and another object.
     *
     * @param collingObject The GameObject representing the other colliding object.
     * @param contact         The Contact information about the collision.
     * @param hitNormal       The surface normal vector at the point of collision.
     */
    public void endCollision(GameObject collingObject, Contact contact, Vector2f hitNormal) {

    }

    /**
     * Called before solving the physics between this object and another object.
     *
     * @param collingObject The GameObject representing the other colliding object.
     * @param contact         The Contact information about the collision.
     * @param hitNormal       The surface normal vector at the point of collision.
     */
    public void preSolve(GameObject collingObject, Contact contact, Vector2f hitNormal) {

    }

    /**
     * Called after solving the physics between this object and another object.
     *
     * @param collingObject The GameObject representing the other colliding object.
     * @param contact         The Contact information about the collision.
     * @param hitNormal       The surface normal vector at the point of collision.
     */
    public void postSolve(GameObject collingObject, Contact contact, Vector2f hitNormal) {

    }

    /**
     * Called to show the object attributes in the UI editor.
     */
    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field: fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient){
                    return;
                }
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                boolean isProtected = Modifier.isProtected(field.getModifiers());
                if (isPrivate || isProtected){
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int) value;
                    field.set(this, JImGui.dragInt(name, val));
                } else if (type == float.class){
                    float val = (float) value;
                    field.set(this, JImGui.dragFloat(name, val));
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    JImGui.drawVec2Control(name,val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum<?>)value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)){
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                } else if (type == String.class) {
                    field.set(this, JImGui.inputText(field.getName() + ": ", (String) value));
                }
                if (isPrivate || isProtected){
                    field.setAccessible(false);
                }
            }

        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an array of string values corresponding to the enum constants.
     *
     * @param enumType The class representing the enum type.
     * @param <T>      The enum type.
     * @return An array of string values.
     */
    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType){
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()){
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }

    /**
     * Finds the index of a specified string in an array.
     *
     * @param str The string to search for.
     * @param arr The array of strings.
     * @return The index of the string in the array, or -1 if not found.
     */
    private int indexOf(String str, String[] arr){
        for (int i=0; i < arr.length; i++){
            if (str.equals(arr[i])){
                return i;
            }
        }
        return -1;
    }

    /**
     * Destroys the object
     */
    public void destroy() {

    }

    /**
     * Generates a unique identifier (UID) for the object.
     * If the UID is already set, this method does nothing.
     */
    public void generateId() {
        if (this.uid == -1){
            this.uid = ID_COUNTER++;
        }
    }

    /**
     * Gets the unique identifier (UID) assigned to the object.
     *
     * @return The UID.
     */
    public int getUid() {
        return this.uid;
    }

    /**
     * Initializes the ID counter with the specified maximum ID value.
     *
     * @param maxId The maximum ID value.
     */
    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

}
