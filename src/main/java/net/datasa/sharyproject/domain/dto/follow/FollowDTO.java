package net.datasa.sharyproject.domain.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {

    private String followerId;
    private String followingId;
    private LocalDateTime followDate;


}
