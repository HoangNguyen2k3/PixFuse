package io.github.cogdanh2k3.android;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.github.cogdanh2k3.Main;
import io.github.cogdanh2k3.DataGame.SaveManager;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        initialize(new Main(), config);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            SaveManager.INSTANCE.saveGame();
            Log.i("SaveManager", "✅ Game đã lưu khi onPause()");
        } catch (Exception e) {
            Log.e("SaveManager", "❌ Lỗi khi lưu game trong onPause()", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            SaveManager.INSTANCE.saveGame();
            Log.i("SaveManager", "✅ Game đã lưu khi onDestroy()");
        } catch (Exception e) {
            Log.e("SaveManager", "❌ Lỗi khi lưu game trong onDestroy()", e);
        }
    }
}
