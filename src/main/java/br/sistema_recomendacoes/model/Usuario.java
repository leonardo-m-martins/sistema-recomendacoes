package br.sistema_recomendacoes.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String senha;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Avaliacao> avaliacaos;

    protected Usuario(){}

    public Usuario(String nome, String senha){
        this.nome = nome;
        this.senha = senha;
    }
}
