import java.net.*;
public class Timer extends Thread{
    private Time time;
    private GBNClient gbnClient;

    public Timer(Time time, GBNClient gbnClient) {
        this.time = time;
        this.gbnClient = gbnClient;
    }

    @Override
    public void run() {
        try {
            //等待1000ms
            Thread.sleep(1000);
            if()
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
