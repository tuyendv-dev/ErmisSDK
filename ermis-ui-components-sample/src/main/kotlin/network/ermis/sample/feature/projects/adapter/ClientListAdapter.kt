package network.ermis.sample.feature.projects.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.client.api.model.response.ErmisClientModel
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.core.models.User
import network.ermis.chat.ui.sample.databinding.ItemClientErmisBinding

internal class ClientListAdapter :
    ListAdapter<ErmisClientModel, ClientListAdapter.ErmisClientViewHolder>(ErmisClientDiffCallback) {

    var onClickClientListener: ((ErmisClientModel) -> Unit)? = null
    var onClickProjectListener: ((ErmisProject) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErmisClientViewHolder {
        return ItemClientErmisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let { binding ->
                ErmisClientViewHolder(binding)
            }
    }

    override fun onBindViewHolder(holder: ErmisClientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ErmisClientViewHolder(
        private val binding: ItemClientErmisBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(client: ErmisClientModel) {
            if (client.projects.size == 1) {
                val projectOnly = client.projects.first()
                binding.clientNameLabel.text = projectOnly.project_name
                binding.clientAvatarView.setUser(User(id = projectOnly.project_id, name = projectOnly.project_name, image = projectOnly.image ?: ""))
                binding.rootClient.setOnClickListener {
                    onClickProjectListener?.invoke(projectOnly)
                }
                binding.recyclerviewProject.isVisible = false
                binding.unreadCountBadge.isVisible = projectOnly.hasUnread
            } else {
                binding.unreadCountBadge.isVisible = false
                binding.clientNameLabel.text = client.client_name
                binding.clientAvatarView.setUser(User(id = client.client_id, name = client.client_name, image = client.client_avatar ?: ""))
                binding.rootClient.setOnClickListener {
                    onClickClientListener?.invoke(client)
                }
                binding.recyclerviewProject.isVisible = client.showProjects == true && client.projects.isNotEmpty()
                // binding.recyclerviewProject.isVisible = true
                val projectAdapter = ProjectListAdapter()
                projectAdapter.submitList(client.projects)
                projectAdapter.onClickProjectListener = { project ->
                    onClickProjectListener?.invoke(project)
                }
                binding.recyclerviewProject.apply {
                    layoutManager = LinearLayoutManager(this.context)
                    setHasFixedSize(true)
                    adapter = projectAdapter
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            LinearLayoutManager.VERTICAL,
                        ).apply {
                            setDrawable(
                                AppCompatResources.getDrawable(
                                    context,
                                    network.ermis.ui.components.R.drawable.divider
                                )!!
                            )
                        },
                    )
                }
            }
        }
    }

    private object ErmisClientDiffCallback : DiffUtil.ItemCallback<ErmisClientModel>() {
        override fun areItemsTheSame(oldItem: ErmisClientModel, newItem: ErmisClientModel): Boolean {
            return oldItem.client_id == newItem.client_id && oldItem.projects.size == newItem.projects.size
                && oldItem.projects.last().project_id == newItem.projects.last().project_id
        }

        override fun areContentsTheSame(oldItem: ErmisClientModel, newItem: ErmisClientModel): Boolean {
            // Comparing only properties used by the ViewHolder
            val comparing =  oldItem.client_id == newItem.client_id && oldItem.showProjects == newItem.showProjects
                && oldItem.projects.size == newItem.projects.size
                && oldItem.projects.last().project_id == newItem.projects.last().project_id
            return comparing
        }
    }
}
