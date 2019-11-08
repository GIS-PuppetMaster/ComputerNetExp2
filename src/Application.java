import java.io.*;
import java.util.ArrayList;

public class Application {
    public static void main(String[] args) throws IOException {
        File file=new File("C:\\Users\\zkx74\\IdeaProjects\\计算机网络实验2\\ServerFile\\test.txt");
        FileInputStream fileInputStream=new FileInputStream(file);
        ArrayList<byte[]> fileList = new ArrayList<>();
        byte[] bytes_=new byte[5];
        int counter = 0;
        while(fileInputStream.read(bytes_)!=-1){
            byte[] bytes=new byte[9];
            for(int i =0;i<5;i++){
                bytes[i]=bytes_[i];
            }
            bytes[6]='@';
            bytes[7]=':';
            bytes[8]= String.valueOf(counter).getBytes()[0];
            fileList.add(bytes);
            counter++;
        }
        new Thread(new GBNClient()).start();
        new Thread(new GBNServer(fileList)).start();

    }
}
