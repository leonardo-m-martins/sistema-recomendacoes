package br.sistema_recomendacoes.model;

import java.util.Map;

import br.sistema_recomendacoes.util.Int2FloatMapConverter;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Vetorização do histórico do usuário:
 * O VetorUsuário é dado pela soma de (VetoresLivros * respectivasNotas) dividido por somaNotas
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "vetor_usuario")
public class VetorUsuario {
    @Id
    private int id;

    @Lob
    @Convert(converter = Int2FloatMapConverter.class)
    private Int2FloatMap vetor_generos;

    @Lob
    @Convert(converter = Int2FloatMapConverter.class)
    private Int2FloatMap vetor_autores;

    private float paginas_normalizado;
    private float ano_normalizado;

    // somaNotas é usado para calcular a média ponderada
    private transient int somaNotas = 0;

    public VetorUsuario(int id, Map<Integer, VetorLivro> historico){
        this.id = id;

        vetor_generos = new Int2FloatOpenHashMap();
        vetor_autores = new Int2FloatOpenHashMap();

        historico.forEach(
            (nota, vetorLivro) -> {
                somaNotas += nota;

                // gêneros
                vetorLivro.getVetor_generos().forEach(
                    (int idGenero) -> {
                        vetor_generos.put(idGenero, vetor_generos.get(idGenero) + 1.0f * nota);
                    }
                );

                // autores
                vetorLivro.getVetor_autores().forEach(
                    (int idAutor) -> {
                        vetor_autores.put(idAutor, vetor_autores.get(idAutor) + 1.0f * nota);
                    }
                );

                paginas_normalizado += vetorLivro.getPaginas_normalizado() * nota;
                ano_normalizado += vetorLivro.getAno_normalizado() * nota;
            }
        );

        // normalizar gêneros
        normalizar(vetor_generos, somaNotas);

        // normalizar autores
        normalizar(vetor_autores, somaNotas);

        paginas_normalizado /= (float) somaNotas;
        ano_normalizado /= (float) somaNotas;
    }

    private void normalizar(Int2FloatMap mapa, int somaNotas) {
        for (Int2FloatMap.Entry entry : mapa.int2FloatEntrySet()) {
            int chave = entry.getIntKey();
            float valor = entry.getFloatValue();
            mapa.put(chave, valor / (float) somaNotas);
        }
    }

    public float moduloGeneros(){
        float modulo = 0.0f;
        for (float value : vetor_generos.values()) {
            modulo += value * value;
        }
        modulo = (float) Math.sqrt( (double) modulo);
        return modulo;
    }
    public float moduloAutores(){
        float modulo = 0.0f;
        for (float value : vetor_autores.values()) {
            modulo += value * value;
        }
        modulo = (float) Math.sqrt( (double) modulo);
        return modulo;
    }
    public float moduloPaginas(){
        return (float) Math.sqrt( (double) paginas_normalizado * paginas_normalizado );
    }
    public float moduloAno(){
        return (float) Math.sqrt( (double) ano_normalizado * ano_normalizado );
    }
}
