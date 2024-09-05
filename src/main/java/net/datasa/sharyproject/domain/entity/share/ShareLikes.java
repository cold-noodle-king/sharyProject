package net.datasa.sharyproject.domain.entity.share;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * FollowEntity 클래스의 복합키
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareLikes implements Serializable {

    private Integer shareNoteNum;
    private Integer likedNum;


}

