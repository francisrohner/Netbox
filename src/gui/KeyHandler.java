package gui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


/**
 * Created by Francis Rohner on 6/13/15.
 */
public class KeyHandler implements EventHandler<KeyEvent>
{
    private GameForm gameForm;
    public KeyHandler(GameForm gameForm) { this.gameForm = gameForm; }
    private KeyCode lastCode;
    @Override
    public void handle(KeyEvent event)
    {
        if(event.getCode() == KeyCode.ESCAPE) gameForm.kill();

        if(event.getEventType() == KeyEvent.KEY_PRESSED)
        {
            if(gameForm.getDirection() != 'E' && (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT))//East
                gameForm.setDirection('E');
            else if(gameForm.getDirection() != 'W' && (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT))//West
                gameForm.setDirection('W');
           else if(gameForm.getDirection() != 'N' && (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP))//North
                gameForm.setDirection('N');
            else if(gameForm.getDirection() != 'S' && (event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN))//South
                gameForm.setDirection('S');
            lastCode = event.getCode();
            //System.out.println("Key Down: " + event.getCode());
        }
        else if(lastCode == event.getCode() && event.getEventType() == KeyEvent.KEY_RELEASED)
        {
            gameForm.setDirection('Z');
            //System.out.println("Key Up: " + event.getCode());
        }
    }
}
