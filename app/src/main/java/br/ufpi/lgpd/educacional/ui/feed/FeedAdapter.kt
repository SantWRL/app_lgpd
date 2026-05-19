package br.ufpi.lgpd.educacional.ui.feed

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ItemFeedPostBinding

class FeedAdapter : ListAdapter<FeedPost, FeedAdapter.FeedViewHolder>(FeedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedViewHolder(private val binding: ItemFeedPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: FeedPost) {
            binding.apply {
                postAuthorName.text = post.authorName
                postAuthorUsername.text = post.authorUsername
                postAvatarInitial.text = post.authorInitials
                postTime.text = post.timeAgo
                postContent.text = post.content
                
                postCommentsCount.text = post.commentsCount.toString()
                postRepostsCount.text = post.repostsCount.toString()
                postLikesCount.text = post.likesCount.toString()

                if (post.linkTitle != null) {
                    postLinkCard.visibility = View.VISIBLE
                    postLinkTitle.text = post.linkTitle
                    postLinkCard.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkUrl ?: "https://gov.br/anpd"))
                        it.context.startActivity(intent)
                    }
                } else {
                    postLinkCard.visibility = View.GONE
                }

                updateLikeUi(post)

                postLikeIcon.setOnClickListener {
                    post.isLiked = !post.isLiked
                    post.likesCount += if (post.isLiked) 1 else -1
                    updateLikeUi(post)
                }
            }
        }

        private fun updateLikeUi(post: FeedPost) {
            binding.postLikesCount.text = post.likesCount.toString()
            if (post.isLiked) {
                binding.postLikeIcon.setColorFilter(binding.root.context.getColor(R.color.error)) // Red heart
            } else {
                binding.postLikeIcon.setColorFilter(binding.root.context.getColor(R.color.text_secondary))
            }
        }
    }

    class FeedDiffCallback : DiffUtil.ItemCallback<FeedPost>() {
        override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem == newItem
        }
    }
}
