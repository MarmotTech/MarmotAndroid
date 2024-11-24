package me.jinheng.cityullm.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.jinheng.cityullm.utils.MapConverter

@Entity(tableName="benchmarkResults")
class BenchmarkResult(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val model_size: Int,
    val model_name: String,
    val model_n_params: Int,
    val num_threads: Int,
    val prefill_throughput: Float,
    val decode_throughput: Float,
    val task_results: Map<String, Float>
)

@Dao
interface BenchmarkResultsDao {
    @Query("SELECT * FROM benchmarkResults")
    fun getResults(): LiveData<List<BenchmarkResult>>

    @Insert
    fun addResults(result: List<BenchmarkResult>)
}

@Database(entities = [(BenchmarkResult::class)], version = 1)
@TypeConverters(MapConverter::class)
abstract class BenchmarkResultsDatabase: RoomDatabase() {

    abstract fun benchmarkResultsDao(): BenchmarkResultsDao

    companion object {
        @Volatile
        private var Instance: BenchmarkResultsDatabase? = null

        fun getDatabase(context: Context): BenchmarkResultsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    BenchmarkResultsDatabase::class.java,
                    "benchmark_results_database"
                )
                .build()
                .also { Instance = it }
            }
        }
    }
}

class BenchmarkResultsRepository(private val benchmarkResultsDao: BenchmarkResultsDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val benchmarkResults: LiveData<List<BenchmarkResult>> = benchmarkResultsDao.getResults()

    fun addResults(result: List<BenchmarkResult>) {
        coroutineScope.launch(Dispatchers.IO) {
            benchmarkResultsDao.addResults(result)
        }
    }
}
