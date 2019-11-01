import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class GBNClient {
    private Time time;
    private Timer timer;
    private int port = 80;
    private DatagramSocket datagramSocket;
    private int nextFileSeq=1;
    private int baseFileSeq=1;
    //设为1则为停等协议
    private int windowsWidth=3;

    public GBNClient() throws IOException {
        //初始化
        this.datagramSocket=new DatagramSocket();
        //构造要发送的文件
        ArrayList<byte[]> file = new ArrayList<>();
        for(int i=0;i<10;i++){
            String context = "This is file with index "+i;
            file.add(context.getBytes());
        }
        //初始化计时器
        this.time = new Time();
        this.timer = new Timer(this.time,this);
        //开始计时
        this.timer.start();
        while(true) {
            sendWindow(file);
        }

    }

    private void send(byte[] data) throws IOException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        DatagramPacket datagramPacket = new DatagramPacket(data,data.length,inetAddress,this.port);
        datagramSocket.send(datagramPacket);
    }

    private void sendWindow(ArrayList<byte[]> fileList) throws IOException {
        while(nextFileSeq<baseFileSeq+windowsWidth && nextFileSeq<=10){
            send(fileList.get(nextFileSeq));
            nextFileSeq++;
        }
    }

    public void timeOut(){
        /*
          处理超时
         */

    }
}
