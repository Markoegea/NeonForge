package com.kingmarco.components;

/**
 * Class that storage a font
 * */
public class FontRenderer extends Component {

    /**
     * This method is called when the scene starts.
     */
    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null){
            System.out.println("Found Font Renderer!");
        }
    }

}
