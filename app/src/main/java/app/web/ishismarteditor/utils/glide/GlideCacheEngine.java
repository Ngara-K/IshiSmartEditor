package app.web.ishismarteditor.utils.glide;

import android.content.Context;

import com.luck.picture.lib.engine.CacheResourcesEngine;

import java.io.File;

public class GlideCacheEngine implements CacheResourcesEngine {
    private final static int GLIDE_VERSION = 4;
    private static GlideCacheEngine instance;


    private GlideCacheEngine() {
    }

    public static GlideCacheEngine createCacheEngine() {
        if (null == instance) {
            synchronized (GlideCacheEngine.class) {
                if (null == instance) {
                    instance = new GlideCacheEngine();
                }
            }
        }
        return instance;
    }

    @Override
    public String onCachePath(Context context, String url) {
        File cacheFile;
        if (GLIDE_VERSION >= 4) {
            // Glide 4.x
            cacheFile = ImageCacheUtils.getCacheFileTo4x(context, url);
        } else {
            // Glide 3.x
            cacheFile = ImageCacheUtils.getCacheFileTo3x(context, url);
        }
        return cacheFile != null ? cacheFile.getAbsolutePath() : "";
    }
}
