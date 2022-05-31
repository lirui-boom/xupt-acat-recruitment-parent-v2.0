package cn.edu.xupt.acat.lib.exception;


import cn.edu.xupt.acat.lib.response.R;

public class CallResultHandler{


    public static void callResCheck(R res) {

        if (res == null) {
            ExceptionCast.exception("远程调用返回值为null！");
        }

        if ((Integer) res.get("code") != 200) {
            ExceptionCast.exception("远程调用返回值状态非法：" + res.get("msg"));
        }
    }

    public static <T> T getData(R res, Object key, Class<T> cls) {

        if (key == null) {
            ExceptionCast.exception("Object key 不能为空！");
        }

        callResCheck(res);

        return null;
    }
}
