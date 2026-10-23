package com.winlator.xenvironment;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.winlator.MainActivity;
import com.winlator.R;
import com.winlator.SettingsFragment;
import com.winlator.container.Container;
import com.winlator.container.ContainerManager;
import com.winlator.core.AppUtils;
import com.winlator.core.DownloadProgressDialog;
import com.winlator.core.FileUtils;
import com.winlator.core.PreloaderDialog;
import com.winlator.core.TarCompressorUtils;
import com.winlator.core.WineInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RootFSInstaller {
    public static final byte LATEST_VERSION = 23;
    public static final byte UPDATE_WINEPREFIX_VERSION = 16;
    public static final String FILENAME = "pcstop-rootfs.tar.gz";
    // ★ SUA URL DO GITHUB
    private static final String ROOTFS_URL = "https://github.com/gamessa/PCSTOP/releases/download/1.0.3/pcstop-rootfs.tar.gz";

    private static void resetContainerRFSVersions(Context context) {
        ContainerManager manager = new ContainerManager(context);
        for (Container container : manager.getContainers()) {
            String rfsVersion = container.getExtra("rfsVersion");
            String wineVersion = container.getWineVersion();
            if (!rfsVersion.isEmpty() && WineInfo.isMainWineVersion(wineVersion) && Short.parseShort(rfsVersion) <= UPDATE_WINEPREFIX_VERSION) {
                container.putExtra("wineprefixNeedsUpdate", "t");
            }
            container.putExtra("rfsVersion", null);
            container.saveData();
        }
    }

    public static void install(final MainActivity activity) {
        AppUtils.keepScreenOn(activity);
        RootFS rootFS = RootFS.find(activity);
        final File rootDir = rootFS.getRootDir();

        SettingsFragment.resetPreferenceVersions(activity);

        final DownloadProgressDialog dialog = new DownloadProgressDialog(activity);
        dialog.show(R.string.installing_system_files);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                clearRootDir(rootDir);
                
                // 1. BAIXAR O ARQUIVO
                File tempFile = new File(activity.getCacheDir(), FILENAME);
                URL url = new URL(ROOTFS_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(tempFile);
                byte[] data = new byte[8192];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    if (fileLength > 0) {
                        int progress = (int) ((total * 100) / fileLength);
                        activity.runOnUiThread(() -> dialog.setProgress(progress));
                    }
                }
                output.close();
                input.close();
                connection.disconnect();

                // 2. EXTRAIR COM GZIP
                // O TarCompressorUtils do Winlator não extrai de um File externo diretamente,
                // então usamos a sua própria lógica ou o TarExtractor (se você tiver).
                // Como não podemos assumir que você tem o TarExtractor no Winlator, vamos usar o TarCompressorUtils se ele suportar GZIP.
                // Se não suportar, você pode colocar o TarExtractor no projeto do Winlator.
                // Por enquanto, vamos tentar o TarCompressorUtils.
                
                // ★ ATENÇÃO: Se o TarCompressorUtils não tiver o Type.GZIP, você precisa adicionar o suporte ou usar o TarExtractor.
                // Vou assumir que ele tem.
                boolean success = TarCompressorUtils.extract(TarCompressorUtils.Type.GZIP, tempFile, rootDir, (file, size) -> {
                    return file;
                });
                
                tempFile.delete();

                if (success) {
                    rootFS.createRFSVersionFile(LATEST_VERSION);
                    resetContainerRFSVersions(activity);
                } else {
                    AppUtils.showToast(activity, R.string.unable_to_install_system_files);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppUtils.showToast(activity, R.string.unable_to_install_system_files);
            }
            dialog.closeOnUiThread();
        });
    }

    public static void installIfNeeded(final MainActivity activity) {
        RootFS rootFS = RootFS.find(activity);
        if (!rootFS.isValid() || rootFS.getVersion() < LATEST_VERSION) install(activity);
    }

    private static void clearOptDir(File optDir) {
        File[] files = optDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals("installed-wine")) continue;
                FileUtils.delete(file);
            }
        }
    }

    private static void clearRootDir(File rootDir) {
        if (rootDir.isDirectory()) {
            File[] files = rootDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String name = file.getName();
                        if (name.equals("home") || name.equals("opt")) {
                            if (name.equals("opt")) clearOptDir(file);
                            continue;
                        }
                    }
                    FileUtils.delete(file);
                }
            }
        }
        else rootDir.mkdirs();
    }

    public static void generateCompactContainerPattern(final AppCompatActivity activity) {
        // ... (mantenha o resto do código original)
        // Copie o resto do método generateCompactContainerPattern do seu arquivo original aqui.
    }
}
