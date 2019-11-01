import java.io.IOException;
import java.net.*;
public class GBNClient implements Runnable {
    private int port = 80;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private int exceptedSeq=0;

    public GBNClient() throws SocketException, UnknownHostException {
        datagramSocket = new DatagramSocket(this.port,InetAddress.getLocalHost());
        System.out.println("客户端：开始接收");
    }

    private void sendACK(int ack) throws IOException {
        String ack_s = "ack:"+ack;
        byte[] data = ack_s.getBytes();
        InetAddress inetAddress = this.datagramPacket.getAddress();
        int port_=this.datagramPacket.getPort();
        datagramPacket = new DatagramPacket(data,data.length,inetAddress,port_);
        datagramSocket.send(datagramPacket);
    }

    @Override
    public void run() {
        try{
            int counter=1;
            while (true) {
                byte[] receivedData = new byte[4096];
                datagramPacket = new DatagramPacket(receivedData, receivedData.length);
                datagramSocket.receive(datagramPacket);
                //实际收到的数据
                String received = new String(receivedData, 0, receivedData.length);
                System.out.println("客户端：接收到数据：\n"+received);
                //如果收到了预期的数据，则发送ACK
                if (Integer.parseInt(received.substring(received.indexOf("编号:") + 3).trim()) == exceptedSeq) {
                    //模拟ack丢失
                    if(counter%3==0){
                        System.out.println("客户端：发送ACK："+exceptedSeq);
                        sendACK(exceptedSeq);
                        exceptedSeq++;
                    }
                    //当超时的时候，阻塞3s，服务器端阈值为1s，ack超时
                    else {
                        System.out.println("客户端：模拟超时");
                        Thread.sleep(3000);
                    }
                    sendACK(exceptedSeq);
                    exceptedSeq++;
                }
                //否则不进行操作
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
