package jp.classmethod.android.sample.lrucache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * GridView ��\������ {@link Activity}.
 */
public class MainActivity extends FragmentActivity {

    /** ���O�o�͗p�̃^�O. */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** �������L���b�V���N���X. */
    private LruCache<String, Bitmap> mLruCache;
    /** {@link GridView}. */
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGridView = new GridView(this);
        mGridView.setNumColumns(4);
        setContentView(mGridView);

        // LruCache �̃C���X�^���X��
        int maxSize = 10 * 1024 * 1024;
        mLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        // Adapter �̍쐬�ƃA�C�e���̒ǉ�
        ImageAdapter adapter = new ImageAdapter(this);
        mGridView.setAdapter(adapter);
        for (int i = 0; i < 50; i++) {
            ImageItem item = new ImageItem();
            item.key = "item" + String.valueOf(i);
            adapter.add(item);
        }

        // onScrollListener �̎���
        mGridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // �X�N���[�����~�܂����Ƃ��ɓǂݍ���
                    loadBitmap();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        loadBitmap();
    }

    /**
     * �摜��ǂݍ���.
     */
    private void loadBitmap() {
        // ���݂̕\������Ă���A�C�e���̂݃��N�G�X�g����
        ImageAdapter adapter = (ImageAdapter) mGridView.getAdapter();
        int first = mGridView.getFirstVisiblePosition();
        int count = mGridView.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageItem item = adapter.getItem(i + first);
            // �L���b�V���̑��݊m�F
            Bitmap bitmap = mLruCache.get(item.key);
            if (bitmap != null) {
                // �L���b�V���ɑ���
                Log.i(TAG, "�L���b�V������=" + item.key);
                setBitmap(item);
                mGridView.invalidateViews();
            } else {
                // �L���b�V���ɂȂ�
                Log.i(TAG, "�L���b�V���Ȃ�=" + item.key);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", item);
                getSupportLoaderManager().initLoader(i, bundle, callbacks);
            }
        }
    }

    /**
     * �A�C�e���� View �� Bitmap ���Z�b�g����.
     * @param item
     */
    private void setBitmap(ImageItem item) {
        ImageView view = (ImageView) mGridView.findViewWithTag(item);
        if (view != null) {
            view.setImageBitmap(item.bitmap);
            mGridView.invalidateViews();
        }
    }

    /**
     * ImageLoader �̃R�[���o�b�N.
     */
    private LoaderCallbacks<Bitmap> callbacks = new LoaderCallbacks<Bitmap>() {
        @Override
        public Loader<Bitmap> onCreateLoader(int i, Bundle bundle) {
            ImageItem item = (ImageItem) bundle.getSerializable("item");
            ImageLoader loader = new ImageLoader(getApplicationContext(), item);
            loader.forceLoad();
            return loader;
        }
        @Override
        public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
            int id = loader.getId();
            getSupportLoaderManager().destroyLoader(id);
            // �������L���b�V���ɓo�^����
            ImageItem item = ((ImageLoader) loader).item;
            Log.i(TAG, "�L���b�V���ɓo�^=" + item.key);
            item.bitmap = bitmap;
            mLruCache.put(item.key, bitmap);
            setBitmap(item);
        }
        @Override
        public void onLoaderReset(Loader<Bitmap> loader) {
        }
    };
}
