package br.sistema_recomendacoes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.sistema_recomendacoes.config.AppProperties;
import br.sistema_recomendacoes.config.AppProperties.Limits;
import br.sistema_recomendacoes.config.AppProperties.Weights;
import br.sistema_recomendacoes.dto.LivroResponseDTO;
import br.sistema_recomendacoes.mapper.LivroMapper;
import br.sistema_recomendacoes.model.Livro;
import br.sistema_recomendacoes.model.Usuario;
import br.sistema_recomendacoes.model.VetorLivro;
import br.sistema_recomendacoes.model.VetorUsuario;
import br.sistema_recomendacoes.repository.VetorLivroRepository;
import br.sistema_recomendacoes.repository.VetorUsuarioRepository;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.IntSet;

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

    @Autowired
    public RecomendacaoService(AppProperties appProperties){
        Limits limits = appProperties.getLimits();
        MAX_PAGINAS = limits.getMaxPaginas();
        MIN_ANO = limits.getMinAno();
        MAX_ANO = limits.getMaxAno();

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
                        similaridadeGeneros * PESO_GENERO) / 100.0f;
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

    public List<LivroResponseDTO> recomendar(Integer id_usuario, int K){
        int numLivros = livroService.count();
        VetorLivro[] vetores = readVetores();
        VetorUsuario vetorUsuario = new VetorUsuario(id_usuario, usuarioService.getHistorico(id_usuario, MAX_PAGINAS, MIN_ANO, MAX_ANO));
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

        List<LivroResponseDTO> recomendacoes = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            Entry entry = minHeap.poll();
            recomendacoes.add(LivroMapper.toResponseDTO(livroService.findById(vetores[entry.index].getId())));
        }
        return recomendacoes;
    }

    // TODO: implementar recomendação por filtragem colaborativa

    public VetorLivro[] readVetores(){
        return vetorLivroRepository.findAllArray();
    }

    public void updateVetorLivros(){
        int numLivros = livroService.count();
        if (numLivros == 0){
            throw new UnsupportedOperationException();
        }
        VetorLivro[] vetores = new VetorLivro[numLivros];
        List<Livro> livros = livroService.findAllList();
        if (livros.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        int i=0;
        for (Livro livro : livros) {
            vetores[i] = new VetorLivro(livro, MAX_PAGINAS, MIN_ANO, MAX_ANO);
            i++;
        }
        vetorLivroRepository.saveAll(List.of(vetores));
    }

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
