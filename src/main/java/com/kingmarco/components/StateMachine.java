package com.kingmarco.components;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Class responsible to control the {@link AnimationState} and change between them.
 * */
public class StateMachine extends Component{

    /**
     * Class responsible to storage the stage name and trigger
     * */
    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger() {}

        public StateTrigger(String state, String trigger){
            this.state = state;
            this.trigger = trigger;
        }

        /**
         * Checks if this {@link StateTrigger} is equal to another object.
         *
         * @param obj The object to compare with.
         * @return True if the objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger) obj;
            return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
        }

        /**
         * Computes the hash code for this StateTrigger.
         *
         * @return The hash code.
         */
        @Override
        public int hashCode() {
            return Objects.hash(trigger, state);
        }
    }

    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";

    /**
     * Refresh the {@link AnimationState} textures
     * */
    public void refreshTextures() {
        for (AnimationState state : states) {
            state.refreshTextures();
        }
    }

    /**
     * Add a {@link StateTrigger} object as the key and the value is the name to another {@link AnimationState}
     *
     * @param from name of the initial animation.
     * @param to name of the final animation.
     * @param onTrigger name of the trigger.
     * */
    public void addStateTrigger(String from, String to, String onTrigger){
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    /**
     * Add a {@link AnimationState} to the list of available {@link AnimationState} states
     *
     * @param state {@link AnimationState} object to add.
     * */
    public void addState(AnimationState state){
        this.states.add(state);
    }


    /**
     * Sets the default state title for the animation.
     *
     * @param animationTitle The title of the state to set as default.
     */
    public void setDefaultStateTitle(String animationTitle) {
        for (AnimationState state : states){
            if (state.title.equals(animationTitle)){
                defaultStateTitle = animationTitle;
                if (currentState == null) {
                    currentState = state;
                    return;
                }
            }
        }
        System.out.println("Unable to find state '" + animationTitle + "' in set default state");
    }


    /**
     * Triggers a state transition based on the specified trigger.
     *
     * @param trigger The trigger to activate the state transition.
     */
    public void trigger(String trigger) {
        for (StateTrigger state : stateTransfers.keySet()) {
            if (state.state.equals(currentState.title) && state.trigger.equals(trigger)){
                if (stateTransfers.get(state) != null) {
                    int newStateIndex = -1;
                    int index = 0;
                    for (AnimationState s : states){
                        if (s.title.equals(stateTransfers.get(state))){
                            newStateIndex = index;
                            break;
                        }
                        index++;
                    }
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
       System.out.println("Unable to find trigger" + trigger + "'");
    }

    /**
     * Set the current state animation name.
     */
    @Override
    public void start() {
        for (AnimationState state : states) {
            if (state.title.equals(defaultStateTitle)){
                currentState = state;
                break;
            }
        }
    }

    /**
     * Update the currentState frame and set it to the game object.
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    /**
     * Update the currentState frame and set it to the game object in editor mode.
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    /**
     * Display in the ImGui the {@link AnimationState} settings to change them.
     * */
    @Override
    public void imgui() {
        int index = 0;
        for (AnimationState state : states){
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);
            state.title = title.get();

            ImBoolean doesLoop = new ImBoolean(state.doesLoop);
            ImGui.checkbox("Does Loop? ", doesLoop);
            state.setLoop(doesLoop.get());
            for (Frame frame : state.animationFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame (" + index + ") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }
}
