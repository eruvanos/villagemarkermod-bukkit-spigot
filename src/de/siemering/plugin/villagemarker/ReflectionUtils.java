package de.siemering.plugin.villagemarker;

import net.minecraft.server.v1_8_R2.PersistentVillage;
import net.minecraft.server.v1_8_R2.WorldServer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

        public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                Class superClass = clazz.getSuperclass();
                if (superClass == null) {
                    throw e;
                } else {
                    return getField(superClass, fieldName);
                }
            }
        }
        public static void makeAccessible(Field field) {
            if (!Modifier.isPublic(field.getModifiers()) ||
                    !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
            {
                field.setAccessible(true);
            }
        }
}
