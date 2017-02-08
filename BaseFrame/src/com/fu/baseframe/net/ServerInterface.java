package com.fu.baseframe.net;

/***
 * 服务器接口
 *
 * @author fu
 */
public class ServerInterface {
    /***
     * 发送短信接口
     */
    public static final String SEND_SMS = "/Member/SendSMS/";//
    /***
     * 用户注册接口
     */
    public static final String USER_REGISTER = "/Member/Register/";

    /***
     * 忘记密码接口
     */
    public static final String RESET_PWD = "/Member/ResetPwd/";
    /***
     * 修改密码接口
     */
    public static final String MODIFY_PWD = "/Member/ModifyPwd/";
    /***
     * 登陆接口
     */
    public static final String LOGIN = "/Member/Login/";
    /***
     * 获取用户个人信息接口
     */
    public static final String GET_MEMBER_INFO = "/Member/GetMemberInfo/";
    /***
     * 修改用户资料接口
     */
    public static final String MODIFY_USER_INFO = "/Member/ModifyInfo/";
    /***
     * 修改用户头像地址接口
     */
    public static final String MODIFY_HEAD = "/Member/ModifyHeadUrl/";
    /***
     * 修改支付宝账号接口
     */
    public static final String MODIFY_ALIPAY = "/Member/ModifyZfb/";
    /***
     * 设置拍照认证接口
     */
    public static final String MODIFY_TAKE_CAMERA = "/Member/ModifyPZ/";
    /***
     * 设置紧急联系人接口
     */
    public static final String MODIFY_CONTACTS = "/Member/ModifyLXR/";
    /***
     * 设置学籍认证接口
     */
    public static final String MODIFY_SCHOOL_ROLL = "/Member/ModifyXJ/";
    /***
     * 绑定支付宝账号接口
     */
    public static final String BIND_ALIPAY = "/Member/BindPayNo/";
    /***
     * 获取用户认证信息接口
     */
    public static final String GET_AUTH = "/Member/GetRzInfo/";

    /***
     * 获取紧急联系人信息接口
     */
    public static final String GET_MEMBER_XRINFO = "/Member/GetMemberLXRInfo/";
    /***
     * 获取学籍认证信息接口
     */
    public static final String GET_MEMBER_XJINFO = "/Member/GetMemberXJInfo/";
    /***
     * 获取拍照认证信息接口
     */
    public static final String GET_MEMBER_RZ_INFO = "/Member/GetMemberRzInfo/";
    /***
     * 获取借款类型接口
     */
    public static final String GET_LOAN_TYPE_LIST = "/Loan/GetLoanTypeList/";

    /***
     * 借款接口
     */
    public static final String CREATE_LOAN = "/Loan/CreateLoan/";
    /***
     * 申请审核资料接口
     */
    public static final String APPLY_STAUS = "/Member/ApplyStatus/";

    /***
     * 获取未完成的数据接口
     * 还款
     */
    public static final String GET_NO_LOAN = "/Loan/GetNoLoan/";

    /****
     * 获取消息列表数据接口
     */
    public static final String GET_NEWS_LIST = "/Member/GetNewsList/";

    /***
     * 获取最新接口进度接口
     */
    public static final String GET_NEW_JKWORK = "/Loan/GetNewJKWork/";

    /***
     * 获取正常借款次数接口
     */
    public static final String GET_JK_NUMBER = "/Member/GetJKNumber/";

    /***
     * 添加联系人接口
     */
    public static final String INSERT_CONTACTS = "/Member/InsertContacts/";
    /***
     * 获取历史借款信息接口
     */
    public static final String GET_LOAN_HISTORY = "/Member/GetLoanLiShi/";
    /***
     * 退出登录接口
     */
    public static final String LOGIN_OUT = "/Member/LoginOut/";
    /***
     * 上传图片接口
     */
    public static final String UP_FILE = "/Other/PostFile/";

}
