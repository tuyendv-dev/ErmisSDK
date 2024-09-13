package network.ermis.sample.feature.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import network.ermis.client.ErmisClient
import network.ermis.client.api.model.response.ErmisChain
import network.ermis.client.api.model.response.ErmisClientModel
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.client.utils.extensions.channelIdToProjectId
import network.ermis.client.utils.extensions.hasUnread
import network.ermis.state.utils.Event
import network.ermis.ui.viewmodel.channels.ChannelListViewModel.PaginationState
import network.ermis.sample.application.App
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProjectListViewModel(
    private val chatClient: ErmisClient = ErmisClient.instance(),
) : ViewModel() {
    private val logger by taggedLogger("ProjectListViewModel")

    private val _state: MediatorLiveData<UiState> = MediatorLiveData()
    val state: LiveData<UiState> = _state.distinctUntilChanged()

    private val _events: MutableLiveData<Event<UiEvent>> = MutableLiveData()
    val events: LiveData<Event<UiEvent>> = _events

    private var _projectUnreadMap: MutableStateFlow<MutableMap<String, MutableMap<String, Boolean>>> =
        MutableStateFlow(mutableMapOf())
    private val projectUnreadMap: StateFlow<Map<String, Map<String, Boolean>>> = _projectUnreadMap

    data class UiState(
        val allChainIdFilter: List<Int> = listOf(),
        val allChainsJoined: List<ErmisChain> = listOf(),
        val allChainsNotJoined: List<ErmisChain> = listOf(),
        val chainIdSelected: Int = 1,
        val filterJoined: Boolean = true,
        val search: String = "",
        val listClientDisplay: List<ErmisClientModel> = listOf(),
    )

    init {
        getChains()
        // getUnreadAllChannels()
    }

    fun getUnreadAllChannels() {
        chatClient.queryAllChannelOfAllProject().enqueue { result ->
            when (result) {
                is Result.Success -> {
                    logger.d { "queryAllChannelOfAllProject Success channel size= ${result.value.size}" }
                    val channels = result.value
                    channels.forEach { channel ->
                        val projectId = channel.id.channelIdToProjectId()
                        _projectUnreadMap.apply {
                            val projectMap = value
                            val channelMap: MutableMap<String, Boolean> = if (projectMap[projectId].isNullOrEmpty()) {
                                mutableMapOf(channel.id to channel.hasUnread)
                            } else {
                                projectMap[projectId]!!.plus(channel.id to channel.hasUnread).toMutableMap()
                            }
                            projectMap[projectId] = channelMap
                            value = projectMap
                        }
                    }
                    filterClient()
                }

                is Result.Failure -> {
                    logger.e { "queryAllChannelOfAllProject Failure= ${result.value}" }
                }
            }
        }
    }

    private fun getChains() {
        chatClient.getChains().enqueue { result ->
            when (result) {
                is Result.Success -> {
                    val chainIds = result.value.chains.filter { it != ErmisClient.ERMIS_CHAIN_ID }
                    val chainJoined = result.value.joined.filter { it.chain_id != ErmisClient.ERMIS_CHAIN_ID }
                    val chainNotJoined = result.value.notJoined.filter { it.chain_id != ErmisClient.ERMIS_CHAIN_ID }
                    val chainIdSelectedBefore = App.instance.userRepository.getChainId()
                    setState {
                        copy(
                            allChainIdFilter = chainIds,
                            chainIdSelected = chainIdSelectedBefore,
                            allChainsJoined = chainJoined,
                            allChainsNotJoined = chainNotJoined,
                        )
                    }
                    filterClient()
                }

                is Result.Failure -> {
                    logger.e { "getChains Failure= ${result.value}" }
                }
            }
        }
    }

    fun joinNewProject(project: ErmisProject) {
        chatClient.joinNewProject(project.project_id).enqueue { result ->
            when (result) {
                is Result.Success -> {
                    val chainIds = result.value.chains.filter { it != ErmisClient.ERMIS_CHAIN_ID }
                    val chainJoined = result.value.joined.filter { it.chain_id != ErmisClient.ERMIS_CHAIN_ID }
                    val chainNotJoined = result.value.notJoined.filter { it.chain_id != ErmisClient.ERMIS_CHAIN_ID }
                    setState {
                        copy(
                            allChainIdFilter = chainIds,
                            allChainsJoined = chainJoined,
                            allChainsNotJoined = chainNotJoined,
                        )
                    }
                    filterClient()
                    _events.value = Event(UiEvent.NavigateToChannelListInProject(project))
                }

                is Result.Failure -> {
                    logger.e { "joinNewProject Failure= ${result.value}" }
                }
            }
        }
    }

    private fun filterClient() {
        val chainId = _state.value?.chainIdSelected ?: 1
        val filterJoined = _state.value?.filterJoined ?: true
        val joined = _state.value?.allChainsJoined ?: listOf()
        val notJoined = _state.value?.allChainsNotJoined ?: listOf()
        val clients: List<ErmisClientModel> = if (filterJoined) {
            val chains = joined.filter { it.chain_id == chainId }
            if (chains.isEmpty()) {
                listOf()
            } else {
                chains.first().clients
            }
        } else {
            val chains = notJoined.filter { it.chain_id == chainId }
            if (chains.isEmpty()) {
                listOf()
            } else {
                chains.first().clients
            }
        }
        val search = _state.value?.search ?: ""
        val clientDisplay = ArrayList<ErmisClientModel> ()
        clients.forEach { client ->
            val projects = client.projects.filter { it.project_name.contains(search, true) }
            if (projects.isNotEmpty()) {
                val projectMap = projectUnreadMap.value
                projects.forEach { project ->
                    project.hasUnread =
                        projectMap[project.project_id] != null && projectMap[project.project_id]!!.containsValue(true)
                }
                clientDisplay.add(
                    ErmisClientModel(
                        client_name = client.client_name,
                        client_id = client.client_id,
                        client_avatar = client.client_avatar,
                        projects = projects
                    )
                )
            }
        }
        setState { copy(listClientDisplay = clientDisplay) }
    }

    fun selectChain(chainId: Int) {
        if (_state.value?.chainIdSelected != chainId) {
            setState {
                copy(
                    chainIdSelected = chainId,
                )
            }
            App.instance.userRepository.setChainId(chainId)
        }
        filterClient()
    }

    fun filterJoined(joined: Boolean) {
        setState {
            copy(
                filterJoined = joined,
            )
        }
        filterClient()
    }

    fun querySearchProject(search: String) {
        setState {
            copy(
                search = search,
            )
        }
        filterClient()
    }

    fun expanClient(client: ErmisClientModel) {
        val clients = _state.value?.listClientDisplay ?: return
        val newClient = ArrayList<ErmisClientModel> ()
        clients.forEach { item ->
            if (item.client_id == client.client_id) {
                newClient.add(item.copy(showProjects = item.showProjects == null || item.showProjects!!.not()))
            } else {
                newClient.add(item)
            }
        }
        setState {
            copy(listClientDisplay = newClient)
        }
    }

    /**
     * Sets the current home screen state.
     *
     * @param reducer A lambda function that returns [PaginationState].
     */
    private fun setState(reducer: UiState.() -> UiState) {
        _state.value = reducer(_state.value ?: UiState())
    }

    sealed class UiEvent {
        class NavigateToChannelListInProject(val project: ErmisProject) : UiEvent()
    }
}