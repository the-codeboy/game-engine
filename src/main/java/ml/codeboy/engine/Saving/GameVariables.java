package ml.codeboy.engine.Saving;

import ml.codeboy.engine.GameObject;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class GameVariables implements Serializable {

    private static final long serialVersionUID = 375632757832634L;

    private final HashMap<Object, HashMap<String, Object>> variables = new HashMap<>();

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
        Class<?> current = object.getClass();
        if (current.isAnnotationPresent(SaveClass.class)) {
            Object classKey = getSaveKey(object, current);
            do {
                boolean saveAll = current.isAnnotationPresent(SaveAll.class);
                for (Field field : current.getDeclaredFields()) {
                    field.setAccessible(true);
                    String key = null;
                    if (field.isAnnotationPresent(SaveValue.class)) {
                        key = field.getAnnotation(SaveValue.class).key();
                    } else if ((saveAll && !field.isAnnotationPresent(ExcludeValue.class))) {
                        key = field.getName();
                    }
                    if (key != null) {
                        try {
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
            } while (current.getSuperclass() != null);
        }
    }

    public void loadVariables(GameObject object) {
        if (object == null)
            throw new IllegalArgumentException("GameObject can not be null");
        Class<?> current = object.getClass();
        if (current.isAnnotationPresent(SaveClass.class)) {
            Object classKey = getSaveKey(object, current);
            do {
                boolean saveAll = current.isAnnotationPresent(SaveAll.class);
                for (Field field : current.getDeclaredFields()) {
                    field.setAccessible(true);
                    String key = null;
                    if (field.isAnnotationPresent(SaveValue.class)) {
                        key = field.getAnnotation(SaveValue.class).key();
                    } else if ((saveAll && !field.isAnnotationPresent(ExcludeValue.class))) {
                        key = field.getName();
                    }
                    if (key != null && variables.containsKey(classKey) && variables.get(classKey).containsKey(key)) {
                        if (key.isEmpty()) {
                            key = field.getName();
                        }
                        try {
                            field.set(object, variables.get(classKey).get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                current = current.getSuperclass();
            } while (current.getSuperclass() != null);
        }
    }

    private Object getSaveKey(GameObject object, Class<?> current) {
        Object classKey = current.getName();
        Class<?> temp = current;
        while (temp != null) {
            for (Field field : temp.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(SaveKey.class)) {
                    try {
                        Object possibleKey = field.get(object);
                        if (possibleKey != null) {
                            classKey = possibleKey;
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (Method method : temp.getDeclaredMethods()) {
                method.setAccessible(true);
                if (method.getParameterCount() == 0 && method.isAnnotationPresent(SaveKey.class)) {
                    try {
                        Object possibleKey = method.invoke(object);
                        if (possibleKey != null) {
                            classKey = possibleKey;
                            break;
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            temp = temp.getSuperclass();
        }
        return classKey;
    }

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
