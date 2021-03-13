package ru.tpu.russiantpu.utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

import ru.tpu.russiantpu.R;
import ru.tpu.russiantpu.main.items.LinkItem;

/**
 * Класс, ответственный за загрузку
 * иконок в боковую шторку (navigation drawer)
 */
public class MenuItemIconLoader {

    private final Resources resources;
    private final WeakReference<MenuItem> itemWeakReference;
    private final int targetHeight;
    private final int targetWidth;

    public MenuItemIconLoader(Resources resources, MenuItem menuItem) {
        this.resources = resources;
        this.itemWeakReference = new WeakReference<>(menuItem);
        this.targetHeight = resources.getDimensionPixelSize(R.dimen.menu_icon_size);
        this.targetWidth = resources.getDimensionPixelSize(R.dimen.menu_icon_size);
    }

    private final Target target = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            MenuItem menuItem = itemWeakReference.get();
            if (menuItem != null) {
                final Drawable drawable = new BitmapDrawable(resources, bitmap);
                drawable.setBounds(0, 0, targetWidth, targetHeight);
                // Без установок tint = null, иконки будут черными
                drawable.setTintMode(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    drawable.setTintBlendMode(null);
                }
                drawable.setTintList(null);
                menuItem.setIcon(drawable);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            e.printStackTrace();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public void load(LinkItem item) {
        String url = item.getImage();
        if (url != null && !url.equals("")) {
            Picasso.get().load(url).resize(targetWidth, targetHeight).into(target);
        }
    }

}
