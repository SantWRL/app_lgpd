package br.ufpi.lgpd.educacional.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        loadFeedData()
    }

    private fun setupRecyclerView() {
        adapter = FeedAdapter()
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }

    private fun loadFeedData() {
        // Dados estáticos iniciais caso a internet falhe
        val mockData = listOf(
            FeedPost(
                id = 1,
                authorName = "Carregando Notícias...",
                authorUsername = "@anpd_gov",
                authorInitials = "A",
                timeAgo = "Agora",
                content = "Conectando ao portal Gov.br para buscar as atualizações mais recentes sobre a LGPD...",
                linkTitle = null,
                linkUrl = null,
                commentsCount = 0,
                repostsCount = 0,
                likesCount = 0
            )
        )
        adapter.submitList(mockData)

        // Extração em Tempo Real (Scraping com Threads e Semáforos)
        NewsScraper.fetchNews(
            onSuccess = { posts ->
                // O resultado vem de uma Thread em background. Precisamos atualizar a UI na Main Thread.
                activity?.runOnUiThread {
                    adapter.submitList(posts)
                }
            },
            onError = { _ ->
                activity?.runOnUiThread {
                    // Se falhar a extração na internet (Thread), usamos Semáforos/Threads para pelo menos exibir os mocks (Fallback em tempo real)
                    val fallbackData = listOf(
                        FeedPost(
                            id = 1,
                            authorName = "ANPD Oficial",
                            authorUsername = "@anpd_gov",
                            authorInitials = "A",
                            timeAgo = "Hoje",
                            content = "Proteção de Dados é tema de Fórum com Educadores Físicos e ANPD. Evento promovido pelo CONFEF teve por objetivo estreitar o diálogo com a Agência.",
                            linkTitle = "Ler artigo completo no gov.br",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-forum-confef",
                            commentsCount = 23,
                            repostsCount = 14,
                            likesCount = 105
                        ),
                        FeedPost(
                            id = 2,
                            authorName = "ANPD Fiscalização",
                            authorUsername = "@anpd_fiscaliza",
                            authorInitials = "F",
                            timeAgo = "1d",
                            content = "ANPD publica nota técnica sobre tratamento de dados pessoais no setor de telecomunicações. Operadoras devem se adequar às diretrizes da LGPD.",
                            linkTitle = "Ver no portal oficial",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias",
                            commentsCount = 45,
                            repostsCount = 67,
                            likesCount = 289
                        ),
                        FeedPost(
                            id = 3,
                            authorName = "ANPD Oficial",
                            authorUsername = "@anpd_gov",
                            authorInitials = "A",
                            timeAgo = "3d",
                            content = "Conheça seus direitos como titular de dados! A LGPD garante acesso, correção e eliminação dos seus dados pessoais. Saiba como exercer esses direitos.",
                            linkTitle = "Saiba mais sobre Titular de Dados",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/titular-de-dados-1",
                            commentsCount = 18,
                            repostsCount = 21,
                            likesCount = 134
                        ),
                        FeedPost(
                            id = 4,
                            authorName = "Governança ANPD",
                            authorUsername = "@gov_dados",
                            authorInitials = "G",
                            timeAgo = "1w",
                            content = "O Conselho Nacional de Proteção de Dados Pessoais e da Privacidade reúne representantes de diversos setores da sociedade para debater políticas públicas de proteção de dados.",
                            linkTitle = "Conheça o CNPD",
                            linkUrl = "https://www.gov.br/anpd/pt-br/cnpd-2/cnpd",
                            commentsCount = 5,
                            repostsCount = 12,
                            likesCount = 88
                        )
                    )
                    adapter.submitList(fallbackData)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
