package com.future.demo.push;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xiaomi.push.sdk.ErrorCode;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 *
 */
public class PushTests {
    /**
     *
     */
    @Test
    public void test() throws IOException {
        Constants.useOfficial();
        String secretKey = System.getenv("secretKey");
        Sender sender = new Sender(secretKey);
        Message.Builder builder = new Message.Builder();
        // 透传消息
        builder.passThrough(1);

        String packageName = System.getenv("packageName");
        builder.restrictedPackageName(packageName);

        ObjectNode payloadNode = JsonNodeFactory.instance.objectNode();
        payloadNode.put("title", "来电提醒");
        payloadNode.put("content", "+86135xxxxxxxx 电话来电...");
        String payloadJson = payloadNode.toString();
        builder.payload(payloadJson);

        Message message = builder.build();

        String registrationId = System.getenv("registrationId");
        Result result = sender.send(message, registrationId, 0);
        Assert.assertEquals("消息发送失败，错误代号：" + result.getErrorCode().getValue() + "，原因：" + result.getReason(), ErrorCode.Success.getValue(), result.getErrorCode().getValue());
    }
}
