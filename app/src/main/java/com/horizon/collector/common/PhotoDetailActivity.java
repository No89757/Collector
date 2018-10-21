

package com.horizon.collector.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.github.chrisbanes.photoview.PhotoView;
import com.horizon.base.event.Event;
import com.horizon.base.ui.BaseActivity;
import com.horizon.base.ui.ToolbarActivity;
import com.horizon.base.util.FileUtil;
import com.horizon.base.util.LogUtil;
import com.horizon.base.util.ToastUtil;
import com.horizon.collector.R;
import com.horizon.collector.setting.SettingActivity;
import com.horizon.collector.setting.model.UserSetting;
import com.horizon.doodle.DiskCacheStrategy;
import com.horizon.doodle.Doodle;
import com.horizon.doodle.MHash;
import com.horizon.doodle.MemoryCacheStrategy;
import com.horizon.event.EventManager;
import com.horizon.task.IOTask;
import com.horizon.task.base.Priority;

import java.io.File;
import java.io.IOException;


public class PhotoDetailActivity extends ToolbarActivity {
    private String mUrl;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_pin_detail;
    }

    @Override
    protected void initView() {
        hideTitle();
        String url = getIntent().getStringExtra(ExtraKey.DETAIL_URL);
        mUrl = url;
        // TODO 当图片分辨率宽或高大于4096时ImageView显示不出来
        PhotoView photoView = findViewById(R.id.photo_view);
        Doodle.load(url)
                .priority(Priority.IMMEDIATE)
                .host(this)
                .memoryCacheStrategy(MemoryCacheStrategy.WEAK)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .noClip()
                .into(photoView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_download:
                download();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void download() {
        if(TextUtils.isEmpty(UserSetting.INSTANCE.getCollectPath())){
            startActivity(SettingActivity.class);
        }else {
            if (!TextUtils.isEmpty(mUrl) && mUrl.startsWith("http")) {
                new DownloadTask().execute();
            } else {
                ToastUtil.showTips("已下载");
            }
        }
    }

    private class DownloadTask extends IOTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            File file = Doodle.downloadOnly(mUrl);
            if (file != null && file.exists()) {
                File desFile = getDesFile(mUrl);
                try {
                    if(FileUtil.makeFileIfNotExist(desFile)){
                        FileUtil.copyFile(file, desFile);
                        EventManager.notify(Event.DOWNLOAD_FINISH);
                        return true;
                    }
                } catch (IOException e) {
                    LogUtil.e(TAG, e);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            ToastUtil.showTips(success ? "下载成功" : "下载失败");
        }
    }

    private File getDesFile(String url) {
        long hash = MHash.hash64(url);
        String suffix = getSuffix(url);
        String dir = UserSetting.INSTANCE.getCollectPath();
        return new File(dir, hash+"." + suffix);
    }

    private String getSuffix(String url) {
        String[] formats = new String[]{"jpg", "jpeg", "png", "gif"};
        for (String format : formats) {
            if (url.endsWith(format)) {
                return format;
            }
        }
        return "jpg";
    }
}
