/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp.online.app.DataObjects;

/**
 *
 * @author Prokop
 */
public class ObserverMessage {
    private final MessageType msgType;
    private final String msg;
    
    public ObserverMessage(MessageType msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public MessageType getMsgType() {
        return msgType;
    }
}