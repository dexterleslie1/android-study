package com.future.study.android.opensource.loadingdialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 *
 */
public class LoadingDialog extends Dialog {
    /**
     *
     * @param context
     */
    private LoadingDialog(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;
        private String message;
        private boolean isShowMessage;
        private boolean isCancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setIsShowMessage(boolean isShowMessage) {
            this.isShowMessage = isShowMessage;
            return this;
        }

        public Builder setIsCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public LoadingDialog create() {
            LoadingDialog dialog = new LoadingDialog(this.context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.loading, null);
            dialog.setContentView(view);

            TextView textViewMessage = dialog.findViewById(R.id.textViewMessage);
            if(!isShowMessage) {
                textViewMessage.setVisibility(View.GONE);
            }
            if(isShowMessage && !TextUtils.isEmpty(message)) {
                textViewMessage.setText(message);
            }
            dialog.setCancelable(isCancelable);
            return dialog;
        }
    }
}
