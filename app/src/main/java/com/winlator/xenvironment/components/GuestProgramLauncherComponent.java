package com.winlator.xenvironment.components;

import android.content.Context;
import android.util.Log;

import com.winlator.container.Container;
import com.winlator.core.AppUtils;
import com.winlator.core.FileUtils;
import com.winlator.core.PreloaderDialog;
import com.winlator.core.ProcessHelper;
import com.winlator.winhandler.WinHandler;
import com.winlator.xenvironment.EnvironmentComponent;
import com.winlator.xserver.XServer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GuestProgramLauncherComponent extends EnvironmentComponent {
    private final Container container;
    private final XServer xServer;
    private final PreloaderDialog preloaderDialog;
    private WinHandler winHandler;
    private String guestProgram;
    private String[] arguments;
    private String envVars = "";
    private String box64Preset = "STABILITY"; // Valor padrão
    private String cpuList = "";
    private TerminationCallback terminationCallback;

    public interface TerminationCallback {
        void onTerminated(int status);
    }

    public GuestProgramLauncherComponent(Container container, XServer xServer, PreloaderDialog preloaderDialog) {
        this.container = container;
        this.xServer = xServer;
        this.preloaderDialog = preloaderDialog;
    }

    public void setGuestProgram(String guestProgram, String[] arguments) {
        this.guestProgram = guestProgram;
        this.arguments = arguments;
    }

    public void setGuestExecutable(String executable) {
        this.guestProgram = executable;
    }

    public void setEnvVars(String envVars) {
        this.envVars = envVars;
    }

    // Método que faltava para o Box64 (versão String)
    public void setBox64Preset(String preset) {
        this.box64Preset = preset;
    }

    // Método que faltava para o Box64 (versão short)
    public void setBox64Preset(short preset) {
        this.box64Preset = String.valueOf(preset);
    }

    // Método para configurar a lista de CPUs
    public void setCPUList(String cpuList) {
        this.cpuList = cpuList;
    }

    public void setTerminationCallback(TerminationCallback callback) {
        this.terminationCallback = callback;
    }

    public WinHandler getWinHandler() {
        return winHandler;
    }

    @Override
    public void start() {
        if (winHandler != null) return;

        winHandler = new WinHandler(container);
        winHandler.start();

        Log.d("GuestProgramLauncher", "Iniciando: " + guestProgram + " com Box64 Preset: " + box64Preset);
    }

    @Override
    public void stop() {
        if (winHandler != null) {
            winHandler.stop();
            winHandler = null;
        }
        if (terminationCallback != null) {
            terminationCallback.onTerminated(0);
        }
    }
}