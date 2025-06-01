package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.GeneroRequestDTO;
import br.sistema_recomendacoes.dto.GeneroResponseDTO;
import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.exception.ResourceNotFoundException;
import br.sistema_recomendacoes.mapper.GeneroMapper;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.repository.GeneroRepository;
import br.sistema_recomendacoes.util.PatchHelper;
import br.sistema_recomendacoes.util.Validator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class GeneroService {
    
    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private EntityManager entityManager;

    // create
    public GeneroResponseDTO add(GeneroRequestDTO requestDTO){
        Genero genero = GeneroMapper.fromRequestDTO(requestDTO);
        Genero salvo = generoRepository.save(genero);
        return GeneroMapper.toResponseDTO(salvo);
    }

    // read all
    public Page<GeneroResponseDTO> findAllDto(int page, int size, String sortBy, String direction){
        Sort sort = (direction.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return generoRepository.findAll(pageable).map(g -> GeneroMapper.toResponseDTO(g));
    }

    // read one
    public GeneroResponseDTO findByIdDto(Integer id){
        Genero genero = findById(id);
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
        Map<String, Genero> generosMap = generoRepository.findAll().stream()
            .collect(Collectors.toMap(
				Genero::getNome, 
				Function.identity(), 
				(existente, novo) -> existente
			));
        List<Genero> generosSalvos = new ArrayList<>();
        List<Genero> generosParaSalvar = new ArrayList<>();
        for (Genero genero : generos) {
            String nome = genero.getNome();
            Genero generoSalvo = generosMap.get(nome);
            if(generoSalvo != null){
                generosSalvos.add(generoSalvo);
            }
            else {
                generosParaSalvar.add(genero);
            }
        }
        generosSalvos.addAll(generoRepository.saveAll(generosParaSalvar));
        return generosSalvos;
    }

    @Transactional
    public Map<String, Integer> searchAndSaveFromDTOs(List<LivroRequestDTO> dtos) {
        final Map<String, Integer> generosMap = generoRepository.findAll().stream()
            .collect(Collectors.toMap(
                Genero::getNome,
                Genero::getId,
                (existente, novo) -> existente
            ));
    
        Set<String> novosNomes = new HashSet<>();
    
        for (LivroRequestDTO dto : dtos) {
            for (GeneroRequestDTO gDto : dto.getGeneros()) {
                if(!Validator.validate(gDto)) continue;
                String nome = gDto.getNome();
    
                if (!generosMap.containsKey(nome)) {
                    novosNomes.add(nome);
                }
            }
        }
    
        final int batchSize = 500;
        List<Genero> batch = new ArrayList<>(batchSize);
        for (String nome : novosNomes) {
            Genero g = new Genero(nome);
            batch.add(g);
    
            if (batch.size() == batchSize) {
                List<Genero> salvos = generoRepository.saveAll(batch);
                salvos.forEach(s -> generosMap.put(s.getNome(), s.getId()));
                batch.clear();
                entityManager.flush();
                entityManager.clear();
            }
        }
    
        // Salva o que sobrou
        if (!batch.isEmpty()) {
            List<Genero> salvos = generoRepository.saveAll(batch);
            salvos.forEach(s -> generosMap.put(s.getNome(), s.getId()));
        }
    
        return generosMap;
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
        return generoRepository.findById((long) id).orElseThrow( () -> new ResourceNotFoundException("Gênero (id: " + id + ") não encontrado."));
    }

    @Transactional
    public List<LivroResponseDTO> getLivros(Integer id){
        Genero genero = findById(id);
        return genero.getLivros().stream()
            .map(l -> LivroMapper.toResponseDTO(l))
            .toList();
    }
}
