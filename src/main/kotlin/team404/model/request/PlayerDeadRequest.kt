package team404.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
data class PlayerDeadRequest(
    @JsonProperty val name: String,
    @JsonProperty val materials: Map<String, Int>
)