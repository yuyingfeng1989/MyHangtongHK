package com.bluebud.chat.utils;

import android.app.AlertDialog;

public abstract class ChatCallbackResult {
    public void callBackStart() {
    }

    public abstract void callBackResult(String result);

    public abstract void callBackFailResult(String result);

    public void callBackFinish() {
    }

    public void callOkDilaog(AlertDialog mDialog) {
    }

    public void callCanceDilaog(AlertDialog mDialog) {
    }

}
