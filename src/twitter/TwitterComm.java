package twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by francis on 1/21/16.
 */

public class TwitterComm
{

    private static final String CONSUMER_KEY = "H0ZNx3yIhadu2MYN8zmzYfq6I";
    private static final String CONSUMER_SECRET = "Pjn4j5jPTZNGCSApoodKR5xmJMjriYFfC9bWvmgow6YfT4ayp9";
    private static final String ACCESS_KEY = "4814887102-lE7kMyA7NLdXUPxU61BROW18OEagAnxN79PX8ge";
    private static final String ACCESS_SECRET = "4FJk49RcQ5R26FQaxk8ndU31jG1L5IOz6z13t61AMQ7Cj";

    private Twitter twitter;

    public TwitterComm()
    {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
        configurationBuilder.setOAuthAccessToken(ACCESS_KEY);
        configurationBuilder.setOAuthAccessTokenSecret(ACCESS_SECRET);
        configurationBuilder.setDebugEnabled(true);
        TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
        twitter = tf.getInstance();
        //Twitter twitter = TwitterFactory.getSingleton();

    }

    public void postRecurringStatus(String statusStr, int times, long timeOffset)
    {
        Timer timer = new Timer();
        TimerTask timerTask = new TweetTask(timer, this, times, statusStr);
        timer.schedule(timerTask, 0, timeOffset);
    }

    public void postStatus(String statusStr)
    {
        Status status = null;
        try {
            status = twitter.updateStatus(statusStr);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
    }
    public static void main(String args[])
    {
        TwitterComm twitterComm = new TwitterComm();
        //twitterComm.postStatus("Debug status from Twitter4j #Twitter4j #HelloWorld");
        //twitterComm.postRecurringStatus("Debug! :)", 5, 2000);
        twitterComm.postStatus("http://i.imgur.com/9W6deYG.jpg");
    }
}

class TweetTask extends TimerTask
{
    private TwitterComm twitterComm;
    private int times;
    private String status;
    private Timer timer;
    public TweetTask(Timer timer, TwitterComm twitterComm, int times, String status)
    {
        this.twitterComm = twitterComm;
        this.times = times;
        this.status = status;
        this.timer = timer;
    }
    @Override
    public void run()
    {
        System.out.println("Timer run #" + times);
        twitterComm.postStatus(status + " #" + times);
        times--;
        if(times == 0)
        {
            timer.cancel();
            timer.purge();
        }
    }
}