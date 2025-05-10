package database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class DBConverters {
    @TypeConverter
    fun toUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUID(uuid: UUID?): String?{
        return uuid?.toString()
    }

    @TypeConverter
    fun fromDate(date: Date?): Long?{
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date?{
        return millisSinceEpoch?.let {
            Date(it)
        }
    }
}