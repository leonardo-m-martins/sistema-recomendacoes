package br.sistema_recomendacoes.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "livro")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String subtitulo;
    private Short primeira_data_publicacao;
    private Short data_publicacao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 500)
    private String capa;

    private Integer paginas;
    private String editora;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "livro_genero",  // nome da tabela intermediária
        joinColumns = @JoinColumn(name = "livro_id"),  // coluna que se refere à Obra
        inverseJoinColumns = @JoinColumn(name = "genero_id")  // coluna que se refere ao Genero
    )
    //@JsonIgnore
    private List<Genero> generos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "livro_autor",  // nome da tabela intermediária
        joinColumns = @JoinColumn(name = "livro_id"),  // coluna que se refere à Obra
        inverseJoinColumns = @JoinColumn(name = "autor_id")  // coluna que se refere ao Genero
    )
    private List<Autor> autores;

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Avaliacao> avaliacaos;
}
