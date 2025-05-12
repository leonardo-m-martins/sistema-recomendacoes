package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

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

    private final int MAX_PAGINAS;
    private final int MIN_ANO;
    private final int MAX_ANO;
    private final float PESO_GENERO;
    private final float PESO_AUTORES;
    private final float PESO_PAGINAS;
    private final float PESO_ANO;
    private final int NOTA_DE_CORTE;

    @Autowired
    public RecomendacaoService(AppProperties appProperties){
        Limits limits = appProperties.getLimits();
        MAX_PAGINAS = limits.getMaxPaginas();
        MIN_ANO = limits.getMinAno();
        MAX_ANO = limits.getMaxAno();
        NOTA_DE_CORTE = limits.getNotaDeCorte();

        Weights weights = appProperties.getWeights();
        PESO_ANO = weights.getAno();
        PESO_AUTORES = weights.getAutores();
        PESO_GENERO = weights.getGeneros();
        PESO_PAGINAS = weights.getPaginas();
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


        
        // (G * G)
        for (Int2FloatMap.Entry entry : vetorUsuario.getVetor_generos().int2FloatEntrySet()) {
            int key = entry.getIntKey();
            float value = entry.getFloatValue();
            if (vetorLivroGenero.contains(key)){
                produtoEscalar += value;
            }
        }
        similaridadeGeneros = produtoEscalar / (vetorLivro.moduloGeneros() * vetorUsuario.moduloGeneros());

        // (A * A)
        produtoEscalar=0;
        for (Int2FloatMap.Entry entry : vetorUsuario.getVetor_autores().int2FloatEntrySet()) {
            int key = entry.getIntKey();
            float value = entry.getFloatValue();
            if (vetorLivroAutores.contains(key)){
                produtoEscalar += value;
            }
        }
        similaridadeAutores = produtoEscalar / (vetorLivro.moduloAutores() * vetorUsuario.moduloAutores());

        // (p * p)
        produtoEscalar = vetorLivro.getPaginas_normalizado() * vetorUsuario.getPaginas_normalizado();
        similaridadePaginas = produtoEscalar / (vetorLivro.moduloPaginas() * vetorUsuario.moduloPaginas());

        // (a * a)
        produtoEscalar = vetorLivro.getAno_normalizado() * vetorUsuario.getPaginas_normalizado();
        similaridadeAno = produtoEscalar / (vetorLivro.moduloAno() * vetorUsuario.moduloAno());

        similaridade = (similaridadeAno * PESO_ANO + 
                        similaridadePaginas * PESO_PAGINAS + 
                        similaridadeAutores * PESO_AUTORES + 
                        similaridadeGeneros * PESO_GENERO);
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
        vetorAutores2.defaultReturnValue(0.0f);
        for (Int2FloatMap.Entry entry : vetorAutores.int2FloatEntrySet()) {
            int key = entry.getIntKey();
            float value = entry.getFloatValue();
            produtoEscalar += value * vetorAutores2.get(key);
            // soma em produto escalar somente os ids que estão presentes em ambos
        }

        // passo 1.2: calcular similaridade de autores
        similaridadeAutores = produtoEscalar / (vetorUsuario.moduloAutores() * vetorUsuario2.moduloAutores());

        // passo 2.1: calcular produto escalar de generos
        produtoEscalar = 0;
        Int2FloatMap vetorGeneros=vetorUsuario.getVetor_generos(),
            vetorGeneros2=vetorUsuario2.getVetor_generos();
        vetorGeneros2.defaultReturnValue(0.0f);
        for (Int2FloatMap.Entry entry : vetorGeneros.int2FloatEntrySet()) {
            int key = entry.getIntKey();
            float value = entry.getFloatValue();
            produtoEscalar += value * vetorGeneros2.get(key);
        }

        // passo 2.2: calcular similaridade de gêneros
        similaridadeGeneros = produtoEscalar / (vetorUsuario.moduloGeneros() * vetorUsuario2.moduloGeneros());

        // passo 3.1: calcular produto escalar de paginas
        produtoEscalar = vetorUsuario.getPaginas_normalizado() * vetorUsuario2.getPaginas_normalizado();
        // passo 3.2: calcular similaridade de páginas
        similaridadePaginas = produtoEscalar / (vetorUsuario.moduloPaginas() * vetorUsuario2.moduloPaginas());

        // passo 4.1: calcular produto escalar de ano de publicação
        produtoEscalar = vetorUsuario.getAno_normalizado() * vetorUsuario2.getAno_normalizado();
        // passo 4.2: calcular similaridade de ano de publicação
        similaridadeAno = produtoEscalar / (vetorUsuario.moduloAno() * vetorUsuario2.moduloGeneros());

        similaridade =  similaridadeAno * PESO_ANO +
                        similaridadeAutores * PESO_AUTORES +
                        similaridadeGeneros * PESO_GENERO + 
                        similaridadePaginas * PESO_PAGINAS;
                    
        return similaridade;
    }




    static class Entry {
        float value;
        int index;

        Entry(float value, int index){
            this.value = value;
            this.index = index;
        }
        
    }

    @Transactional
    public List<LivroResponseDTO> recomendar(Integer id_usuario, int K){

        // Carregar os vetores
        int numLivros = livroService.count();
        VetorLivro[] vetores = readVetores();
        VetorUsuario vetorUsuario = new VetorUsuario(id_usuario, usuarioService.getHistorico(id_usuario, MAX_PAGINAS, MIN_ANO, MAX_ANO));

        // Calcular a similaridade cosseno de cada um (Ignorar se o usuário já tiver lido o livro)
        Set<Integer> historicoSet = avaliacaoService.findLivro_idByUsuario_id(id_usuario);
        PriorityQueue<Entry> minHeap = new PriorityQueue<>(K, (a, b) -> Float.compare(a.value, b.value));
        for (int i = 0; i < numLivros; i++) {
            if (historicoSet.contains(vetores[i].getId())) {
                continue;
            }
            float sim = similaridade(vetorUsuario, vetores[i]);
            if (minHeap.size() < K){
                minHeap.offer(new Entry(sim, i));
            } else if (sim > minHeap.peek().value) {
                minHeap.poll();
                minHeap.offer(new Entry(sim, i));
            }
        }

        // Retornar lista de recomendações ordenada do mais similar ao menos similar
        List<LivroResponseDTO> recomendacoes = new LinkedList<>();
        while (!minHeap.isEmpty()) {
            Entry entry = minHeap.poll();
            recomendacoes.addFirst(LivroMapper.toResponseDTO(livroService.findById(vetores[entry.index].getId())));
        }
        return recomendacoes;
    }

    @Transactional
    public List<LivroResponseDTO> recomendarColaborativa(Integer id_usuario, int K){

        // Carregar os vetores de usuários
        int numUsuarios = usuarioService.count();
        VetorUsuario[] vetores = readVetoresUsuario();
        VetorUsuario vetorUsuario = new VetorUsuario(id_usuario, usuarioService.getHistorico(id_usuario, MAX_PAGINAS, MIN_ANO, MAX_ANO));

        // Calcular a similaridade cosseno de cada vetor, salvando em PriorityQueue
        PriorityQueue<Entry> minHeap = new PriorityQueue<>(K, (a, b) -> Float.compare(a.value, b.value));
        for (int i = 0; i < numUsuarios; i++) {
            float sim = similaridade(vetorUsuario, vetores[i]);
            if (minHeap.size() < K){
                minHeap.offer(new Entry(sim, i));
            } else if (sim > minHeap.peek().value) {
                minHeap.poll();
                minHeap.offer(new Entry(sim, i));
            }
        }

        // Extrair os livros (ignorar se o usuário já tiver lido)
        List<Integer> topKUsuarios = new ArrayList<>(K);
        List<Livro> topKLivros = new ArrayList<>(K);
        Set<Integer> historicoSet = avaliacaoService.findLivro_idByUsuario_id(id_usuario);
        while (!minHeap.isEmpty()) {
            Entry e = minHeap.poll(); 
            topKUsuarios.add(e.index);
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

    public VetorLivro[] readVetores(){
        return vetorLivroRepository.findAllArray();
    }

    public VetorUsuario[] readVetoresUsuario(){
        return vetorUsuarioRepository.findAllArray();
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
            pageable = PageRequest.of(index, batchSize);
            livros = livroService.findAll(pageable);
            for (Livro livro : livros) {
                vetores.add(new VetorLivro(livro, MAX_PAGINAS, MIN_ANO, MAX_ANO));
            }
            vetorLivroRepository.saveAllAndFlush(vetores);
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
            vetores[i] = new VetorUsuario(usuario_id, usuarioService.getHistorico(usuario_id, MAX_PAGINAS, MIN_ANO, MAX_ANO));
            i++;
        }
        vetorUsuarioRepository.saveAll(List.of(vetores));
    }
}
