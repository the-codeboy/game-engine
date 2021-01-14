package ml.codeboy.engine;

import java.util.ArrayList;

public enum Layer {
    INVISIBLE,UI,TOP,MIDDLE,DEFAULT,BACK,BACKGROUND;

    public ArrayList<Sprite>getSprites(){
        return Sprite.getSpritesAt(this);
    }

    public boolean hasNext(){
        return ordinal()!=0;
    }

    public void clear(){
        getSprites().forEach(Sprite::destroy);
        getSprites().clear();
    }

    public Layer getNext(){
        return hasNext()?values()[ordinal()-1]:null;
    }

}
