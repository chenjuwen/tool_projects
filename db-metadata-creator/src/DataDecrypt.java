import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cjm.utils.ConnectionUtil;
import com.cjm.utils.RSAencryptUtil;
import com.cjm.utils.StringUtil;


/**
 * 数据解密
 */
public class DataDecrypt {
    private static Connection getConnection() throws Exception {
        //"172.29.24.13", "dep1", "deptestnew", "deptestnew"
        Connection conn = ConnectionUtil.getConnection("172.29.24.13", "dep1", "deptestnew", "deptestnew");
        return conn;
    }
    
    public static void main(String[] args) {
        //encryptPassenger();  //旅客数据
        //encryptCertificate();   //旅客证件数据
        //encryptPsgItem(); //旅客项数据，针对FOID项
        //encryptApi(); //API数据
    }

    private static void encryptPassenger() {
        Connection conn = null;
        try{
            conn = getConnection();
            
            String sql = "select ID,PSG_FF_CARD_NO,PSG_FF_ALLI_CARD_NO from T_DEP_CP_PASSENGER where PSG_FF_CARD_NO is not null or PSG_FF_ALLI_CARD_NO is not null";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String ffCardNo = StringUtil.trim(rs.getString("PSG_FF_CARD_NO"));
                String ffAlliCardNo = StringUtil.trim(rs.getString("PSG_FF_ALLI_CARD_NO"));
                
                System.out.print(id);
                System.out.print(", " + RSAencryptUtil.DecRsa(ffCardNo));
                System.out.print(", " + RSAencryptUtil.DecRsa(ffAlliCardNo));
                System.out.println();
            }
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm);
            
            System.out.println("end");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ConnectionUtil.close(conn);
        }
    }

    private static void encryptCertificate() {
        Connection conn = null;
        try{
            conn = getConnection();
            
            String sql = "select ID,CTF_NO from T_DEP_CP_CERTIFICATE where CTF_NO is not null";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String ctfNo = StringUtil.trim(rs.getString("CTF_NO"));
                
                System.out.print(id);
                System.out.print(", " + RSAencryptUtil.DecRsa(ctfNo));
                System.out.println();
            }
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm);
            
            System.out.println("end");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ConnectionUtil.close(conn);
        }
    }
    
    private static void encryptPsgItem() {
        Connection conn = null;
        try{
            conn = getConnection();
            
            String sql = "select ID,PIT_REMARK,PIT_PD_REMARK from T_DEP_CP_PSG_ITEM where PIT_CODE='FOID' and (PIT_REMARK is not null or PIT_PD_REMARK is not null)";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String remark = StringUtil.trim(rs.getString("PIT_REMARK"));
                
                System.out.print(id);
                System.out.print(", " + RSAencryptUtil.DecRsa(remark));
                System.out.println();
            }
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm);
            
            System.out.println("end");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ConnectionUtil.close(conn);
        }
    }
    
    private static void encryptApi() {
        Connection conn = null;
        try{
            conn = getConnection();
            
            String sql = "select ID,API_PSPT_NO from T_DEP_CP_API where API_PSPT_NO is not null";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String psptNo = StringUtil.trim(rs.getString("API_PSPT_NO"));
                
                System.out.print(id);
                System.out.print(", " + RSAencryptUtil.DecRsa(psptNo));
                System.out.println();
            }
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm);
            
            System.out.println("end");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ConnectionUtil.close(conn);
        }
    }
    
}
