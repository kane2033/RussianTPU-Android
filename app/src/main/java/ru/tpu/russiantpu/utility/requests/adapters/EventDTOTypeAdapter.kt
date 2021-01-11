package ru.tpu.russiantpu.utility.requests.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.tpu.russiantpu.dto.EventDTO
import java.lang.reflect.Type

class EventDTOTypeAdapter : JsonDeserializer<EventDTO> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EventDTO {
        TODO("Not yet implemented")
    }

}