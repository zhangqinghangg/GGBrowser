package com.guoguang.ggbrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 描述：APP自启动的实现
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {

    }

    @Override

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Intent intent2 = new Intent(context, MainActivity.class);

            context.startActivity(intent2);

        }
    }

}