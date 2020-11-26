package com.asimplenerd.legobattlebots;

public class GameLoop extends Thread {

    private final Game game;
    private final int latency;
    private boolean isRunning = false;
    private boolean isOutput =  false;

    public GameLoop(Game game, int lat){
        this.game = game;
        latency = lat;
    }

    public void startLoop(boolean out)
    {
        isOutput = out;
        isRunning = true;
        start();
    }

    @SuppressWarnings("BusyWait")
    public void run() {
        super.run();

        while(isRunning) {
            try {
                game.update(isOutput);
                sleep(latency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void endLoop()
    {
        isRunning = false;
    }
}
