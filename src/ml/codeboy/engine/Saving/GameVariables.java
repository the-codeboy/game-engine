package ml.codeboy.engine.Saving;

import java.io.*;
import java.util.HashMap;

public class GameVariables implements Serializable {

    private static final long serialVersionUID = 375632757832634L;

    private HashMap<String, Object> variables = new HashMap<>();

    public Object addVariable(String key, Object value) {
        return variables.put(key, value);
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public void clearVariables() {
        variables.clear();
    }

    public boolean containsVariable(String key) {
        return variables.containsKey(key);
    }

    public Integer getIntVariable(String key){
        Object obj=getVariable(key);
        if(obj instanceof Integer)
            return (Integer) getVariable(key);
        return null;
    }

    public Boolean getBoolVariable(String key){
        Object obj=getVariable(key);
        if(obj instanceof Boolean)
            return (Boolean) getVariable(key);
        return null;
    }

    public String getStringVariable(String key){
        Object obj=getVariable(key);
        if(obj instanceof String)
            return (String) getVariable(key);
        return null;
    }

    public static GameVariables loadFromFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (GameVariables) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new GameVariables();
        }
    }

    public void saveToFile(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
