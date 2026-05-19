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
            onError = { exception ->
                activity?.runOnUiThread {
                    // Se falhar a extração na internet (Thread), usamos Semáforos/Threads para pelo menos exibir os mocks (Fallback em tempo real)
                    val fallbackData = listOf(
                        FeedPost(
                            id = 1,
                            authorName = "ANPD Oficial",
                            authorUsername = "@anpd_gov",
                            authorInitials = "A",
                            timeAgo = "Agora",
                            content = "Proteção de Dados é tema de Fórum com Educadores Físicos e ANPD. Evento promovido teve por objetivo estreitar o diálogo. Gestão, ECA Digital e atuação dos encarregados também estavam entre os assuntos abordados.",
                            linkTitle = "Ler artigo completo no gov.br",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-forum-confef",
                            commentsCount = 12,
                            repostsCount = 5,
                            likesCount = 48
                        ),
                        FeedPost(
                            id = 2,
                            authorName = "Sistema Offline",
                            authorUsername = "@alerta",
                            authorInitials = "!",
                            timeAgo = "1m",
                            content = "Conexão com a ANPD falhou ou está lenta (${exception.localizedMessage}). Exibindo feed armazenado localmente.",
                            linkTitle = null,
                            linkUrl = null,
                            commentsCount = 0,
                            repostsCount = 0,
                            likesCount = 0
                        ),
                        FeedPost(
                            id = 3,
                            authorName = "Guia da LGPD",
                            authorUsername = "@guia_lgpd",
                            authorInitials = "G",
                            timeAgo = "5h",
                            content = "Você sabia? A LGPD garante o direito de portabilidade dos seus dados. Isso significa que você pode solicitar que uma empresa envie suas informações para outra, de forma estruturada e legível.",
                            linkTitle = null,
                            linkUrl = null,
                            commentsCount = 34,
                            repostsCount = 89,
                            likesCount = 312
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
