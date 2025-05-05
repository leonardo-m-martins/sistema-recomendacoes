package br.sistema_recomendacoes.model;

import br.sistema_recomendacoes.util.IntSetConverter;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vetor_livro")
public class VetorLivro {
    @Id
    private int id;

    @Lob
    @Convert(converter = IntSetConverter.class)
    private IntSet vetor_generos;

    @Lob
    @Convert(converter = IntSetConverter.class)
    private IntSet vetor_autores;

    private float paginas_normalizado;
    private float ano_normalizado;

    /*
     * Vetorização de Livros:
     * Os valores dentro de vetor_generos e vetor_autores indicam quais generos/autores
     * estão presentes por meio de um HashSet com os ids das entidades (id equivalente ao id do banco)
     * Em qualquer operação com esses vetores, será considerado 1 para valores presentes
     * e 0 para valores ausentes.
     * Os valores numéricos são normalizados e salvos em float.
     */
    public VetorLivro(Livro livro, int maxPaginas, int minAno, int maxAno) {
        id = (int) livro.getId();
        vetor_generos = new IntOpenHashSet();
        vetor_autores = new IntOpenHashSet();

        for (Genero genero : livro.getGeneros()) {
            vetor_generos.add((int) genero.getId());
        }

        for (Autor autor : livro.getAutores()) {
            vetor_autores.add((int) autor.getId());
        }

        if (livro.getPaginas() != null && maxPaginas > 0) {
            paginas_normalizado = livro.getPaginas() / (float) maxPaginas;
        }

        if (livro.getPrimeira_data_publicacao() != null && maxAno > minAno) {
            ano_normalizado = (livro.getPrimeira_data_publicacao() - minAno) / (float)(maxAno - minAno);
        }
    }
    // A intensidade do vetor é sempre 1, 1² = 1, portanto, módulo = sqrt(size)
    public float moduloGeneros(){
        return (float) Math.sqrt((double) vetor_generos.size());
    }
    public float moduloAutores(){
        return (float) Math.sqrt((double) vetor_autores.size());
    }
    public float moduloPaginas(){
        return (float) Math.sqrt( (double) paginas_normalizado * paginas_normalizado );
    }
    public float moduloAno(){
        return (float) Math.sqrt( (double) ano_normalizado * ano_normalizado );
    }
}
