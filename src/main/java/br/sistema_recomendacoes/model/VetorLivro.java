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

    private Float modulo_generos;

    @Lob
    @Convert(converter = IntSetConverter.class)
    private IntSet vetor_autores;

    private Float modulo_autores;

    private Float paginas;
    private Float ano;

    /*
     * Vetorização de Livros:
     * Os valores dentro de vetor_generos e vetor_autores indicam quais generos/autores
     * estão presentes por meio de um HashSet com os ids das entidades (id equivalente ao id do banco)
     * Em qualquer operação com esses vetores, será considerado 1 para valores presentes
     * e 0 para valores ausentes.
     * Os valores numéricos são normalizados e salvos em float.
     */
    public VetorLivro(Livro livro) {
        id = (int) livro.getId();
        vetor_generos = new IntOpenHashSet();
        vetor_autores = new IntOpenHashSet();

        for (Genero genero : livro.getGeneros()) {
            vetor_generos.add((int) genero.getId());
        }

        for (Autor autor : livro.getAutores()) {
            vetor_autores.add((int) autor.getId());
        }

        paginas = (livro.getPaginas() != null) ? Float.valueOf(livro.getPaginas()) : null;

        ano = (livro.getPrimeira_data_publicacao() != null) ? Float.valueOf(livro.getPrimeira_data_publicacao()) : null;

        modulo_generos = (vetor_generos.isEmpty()) ? null : moduloGeneros();

        modulo_autores = (vetor_autores.isEmpty()) ? null : moduloAutores();
    }
    // A intensidade do vetor é sempre 1, 1² = 1, portanto, módulo = sqrt(size)
    public float moduloGeneros(){
        return (float) Math.sqrt((double) vetor_generos.size());
    }
    public float moduloAutores(){
        return (float) Math.sqrt((double) vetor_autores.size());
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VetorLivro other = (VetorLivro) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode(){
        return Integer.hashCode(id);
    }
}
