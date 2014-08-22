package com.harry.ireader;

import java.io.File;
import java.util.ArrayList;

import com.harry.file.FileUtil;
import com.harry.ireader.MainActivity;
import com.harry.ireader.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class IntentBuilder {

    public static void viewFile(final Context context, final String filePath) {
    	// FLAG_ACTIVITY_CLEAR_TOP 
    	// 如果已经启动了四个Activity：A，B，C和D。在D Activity里，
    	// 我们要跳到B Activity，同时希望C finish掉，
    	// 可以在startActivity(intent)里的intent里添加flags标记
    	// 这样启动B Activity，就会把D，C都finished掉，
    	// 如果你的B Activity的启动模式是默认的（multiple） ，
    	// 则B Activity会finished掉，再启动一个新的Activity B。 

    	// 如果不想重新再创建一个新的B Activity，则在上面的代码里再加上：
    	// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);  
    	// 这样B Activity就会再创建一个新的了，而是会重用之前的B Activity，
    	// 同时调用B Activity的onNewIntent()方法。
    	
    	
    	// 因为MainActivity 我设置为了singleTask，所以这里不用加FLAG_ACTIVITY_SINGLE_TOP
    	if(true){
    		context.startActivity(
    				new Intent(context, MainActivity.class)
    					.setAction(Intent.ACTION_VIEW)
    					.putExtra(FileUtil.BOOK_PATH_KEY, filePath)
    					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    			);
        	return;
    	}
    	
    	
    	
    	// 以下为原来的代码
        String type = getMimeType(filePath);

        if (!TextUtils.isEmpty(type) && !TextUtils.equals(type, "*/*")) {
            /* 设置intent的file与MimeType */
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
            context.startActivity(intent);
        } else {
            // unknown MimeType
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle(R.string.dialog_select_type);

            CharSequence[] menuItemArray = new CharSequence[] {
                    context.getString(R.string.dialog_type_text),
                    context.getString(R.string.dialog_type_audio),
                    context.getString(R.string.dialog_type_video),
                    context.getString(R.string.dialog_type_image) };
            dialogBuilder.setItems(menuItemArray,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectType = "*/*";
                            switch (which) {
                            case 0:
                                selectType = "text/plain";
                                break;
                            case 1:
                                selectType = "audio/*";
                                break;
                            case 2:
                                selectType = "video/*";
                                break;
                            case 3:
                                selectType = "image/*";
                                break;
                            }
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(filePath)), selectType);
                            context.startActivity(intent);
                        }
                    });
            dialogBuilder.show();
        }
    }

    public static Intent buildSendFile(ArrayList<FileInfo> files) {
        ArrayList<Uri> uris = new ArrayList<Uri>();

        String mimeType = "*/*";
        for (FileInfo file : files) {
            if (file.IsDir)
                continue;

            File fileIn = new File(file.filePath);
            mimeType = getMimeType(file.fileName);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }

        if (uris.size() == 0)
            return null;

        boolean multiple = uris.size() > 1;
        Intent intent = new Intent(multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
                : android.content.Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }

        return intent;
    }

    private static String getMimeType(String filePath) {
        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
            return "*/*";

        String ext = filePath.substring(dotPosition + 1, filePath.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);
        if (ext.equals("mtz")) {
            mimeType = "application/miui-mtz";
        }

        return mimeType != null ? mimeType : "*/*";
    }
}
