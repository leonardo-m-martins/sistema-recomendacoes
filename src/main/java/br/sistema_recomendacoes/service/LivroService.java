package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.model.Livro;
import br.sistema_recomendacoes.repository.LivroRepository;

@Service
public class LivroService {
    
    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private GeneroService generoService;

    // create
    public LivroResponseDTO add(LivroRequestDTO requestDTO){
        Livro livro = LivroMapper.fromRequestDTO(requestDTO);

        // salvar os gêneros do livro se não existirem
        List<Genero> generosSalvos = generoService.searchAndSave(livro.getGeneros());
        livro.setGeneros(generosSalvos);

        Livro salvo = livroRepository.save(livro);
        return LivroMapper.toResponseDTO(salvo);
    }

    // read all
    public List<LivroResponseDTO> findAll(){
        Iterable<Livro> livroIterable = livroRepository.findAll();
        List<LivroResponseDTO> responseDTOs = new ArrayList<>();
        for (Livro livro : livroIterable) {
            responseDTOs.add(LivroMapper.toResponseDTO(livro));
        }
        return responseDTOs;
    }

    // read one
    public LivroResponseDTO findByIdDto(Integer id){
        Livro livro = livroRepository.findById((long) id).orElseThrow();
        return LivroMapper.toResponseDTO(livro);
    }

    // put
    public LivroResponseDTO put(Integer id, LivroRequestDTO requestDTO){
        findById(id);
        Livro livroAtualizado = LivroMapper.fromRequestDTO(requestDTO);
        livroAtualizado.setId(id);
        // Salvar gêneros
        livroAtualizado.setGeneros(generoService.searchAndSave(livroAtualizado.getGeneros()));
        Livro salvo = livroRepository.save(livroAtualizado);
        return LivroMapper.toResponseDTO(salvo);
    }

    // patch
    public LivroResponseDTO patch(Integer id, Map<String, Object> updateMap){
        Livro livro = findById(id);
        
        if(updateMap.containsKey("generos")){
            Object generosObject = updateMap.get("generos");
            List<Genero> generos = generoService.patchLivroGeneros(generosObject);
            livro.setGeneros(generos);
            updateMap.remove("generos");
        }
        PatchHelper.applyPatch(livro, updateMap);
        Livro salvo = livroRepository.save(livro);
        return LivroMapper.toResponseDTO(salvo);
    }

    // delete
    public void delete(Integer id){
        Livro livro = findById(id);
        livroRepository.delete(livro);
    }

    private Livro findById(Integer id){
        return livroRepository.findById((long) id).orElseThrow();
    }
}
