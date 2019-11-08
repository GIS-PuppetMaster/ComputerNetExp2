import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        /*
        数据帧格式
        文件编号:1
         */
        //构造要发送的文件
        ArrayList<byte[]> file = new ArrayList<>();
        for(int i=0;i<10;i++){
            String context = "@:"+i;
            file.add(context.getBytes());
        }
        new Thread(new GBNServer(file)).start();
        new Thread(new GBNClient()).start();
    }
}
