package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.mapper.GeneroMapper;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.repository.GeneroRepository;

@Service
public class GeneroService {
    
    @Autowired
    private GeneroRepository generoRepository;

    // create
    public GeneroResponseDTO add(GeneroRequestDTO requestDTO){
        Genero genero = GeneroMapper.fromRequestDTO(requestDTO);
        Genero salvo = generoRepository.save(genero);
        return GeneroMapper.toResponseDTO(salvo);
    }

    // read all
    public List<GeneroResponseDTO> findAllDto(){
        Iterable<Genero> generos = generoRepository.findAll();
        List<GeneroResponseDTO> responseDTOs = new ArrayList<>();
        for (Genero genero : generos) {
            responseDTOs.add(GeneroMapper.toResponseDTO(genero));
        }
        return responseDTOs;
    }

    // read one
    public GeneroResponseDTO findByIdDto(Integer id){
        Genero genero = generoRepository.findById((long) id).orElseThrow();
        return GeneroMapper.toResponseDTO(genero);
    }

    // put
    public GeneroResponseDTO put(Integer id, GeneroRequestDTO generoAtualizadoDTO){
        findById(id);
        Genero generoAtualizado = GeneroMapper.fromRequestDTO(generoAtualizadoDTO);
        generoAtualizado.setId(id);
        Genero salvo = generoRepository.save(generoAtualizado);
        return GeneroMapper.toResponseDTO(salvo);
    }

    // patch
    public GeneroResponseDTO patch(Integer id, Map<String, Object> updateMap){
        Genero genero = findById(id);
        PatchHelper.applyPatch(genero, updateMap);
        Genero salvo = generoRepository.save(genero);
        return GeneroMapper.toResponseDTO(salvo);
    }

    // delete
    public void delete(Integer id){
        Genero genero = findById(id);
        generoRepository.delete(genero);
    }

    // atualizar o número de livros de cada gênero.
    // é mais eficiente salvar o valor no banco de dados esporadicamente, do que procurar por ele toda vez.
    public void updateNum_livros(){
        Iterable<Genero> generos = findAll();
        for(Genero genero : generos){
            genero.setNum_livros(generoRepository.countNum_livros(genero.getId()));
            generoRepository.save(genero);
        }
    }

    // procurar por gênero no banco, se não existir, criar
    // isso é útil para não ter que adicionar todos os gêneros manualmente
    public List<Genero> searchAndSave(List<Genero> generos) {
        List<Genero> generosSalvos = new ArrayList<>();
        for (Genero genero : generos) {
            Optional<Genero> generoOptional = generoRepository.findByNome(genero.getNome());
            if(generoOptional.isPresent()){
                generosSalvos.add(generoOptional.get());
            }
            else {
                generosSalvos.add(generoRepository.save(genero));
            }
        }
        return generosSalvos;
    }

    public List<Genero> patchLivroGeneros(Object generosObject) {
        if(generosObject instanceof List){
            List<?> generosList = (ArrayList<?>) generosObject;
            List<Genero> generos = new ArrayList<>();
            for (Object object : generosList) {
                String generoString = object.toString();
                GeneroRequestDTO gDto = new GeneroRequestDTO();
                gDto.setNome(generoString);
                Genero genero = GeneroMapper.fromRequestDTO(gDto);
                generos.add(genero);
            }
            return searchAndSave(generos);
        }
        else{
            throw new RuntimeException();
        }
    }

    private Iterable<Genero> findAll(){
        return generoRepository.findAll();
    }
    
    private Genero findById(Integer id){
        return generoRepository.findById((long) id).orElseThrow();
    }
}
