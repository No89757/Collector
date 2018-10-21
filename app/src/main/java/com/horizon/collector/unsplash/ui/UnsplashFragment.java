

package com.horizon.collector.unsplash.ui;


import android.text.TextUtils;

import com.horizon.collector.common.ChannelFragment;
import com.horizon.collector.common.PageFragment;
import com.horizon.collector.common.channel.Channel;
import com.horizon.collector.common.channel.ChannelManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnsplashFragment extends PageFragment {
    public static final String TAG = "UnsplashFragment";

    private List<UnsplashChannelFragment> mFragments = new ArrayList<>();

    public static UnsplashFragment newInstance() {
        return new UnsplashFragment();
    }

    @Override
    protected List<? extends ChannelFragment> getFragments() {
        List<Channel> channels = getMyChannels();
        for (Channel channel : channels) {
            mFragments.add(UnsplashChannelFragment.newInstance(channel));
        }
        return mFragments;
    }

    @Override
    protected void updateFragments() {
        // 保存渠道配置
        ChannelManager.getUnsplashManager().saveChannels();

        // 构造新的渠道列表
        List<Channel> channels = getMyChannels();
        List<UnsplashChannelFragment> newFragments = new ArrayList<>(channels.size());
        for (Channel channel : channels) {
            UnsplashChannelFragment fragment = pickFragment(channel);
            if (fragment == null) {
                fragment = UnsplashChannelFragment.newInstance(channel);
            }
            newFragments.add(fragment);
        }

        // 清除旧的渠道列表
        mFragments.clear();

        // 添加新的渠道列表
        mFragments.addAll(newFragments);

    }

    private UnsplashChannelFragment pickFragment(Channel channel) {
        Iterator<UnsplashChannelFragment> iterator = mFragments.iterator();
        while (iterator.hasNext()) {
            UnsplashChannelFragment fragment = iterator.next();
            if (TextUtils.equals(fragment.getChannelID(), channel.id)) {
                iterator.remove();
                return fragment;
            }
        }
        return null;
    }

    @Override
    protected List<Channel> getMyChannels() {
        return ChannelManager.getUnsplashManager().getMyChannels();
    }

    @Override
    protected List<Channel> getOtherChannels() {
        return ChannelManager.getUnsplashManager().getOtherChannels();
    }

}
