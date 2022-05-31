## 为什么需要这个产品
个人在网上搜集过很多关于招聘/人才系统的设计文档，发现目前很多系统大多只实现了求职者与招聘者相互关联这一步，也就是说这些系统并没有覆盖到招聘到面试再到结果的整个过程，我设计这个产品最主要的目的就是为从求职到结果的提供一站式解决方案。

## ACAT纳新系统V2.0解决的需求是什么
比较V1.0的纳新系统，2.0要做的事情是：
- 在1.0已实现的功能上进行扩展
   可配置化的面试分组
- 高可用方案设计（核心）

v2.0的核心需求，就是要优化系统设计结构，尽可能的实现全方位的高可用方案设计

## ACAT纳新系统V2.0架构设计

核心模块
- 前置模块
- 报名系统
- 流程控制中心
- 面试系统

其他模块
- 日志模块
- 导出报表
- 通知模块
- 权限系统

## 数据流
```
                                结束
                                ^   
                                |
用户信息 ----> 前置模块 ----> 流控中心 ----> 面试系统/报名系统
                                ^                  |         
                                |-------------------
```        

## 详细设计

### 1.前置模块
报名信息写pipe

```
用户信息 --> 写pipe ---> 成功响应ok
              |
              --------> 失败------rpc同步调用流控---->响应调用结果
```

核心代码如下：
```java
@Service
public class ProcessReceiveImpl implements ProcessReceive {

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @Resource
    private ProducerCallBackListener listener;

    @Override
    public R receive(ProcessInput input) {
        //使用kafka模板发送信息
        FlowReceiveInput message = ProcessUtil.buildMqCondition(input);
        //设置监听
        kafkaTemplate.setProducerListener(listener);
        //发送消息，获取future对象
        kafkaTemplate.send(ProcessConstant.FLOW_RECEIVE_TOPIC, JSON.toJSONString(message));
        return R.ok();
    }
}
```
使用消息队列发送到流控中心去，这里设置了一个监听器去监听发送状态，如果失败就会执行下面的逻辑：

```java
    @Override
    public void onError(String topic, Integer partition, String key, String value, Exception exception) {
        logger.warning(ProcessCodeEnum.SEND_QUEUE_FAIL.getMsg() + ":" + " topic:" + topic + 
        " partition:" + partition + " value:" + value + " exception:" + exception.getMessage());
        //rpc调用
        FlowReceiveInput input = ProcessUtil.buildRpcCondition(
            JSON.parseObject(value,FlowReceiveInput.class));
        R res = flowReceiveService.receive(input);
        //若限流调用失败则重放pipe
        if ((int)res.get("code") == ExceptionCodeEnum.FLOW_CONTROL_LIMIT_EXCEPTION.getCode()){
            //重放pipe
            kafkaTemplate.send(ProcessConstant.FLOW_RECEIVE_TOPIC, value);
            logger.info("because flow control, repeat send pipe: " + value);
            return;
        }
        //RPC调用失败，记录日志
        if ((int) res.get("code") != ProcessConstant.RESPONSE_SUCCESS) {
            logger.warning(ProcessCodeEnum.MESSAGE_GIVE_UP_ERROR.getMsg() + ":" + 
            "RPC call fail, res=" + JSON.toJSONString(res));
            return;
        }
        //RPC调用成功
        logger.info("flow control call success :" + JSON.toJSONString(res));
    }
```
pipe方式失败就执行rpc调用，流控中心会对请求进行流量限制，如果被限流了在放到pipe里去。

需要注意的是，流控中心可以去监听多个pipe，所以被限流的请求完全可以放入备用pipe。


### 2.流控中心
流控中心主要实现的功能是：
- 不同小组，面试流程可配置化，并且根据配置化流程进行流程流转
- 流量控制，以不同小组QPS配置基准进行流量控制

```
前置模块入流控：

前置模块-->流量控制--> 读取数据库流程配置 --> 异步流转 --> 入流控db、es写日志
            |
            --->fail判断调用方式-->pipe 则重新放入pipe（TTL=3）
                   |
                   ---> rpc 直接响应降级


报名系统or面试系统回调流控：

 读取流程配置文件 --> 异步流转 --> 更新es面试状态-->写日志
```

