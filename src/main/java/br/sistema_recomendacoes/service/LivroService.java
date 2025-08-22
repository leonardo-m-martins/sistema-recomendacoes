package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
import br.sistema_recomendacoes.util.Validator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

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

    @Autowired
    private EntityManager entityManager;

    // create
    @Transactional
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
    @Transactional
    public Page<LivroResponseDTO> findAll(int page, int size, String sortBy, String direction){
        Sort sort = (direction.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Livro> livros = livroRepository.findAll(pageable);
        return livros.map(LivroMapper::toLazyResponseDTO);
    }

    // read one
    @Transactional
    public LivroResponseDTO findByIdDto(Integer id){
        Livro livro = findById(id);
        return LivroMapper.toResponseDTO(livro);
    }

    // put
    @Transactional
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
    @Transactional
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
    public int delete(Integer id){
        Livro livro = findById(id);
        livroRepository.delete(livro);

        return id;
    }

    // adicionar lista
    @Transactional
    public Integer addMany(List<LivroRequestDTO> requestDTOs) {
        if (requestDTOs == null || requestDTOs.isEmpty()) return 0;

        // carrega todos os gêneros e autores, e compara pelo nome 
        // com os da lista de livros para não salvar duplicatas
        final Map<String, Integer> generosMap = generoService.searchAndSaveFromDTOs(requestDTOs);
        final Map<String, Integer> autoresMap = autorService.searchAndSaveFromDTOs(requestDTOs);
    
        final int batchSize = 50;
        int totalSalvos = 0;
        List<Livro> batch = new ArrayList<>(batchSize);
    
        for (LivroRequestDTO dto : requestDTOs) {
            Livro livro = LivroMapper.fromRequestDTO(dto, generosMap, autoresMap);
            if (Validator.validate(livro)) batch.add(livro);
    
            if (batch.size() == batchSize) {
                try {
                    livroRepository.saveAll(batch);
                    entityManager.flush();
                    entityManager.clear();
                    totalSalvos += batch.size(); // usa o tamanho real salvo
                } catch (Exception e) {
                    System.out.println("Falha ao salvar batch com " + batch.size() + " livros. Erro: " + e.getMessage());
                } finally {
                    batch.clear(); // limpa sempre, com ou sem falha
                }
            }
        }
    
        if (!batch.isEmpty()) {
            try {
                livroRepository.saveAll(batch);
                totalSalvos += batch.size();
                entityManager.flush();
                entityManager.clear();
            } catch (Exception e) {
                System.out.println("Falha ao salvar último batch com " + batch.size() + " livros. Erro: " + e.getMessage());
            }
        }
    
        return totalSalvos;
    }    
    
    @Transactional
    public Page<LivroResponseDTO> search(String q, int page, int size, String sortBy, String direction){
        Sort sort = (direction.equalsIgnoreCase("desc")) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Livro> livros = livroRepository.buscaPorTituloDescricao(q, pageable);
        return livros.map(LivroMapper::toLazyResponseDTO);
    }

    private void vectorize(Livro livro){
        VetorLivro vetor = new VetorLivro(livro);
        vetorLivroRepository.save(vetor);
    }

    // private void vectorizeAll(List<Livro> livros){
    //     List<VetorLivro> vetores = new ArrayList<>();
    //     for (Livro livro : livros) {
    //         vetores.add(new VetorLivro(livro, MAX_PAGINAS, MIN_ANO, MAX_ANO));
    //     }
    //     vetorLivroRepository.saveAll(vetores);
    // }

    public Livro findById(Integer id){
        return livroRepository.findById((long) id).orElseThrow( () -> new ResourceNotFoundException("Livro (id: " + id + ") não encontrado."));
    }

    public int count(){
        return (int) livroRepository.count();
    }

    public List<Livro> findAllByIds(List<Integer> ids){
        List<Long> longIds = ids.stream()
                .map(Integer::longValue)
                .toList();
        return livroRepository.findAllById(longIds);
    }

    public Page<Livro> findAll(Pageable pageable){
        return livroRepository.findAll(pageable);
    }
}
