package com.sharon.allen.a18_sharon.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Allen on 2016/5/30.
 */
public class DialogUtils {
    // 返回一个列表对话框
    public static AlertDialog.Builder getListDialogBuilder(Context context,
                                                           String[] items,
                                                           String title,
                                                           DialogInterface.OnClickListener clickListener) {
        return new AlertDialog.Builder(context).setTitle(title).setItems(items, clickListener);

    }

}
