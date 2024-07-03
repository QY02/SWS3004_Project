package org.cs304.backend.constant;

public class constant_OrderRecordStatus {
    public static int SUBMITTED = 0; //已提交，未支付或无需支付
    public static int PAID = 1; //已支付
    public static int USED = 2; //已使用

    public static int EXPIRED = -1; //过期

    public static int UNPAID = 3;
}
