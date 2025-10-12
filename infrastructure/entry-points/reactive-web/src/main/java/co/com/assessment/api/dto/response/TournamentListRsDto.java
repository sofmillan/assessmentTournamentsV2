package co.com.assessment.api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentListRsDto {
    private List<TournamentRsDto> tournaments;
}
