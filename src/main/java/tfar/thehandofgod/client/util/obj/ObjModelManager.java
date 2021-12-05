package tfar.thehandofgod.client.util.obj;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.util.ResourceLocation;
import tfar.thehandofgod.client.model.ModelArchangel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ObjModelManager {


    private static WavefrontObject defaultModel;
    private static final LoadingCache<ResourceLocation, WavefrontObject> cache;

    static {
        defaultModel = new WavefrontObject(ModelArchangel.resourceDefaultModel);
        cache = CacheBuilder.newBuilder()
                .build(CacheLoader.asyncReloading(new CacheLoader<ResourceLocation, WavefrontObject>() {

                    @Override
                    public WavefrontObject load(ResourceLocation key) {
                        try {
                            return new WavefrontObject(key);
                        } catch (Exception e) {
                            return defaultModel;
                        }
                    }

                }, Executors.newCachedThreadPool()));
    }

    public static void reload() {
        cache.invalidateAll();
        defaultModel = new WavefrontObject(ModelArchangel.resourceDefaultModel);
    }

    public static WavefrontObject getModel(ResourceLocation loc) {
        try {
            return cache.get(loc);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return defaultModel;
        }
    }
}
