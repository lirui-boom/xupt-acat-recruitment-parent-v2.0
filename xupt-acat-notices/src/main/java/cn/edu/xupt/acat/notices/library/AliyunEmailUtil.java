package cn.edu.xupt.acat.notices.library;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 阿里云邮件发送工具
 */
public class AliyunEmailUtil {


    // 设置需要操作的账号的AK和SK
    private static final String ACCESS_KEY = "LTAI4GA4Lenknqwybr5uDbva";
    private static final String SECRET_KEY = "WkxmbmoOCjphtRj9cLyaf8ifc5QzNi";
    //地区
    private static final String REGION = "cn-hangzhou";
    //发信地址
    private static final String ACCOUNT_NAME = "lirui@email.lirui123.top";
    //发信人昵称
    private static final String FROM_ALIAS = "ACAT";
    //标签
    private static final String TAG_NAME = "ACAT";

    /**
     * 邮件发送
     * @param toAddress 收件人地址
     * @param subject   主题
     * @param htmlBody  内容
     * @return 发送是否成功
     */
    public static boolean  sendSampleEmail(String toAddress,String subject,String htmlBody) {
        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的”cn-hangzhou”替换为”ap-southeast-1”、或”ap-southeast-2”。
        IClientProfile profile = DefaultProfile.getProfile(REGION, ACCESS_KEY, SECRET_KEY);
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();

        try {
            //request.setVersion(“2017-06-22”);// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAccountName(ACCOUNT_NAME);//控制台创建的发信地址
            request.setFromAlias(FROM_ALIAS);//发信人昵称
            request.setAddressType(1);
            request.setTagName(TAG_NAME);//控制台创建的标签
            request.setReplyToAddress(true);

            request.setToAddress(toAddress);
            //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            //request.setToAddress(“邮箱1,邮箱2”);
            request.setSubject(subject);//邮件主题
            //如果采用byte[].toString的方式的话请确保最终转换成utf-8的格式再放入htmlbody和textbody，若编码不一致则会被当成垃圾邮件。
            //注意：文本邮件的大小限制为3M，过大的文本会导致连接超时或413错误
            request.setHtmlBody(htmlBody);//邮件正文
            //SDK 采用的是http协议的发信方式, 默认是GET方法，有一定的长度限制。
            //若textBody、htmlBody或content的大小不确定，建议采用POST方式提交，避免出现uri is not valid异常
            request.setMethod(MethodType.POST);
            //开启需要备案，0关闭，1开启
            //request.setClickTrace(“0”);
            //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);

        } catch (ServerException e) {
            //捕获错误异常码
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
            return false;
        } catch (ClientException e) {
            //捕获错误异常码
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[_|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
//        System.out.println(AliyunEmailUtil.isEmail("llr_886@163.com"));
//        AliyunEmailUtil.sendSampleEmail("2495399053@qq.com","TEST","TEST");
    }
}
