public class Time {
    private volatile int timeValue;
    public synchronized int getTime(){
        return this.timeValue;
    }
    public synchronized void setTime(int time){
        this.timeValue=time;
    }
}
