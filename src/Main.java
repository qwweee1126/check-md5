import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;

import DB.DataBaseOP;
import DB.DatabaseFactory;


public class Main {
    public static String DECODE = "MD5";
    public static int BYTE_LENGTH = 10240;
    private ArrayList<String> dirArr;
    private String host;
    public Main() throws IOException, NoSuchAlgorithmException {
        this.host = InetAddress.getLocalHost().getHostName();
        dirArr = new ArrayList<String>();
        File[] array = File.listRoots();
        for (int i  = 0 ; i < array.length ; i ++ ) {
            if (array[i].getCanonicalPath().equalsIgnoreCase("c:\\")) {
                continue;
            }
            ReadDirectory(array[i]);
        }
        CheckMD5();
        System.out.println("檔案數量有："+NumberFormat.getIntegerInstance().format(dirArr.size())+"個檔案。");
    }
    private void CheckMD5() throws IOException, NoSuchAlgorithmException {
        for (int i = 0 ; i < dirArr.size() ; i ++) {
            String md5String = "";
            File tmp = new File(dirArr.get(i));
            FileInputStream fis = new FileInputStream(tmp);
            byte[] data = new byte[Main.BYTE_LENGTH];
            MessageDigest md = MessageDigest.getInstance(Main.DECODE);
            while (fis.read(data) != -1) {
                md.update(data);
            }
            fis.close();
            byte[] digest = md.digest();
            md5String = toHex(digest);
            if (!DataBaseOP.checkMD5Table(md5String)) {
                if (tmp.length() == 0) {
                    //空資料夾
                    //DataBaseOP.insertEmptyTable(host, tmp.getName(), tmp.getAbsolutePath());
                    System.out.println("錯誤資訊：檔案-"+tmp.getAbsolutePath()+tmp.getName());
                } else {
                    //文件
                    DataBaseOP.insertMD5Table(md5String, host, tmp.getName(), tmp.getAbsolutePath());
                }
            } else {
                //相同的檔案
                DataBaseOP.insertSameTable(md5String, host, tmp.getName(), tmp.getAbsolutePath());
            }
        }
    }
    private String toHex(byte[] digest){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; ++i){
                byte b = digest[i];
                int value = (b & 0x7F) + (b < 0 ? 128 : 0);
                buffer.append(value < 16 ? "0" : "");
                buffer.append(Integer.toHexString(value));
            }
        return buffer.toString();
    }
    private void ReadDirectory(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] filelist = dir.listFiles();
            if (filelist.length > 0) {
                for (int i = 0 ; i < filelist.length ; i ++) {
                    ReadDirectory(filelist[i]);
                }
            } else {
                //空資料夾
                DataBaseOP.insertEmptyTable(host, dir.getName(), dir.getAbsolutePath());
            }
        } else {
            try {
                dirArr.add(dir.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressWarnings("unused")
    private void printList() {
        for (int i = 0 ; i < dirArr.size() ; i ++) {
            System.out.println(dirArr.get(i));
        }
    }
    public static void main (String[] arg) throws SQLException, IOException, NoSuchAlgorithmException {
    	DatabaseFactory.setDatabaseSettings("com.mysql.jdbc.Driver", "jdbc:mysql://nas.im.ncnu.edu.tw/filepath?useUnicode=true&characterEncoding=utf8", "qwweee", "vul3ru/ ji394su3", 30);
        DatabaseFactory.getInstance();
        System.out.println("MD5Table資料表"+(DataBaseOP.createTable("md5table")?"建立完成":"存在"));
        System.out.println("SameTable資料表"+(DataBaseOP.createTable("sametable")?"建立完成":"存在"));
        System.out.println("EmptyTable資料表"+(DataBaseOP.createTable("emptytable")?"建立完成":"存在"));
        new Main();
        
    }
}
