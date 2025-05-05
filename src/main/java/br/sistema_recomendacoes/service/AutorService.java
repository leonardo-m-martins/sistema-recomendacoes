package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.dto.AutorRequestDTO;
import br.sistema_recomendacoes.mapper.AutorMapper;
import br.sistema_recomendacoes.model.Autor;
import br.sistema_recomendacoes.repository.AutorRepository;

@Service
public class AutorService {
    
    @Autowired
    private AutorRepository autorRepository;

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
}
