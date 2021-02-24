package ml.codeboy.engine.animation;

import java.util.HashMap;
import java.util.Objects;

public class CharacterAnimator {
    
    public CharacterAnimator(){
        
    }

    public void addAnimation(Animation animation,State state){
        generalAnimations.put(state,animation);
    }

    public void addAnimation(Animation animation,State state,Direction direction){
        animations.put(new CharacterState(state, direction),animation);
        if(!generalAnimations.containsKey(state))
            generalAnimations.put(state,animation);
    }

    private final HashMap<CharacterState,Animation>animations=new HashMap<>();
    private final HashMap<State,Animation> generalAnimations =new HashMap<>();

    private static final Animation none=new Animation("error.png");

    public Animation getAnimation(State state,Direction direction){
        return animations.getOrDefault(new CharacterState(state, direction), generalAnimations.getOrDefault(state,none)).clone();
    }

    private static class CharacterState{
        State state;
        Direction direction;

        public CharacterState(State state, Direction direction) {
            this.state = state;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CharacterState that = (CharacterState) o;
            return state == that.state && direction == that.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, direction);
        }
    }

}
