package br.sistema_recomendacoes.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
 * Aplica o Patch para as classes em CONVERSORS
 * Não funciona para List
 */
public class PatchHelper {

    public static <T> void applyPatch(T target, Map<String, Object> updateMap) {
        Class<?> clazz = target.getClass();

        updateMap.forEach((key, value) -> {
            if (!key.equals("id")) {
                try {
                    Class<?> tipo;
                    Field field = clazz.getDeclaredField(key);
                    field.setAccessible(true);
                    tipo = field.getType();
                    Object valueConverted = convert(tipo, value);
                    field.set(target, valueConverted);
                } catch (Exception e) {
                    throw new RuntimeException("Erro ao atualizar o campo '" + key + "': " + e.getMessage(), e);
                }
            }
        });
    }

    private static final Map<Class<?>, Function<Object, Object>> CONVERSORS = new HashMap<>();

    static {
        CONVERSORS.put(String.class, Object::toString);
        CONVERSORS.put(Short.class, v -> Short.parseShort(v.toString()));
        CONVERSORS.put(short.class, v -> Short.parseShort(v.toString()));
        CONVERSORS.put(Integer.class, v -> Integer.parseInt(v.toString()));
        CONVERSORS.put(int.class, v -> Integer.parseInt(v.toString()));
        CONVERSORS.put(Float.class, v -> Float.parseFloat(v.toString()));
        CONVERSORS.put(float.class, v -> Float.parseFloat(v.toString()));
        CONVERSORS.put(Double.class, v -> Double.parseDouble(v.toString()));
        CONVERSORS.put(double.class, v -> Double.parseDouble(v.toString()));
        CONVERSORS.put(Boolean.class, v -> Boolean.parseBoolean(v.toString()));
        CONVERSORS.put(boolean.class, v -> Boolean.parseBoolean(v.toString()));
    }

    private static Object convert(Class<?> tipo, Object value) {
        if (value == null) return null;
        Function<Object, Object> conversor = CONVERSORS.get(tipo);
        if (conversor != null) {
            return conversor.apply(value);
        }
        throw new IllegalArgumentException("Tipo não suportado para PATCH: " + tipo.getSimpleName());
    }
}
