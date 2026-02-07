package com.growfund.seedtowealth.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Utility class for loading images with Glide.
 * Provides centralized image loading configuration.
 */
public class ImageLoader {

    /**
     * Load image from URL into ImageView with caching.
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized
                .centerCrop();

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }

    /**
     * Load image with placeholder and error handling.
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView,
            int placeholderResId, int errorResId) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(placeholderResId)
                .error(errorResId);

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }

    /**
     * Load circular image (useful for profile pictures).
     */
    public static void loadCircularImage(Context context, String imageUrl, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop();

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }

    /**
     * Clear Glide cache (useful for testing or memory management).
     */
    public static void clearCache(Context context) {
        Glide.get(context).clearMemory();
        // Clear disk cache in background thread
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }
}
