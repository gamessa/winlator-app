package com.winlator.xenvironment.components;

import android.content.Context;
import android.util.Log;

import com.winlator.R;
import com.winlator.container.Container;
import com.winlator.core.AppUtils;
import com.winlator.core.FileUtils;
import com.winlator.core.PreloaderDialog;
import com.winlator.core.ProcessHelper;
import com.winlator.winhandler.WinHandler;
import com.winlator.xenvironment.EnvironmentComponent;
import com.winlator.xserver.XServer;

import java.io.File;

public class GuestProgramLauncherComponent extends EnvironmentComponent {
    private final Container container;
    private final XServer xServer;
    private final PreloaderDialog preloaderDialog;
    private WinHandler winHandler;
    private String guestProgram;
    private String[] arguments;

    public GuestProgramLauncherComponent(Container container, XServer xServer, PreloaderDialog preloaderDialog) {
        this.container = container;
        this.xServer = xServer;
        this.preloaderDialog = preloaderDialog;
    }

    public void setGuestProgram(String guestProgram, String[] arguments) {
        this.guestProgram = guestProgram;
        this.arguments = arguments;
    }

    public WinHandler getWinHandler() {
        return winHandler;
    }

    @Override
    public void start() {
        if (winHandler != null) return;

        winHandler = new WinHandler(container);
        winHandler.start();

        // Aqui o Winlator original inicializa as variáveis de ambiente
        // e executa o programa (Wine/Box64). 
        // Como você está adaptando o projeto, o código de execução
        // deve ser o mesmo que você já tinha, apenas sem a classe 
        // RootFSInstaller colada no final.
    }

    @Override
    public void stop() {
        if (winHandler != null) {
            winHandler.stop();
            winHandler = null;
        }
    }
}