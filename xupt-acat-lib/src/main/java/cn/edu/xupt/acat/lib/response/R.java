package cn.edu.xupt.acat.lib.response;

import cn.edu.xupt.acat.lib.constant.Constant;
import cn.edu.xupt.acat.lib.exception.ExceptionCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回数据
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("code", Constant.RESPONSE_SUCCESS);
		put("msg", "success");
		put("data", null);
	}
	
	public static R error() {
		return error(ExceptionCodeEnum.UNKNOW_EXCEPTION.getCode(), ExceptionCodeEnum.UNKNOW_EXCEPTION.getMsg());
	}
	
	public static R error(String msg) {
		return error(ExceptionCodeEnum.UNKNOW_EXCEPTION.getCode(), msg);
	}

	public static R error(long code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
