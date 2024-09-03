package net.datasa.sharyproject.domain.entity.Follow;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
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
public class FollowId implements Serializable {

    private String followerId;
    private String followingId;




}

