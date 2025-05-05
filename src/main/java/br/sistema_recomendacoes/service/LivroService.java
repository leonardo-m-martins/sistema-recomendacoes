package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.sistema_recomendacoes.config.AppProperties;
import br.sistema_recomendacoes.config.AppProperties.Limits;
import br.sistema_recomendacoes.dto.LivroRequestDTO;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.exception.ResourceNotFoundException;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.Autor;
import br.sistema_recomendacoes.model.Genero;
import br.sistema_recomendacoes.model.Livro;
import br.sistema_recomendacoes.model.VetorLivro;
import br.sistema_recomendacoes.repository.LivroRepository;
import br.sistema_recomendacoes.repository.VetorLivroRepository;
import br.sistema_recomendacoes.util.PatchHelper;

@Service
public class LivroService {
    
    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private VetorLivroRepository vetorLivroRepository;

    @Autowired
    private GeneroService generoService;

    @Autowired
    private AutorService autorService;

    // limites para vetorização
    private final int MAX_PAGINAS;
    private final int MIN_ANO;
    private final int MAX_ANO;

    @Autowired
    public LivroService(AppProperties appProperties){
        Limits limits = appProperties.getLimits();
        MAX_PAGINAS = limits.getMaxPaginas();
        MIN_ANO = limits.getMinAno();
        MAX_ANO = limits.getMaxAno();
    }

    // create
    public LivroResponseDTO add(LivroRequestDTO requestDTO){
        Livro livro = LivroMapper.fromRequestDTO(requestDTO);

        // salvar os gêneros do livro se não existirem
        List<Genero> generosSalvos = generoService.searchAndSave(livro.getGeneros());
        livro.setGeneros(generosSalvos);

        // salvar os autores do livro se não existirem
        List<Autor> autoresSalvos = autorService.searchAndSave(livro.getAutores());
        livro.setAutores(autoresSalvos);

        Livro salvo = livroRepository.save(livro);
        vectorize(salvo);
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
        Livro livro = findById(id);
        return LivroMapper.toResponseDTO(livro);
    }

    // put
    public LivroResponseDTO put(Integer id, LivroRequestDTO requestDTO){
        findById(id);
        Livro livroAtualizado = LivroMapper.fromRequestDTO(requestDTO);
        livroAtualizado.setId(id);

        // Salvar gêneros
        livroAtualizado.setGeneros(generoService.searchAndSave(livroAtualizado.getGeneros()));

        // Salvar autores
        livroAtualizado.setAutores(autorService.searchAndSave(livroAtualizado.getAutores()));

        Livro salvo = livroRepository.save(livroAtualizado);
        vectorize(salvo);
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
        if(updateMap.containsKey("autores")){
            Object autoresObject = updateMap.get("autores");
            List<Autor> autores = autorService.patchLivroAutores(autoresObject);
            livro.setAutores(autores);
            updateMap.remove("autores");
        }
        PatchHelper.applyPatch(livro, updateMap);
        Livro salvo = livroRepository.save(livro);
        vectorize(salvo);
        return LivroMapper.toResponseDTO(salvo);
    }

    // delete
    public void delete(Integer id){
        Livro livro = findById(id);
        livroRepository.delete(livro);
    }

    // add from jsonl
    public void addFromJSONL(InputStream inputStream){
        ObjectMapper mapper = new ObjectMapper();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String linha;
        List<Livro> livros = new LinkedList<>();

        try {
            while ((linha = reader.readLine()) != null) {
                LivroRequestDTO requestDTO = mapper.readValue(linha, LivroRequestDTO.class);
                Livro livro = LivroMapper.fromRequestDTO(requestDTO);

                // salvar os gêneros do livro se não existirem
                List<Genero> generosSalvos = generoService.searchAndSave(livro.getGeneros());
                livro.setGeneros(generosSalvos);

                // salvar os autores do livro se não existirem
                List<Autor> autoresSalvos = autorService.searchAndSave(livro.getAutores());
                livro.setAutores(autoresSalvos);

                // Colocar na lista
                livros.add(livro);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler JSONL", e);
        }
        livros = livroRepository.saveAll(livros);
        vectorizeAll(livros);
    }

    private void vectorize(Livro livro){
        VetorLivro vetor = new VetorLivro(livro, MAX_PAGINAS, MIN_ANO, MAX_ANO);
        vetorLivroRepository.save(vetor);
    }

    private void vectorizeAll(List<Livro> livros){
        List<VetorLivro> vetores = new LinkedList<>();
        for (Livro livro : livros) {
            vetores.add(new VetorLivro(livro, MAX_PAGINAS, MIN_ANO, MAX_ANO));
        }
        vetorLivroRepository.saveAll(vetores);
    }

    public Livro findById(Integer id){
        return livroRepository.findById((long) id).orElseThrow( () -> new ResourceNotFoundException("Livro (id: " + id + ") não encontrado."));
    }

    public int count(){
        return (int) livroRepository.count();
    }

    public List<Livro> findAllList(){
        return livroRepository.findAll();
    }
}
