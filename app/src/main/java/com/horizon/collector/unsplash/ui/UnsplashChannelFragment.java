
package com.horizon.collector.unsplash.ui;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.horizon.base.network.NetworkUtil;
import com.horizon.base.ui.BaseAdapter;
import com.horizon.base.util.CollectionUtil;
import com.horizon.base.util.LogUtil;
import com.horizon.base.util.ToastUtil;
import com.horizon.collector.R;
import com.horizon.collector.common.ChannelFragment;
import com.horizon.collector.common.channel.Channel;
import com.horizon.collector.unsplash.suorce.UPhoto;
import com.horizon.collector.unsplash.suorce.UnsplashCatcher;
import com.horizon.task.UITask;
import com.horizon.task.base.Priority;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnsplashChannelFragment extends ChannelFragment {
    private UPhotoAdapter mAdapter;
    private SwipeRefreshLayout mPageSrl;

    public static UnsplashChannelFragment newInstance(Channel channel) {
        UnsplashChannelFragment fragment = new UnsplashChannelFragment();
        initFragment(fragment, channel);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragement_channel_page;
    }

    @Override
    protected void initView() {
        super.initView();

        mAdapter = new UPhotoAdapter(mActivity, new ArrayList<UPhoto>(), true);
        mAdapter.setLoadingFooter(R.layout.footer_loading);
        mAdapter.setHost(this);
        mAdapter.setOnLoadMoreListener(new BaseAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(boolean forceReload) {
                if (mLastPage == mNextPage && !forceReload) {
                    return;
                }
                mLastPage = mNextPage;
                mLoadingMode = MODE_LOAD_MORE;
                loadData();
            }
        });

        mPageSrl = (SwipeRefreshLayout) findViewById(R.id.page_sfl);
        mPageSrl.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent,
                R.color.colorPrimaryDark);
        mPageSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadingMode = MODE_REFRESH;
                loadData();
            }
        });

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.page_rv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    protected void loadData() {
        if (NetworkUtil.isConnected()) {
            new FetchUPhotoTask()
                    .priority(Priority.HIGH)
                    .host(this)
                    .execute();
        }else {
            ToastUtil.showTips(R.string.connect_tips);
        }
    }

    private class FetchUPhotoTask extends UITask<Void, Void, List<UPhoto>> {
        private boolean hasData = true;

        protected List<UPhoto> doInBackground(Void... params) {
            try {
                List<UPhoto> photoList = UnsplashCatcher.getPhotos(mChannel.id, mLastPage);
                hasData = !CollectionUtil.isEmpty(photoList);

                Iterator<UPhoto> iterator = photoList.iterator();
                while (iterator.hasNext()) {
                    String id = iterator.next().id;
                    if (mIdSet.contains(id)) {
                        iterator.remove();
                    } else {
                        mIdSet.add(id);
                    }
                }
                return photoList;
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<UPhoto> photoList) {
            if (mLoadingMode == MODE_LOAD_MORE) {
                if (photoList == null) {
                    mAdapter.setFailedFooter(R.layout.footer_failed);
                } else if (!hasData) {
                    mAdapter.setEndFooter(R.layout.footer_end);
                } else {
                    mNextPage++;
                    mAdapter.appendData(photoList);
                }
            } else {
                if (!CollectionUtil.isEmpty(photoList)) {
                    if (mAdapter.getDataSize() == 0) {
                        mAdapter.setData(photoList);
                    } else {
                        mAdapter.insertFront(photoList);
                    }
                }
                mPageSrl.setRefreshing(false);
            }
        }
    }
}
