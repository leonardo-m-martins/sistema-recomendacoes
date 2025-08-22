package br.sistema_recomendacoes.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IntSetConverter implements AttributeConverter<IntSet, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(IntSet set) {
        if (set == null) return null;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(set.toIntArray());
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao serializar IntSet", e);
        }
    }

    @Override
    public IntSet convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length == 0) return new IntOpenHashSet();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(dbData);
             ObjectInputStream ois = new ObjectInputStream(bis)){
            int[] arr = (int[]) ois.readObject();
//            return new IntOpenHashSet(arr);
            return new IntArraySet(arr);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao desserializar IntSet", e);
        }
    }


}
