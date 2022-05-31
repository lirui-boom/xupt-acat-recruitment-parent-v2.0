package cn.edu.xupt.acat.lib.exception;

/**
 * 自定义异常
 *
 */
public class RRException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    private String msg;
    private long code = 500;

    public RRException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public RRException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public RRException(String msg, long code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}


	public RRException(String msg, long code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}


	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
