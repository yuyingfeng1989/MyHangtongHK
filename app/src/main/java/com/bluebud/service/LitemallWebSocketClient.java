package com.bluebud.service;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class LitemallWebSocketClient extends WebSocketClient {
    public LitemallWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("LitemallWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("LitemallWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("LitemallWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("LitemallWebSocketClient", "onError()");
    }

}
