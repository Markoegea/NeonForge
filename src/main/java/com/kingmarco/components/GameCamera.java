package com.kingmarco.components;

import com.kingmarco.GameFunctionality.PlayerController;
import com.kingmarco.forge.Camera;
import com.kingmarco.forge.GameObject;
import com.kingmarco.forge.Window;
import org.joml.Vector4f;

/**
 * Class used to control the game camera
 * */
public class GameCamera extends Component{
    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;

    private Vector4f skyColor = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
    private Vector4f undergroundColor = new Vector4f(0, 0, 0, 1);

    public GameCamera(Camera gameCamera){
        this.gameCamera = gameCamera;
    }

    /**
     * Initializes the level editor state.
     * Sets up the camera, background color, and other necessary variables.
     */
    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.gameCamera.getClearColor().set(skyColor);
        undergroundYLevel = this.gameCamera.position.y -
                this.gameCamera.getProjectionSize().y - this.cameraBuffer;
    }

    /**
     * Updates the level editor state based on user input.
     *
     * @param dt The time elapsed since the last update (in seconds).
     */
    @Override
    public void update(float dt) {
        if (player == null){
            return;
        }
        if (!player.getComponent(PlayerController.class).hasWon()){
            gameCamera.position.x = player.transform.position.x - 3f;
            highestX = Math.max(highestX, gameCamera.position.x);
        }

        if (player.transform.position.y < -playerBuffer) {
            this.gameCamera.position.y = undergroundYLevel;
            this.gameCamera.getClearColor().set(undergroundColor);
        } else if (player.transform.position.y >= 0.0f) {
            this.gameCamera.position.y = 0.0f;
            this.gameCamera.getClearColor().set(skyColor);
        }
    }
}
