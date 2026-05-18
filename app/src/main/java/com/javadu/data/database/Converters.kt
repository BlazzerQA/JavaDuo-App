package com.javadu.data.database

import androidx.room.TypeConverter
import com.javadu.data.database.entities.BonusType

class Converters {
    @TypeConverter
    fun fromBonusType(type: BonusType): String = type.name

    @TypeConverter
    fun toBonusType(value: String): BonusType = BonusType.valueOf(value)
}
