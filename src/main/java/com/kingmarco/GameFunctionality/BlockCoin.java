package com.kingmarco.GameFunctionality;

import com.kingmarco.components.Component;
import com.kingmarco.util.AssetPool;
import org.joml.Vector2f;

public class BlockCoin extends Component {
    private Vector2f topY;
    private float coinSpeed = 1.4f;

    @Override
    public void start() {
        topY = new Vector2f(this.gameObject.transform.position.y).add(0f, 0.5f);
        AssetPool.getSound("");
    }

    @Override
    public void update(float dt) {
        if (this.gameObject.transform.position.y < topY.y){
            this.gameObject.transform.position.y += dt * coinSpeed;
            this.gameObject.transform.scale.x -= (0.5f * dt) % -1.0f;
        } else {
            gameObject.destroy();
        }
    }
}
