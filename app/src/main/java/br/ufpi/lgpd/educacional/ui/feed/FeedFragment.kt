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
        val mockData = listOf(
            FeedPost(
                id = 1,
                authorName = "ANPD Oficial",
                authorUsername = "@anpd_gov",
                authorInitials = "A",
                timeAgo = "2h",
                content = "Proteção de Dados é tema de Fórum com Educadores Físicos e ANPD. Evento promovido teve por objetivo estreitar o diálogo. Gestão, ECA Digital e atuação dos encarregados também estavam entre os assuntos abordados.",
                linkTitle = "Ler artigo completo no gov.br",
                linkUrl = "https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-forum-confef",
                commentsCount = 12,
                repostsCount = 5,
                likesCount = 48
            ),
            FeedPost(
                id = 2,
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
            ),
            FeedPost(
                id = 3,
                authorName = "Professor Silva",
                authorUsername = "@profsilva_law",
                authorInitials = "S",
                timeAgo = "1d",
                content = "A diferença entre 'Dado Pessoal' e 'Dado Pessoal Sensível' é fundamental. O sensível (como biometria, religião, saúde) exige bases legais muito mais rigorosas para o tratamento. Fiquem atentos nas aulas do app!",
                linkTitle = null,
                linkUrl = null,
                commentsCount = 8,
                repostsCount = 15,
                likesCount = 105
            ),
            FeedPost(
                id = 4,
                authorName = "Tech Privacy News",
                authorUsername = "@techprivacybr",
                authorInitials = "T",
                timeAgo = "2d",
                content = "Multas da ANPD começam a ser aplicadas a empresas de pequeno porte que ignoram a LGPD. A adaptação não é apenas para gigantes de tecnologia. Toda empresa que coleta dados precisa estar adequada.",
                linkTitle = "Veja as últimas sanções aplicadas",
                linkUrl = "https://www.gov.br/anpd/pt-br",
                commentsCount = 56,
                repostsCount = 112,
                likesCount = 420
            )
        )
        
        adapter.submitList(mockData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
