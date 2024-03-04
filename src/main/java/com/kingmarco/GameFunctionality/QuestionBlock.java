package com.kingmarco.GameFunctionality;

import com.kingmarco.components.StateMachine;

public class QuestionBlock extends Block{

    private enum  BlockType {
        Coin,
        PowerUp,
        Invincibility
    }

    public BlockType blockType = BlockType.Coin;

    @Override
    void playerHit(PlayerController playerController) {
        switch (blockType) {
            case Coin:
                doCoin(playerController);
                break;
            case PowerUp:
                doPowerUp(playerController);
                break;
            case Invincibility:
                doInvincibility(playerController);
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("SetInactive");
            this.setActive(false);
        }


    }

    private void doInvincibility(PlayerController playerController) {
    }

    private void doPowerUp(PlayerController playerController) {
        switch (playerController.getPlayerState()){
            case Small:
                spawnMushroom();
                break;
            default:
                spawnFlower();
                break;
        }
    }

    private void doCoin(PlayerController playerController) {
        /*GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(this.gameObject.transform.position);
        coin.transform.position.y += 0.25;
        Window.getScene().addGameObjectToScene(coin);*/
    }

    private void spawnMushroom() {
        /*GameObject mushroom = Prefabs.generateMushroom();
        mushroom.transform.position.set(gameObject.transform.position);
        mushroom.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(mushroom);*/
    }

    private void spawnFlower() {
        /*GameObject flower = Prefabs.generateMushroom();
        flower.transform.position.set(gameObject.transform.position);
        flower.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(flower);*/
    }
}
