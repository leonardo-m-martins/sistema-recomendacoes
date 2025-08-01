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

    private Float modulo_generos;

    @Lob
    @Convert(converter = Int2FloatMapConverter.class)
    private Int2FloatMap vetor_autores;

    private Float modulo_autores;

    private Float paginas;
    private Float ano;

    public VetorUsuario(int id, Map<Integer, VetorLivro> historico, int notaMax, int notaMin){
        this.id = id;
        float[] somaNotas = {0.0f, 0.0f, 0.0f, 0.0f};

        vetor_generos = new Int2FloatOpenHashMap();
        vetor_generos.defaultReturnValue(0.0f);
        vetor_autores = new Int2FloatOpenHashMap();
        vetor_autores.defaultReturnValue(0.0f);

        // placeholders
        paginas = 0.0f; 
        ano = 0.0f;

        historico.forEach(
            (nota, vetorLivro) -> {
                float nota_normalizada = (float) (nota - notaMin) / (float) (notaMax - notaMin);

                // gêneros
                if (!vetorLivro.getVetor_generos().isEmpty()) {
                    somaNotas[0] += nota_normalizada;
                    vetorLivro.getVetor_generos().forEach(
                        (int idGenero) -> {
                            vetor_generos.put(idGenero, (vetor_generos.get(idGenero) + 1.0f * nota_normalizada));
                        }
                    );
                }

                // autores
                if (!vetorLivro.getVetor_autores().isEmpty()) {
                    somaNotas[1] += nota_normalizada;
                    vetorLivro.getVetor_autores().forEach(
                        (int idAutor) -> {
                            vetor_autores.put(idAutor, (vetor_autores.get(idAutor) + 1.0f * nota_normalizada));
                        }
                    );
                }

                if (vetorLivro.getPaginas() != null) {
                    somaNotas[2] += 1.0f;
                    paginas += vetorLivro.getPaginas();
                }

                if (vetorLivro.getAno() != null) {
                    somaNotas[3] += 1.0f;
                    ano += vetorLivro.getAno();
                }
                
            }
        );

        // normalizar gêneros
        normalizar(vetor_generos, somaNotas[0]);
        modulo_generos = (vetor_generos.isEmpty()) ? null : moduloGeneros();

        // normalizar autores
        normalizar(vetor_autores, somaNotas[1]);
        modulo_autores = (vetor_autores.isEmpty()) ? null : moduloAutores();


        if (paginas != 0.0f) paginas /= somaNotas[2];
        else paginas = null;
        if (ano != 0.0f) ano /= somaNotas[3];
        else ano = null;
    }

    private void normalizar(Int2FloatMap mapa, float somaNotas) {
        if (mapa.isEmpty() || somaNotas == 0.0f){ return; }
        for (Int2FloatMap.Entry entry : mapa.int2FloatEntrySet()) {
            int chave = entry.getIntKey();
            float valor = entry.getFloatValue();
            mapa.put(chave, valor / somaNotas);
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

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VetorUsuario other = (VetorUsuario) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode(){
        return Integer.hashCode(id);
    }
}
