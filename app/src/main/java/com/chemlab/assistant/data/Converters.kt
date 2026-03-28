package com.chemlab.assistant.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromReagentNeedList(list: List<ReagentNeed>?): String = gson.toJson(list)

    @TypeConverter
    fun toReagentNeedList(json: String?): List<ReagentNeed> {
        if (json == null) return emptyList()
        val type = object : TypeToken<List<ReagentNeed>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromEquipmentNeedList(list: List<EquipmentNeed>?): String = gson.toJson(list)

    @TypeConverter
    fun toEquipmentNeedList(json: String?): List<EquipmentNeed> {
        if (json == null) return emptyList()
        val type = object : TypeToken<List<EquipmentNeed>>() {}.type
        return gson.fromJson(json, type)
    }
}