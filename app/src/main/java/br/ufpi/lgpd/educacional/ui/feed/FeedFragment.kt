package br.ufpi.lgpd.educacional.ui.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FeedAdapter
    private var allPosts: List<FeedPost> = emptyList()
    private var activeCategory = "Todas"
    private var searchQuery = ""

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
        setupSearch()
        setupCategoryChips()
        setupSwipeRefresh()
        loadFeedData()
    }

    private fun setupRecyclerView() {
        adapter = FeedAdapter(onCategoryClick = { category ->
            activeCategory = category
            updateCategoryChips()
            applyFilters()
        })
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim()?.lowercase() ?: ""
                binding.clearSearchBtn.visibility = if (searchQuery.isNotEmpty()) View.VISIBLE else View.GONE
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.clearSearchBtn.setOnClickListener {
            binding.searchInput.text?.clear()
        }
    }

    private fun setupCategoryChips() {
        val container = binding.categoryChipsContainer
        container.removeAllViews()

        for (cat in NewsScraper.categories) {
            container.addView(createCategoryChip(cat))
        }

        updateCategoryChips()
    }

    private fun createCategoryChip(category: String): TextView {
        val chip = TextView(requireContext()).apply {
            text = category
            setPadding(20, 10, 20, 10)
            setTextSize(13f)
            setTextColor(
                if (category == activeCategory) ContextCompat.getColor(context, R.color.primary)
                else ContextCompat.getColor(context, R.color.text_secondary)
            )
            setTypeface(null, android.graphics.Typeface.BOLD)
            setBackgroundResource(
                if (category == activeCategory) R.drawable.badge_background
                else R.drawable.bg_glass_card
            )
            if (category == activeCategory) {
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
            } else {
                backgroundTintList = null
            }
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 10
            }
            isClickable = true
            isFocusable = true
            setOnClickListener {
                activeCategory = category
                updateCategoryChips()
                applyFilters()
            }
        }
        return chip
    }

    private fun updateCategoryChips() {
        val container = binding.categoryChipsContainer
        for (i in 0 until container.childCount) {
            val chip = container.getChildAt(i) as? TextView ?: continue
            val cat = chip.text.toString()
            val isActive = cat == activeCategory
            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isActive) R.color.primary else R.color.text_secondary
                )
            )
            chip.setBackgroundResource(
                if (isActive) R.drawable.badge_background else R.drawable.bg_glass_card
            )
            if (isActive) {
                chip.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            } else {
                chip.backgroundTintList = null
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent, R.color.success)
        binding.swipeRefresh.setOnRefreshListener {
            loadFeedData()
        }
    }

    private fun loadFeedData() {
        NewsScraper.fetchNews(
            onSuccess = { posts ->
                allPosts = posts
                binding.feedNewsCount.text = "${posts.size} notícias"
                applyFilters()
                binding.swipeRefresh.isRefreshing = false
            },
            onError = { _ ->
                binding.swipeRefresh.isRefreshing = false
            }
        )
    }

    private fun applyFilters() {
        val filtered = allPosts.filter { post ->
            val matchesCategory = activeCategory == "Todas" || post.category == activeCategory
            val matchesSearch = searchQuery.isEmpty() ||
                post.content.lowercase().contains(searchQuery) ||
                post.authorName.lowercase().contains(searchQuery) ||
                post.category.lowercase().contains(searchQuery)
            matchesCategory && matchesSearch
        }
        adapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
