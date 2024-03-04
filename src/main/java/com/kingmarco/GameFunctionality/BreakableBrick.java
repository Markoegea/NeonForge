package com.kingmarco.GameFunctionality;

import com.kingmarco.util.AssetPool;

public class BreakableBrick extends Block{
    @Override
    void playerHit(PlayerController playerController) {
        gameObject.destroy();
    }
}
