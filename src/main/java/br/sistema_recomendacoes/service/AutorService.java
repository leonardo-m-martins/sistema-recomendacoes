package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.sistema_recomendacoes.dto.AutorResponseDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.exception.ResourceNotFoundException;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.mapper.AutorMapper;
import br.sistema_recomendacoes.model.Autor;
import br.sistema_recomendacoes.repository.AutorRepository;
import br.sistema_recomendacoes.util.Validator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class AutorService {
    
    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private EntityManager entityManager;

    public Autor findById(Integer autorId) {
        return autorRepository.findById((long) autorId).orElseThrow( () -> new ResourceNotFoundException("Autor (id: " + autorId + ") não encontrado."));
    }

    public AutorResponseDTO findByIdDto(Integer autorId) {
        Autor autor = findById(autorId);
        return AutorMapper.toResponseDTO(autor);
    }

    public List<Autor> searchAndSave(List<Autor> autores) {
        List<Autor> autoresSalvos = new ArrayList<>();
        for (Autor autor : autores) {
            Optional<Autor> autorOptional = autorRepository.findByNome(autor.getNome());
            if(autorOptional.isPresent()){
                autoresSalvos.add(autorOptional.get());
            }
            else {
                autoresSalvos.add(autorRepository.save(autor));
            }
        }
        return autoresSalvos;
    }

    @Transactional
    public Map<String, Integer> searchAndSaveFromDTOs(List<LivroRequestDTO> dtos) {
        final Map<String, Integer> autoresMap = autorRepository.findAll().stream()
            .collect(Collectors.toMap(
                Autor::getNome,
                Autor::getId,
                (existente, novo) -> existente
            ));
    
        Set<String> novosNomes = new HashSet<>();
    
        for (LivroRequestDTO dto : dtos) {
            for (AutorRequestDTO aDto : dto.getAutores()) {
                if(!Validator.validate(aDto)) continue;
                String nome = aDto.getNome();
    
                if (!autoresMap.containsKey(nome)) {
                    novosNomes.add(nome);
                }
            }
        }
    
        final int batchSize = 500;
        List<Autor> batch = new ArrayList<>(batchSize);
        for (String nome : novosNomes) {
            Autor a = new Autor(nome);
            batch.add(a);
    
            if (batch.size() == batchSize) {
                List<Autor> salvos = autorRepository.saveAll(batch);
                salvos.forEach(s -> autoresMap.put(s.getNome(), s.getId()));
                batch.clear();
                entityManager.flush();
                entityManager.clear();
            }
        }
    
        // Salva o que sobrou
        if (!batch.isEmpty()) {
            List<Autor> salvos = autorRepository.saveAll(batch);
            salvos.forEach(s -> autoresMap.put(s.getNome(), s.getId()));
        }
    
        return autoresMap;
    }

    public List<Autor> patchLivroAutores(Object autoresObject) {
        if(autoresObject instanceof List){
            List<?> autoresList = (ArrayList<?>) autoresObject;
            List<Autor> autores = new ArrayList<>();
            for (Object object : autoresList) {
                String autorString = object.toString();
                AutorRequestDTO aDto = new AutorRequestDTO();
                aDto.setNome(autorString);
                Autor autor = AutorMapper.fromRequestDTO(aDto);
                autores.add(autor);
            }
            return searchAndSave(autores);
        }
        else{
            throw new RuntimeException();
        }
    }

    @Transactional
    public Page<LivroResponseDTO> getLivros(Integer autorId, int page, int size, String sortBy, String direction) {
        Sort sort = (direction.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Livro> livros = autorRepository.getLivrosByAutor(autorId, pageable);
        return livros.map(LivroMapper::toLazyResponseDTO);
    }
}
