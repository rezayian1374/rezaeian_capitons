package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptionDao {
  @Query("SELECT * FROM caption_projects ORDER BY createdAt DESC")
  fun getAllProjects(): Flow<List<CaptionProjectEntity>>

  @Query("SELECT * FROM caption_projects WHERE id = :id LIMIT 1")
  suspend fun getProjectById(id: Long): CaptionProjectEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: CaptionProjectEntity): Long

  @Update
  suspend fun updateProject(project: CaptionProjectEntity)

  @Delete
  suspend fun deleteProject(project: CaptionProjectEntity)
}
