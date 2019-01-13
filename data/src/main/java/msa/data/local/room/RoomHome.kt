package msa.data.local.room

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.reactivex.Observable
import msa.domain.entities.Note
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */
@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null, var title: String,
    var body: String,
    var date: Date,
    var imagePath: String?
)

object NoteMapper {

    fun transform(note: Note) =
        NoteEntity(id = note.id, title = note.title, body = note.body, date = note.date, imagePath = note.imagePath)

    fun transform(noteEntity: NoteEntity) =
        Note(
            id = noteEntity.id,
            title = noteEntity.title,
            body = noteEntity.body,
            date = noteEntity.date,
            imagePath = noteEntity.imagePath
        )
}

@Dao
interface NoteDao {

    @Query("SELECT * FROM noteentity")
    fun getNotes(): Observable<List<NoteEntity>>

    @Insert
    fun insertNote(noteEntity: NoteEntity)

    @Update
    fun updateNote(noteEntity: NoteEntity)

    @Delete
    fun deleteNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM noteentity WHERE id = :id")
    fun getNote(id: Int): Observable<NoteEntity>

}

@Database(entities = [NoteEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE NoteEntity ADD COLUMN imagePath STRING")
    }
}