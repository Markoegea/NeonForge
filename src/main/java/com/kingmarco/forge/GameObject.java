package com.kingmarco.forge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingmarco.components.Component;
import com.kingmarco.components.SpriteRenderer;
import com.kingmarco.components.Transform;
import com.kingmarco.deserializers.ComponentDeserializer;
import com.kingmarco.deserializers.GameObjectDeserializer;
import com.kingmarco.util.AssetPool;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible to storage the game object, their components and transform.
 * */
public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    private String name;
    private List<Component> components;
    public transient Transform transform;
    private transient boolean doSerialization = true;
    private transient boolean isDead = false;


    public GameObject(String name){
        this.name = name;
        this.components = new ArrayList<>();

        this.uid = ID_COUNTER++;
    }

    /**
     * Retrieves a component of the specified type from this GameObject.
     *
     * @param componentClass The class object representing the type of component to retrieve.
     * @return The component of the specified type if found, otherwise null.
     */
    public <T extends Component> T getComponent(Class<T> componentClass){
        for (Component c : this.components){
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException cce){
                    cce.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }

        return null;
    }

     /**
     * Removes a component of the specified type from this GameObject.
      *
     * @param componentClass The class object representing the type of component to remove.
     */
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i=0; i < this.components.size(); i++){
            Component c = this.components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                this.components.remove(i);
                return;
            }
        }
    }

    /**
    * Adds a component to this GameObject.
    *
    * @param c The component to add.
    */
    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    /**
     * Updates all components of this GameObject.
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    public void update(float dt){
        for (int i=0; i < components.size(); i++){
            components.get(i).update(dt);
        }
    }

    /**
     * Updates all components of this GameObject in editor mode.
     *
     * @param dt The time elapsed since the last update.
     */
    public void editorUpdate(float dt) {
        for (int i=0; i < components.size(); i++){
            components.get(i).editorUpdate(dt);
        }
    }

    /**
     * Starts all components of this GameObject.
     */
    public void start(){
        for (int i=0; i < components.size(); i++){
            components.get(i).start();
        }
    }

    /**
     * Displays ImGui components for this GameObject.
     */
    public void imgui(){
        for (Component c : components){
            if (ImGui.collapsingHeader(c.getClass().getSimpleName())){
                c.imgui();
            }
        }
    }

    /**
     * Destroys this GameObject and all its components.
     */
    public void destroy() {
        this.isDead = true;
        for (int i=0; i < components.size(); i++){
            components.get(i).destroy();
        }
    }

    /**
     * Copy the current GameObject attributes into another object.
     *
     * @return The copied GameObject.
     * */
    public GameObject copy(){
        // TODO: Come up with a clean solution
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);
        obj.generateUid();
        for (Component c : obj.getAllComponents()){
            c.generateId();
        }

        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null){
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }
        return obj;
    }

    /**
     * Returns whether this GameObject is marked as dead.
     *
     * @return true if the GameObject is dead, false otherwise.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Initializes the GameObject with the given maximum ID value.
     *
     * @param maxId The maximum ID value to be assigned to GameObjects.
     */
    public static void init(int maxId){
        ID_COUNTER = maxId;
    }

    /**
     * Returns the unique identifier of this GameObject.
     *
     * @return The unique identifier.
     */
    public int getUid() {
        return this.uid;
    }

    /**
     * Returns a list of all components attached to this GameObject.
     * @return A list of components.
     */
    public List<Component> getAllComponents() {
        return this.components;
    }

    /**
     Sets the serialization to false.
     */
    public void setNoSerialize() {
        this.doSerialization = false;
    }

    /**
     * Returns whether serialization should be performed for this GameObject.
     *
     * @return true if serialization is enabled, false otherwise.
     */
    public boolean doSerialization() {
        return this.doSerialization;
    }

    /**
     * Generates a unique identifier for this GameObject.
     */
    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    /**
     * Returns the name of this GameObject.
     *
     * @return The name of the GameObject.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this GameObject.
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of this GameObject, which is its name.
     * @return The name of the GameObject.
     */
    @Override
    public String toString() {
        return this.name;
    }
}
