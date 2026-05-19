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
                    // Se falhar, adicionamos um post de erro com mock alternativo
                    val errorData = mockData.toMutableList()
                    errorData[0] = FeedPost(
                        id = 1,
                        authorName = "Erro de Conexão",
                        authorUsername = "@sistema",
                        authorInitials = "!",
                        timeAgo = "Agora",
                        content = "Não foi possível carregar as notícias em tempo real: ${exception.localizedMessage}. Verifique sua internet.",
                        linkTitle = "Ir para o portal ANPD",
                        linkUrl = "https://www.gov.br/anpd/pt-br",
                        commentsCount = 0,
                        repostsCount = 0,
                        likesCount = 0
                    )
                    adapter.submitList(errorData)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
