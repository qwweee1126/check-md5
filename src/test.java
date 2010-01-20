import java.security.MessageDigest;


public class test {
 
    public static void main(String[] args) {
        String test1=toMd5("jack@testing.com");
        String test2=toMd5("jack@testing.com");
        System.out.println("test1="+test1);
        System.out.println("test2="+test2);
 
    }
    //
    public static String toMd5(String source_string){
        String md5String=null;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source_string.getBytes());
            byte[] digest = md.digest();
            md5String = toHex(digest);
        }catch(Exception e)
        {e.printStackTrace();}
        return md5String;
    }
    //
    public static String toHex(byte[] digest){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; ++i){
                byte b = digest[i];
                int value = (b & 0x7F) + (b < 0 ? 128 : 0);
                buffer.append(value < 16 ? "0" : "");
                buffer.append(Integer.toHexString(value));
            }
        return buffer.toString();
    }
}
