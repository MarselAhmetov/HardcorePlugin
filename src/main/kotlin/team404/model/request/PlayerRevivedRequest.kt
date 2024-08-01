package team404.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize
data class PlayerRevivedRequest(
    @JsonProperty val name: String
)