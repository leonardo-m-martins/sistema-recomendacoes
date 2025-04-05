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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "livro")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String pais_origem;
    private Short ano;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 500)
    private String capa;

    private String autor;
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

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Avaliacao> avaliacaos;

    protected Livro(){}

    public Livro(String titulo, String pais_origem, Short ano, String descricao,
                 String capa, String autor, Integer paginas, String editora){
        this.titulo = titulo;
        this.pais_origem = pais_origem;
        this.ano = ano;
        this.descricao = descricao;
        this.capa = capa;
        this.autor = autor;
        this.paginas = paginas;
        this.editora = editora;
    }
}
