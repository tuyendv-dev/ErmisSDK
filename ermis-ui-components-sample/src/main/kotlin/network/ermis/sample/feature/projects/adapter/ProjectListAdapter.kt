package network.ermis.sample.feature.projects.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.core.models.User
import network.ermis.chat.ui.sample.databinding.ItemProjectErmisBinding

internal class ProjectListAdapter :
    ListAdapter<ErmisProject, ProjectListAdapter.ErmisProjectViewHolder>(ErmisProjectDiffCallback) {

    var onClickProjectListener: ((ErmisProject) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErmisProjectViewHolder {
        return ItemProjectErmisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let { binding ->
                ErmisProjectViewHolder(binding)
            }
    }

    override fun onBindViewHolder(holder: ErmisProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ErmisProjectViewHolder(
        private val binding: ItemProjectErmisBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var project: ErmisProject

        init {
            binding.root.setOnClickListener {
                onClickProjectListener?.invoke(project)
            }
        }

        internal fun bind(project: ErmisProject) {
            this.project = project
            binding.projectNameLabel.text = project.project_name
            binding.projectAvatarView.setUser(
                User(
                    id = project.project_id,
                    name = project.project_name,
                    image = project.image ?: ""
                )
            )
            binding.unreadCountBadge.isVisible = project.hasUnread
        }
    }

    private object ErmisProjectDiffCallback : DiffUtil.ItemCallback<ErmisProject>() {
        override fun areItemsTheSame(oldItem: ErmisProject, newItem: ErmisProject): Boolean {
            return oldItem.project_id == newItem.project_id
        }

        override fun areContentsTheSame(oldItem: ErmisProject, newItem: ErmisProject): Boolean {
            // Comparing only properties used by the ViewHolder
            return oldItem.project_id == newItem.project_id
        }
    }
}
