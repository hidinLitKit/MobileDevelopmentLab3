package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import data.Faculty
import data.Group
import data.Student
import java.lang.reflect.TypeVariable

@Database(
    entities = [Faculty::class, Group::class, Student::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(DBConverters::class)

abstract class MyDatabase : RoomDatabase(){
    abstract fun MyDAO(): MyDAO

    companion object{
        @Volatile
        private var INSTANCE:  MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase{
            return INSTANCE ?: synchronized(this){
                buildDatabase(context).also { INSTANCE = it }
            }
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            MyDatabase::class.java,
            "list_database"
        ).fallbackToDestructiveMigration(false).build()
    }
}