package br.sistema_recomendacoes.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.config.AppProperties;
import br.sistema_recomendacoes.config.AppProperties.Limits;
import br.sistema_recomendacoes.config.AppProperties.Weights;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.Avaliacao;
import br.sistema_recomendacoes.model.Livro;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.model.VetorLivro;
import br.sistema_recomendacoes.model.VetorUsuario;
import br.sistema_recomendacoes.repository.VetorLivroRepository;
import br.sistema_recomendacoes.repository.VetorUsuarioRepository;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class RecomendacaoService {

    @Autowired
    private LivroService livroService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private VetorUsuarioRepository vetorUsuarioRepository;

    @Autowired
    private VetorLivroRepository vetorLivroRepository;

    @Autowired
    private EntityManager entityManager;

    private final int NOTA_MAX;
    private final int NOTA_MIN;
    private final int NOTA_DE_CORTE;
    private final float PESO_GENERO;
    private final float PESO_AUTORES;
    private final float PESO_PAGINAS;
    private final float PESO_ANO;

    private final Set<VetorLivro> vetoresLivro;
    private final Set<VetorUsuario> vetoresUsuario;

    @Autowired
    public RecomendacaoService(AppProperties appProperties, VetorLivroRepository vetorLivroRepository, VetorUsuarioRepository vetorUsuarioRepository){
        Limits limits = appProperties.getLimits();
        NOTA_DE_CORTE = limits.getNotaDeCorte();
        NOTA_MAX = limits.getNotaMax();
        NOTA_MIN = limits.getNotaMin();


        Weights weights = appProperties.getWeights();
        PESO_ANO = weights.getAno();
        PESO_AUTORES = weights.getAutores();
        PESO_GENERO = weights.getGeneros();
        PESO_PAGINAS = weights.getPaginas();

        vetoresLivro = vetorLivroRepository.findAllSet();
        vetoresUsuario = vetorUsuarioRepository.findAllSet();
    }

    /* produto escalar
     * U * V
     * U = G, A, p, a
     * V = G, A, p, a
     * U * V = (G * G), (A * A), (p * p), (a * a)
     * Produto esclar deve ser dividido em quatro partes, e depois tirar uma média de acordo com os pesos
     */
    private float similaridade(VetorUsuario vetorUsuario, VetorLivro vetorLivro){
        IntSet vetorLivroGenero = vetorLivro.getVetor_generos(), vetorLivroAutores = vetorLivro.getVetor_autores();
        float similaridade, similaridadeGeneros, similaridadeAutores, similaridadePaginas, 
              similaridadeAno, produtoEscalar=0;

        
        // passo 1.1: calcular o produto escalar de gêneros
        if (vetorLivroGenero.isEmpty() || vetorUsuario.getVetor_generos().isEmpty()) similaridadeGeneros = 0.0f;
        else{
            for (Int2FloatMap.Entry entry : vetorUsuario.getVetor_generos().int2FloatEntrySet()) {
                int key = entry.getIntKey();
                float value = entry.getFloatValue();
                if (vetorLivroGenero.contains(key)){
                    produtoEscalar += value;
                }
            }
            // passo 1.2: calcular a similaridade entre gêneros
            similaridadeGeneros = produtoEscalar / (vetorLivro.getModulo_generos() * vetorUsuario.getModulo_generos());
        }
        

        // passo 2.1: calcular o produto escalar de autores
        if (vetorLivroAutores.isEmpty() || vetorUsuario.getVetor_autores().isEmpty()) similaridadeAutores = 0.0f;
        else {
            produtoEscalar=0;
            for (Int2FloatMap.Entry entry : vetorUsuario.getVetor_autores().int2FloatEntrySet()) {
                int key = entry.getIntKey();
                float value = entry.getFloatValue();
                if (vetorLivroAutores.contains(key)){
                    produtoEscalar += value;
                }
            }
            // passo 2.2: calcular a similaridade entre autores
            similaridadeAutores = produtoEscalar / (vetorLivro.getModulo_autores() * vetorUsuario.getModulo_autores());
        }
        
        // passo 3: paginas
        if (vetorUsuario.getPaginas() != null && vetorLivro.getPaginas() != null){
            float usuarioPaginas = vetorUsuario.getPaginas(), livroPaginas = vetorLivro.getPaginas();
            similaridadePaginas = Math.min(usuarioPaginas, livroPaginas) / Math.max(usuarioPaginas, livroPaginas);
        }
        else similaridadePaginas = 0.0f;

        // passo 4: ano
        if (vetorUsuario.getAno() != null && vetorLivro.getAno() != null){
            float usuarioAno = vetorUsuario.getAno(), livroAno = vetorLivro.getAno();
            similaridadeAno = Math.min(usuarioAno, livroAno) / Math.max(usuarioAno, livroAno); 
        }
        else similaridadeAno = 0.0f;

        similaridade = 
            similaridadeAno * PESO_ANO +
            similaridadeAutores * PESO_AUTORES +
            similaridadeGeneros * PESO_GENERO +
            similaridadePaginas * PESO_PAGINAS;

        return similaridade;
    }

    /* produto escalar
     * U * V
     * U = G, A, p, a
     * V = G, A, p, a
     * U * V = (G * G), (A * A), (p * p), (a * a)
     * Produto esclar deve ser dividido em quatro partes, e depois tirar uma média de acordo com os pesos
     */
    private float similaridade(VetorUsuario vetorUsuario, VetorUsuario vetorUsuario2){
        float similaridade, similaridadeGeneros, similaridadeAutores, similaridadePaginas, 
            similaridadeAno, produtoEscalar=0;

        // passo 1.1: calcular produto escalar de autores
        Int2FloatMap vetorAutores=vetorUsuario.getVetor_autores(), 
            vetorAutores2=vetorUsuario2.getVetor_autores();
        if (vetorAutores.isEmpty() || vetorAutores2.isEmpty()) similaridadeAutores = 0.0f;
        else {
            vetorAutores2.defaultReturnValue(0.0f);
            for (Int2FloatMap.Entry entry : vetorAutores.int2FloatEntrySet()) {
                int key = entry.getIntKey();
                float value = entry.getFloatValue();
                produtoEscalar += value * vetorAutores2.get(key);
                // soma em produto escalar somente os ids que estão presentes em ambos
            }
            // passo 1.2: calcular similaridade de autores
            similaridadeAutores = produtoEscalar / (vetorUsuario.moduloAutores() * vetorUsuario2.moduloAutores());
        }

        // passo 2.1: calcular produto escalar de generos
        produtoEscalar = 0;
        Int2FloatMap vetorGeneros=vetorUsuario.getVetor_generos(),
            vetorGeneros2=vetorUsuario2.getVetor_generos();
        if (vetorGeneros.isEmpty() || vetorGeneros2.isEmpty()) similaridadeGeneros = 0.0f;
        else {
            vetorGeneros2.defaultReturnValue(0.0f);
            for (Int2FloatMap.Entry entry : vetorGeneros.int2FloatEntrySet()) {
                int key = entry.getIntKey();
                float value = entry.getFloatValue();
                produtoEscalar += value * vetorGeneros2.get(key);
            }
            // passo 2.2: calcular similaridade de gêneros
            similaridadeGeneros = produtoEscalar / (vetorUsuario.moduloGeneros() * vetorUsuario2.moduloGeneros());
        }
        

        // passo 3: paginas
        if (vetorUsuario.getPaginas() != null && vetorUsuario2.getPaginas() != null){
            float usuarioPaginas = vetorUsuario.getPaginas(), livroPaginas = vetorUsuario2.getPaginas();
            similaridadePaginas = Math.min(usuarioPaginas, livroPaginas) / Math.max(usuarioPaginas, livroPaginas);
        }
        else similaridadePaginas = 0.0f;

        // passo 4: ano
        if (vetorUsuario.getAno() != null && vetorUsuario2.getAno() != null){
            float usuarioAno = vetorUsuario.getAno(), livroAno = vetorUsuario2.getAno();
            similaridadeAno = Math.min(usuarioAno, livroAno) / Math.max(usuarioAno, livroAno); 
        }
        else similaridadeAno = 0.0f;

        similaridade = 
            similaridadeAno * PESO_ANO +
            similaridadeAutores * PESO_AUTORES +
            similaridadeGeneros * PESO_GENERO +
            similaridadePaginas * PESO_PAGINAS;

        return similaridade;
    }




    static class Entry {
        float value;
        int id;

        Entry(float value, int id){
            this.value = value;
            this.id = id;
        }
        
    }

    @Transactional
    public List<LivroResponseDTO> recomendarConteudo(Integer idUsuario, int K){

        // Obter o histórico, retornar uma lista vazia (status code 204) caso o usuário não tenha histórico.
        Map<Integer, VetorLivro> historico = usuarioService.getHistorico(idUsuario);
        if (historico.isEmpty()) return List.of();

        VetorUsuario vetorUsuario = new VetorUsuario(idUsuario, historico, NOTA_MAX, NOTA_MIN);

        // Calcular a similaridade cosseno de cada um (Ignorar se o usuário já tiver lido o livro)
        Set<Integer> historicoSet = avaliacaoService.findLivro_idByUsuario_id(idUsuario);
        PriorityQueue<Entry> minHeap = new PriorityQueue<>(K, (a, b) -> Float.compare(a.value, b.value));
        for (VetorLivro vetorLivro : vetoresLivro) {
            if (historicoSet.contains(vetorLivro.getId())) continue;

            float sim = similaridade(vetorUsuario, vetorLivro);
            if (minHeap.size() < K){
                minHeap.offer(new Entry(sim, vetorLivro.getId()));
            } else if (sim > minHeap.peek().value) {
                minHeap.poll();
                minHeap.offer(new Entry(sim, vetorLivro.getId()));
            }
        }

        // Retornar lista de recomendações ordenada do mais similar ao menos similar
        List<LivroResponseDTO> recomendacoes = new LinkedList<>();
        while (!minHeap.isEmpty()) {
            Entry entry = minHeap.poll();
            recomendacoes.addFirst(LivroMapper.toResponseDTO(livroService.findById(entry.id)));
        }
        return recomendacoes;
    }

    @Transactional
    public List<LivroResponseDTO> recomendarColaborativa(Integer idUsuario, int K){
        
        if (vetoresUsuario.isEmpty()) return List.of(); // Retorna uma lista vazia ao invés de lançar uma exceção, com status code 204 para o frontend

        Map<Integer, VetorLivro> historico = usuarioService.getHistorico(idUsuario);
        if (historico.isEmpty()) return List.of();

        VetorUsuario vetorUsuario = new VetorUsuario(idUsuario, historico, NOTA_MAX, NOTA_MIN);

        // Calcular a similaridade cosseno de cada vetor, salvando em PriorityQueue
        PriorityQueue<Entry> minHeap = new PriorityQueue<>(K, (a, b) -> Float.compare(a.value, b.value));
        for (VetorUsuario v : vetoresUsuario) {
            if (v.getId() == vetorUsuario.getId()) continue;
            
            float sim = similaridade(vetorUsuario, v);
            if (minHeap.size() < K){
                minHeap.offer(new Entry(sim, v.getId()));
            } else if (sim > minHeap.peek().value) {
                minHeap.poll();
                minHeap.offer(new Entry(sim, v.getId()));
            }
        }

        // Extrair os livros (ignorar se o usuário já tiver lido)
        List<Integer> topKUsuarios = new ArrayList<>(K);
        Set<Livro> topKLivros = new LinkedHashSet<>(K);
        Set<Integer> historicoSet = avaliacaoService.findLivro_idByUsuario_id(idUsuario);
        while (!minHeap.isEmpty()) {
            Entry e = minHeap.poll();
            topKUsuarios.add(e.id);
        }
        for (Integer usuario_id : topKUsuarios.reversed()) {
            Usuario usuario = usuarioService.findById(usuario_id);
            List<Avaliacao> avaliacaos = usuario.getAvaliacaos();
            for (Avaliacao avaliacao : avaliacaos) {
                if (avaliacao.getNota() >= NOTA_DE_CORTE) {
                    Livro livro = avaliacao.getLivro();
                    if (!historicoSet.contains(livro.getId())) topKLivros.add(livro);
                }
                if (topKLivros.size() == K) break;
            }
            if (topKLivros.size() == K) break;
        }

        // Retornar lista de recomendações
        List<LivroResponseDTO> recomendacoes = new ArrayList<>();
        for (Livro livro : topKLivros) {
            recomendacoes.add(LivroMapper.toResponseDTO(livro));
        }
        return recomendacoes;
    }

    // vetor_livro
    @Transactional
    public void updateVetorLivros(){
        final int numLivros = livroService.count(), batchSize = 500;
        if (numLivros == 0){
            throw new UnsupportedOperationException();
        }
        List<VetorLivro> vetores = new ArrayList<>(batchSize);
        Pageable pageable = PageRequest.of(0, batchSize);
        Page<Livro> livros = livroService.findAll(pageable);
        if (livros.isEmpty()) throw new UnsupportedOperationException();
        for (int index = 0; index < livros.getTotalPages(); index++) {
            if ((index % 10) == 0) {
                System.out.println("Progresso: " + (index * batchSize));
            }
            pageable = PageRequest.of(index, batchSize);
            livros = livroService.findAll(pageable);
            for (Livro livro : livros) {
                vetores.add(new VetorLivro(livro));
            }
            vetorLivroRepository.saveAllAndFlush(vetores);
            entityManager.flush();
            entityManager.clear();
            vetores.clear();
        }
    }

    // vetor_usuario
    public void updateVetorUsuarios(){
        int numUsuarios = usuarioService.count();
        if (numUsuarios == 0) {
            throw new UnsupportedOperationException();
        }
        VetorUsuario[] vetores = new VetorUsuario[numUsuarios];
        List<Usuario> usuarios = usuarioService.findAllList();
        if (usuarios.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        int i = 0;
        for (Usuario usuario : usuarios) {
            int usuario_id = usuario.getId();
            vetores[i] = new VetorUsuario(usuario_id, usuarioService.getHistorico(usuario_id), NOTA_MAX, NOTA_MIN);
            i++;
        }
        vetorUsuarioRepository.saveAll(List.of(vetores));
    }

    public void updateVetorUsuario(Integer usuarioId) {
        VetorUsuario vetorUsuario = new VetorUsuario(usuarioId, usuarioService.getHistorico(usuarioId), NOTA_MAX, NOTA_MIN);
        vetorUsuarioRepository.save(vetorUsuario);
        vetoresUsuario.remove(vetorUsuario);
        vetoresUsuario.add(vetorUsuario);
    }

    public void updateVetorLivro(int livroId) {
        Livro livro = livroService.findById(livroId);
        VetorLivro vetorLivro = new VetorLivro(livro);
        vetorLivroRepository.save(vetorLivro);
        vetoresLivro.remove(vetorLivro);
        vetoresLivro.add(vetorLivro);
    }

    public void removeVetorLivro(int livroId) {
        VetorLivro vetorLivro = new VetorLivro();
        vetorLivro.setId(livroId);
        vetoresLivro.remove(vetorLivro);
    }

    public void removeVetorUsuario(int usuarioId) {
        VetorUsuario vetorUsuario = new VetorUsuario();
        vetorUsuario.setId(usuarioId);
        vetoresUsuario.remove(vetorUsuario);
    }
}
