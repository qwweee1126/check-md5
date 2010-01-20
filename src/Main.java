import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DB.DatabaseFactory;


public class Main {
    public static String DECODE = "MD5";
    public static int BYTE_LENGTH = 10240;
    private ArrayList<String> dirArr;
    private String mvdir;
    private HashMap<String, String> md5Hash;
    public Main(String dir, String mvdir) {
        this.mvdir = mvdir;
        dirArr = new ArrayList<String>();
        md5Hash = new HashMap<String, String>();
        try {
            ReadDirectory(dir);
            CheckMD5();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("檔案數量有："+dirArr.size()+"個檔案。");
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
            if (!md5Hash.containsKey(md5String)) {
                if (tmp.length() == 0) {
                    System.out.println("刪除："+tmp.getCanonicalPath());
                    tmp.renameTo(new File(mvdir+tmp.getName()));
                    //tmp.delete();
                } else {
                    md5Hash.put(md5String, tmp.getCanonicalPath());
                }
            } else {
                System.out.println("有一樣的："+md5Hash.get(md5String)+", "+tmp.getCanonicalPath());
                tmp.renameTo(new File(mvdir+tmp.getName()));
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
    private void ReadDirectory(String dirStr) throws IOException {
        File dir = new File(dirStr);
        if (dir.isDirectory()) {
            File[] filelist = dir.listFiles();
            if (filelist.length > 0) {
                for (int i = 0 ; i < filelist.length ; i ++) {
                    if (filelist[i].isDirectory()) {
                        try {
                            ReadDirectory(filelist[i].getCanonicalPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dirArr.add(filelist[i].getCanonicalPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                System.out.println("刪除："+dir.getCanonicalPath());
                dir.renameTo(new File(mvdir+dir.getName()));
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
    public static void main (String[] arg) throws SQLException {
        DatabaseFactory.setDatabaseSettings("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/md5?useUnicode=true&characterEncoding=utf8", "root", "ji394su3", 30);
        DatabaseFactory.getInstance();
        //new Main("D:/eMule/Incoming", "D:/一樣的/");
        
    }
}
