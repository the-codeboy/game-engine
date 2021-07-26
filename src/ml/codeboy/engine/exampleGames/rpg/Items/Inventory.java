package ml.codeboy.engine.exampleGames.rpg.Items;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.ButtonGroup;
import ml.codeboy.engine.UI.UIText;
import ml.codeboy.engine.exampleGames.rpg.Dialogs.Interaction;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.characters.Character;
import ml.codeboy.engine.exampleGames.rpg.Rpg;

public class Inventory extends Interaction {

    private final ItemStack[]items;
    private Character owner;

    public Inventory() {
        this(9);
    }

    public Inventory(int size) {
        this(size,null);
    }

    public Inventory(int size,Character owner) {
        items=new ItemStack[size];
        this.owner=owner;
        rpg= (Rpg) Rpg.get();
        rpg.doNextTick(()->{
            text=new UIText("",(int)(rpg.getWidth()*0.5),(int) (rpg.getHeight()*0.8),(int) (rpg.getWidth()*0.8),(int) (rpg.getHeight()*0.2));
            group=new ButtonGroup((int)(rpg.getWidth()*0.5),(int)(rpg.getHeight()*0.5),(int)(rpg.getWidth()*0.8),(int)(rpg.getHeight()*0.6));
            text.setVisible(false);
            group.setVisible(false);
        });
    }

    public ItemStack[] getItems() {
        return items;
    }
    
    public boolean addItem(ItemStack stack){
        for (int i = 0; i < getItems().length; i++) {
            ItemStack other = getItems()[i];
            if(other==null) {
                getItems()[i] = stack;
                update();
                return true;
            }
            else if(other.getType()==stack.getType()&&other.getMaxCount()>other.getCount()){
                int amountToTransfer=other.getMaxCount()-other.getCount();
                stack.removeItems(amountToTransfer);
                other.addItems(amountToTransfer);
                if(stack.getCount()==0)
                    break;
            }
        }
        update();
        return stack.getCount()==0;
    }

    public Character getOwner() {
        return owner;
    }

    private ButtonGroup group;
    private UIText text;

    @Override
    public void open(Rpg rpg) {
        super.open(rpg);
        text.setVisible(true);
        group.setVisible(true);
        text.setText(owner==null?"Inventory":owner.getName()+"'s Inventory");
    }

    private void update(){
        if(active){
            group.clear();
            for (int i = 0; i < getItems().length; i++) {
                group.addButton("number: "+(getItems()[i]==null?0:getItems()[i].getCount()),()->{});
            }
        }
    }

    public void destroy(){
        text.destroy();
    }

    @Override
    public void close() {
        super.close();
        text.setVisible(false);
        group.setVisible(false);
    }
    
}