下面给出实现流量限制的核心代码：
```java
@Component
public class RedisFlowControl {

    private static Logger logger = Logger.getLogger(RedisFlowControl.class.toString());

    @Resource
    private RedisTemplate redisTemplate;

    //1s过期
    private static int expireTime = 1;

    private static String FLOW_CONTROL_PREFIX = "FLOW_CONTROL_";

    public boolean isPass(String key, int value) {
        String val = (String) redisTemplate.opsForValue().get(FLOW_CONTROL_PREFIX + key);
        if (val == null) {
            redisTemplate.opsForValue().setIfAbsent(FLOW_CONTROL_PREFIX + key, "1", 
            expireTime, TimeUnit.SECONDS);
            logger.info("set redis key " + FLOW_CONTROL_PREFIX + key +
             " expire time " + expireTime + "s.");
            return true;
        } else{
            return redisTemplate.opsForValue().increment(FLOW_CONTROL_PREFIX + key) <= value;
        }
    }
}
```
实现流程控制核心代码：
```java
    @Override
    public void runNextFlow(FlowReceiveInput input, TbServiceLine serviceLine) {

        String next = FlowControlUtil.getNextFlow(input.getWorkType(),
         JSON.parseArray(serviceLine.getWorkDetails(), FlowModel.class));

        if (next != null && next.equals(FlowControlConstant.NOT_FOUND_NEXT_FLOW)) {
            logger.warning("work detail property error, not find next flow. current is " + 
            input.getWorkType() + ", but work detail is " + JSON.toJSONString(serviceLine.getWorkDetails()));
            ExceptionCast.exception(FlowControlCodeEnum.ILLEGAL_WORK_FLOW_STATUS_EXCEPTION.getMsg(),
             FlowControlCodeEnum.ILLEGAL_WORK_FLOW_STATUS_EXCEPTION.getCode());
        }

        //流程是否结束
        int status = 0;

        //是最后一个流程 并且 通过
        if (next == null && input.getModuleStatus() == 1) {
            status = 1;
            //面试不通过
        } else if (input.getModuleStatus() == 0) {
            status = 2;
        }

        //调用模块 面试通过了 但是不是最后一个流程
        if (next != null && input.getModuleStatus() == 1) {
            rpcNextFlow(input, serviceLine, next);
        } else{
            logger.info("this is last flow, and flow info is " + JSON.toJSONString(input));
        }

        if (status != 0) {
            doUpdateStatus(input, status);
        }
        //写操作日志
        doWriteLogs(input, serviceLine, status);
    }
```
              
### 3.报名系统
提供报名系统平台相关功能：报名及报名信息查询修改等、管理员关闭或打开报名通道

```
流控中心-->入报名系统db-->回调流控
```

### 4.面试系统
提供签到、获取面试任务、面试评价等功能

```
流控中心-->入redis list队列-->更新es面试状态-->记录操作日志
```

获取面试任务
```
加锁-->取redis list队列首部元素-->更新es面试状态-->记录操作日志
```

提交评价
```
入评价db-->更新es面试状态-->释放锁-->记录操作日志
```

提交结果
```
更新es面试状态-->发送邮件通知-->记录操作日志
```

### 5.日志模块

异步写日志（放入pipe），存入es
检索数据es

### 6.数据导出

提交数据导出任务，定时任务执行.

定时任务每隔10秒钟扫描一次数据库中新建的任务---> 创建任务对象 --> 将任务加入线程池中，线程池对任务进行调度执行。
```java
    @Override
    public void execute() {
        //1.扫描数据库，获取待执行任务
        List<TbReport> reports = reportDao.selectList(buildDBCondition());

        if (reports == null || reports.size() == 0) {
            logger.info("there is no task to execute.");
            return;
        }

        //2.生成任务对象
        List<ReportThread> reportThreads = getReportRunnable(reports);
        //3.更改任务状态
        for (TbReport report : reports) {
            report.setStatus(ReportConstant.REPORT_STATUS_WAIT);
            reportDao.updateById(report);
        }
        //4.提交任务
        submitTask(reportThreads);
        logger.info("submit report task with data: " + JSON.toJSONString(reports));
    }
```

导出任务完成后生成Excel文件，将文件下载路径保存到数据库，供前端下载。
```java
    @Override
    public void run() {
        logger.info(Thread.currentThread().getName() + " running, report task is " + JSON.toJSONString(report));
        //1.修改任务状态为 running
        report.setStatus(ReportConstant.REPORT_STATUS_RUNNING);
        doUpdateReportStatus(report);
        //2.查询任务数据
        List<FlowEsEntity> esEntities = doSearchTaskData();
        //3.生成Excel
        String url = null;
        try {
            url = doCreateExcel(esEntities);
        } catch (Exception e) {
            report.setDetail(e.getMessage());
        }
        int status = url != null ? ReportConstant.REPORT_STATUS_SUCCESS : ReportConstant.REPORT_STATUS_FAIL;
        report.setStatus(status);
        report.setDownload(url);
        //4.修改任务状态为 end
        doUpdateReportStatus(report);
        logger.info(Thread.currentThread().getName() + " end, work status is " + status);
    }
```

