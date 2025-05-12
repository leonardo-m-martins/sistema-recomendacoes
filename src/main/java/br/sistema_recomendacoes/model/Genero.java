package br.sistema_recomendacoes.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "genero")
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
    private String nome;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "livro_genero",  // nome da tabela intermediária
        joinColumns = @JoinColumn(name = "genero_id"),  // coluna que se refere à livro
        inverseJoinColumns = @JoinColumn(name = "livro_id")  // coluna que se refere ao Genero
    )
    @JsonIgnore
    private List<Livro> livros;

    private Integer num_livros;

    protected Genero(){}

    public Genero(String nome){
        this.nome = nome;
    }

    public Genero(Integer id){
        this.id = id;
    }
}
