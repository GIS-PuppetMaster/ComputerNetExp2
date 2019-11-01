import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class GBNServer implements Runnable{
    private int port = 80;
    private InetAddress inetAddress = InetAddress.getLocalHost();

    private DatagramSocket datagramSocket= new DatagramSocket();
    private DatagramPacket datagramPacket;
    private int nextFileSeq = 0;
    private int baseFileSeq = 0;
    //设为1则为停等协议
    private int windowsWidth = 4;
    private ArrayList<byte[]> fileList;

    public GBNServer(int nextFileSeq, int baseFileSeq, int windowsWidth, ArrayList<byte[]> fileList) throws IOException {
        //初始化
        this.nextFileSeq = nextFileSeq;
        this.baseFileSeq = baseFileSeq;
        this.windowsWidth = windowsWidth;
        this.fileList = fileList;

    }

    public GBNServer(ArrayList<byte[]> fileList) throws IOException {
        this.fileList = fileList;
    }

    private void send(byte[] data) throws IOException {
        datagramPacket = new DatagramPacket(data, data.length, this.inetAddress, this.port);
        datagramSocket.send(datagramPacket);
    }

    private boolean sendWindow() throws IOException {
        while (nextFileSeq < baseFileSeq + windowsWidth && nextFileSeq < 9) {
            System.out.println("服务器：发送文件窗口内容："+new String(fileList.get(nextFileSeq)));
            send(fileList.get(nextFileSeq));
            nextFileSeq++;
        }
        baseFileSeq = nextFileSeq;
        //发送结束返回false
        return baseFileSeq <= fileList.size();
    }

    private void resendWindow(int ack) throws IOException{
        baseFileSeq = ack+1;
        nextFileSeq = baseFileSeq;
        sendWindow();
    }

    public void sendFile() throws IOException, InterruptedException {
        int ack=-1;
        while(sendWindow()){
            //接收ACk
            System.out.println("服务器：接收ACK");
            byte[] bytes = new byte[4096];
            datagramPacket = new DatagramPacket(bytes, bytes.length);
            //开一个新线程，以阻塞的方式等待接收ack
            Thread thread = new Thread(new ReceiveThread(datagramPacket,datagramSocket));
            thread.start();
            String fromServer="";
            //检测是否接收到ack
            //检测是否超时
            long start = System.currentTimeMillis();
            while(true) {
                if(System.currentTimeMillis()-start>=1000){
                    //如果超时，重新发送
                    System.out.println("服务器：ACK超时，重新发送文件");
                    resendWindow(ack);
                }
                fromServer = new String(bytes, 0, bytes.length);
                if(fromServer.indexOf("ack:")!=-1){
                    break;
                }
                //Thread.sleep(2000);
            }
            ack = Integer.parseInt(fromServer.substring(fromServer.indexOf("ack:") + 4).trim());
            System.out.println("服务器：接收到ACK："+ack);
            if(ack==8){
                System.out.println("文件传输完成");
                System.exit(0);
            }
            baseFileSeq = ack + 1;
            //检查ack
            if (baseFileSeq != nextFileSeq+1){
                //发生错误
                System.out.println("服务器：ack错误，收到的消息为："+fromServer+"期望ack为:"+(nextFileSeq)+"，重新发送");
                //重新发送
                resendWindow(ack);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("服务器：窗口大小："+this.windowsWidth);
        System.out.println("服务器：开始发送文件");
        try {
            sendFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class ReceiveThread implements Runnable{
        private DatagramPacket datagramPacket;
        private DatagramSocket datagramSocket;

        public ReceiveThread(DatagramPacket datagramPacket, DatagramSocket datagramSocket) {
            this.datagramPacket = datagramPacket;
            this.datagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
