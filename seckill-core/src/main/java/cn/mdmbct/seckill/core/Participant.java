package cn.mdmbct.seckill.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * The user who want to seckill the award.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 23:24
 * @modified mdmbct
 * @since 0.1
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Participant implements Serializable {

    private static final long serialVersionUID = 5543328376022261131L;

    private final String id;

    private final String name;

    /**
     * username is ""
     * @param id userid
     */
    public Participant(String id) {
        this.id = id;
        this.name = "";
    }
}
