package network.ermis.offline.repository.domain.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntity: UserEntity)

    @Query("SELECT * FROM $USER_ENTITY_TABLE_NAME WHERE stream_chat_user.id IN (:ids)")
    suspend fun select(ids: List<String>): List<UserEntity>

    @Query("SELECT * FROM $USER_ENTITY_TABLE_NAME WHERE stream_chat_user.id IN (:id)")
    suspend fun select(id: String): UserEntity?

    @Query("SELECT * FROM $USER_ENTITY_TABLE_NAME ORDER BY name ASC LIMIT :limit OFFSET :offset")
    fun selectAllUser(limit: Int, offset: Int): List<UserEntity>

    @Query(
        "SELECT * FROM $USER_ENTITY_TABLE_NAME " +
            "WHERE name " +
            "LIKE :searchString " +
            "ORDER BY name " +
            "ASC LIMIT :limit " +
            "OFFSET :offset",
    )
    fun selectUsersLikeName(searchString: String, limit: Int, offset: Int): List<UserEntity>

    @Query("DELETE FROM $USER_ENTITY_TABLE_NAME")
    suspend fun deleteAll()
}
