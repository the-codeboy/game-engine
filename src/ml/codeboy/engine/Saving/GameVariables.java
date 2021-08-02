package ml.codeboy.engine.Saving;

import ml.codeboy.engine.GameObject;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;

public class GameVariables implements Serializable {

    private static final long serialVersionUID = 375632757832634L;

    private HashMap<String, HashMap<String, Object>> variables = new HashMap<>();

    private transient String saveFilePath;

    private GameVariables(String path) {
        this.saveFilePath = path;
    }
//
//    public Object putVariable(String key, Object value) {
//        return variables.put(key, value);
//    }

    public void saveVariables(GameObject object) {
        if (object == null)
            throw new IllegalArgumentException("GameObject can not be null");
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(SaveClass.class)) {
            String classKey = clazz.getName();
            Class<?> current = clazz;
            while (current.getSuperclass() != null) {
                for (Field field : current.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(SaveValue.class)) {
                        try {
                            String key = field.getAnnotation(SaveValue.class).key();
                            if (key.isEmpty()) {
                                key = field.getName();
                            }
                            if (!variables.containsKey(classKey)) {
                                variables.put(classKey, new HashMap<>());
                            }
                            variables.get(classKey).put(key, field.get(object));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                current = current.getSuperclass();
            }
        }
    }

    public void loadVariables(GameObject object) {
        if (object == null)
            throw new IllegalArgumentException("GameObject can not be null");
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(SaveClass.class)) {
            String classKey = clazz.getName();
            Class<?> current = clazz;
            while (current.getSuperclass() != null) {
                for (Field field : current.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(SaveValue.class)) {
                        String key = field.getAnnotation(SaveValue.class).key();
                        if (key.isEmpty()) {
                            key = field.getName();
                        }
                        if (variables.containsKey(classKey) && variables.get(classKey).containsKey(key)) {
                            try {
                                field.set(object, variables.get(classKey).get(key));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                current = current.getSuperclass();
            }
        }
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public void clearVariables() {
        variables.clear();
    }

//    public boolean containsVariable(String key) {
//        return variables.containsKey(key);
//    }

//    public Integer getIntVariable(String key) {
//        Object obj = getVariable(key);
//        if (obj instanceof Integer)
//            return (Integer) obj;
//        return null;
//    }
//
//    public Boolean getBoolVariable(String key) {
//        Object obj = getVariable(key);
//        if (obj instanceof Boolean)
//            return (Boolean) obj;
//        return null;
//    }
//
//    public String getStringVariable(String key) {
//        Object obj = getVariable(key);
//        if (obj instanceof String)
//            return (String) obj;
//        return null;
//    }

    public static GameVariables loadFromFile(String path) {
        try {
            System.out.println("Loading game state");
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            GameVariables variables = (GameVariables) ois.readObject();
            variables.saveFilePath = path;
            return variables;
        } catch (IOException | ClassNotFoundException e) {
            return new GameVariables(path);
        }
    }

    public void save() {
        try {
            System.out.println("Saving game state");
            File file = new File(this.saveFilePath);
            if (!file.exists()) {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
