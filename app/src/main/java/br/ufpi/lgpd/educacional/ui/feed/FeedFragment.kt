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
                            content = "A ANPD (Autoridade Nacional de Proteção de Dados) publicou o Guia Orientativo sobre Cookies e Proteção de Dados Pessoais. Acesse as recomendações completas.",
                            linkTitle = "Ler artigo completo no gov.br",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-lanca-guia-orientativo-sobre-cookies-e-protecao-de-dados-pessoais",
                            commentsCount = 23,
                            repostsCount = 14,
                            likesCount = 105
                        ),
                        FeedPost(
                            id = 2,
                            authorName = "Fiscalização ANPD",
                            authorUsername = "@anpd_fiscaliza",
                            authorInitials = "F",
                            timeAgo = "1d",
                            content = "Nova regulamentação! A ANPD acaba de publicar o Regulamento de Dosimetria e Aplicação de Sanções Administrativas, trazendo mais segurança jurídica.",
                            linkTitle = "Ver detalhes da Dosimetria",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-publica-regulamento-de-dosimetria-e-aplicacao-de-sancoes-administrativas",
                            commentsCount = 45,
                            repostsCount = 67,
                            likesCount = 289
                        ),
                        FeedPost(
                            id = 3,
                            authorName = "ANPD Oficial",
                            authorUsername = "@anpd_gov",
                            authorInitials = "A",
                            timeAgo = "2d",
                            content = "O Setor Farmacêutico também precisa de atenção especial. Foi publicado o novo guia orientativo sobre tratamento de dados sensíveis nas farmácias e drogarias do país.",
                            linkTitle = "Acessar Guia do Setor Farmacêutico",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-publica-guia-orientativo-sobre-tratamento-de-dados-pessoais-no-setor-farmaceutico",
                            commentsCount = 18,
                            repostsCount = 21,
                            likesCount = 134
                        ),
                        FeedPost(
                            id = 4,
                            authorName = "Governança & Dados",
                            authorUsername = "@gov_dados",
                            authorInitials = "G",
                            timeAgo = "1w",
                            content = "Planejamento Estratégico 2024-2027 da ANPD já está disponível. Conheça as principais metas do órgão para a consolidação da cultura de proteção de dados no Brasil.",
                            linkTitle = "Ver Planejamento Estratégico",
                            linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/autoridade-nacional-de-protecao-de-dados-publica-seu-planejamento-estrategico-2024-2027",
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
