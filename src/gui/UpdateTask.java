package gui;

import java.util.TimerTask;

/**
 * Created by Francis Rohner on 6/13/15.
 */
public class UpdateTask implements Runnable {
    private GameForm gameForm;
    public UpdateTask(GameForm gameForm)
    {
        this.gameForm = gameForm;
    }
    @Override
    public void run()
    {
        gameForm.update();
    }
}
