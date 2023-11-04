package team404.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;

import java.util.Map;

@JsonSerialize
@AllArgsConstructor
public class PlayerDeadRequest {
    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, Integer> materials;
}
