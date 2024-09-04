package com.dct.constant.enums;

/**
 * 数字枚举.
 *
 * @author magic
 * @data 2022/4/19
 */
public enum NumberEnum {

    /**
     * -1.
     */
    NEGATIVE_ONE(-1),

    /**
     * 0.
     */
    ZERO(0),

    /**
     * 1.
     */
    ONE(1),

    /**
     * 2.
     */
    TWO(2),

    /**
     * 3.
     */
    THREE(3),

    /**
     * 4.
     */
    FOUR(4),

    /**
     * 5.
     */
    FIVE(5),

    /**
     * 6.
     */
    SIX(6),

    /**
     * 7.
     */
    SEVEN(7),

    /**
     * 8.
     */
    EIGHT(8),

    /**
     * 9.
     */
    NINE(9),

    /**
     * 10.
     */
    TEN(10),

    /**
     * 13.
     */
    THIRTEEN(13),

    /**
     * 14.
     */
    FOURTEEN(14),

    /**
     * 15.
     */
    FIFTEEN(15),

    /**
     * 16.
     */
    SIXTEEN(16),
    /**
     * 17.
     */
    SEVENTEEN(17),

    /**
     * 18.
     */
    EIGHTEEN(18),

    /**
     * 19.
     */
    NINETEEN(19),

    /**
     * 20.
     */
    TWENTY(20),


    /**
     *
     */
    TWENTY_FIVE(25),
    TWENTY_SIX(26),
    TWENTY_SEVEN(27),
    /**
     * 30.
     */
    THIRTY(30),

    /**
     * 50.
     */
    FIFTY(50),
    /**
     * 60.
     */
    SIXTY(60),

    /**
     * 100.
     */
    ONE_HUNDRED(100),

    /**
     * 200.
     */
    TWO_HUNDRED(200),

    /**
     * 224.
     */
    TWO_HUNDRED_TWENTY_FOUR(224),

    /**
     * 225.
     */
    TWO_HUNDRED_TWENTY_FIVE(225),

    /**
     * 227.
     */
    TWO_HUNDRED_TWENTY_SEVEN(227),

    /**
     * 250.
     */
    TWO_HUNDRED_FIFTY(250),

    /**
     * 300.
     */
    THREE_HUNDRED(300),

    /**
     * 900.
     */
    NINE_HUNDRED(900),

    /**
     * 999.
     */
    NINE_HUNDRED_NINETY_NINE(999),

    /**
     * 1024.
     */
    ONE_THOUSAND_AND_TWENTY_FOUR(1024),


    /**
     * 6379
     */
    SIX_THOUSAND_THREE_HUNDRED_AND_SEVENTY_NINE(6379),

    /**
     * 8219.
     */
    EIGHT_ONE_NINE_TWO(8192),



    /**
     * 1440.
     */
    One_THOUSAND_FOUR_HUNDERD_AND_FORTH(1440);



    /**
     * 数字.
     */
    private int num;

    NumberEnum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
