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

        // Monta o comando para rodar o programa no container
        String command = guestProgram;
        if (arguments != null && arguments.length > 0) {
            for (String arg : arguments) {
                command += " " + arg;
            }
        }

        // Aqui você pode ajustar a lógica de inicialização do processo.
        // O código original do Winlator usa o ProcessHelper, mas como você
        // está adaptando o projeto, a lógica de execução fica a seu critério.
        Log.d("GuestProgramLauncher", "Iniciando: " + command);
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