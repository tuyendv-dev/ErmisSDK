package network.ermis.offline.repository.domain.syncState

import network.ermis.client.sync.SyncState

internal fun SyncStateEntity.toModel() =
    SyncState(userId, activeChannelIds, lastSyncedAt, rawLastSyncedAt, markedAllReadAt)

internal fun SyncState.toEntity() =
    SyncStateEntity(userId, activeChannelIds, lastSyncedAt, rawLastSyncedAt, markedAllReadAt)
