package cn.edu.xupt.acat.lib.exception;


import cn.edu.xupt.acat.lib.response.R;
import com.alibaba.fastjson.JSON;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.logging.Logger;

import static cn.edu.xupt.acat.lib.exception.ExceptionCodeEnum.ILLEGAL_HTTP_TYPE_EXCEPTION;


/**
 * 异常处理器
 */
@RestControllerAdvice
public class RRExceptionHandler {
	private Logger logger = Logger.getLogger(RRExceptionHandler.class.toString());

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RRException.class)
	public R handleRRException(RRException e){
		R r = new R();
		r.put("code", e.getCode());
		r.put("msg", e.getMessage());
		return r;
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public R HttpRequestMethodNotSupportedException(Exception e) {
		logger.warning(JSON.toJSONString(e));
		return R.error(ILLEGAL_HTTP_TYPE_EXCEPTION.getCode(), ILLEGAL_HTTP_TYPE_EXCEPTION.getMsg());
	}


	@ExceptionHandler(Exception.class)
	public R handleException(Exception e){
		logger.warning(JSON.toJSONString(e));
		return R.error();
	}
}
