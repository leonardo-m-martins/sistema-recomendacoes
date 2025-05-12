package br.sistema_recomendacoes.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "autor")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private Integer num_livros;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(
        name = "livro_autor",  // nome da tabela intermediária
        joinColumns = @JoinColumn(name = "autor_id"),  // coluna que se refere à livro
        inverseJoinColumns = @JoinColumn(name = "livro_id")  // coluna que se refere ao Genero
    )
    @JsonIgnore
    private List<Livro> livros;

    protected Autor(){}

    public Autor(String nome){
        this.nome = nome;
    }

    public Autor(Integer id){
        this.id = id;
    }
}
