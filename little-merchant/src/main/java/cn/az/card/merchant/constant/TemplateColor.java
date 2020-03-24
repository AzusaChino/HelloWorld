package cn.az.card.merchant.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author az
 * @date 2020/3/17
 */
@Getter
@AllArgsConstructor
public enum TemplateColor {

    /**
     * red
     */
    RED(1, "red"),
    /**
     * green
     */
    GREEN(2, "green"),
    /**
     * blue
     */
    BLUE(3, "blue"),
    ;

    private Integer id;
    private String color;

}
