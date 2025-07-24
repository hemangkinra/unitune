package in.weekend.unitune.dto.external.jiosaavn.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artists {
    private List<Artist> primary;
    private List<Artist> featured;
    private List<Artist> all;
}
