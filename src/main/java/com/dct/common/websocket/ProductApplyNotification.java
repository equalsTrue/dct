package com.dct.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author david
 */
@Slf4j
@ServerEndpoint(value = "/product/apply/websocket")
@Component
public class ProductApplyNotification {

    @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
    private static CopyOnWriteArraySet<ProductApplyNotification> webSocketSet = new CopyOnWriteArraySet<ProductApplyNotification>();
    private Session session;

    @Override
    public int hashCode(){
        return 1;
    }

    @Override
    public boolean equals(Object object){
        if(object!=null){
            ProductApplyNotification webSocketServer = (ProductApplyNotification) object;
            if(this.session.equals(webSocketServer.session)){
                return true;
            }else {
                return false;
            }
        }
       return false;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误"+error.toString());
        error.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message){
        for (ProductApplyNotification item : webSocketSet) {
            try {
                item.sendMessage("ping heart");
            } catch (Exception e) {
                continue;
            }
        }
    }


    public  void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendInfo(String message)  {
        for (ProductApplyNotification item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (Exception e) {
                continue;
            }
        }
    }

}