### 7.消息通知

消息队列读取数据，异步发送通知，记录日志

### 8.权限系统 没做

- 插拔式权限校验
- 权限粒度可配置化的分组service_line

## 技术选型

下面罗列一下项目中用到的主要技术。

1.框架 springboot、mybatis

2.数据库 mysql、redis、es

3.中间件 kafka作为消息队列

4.rpc框架 dubbo+zookeeper，负责服务发现、注册以及负载均衡

当然还用到了第三方的服务，比如说阿里云的邮件服务。

## 数据库设计

1. 流控中心
```sql
CREATE TABLE `tb_flow_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) NOT NULL DEFAULT 'nid=username+serviceline+version',
  `username` varchar(255) NOT NULL,
  `service_line` varchar(255) NOT NULL COMMENT '面试分组',
  `version` int(11) DEFAULT NULL COMMENT 'service_line版本',
  `work_type` varchar(255) NOT NULL COMMENT '流程状态',
  `status` int(11) NOT NULL COMMENT '流程是否结束 0 1pass 2 nopass',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_time` datetime DEFAULT NULL COMMENT '修改日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;
```
2. 报名系统
```sql
CREATE TABLE `tb_apply_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) DEFAULT NULL COMMENT 'nid',
  `username` varchar(255) DEFAULT NULL COMMENT 'username',
  `snumber` varchar(255) DEFAULT NULL COMMENT '学号',
  `real_name` varchar(255) DEFAULT NULL COMMENT 'real name',
  `class_name` varchar(255) DEFAULT NULL COMMENT 'class name',
  `sex` int(11) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `content` varchar(1023) DEFAULT NULL COMMENT '留言',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
```
3. 面试系统
```sql
CREATE TABLE `tb_evaluate_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) NOT NULL,
  `evaluate_user` varchar(255) NOT NULL,
  `content` varchar(1023) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `work_type` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_service_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_line` varchar(255) DEFAULT NULL COMMENT '分组(业务线)名称',
  `qps` int(11) DEFAULT NULL,
  `work_details` varchar(1023) DEFAULT NULL COMMENT '流转配置json',
  `turns` int(11) NOT NULL COMMENT '轮次',
  `auth_key` varchar(255) NOT NULL COMMENT '权限关键字',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述信息（富文本）',
  `status` int(11) NOT NULL COMMENT '开放状态',
  `version` int(11) NOT NULL COMMENT '版本',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
```

4. 日志模块
```sql
CREATE TABLE `tb_logs_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `op_user` varchar(255) DEFAULT NULL COMMENT '操作人',
  `op_details` varchar(255) DEFAULT '' COMMENT '操作细节',
  `work_type` varchar(255) DEFAULT NULL,
  `op_ext` varchar(1023) DEFAULT NULL COMMENT '扩展信息',
  `op_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;
```
5. 数据导出

```sql
CREATE TABLE `tb_report_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `op_user` varchar(255) NOT NULL,
  `conds` varchar(1023) NOT NULL COMMENT 'ES查询条件',
  `status` int(11) DEFAULT NULL COMMENT '运行状态 0:新建 1:wait 2:执行ing 3:ok -1:fail',
  `detail` varchar(1023) DEFAULT NULL,
  `download` varchar(1023) DEFAULT NULL COMMENT 'url',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `status` (`status`) USING BTREE,
  KEY `op_user` (`op_user`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
```
6. 消息通知
```sql
CREATE TABLE `tb_notice_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nid` varchar(255) DEFAULT NULL,
  `work_type` varchar(255) DEFAULT NULL,
  `send_user` varchar(255) DEFAULT NULL COMMENT '发送方',
  `receive_user` varchar(255) DEFAULT NULL COMMENT '接收方',
  `receive_address` varchar(255) NOT NULL COMMENT '目的地址',
  `title` varchar(1024) DEFAULT NULL COMMENT '标题',
  `content` varchar(2048) DEFAULT NULL COMMENT '内容',
  `status` int(11) DEFAULT NULL COMMENT '通知状态',
  `send_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
```

## 运行