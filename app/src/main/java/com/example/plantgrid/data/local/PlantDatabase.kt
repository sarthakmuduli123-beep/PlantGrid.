package com.example.plantgrid.data.local

import android.content.Context
import androidx.room.*
import com.example.plantgrid.data.model.PlantMood
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorNodeDao {
    @Query("SELECT * FROM sensor_nodes")
    fun getAllNodes(): Flow<List<SensorNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<SensorNodeEntity>)

    @Query("SELECT * FROM sensor_nodes WHERE id = :id")
    suspend fun getNodeById(id: String): SensorNodeEntity?

    @Insert
    suspend fun insertLog(log: TelemetryLogEntity)

    @Query("SELECT * FROM telemetry_logs WHERE nodeId = :nodeId AND timestamp >= :startTime")
    suspend fun getLogsForNode(nodeId: String, startTime: Long): List<TelemetryLogEntity>
}

class Converters {
    @TypeConverter
    fun fromPlantMood(value: PlantMood) = value.name

    @TypeConverter
    fun toPlantMood(value: String) = try {
        enumValueOf<PlantMood>(value)
    } catch (e: Exception) {
        PlantMood.UNKNOWN
    }
}

@Database(entities = [SensorNodeEntity::class, TelemetryLogEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun sensorNodeDao(): SensorNodeDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
