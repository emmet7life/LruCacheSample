package jp.classmethod.android.sample.lrucache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

/**
 * {@link Bitmap} ��񓯊��œǂݍ��� {@link AsyncTaskLoader}.
 */
public class ImageLoader extends AsyncTaskLoader<Bitmap> {

    /** �Ώۂ̃A�C�e��. */
    public ImageItem item;

    /**
     * �R���X�g���N�^.
     * @param context {@link Context}
     * @param item {@link ImageItem}
     */
    public ImageLoader(Context context, ImageItem item) {
        super(context);
        this.item = item;
    }

    @Override
    public Bitmap loadInBackground() {
        return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.item);
    }

}
