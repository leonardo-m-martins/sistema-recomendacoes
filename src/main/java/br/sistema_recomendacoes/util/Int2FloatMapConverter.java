package br.sistema_recomendacoes.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Int2FloatMapConverter implements AttributeConverter<Int2FloatMap, byte[]> {
    
    @Override
    public byte[] convertToDatabaseColumn(Int2FloatMap map){
        if (map == null) { return null; }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            int[] keys = map.keySet().toIntArray();
            float[] values = new float[keys.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = map.get(i);
            }

            oos.writeObject(keys);
            oos.writeObject(values);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao serializar Int2FloatMap", e);
        }
    }

    @Override
    public Int2FloatMap convertToEntityAttribute(byte[] dbData){
        if (dbData == null) { return null; }
        try (ByteArrayInputStream bis = new ByteArrayInputStream(dbData);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            int[] keys = (int[]) ois.readObject();
            float[] values = (float[]) ois.readObject();

            Int2FloatMap map = new Int2FloatOpenHashMap();
            for (int i = 0; i < values.length; i++) {
                map.put(keys[i], values[i]);
            }
            return map;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao desserializar Int2FloatMap", e);
        }
    }
}
