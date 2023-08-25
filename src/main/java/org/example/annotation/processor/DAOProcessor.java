package org.example.annotation.processor;

import org.example.annotation.DAO;
import org.example.controller.IController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAOProcessor implements Runnable {
    private static final String SET_FILE_METHOD_NAME = "setFile";

    private IController controller;

    private Map<String, Class<?>> registeredDAOMap = new HashMap<>();

    public DAOProcessor setController(IController controller) {
        this.controller = controller;
        return this;
    }

    public void registerDAO(String name, Class<?> daoClass) {
        this.registeredDAOMap.put(name, daoClass);
    }

    @Override
    public void run() {
        for (Field declaredField : this.controller.getClass().getDeclaredFields()) {
            var daoAnnotation = declaredField.getAnnotation(DAO.class);
            var value = daoAnnotation.value();
            var path = daoAnnotation.path();

            try {
                var daoClass = this.registeredDAOMap.get(value);
                var daoInstance = daoClass.getDeclaredConstructor().newInstance();


                if (path != null && !path.isBlank()) {
                    daoClass.getDeclaredMethod(SET_FILE_METHOD_NAME, String.class).invoke(daoInstance, path);
                }

                declaredField.setAccessible(true);
                declaredField.set(this.controller, daoInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
