# 1. ОБЯЗАТЕЛЬНО: Сохраняем сигнатуры дженериков. Это прямое лекарство от IllegalStateException в TypeToken
-keepattributes Signature

# 2. Сохраняем дата-классы, которые парсит Gson.
# Иначе Gson попытается сериализовать поля как 'a', 'b', что сломает базу при чтении
-keep class com.chemlab.assistant.data.ReagentNeed { *; }
-keep class com.chemlab.assistant.data.EquipmentNeed { *; }

# Опционально: если у вас будут другие классы для Gson, проще оставить весь пакет
# -keep class com.chemlab.assistant.data.** { *; }