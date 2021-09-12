package cn.edu.xupt.acat.lib.exception;

public class ExceptionCast {

    public static void exception(String msg) {
        throw new RRException(msg);
    }

    public static void exception(String msg, long code) {
        throw new RRException(msg,code);
    }
}
