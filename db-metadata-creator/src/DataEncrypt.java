import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cjm.utils.ConnectionUtil;
import com.cjm.utils.RSAencryptUtil;
import com.cjm.utils.StringUtil;


/**
 * 数据加密
 */
public class DataEncrypt {
    private static Connection getConnection() throws Exception {
        //"172.29.24.13", "dep1", "deptestnew", "deptestnew"
        Connection conn = ConnectionUtil.getConnection("172.29.24.13", "dep1", "deptestnew", "deptestnew");
        return conn;
    }
    
    public static void main(String[] args) {
        //encryptPassenger();  //旅客敏感数据加密
        //encryptCertificate();   //旅客证件数据加密
        //encryptPsgItem(); //旅客项数据加密，针对FOID项
        //encryptApi(); //API数据
    }

    private static void encryptPassenger() {
        Connection conn = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            
            String sql1 = "select ID,PSG_FF_CARD_NO,PSG_FF_ALLI_CARD_NO from T_DEP_CP_PASSENGER where PSG_FF_CARD_NO is not null or PSG_FF_ALLI_CARD_NO is not null";
            String sql2 = "update T_DEP_CP_PASSENGER set PSG_FF_CARD_NO=?,PSG_FF_ALLI_CARD_NO=? where ID=?";
            PreparedStatement pstm1 = conn.prepareStatement(sql1);
            PreparedStatement pstm2 = conn.prepareStatement(sql2);
            ResultSet rs = pstm1.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String ffCardNo = StringUtil.trim(rs.getString("PSG_FF_CARD_NO"));
                String ffAlliCardNo = StringUtil.trim(rs.getString("PSG_FF_ALLI_CARD_NO"));
                
                String ffCardNo2 = "";
                if(StringUtil.isNotEmpty(ffCardNo)){
                    ffCardNo2 = RSAencryptUtil.EncRsa(ffCardNo);
                }
                
                String ffAlliCardNo2 = "";
                if(StringUtil.isNotEmpty(ffAlliCardNo)){
                    ffAlliCardNo2 = RSAencryptUtil.EncRsa(ffAlliCardNo);
                }
                
                pstm2.setString(1, ffCardNo2);
                pstm2.setString(2, ffAlliCardNo2);
                pstm2.setLong(3, id);
                int i = pstm2.executeUpdate();
                System.out.println(id + ", " + i);
                
                
//                System.out.print(id);
//                System.out.print(", " + ffCardNo.equals(Rsaencrypt.DecRsa(ffCardNo2)));
//                System.out.print(", " + ffAlliCardNo.equals(Rsaencrypt.DecRsa(ffAlliCardNo2)));
//                System.out.println();
            }
            
            conn.commit();
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm1);
            
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
            conn.setAutoCommit(false);
            
            String sql1 = "select ID,CTF_NO from T_DEP_CP_CERTIFICATE where CTF_NO is not null";
            String sql2 = "update T_DEP_CP_CERTIFICATE set CTF_NO=? where ID=?";
            PreparedStatement pstm1 = conn.prepareStatement(sql1);
            PreparedStatement pstm2 = conn.prepareStatement(sql2);
            ResultSet rs = pstm1.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String ctfNo = StringUtil.trim(rs.getString("CTF_NO"));
                
                String ctfNo2 = "";
                if(StringUtil.isNotEmpty(ctfNo)){
                    ctfNo2 = RSAencryptUtil.EncRsa(ctfNo);
                }
                
                pstm2.setString(1, ctfNo2);
                pstm2.setLong(2, id);
                int i = pstm2.executeUpdate();
                System.out.println(id + ", " + i);
            }
            
            conn.commit();
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm1);
            
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
            conn.setAutoCommit(false);
            
            String sql1 = "select ID,PIT_REMARK,PIT_PD_REMARK from T_DEP_CP_PSG_ITEM where PIT_CODE='FOID' and (PIT_REMARK is not null or PIT_PD_REMARK is not null)";
            String sql2 = "update T_DEP_CP_PSG_ITEM set PIT_REMARK=? where ID=?";
            PreparedStatement pstm1 = conn.prepareStatement(sql1);
            PreparedStatement pstm2 = conn.prepareStatement(sql2);
            ResultSet rs = pstm1.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String remark = StringUtil.trim(rs.getString("PIT_REMARK"));
                
                String remark2 = "";
                if(StringUtil.isNotEmpty(remark)){
                    remark2 = RSAencryptUtil.EncRsa(remark);
                }
                
                pstm2.setString(1, remark2);
                pstm2.setLong(2, id);
                int i = pstm2.executeUpdate();
                System.out.println(id + ", " + i);
            }
            
            conn.commit();
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm1);
            
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
            conn.setAutoCommit(false);
            
            String sql1 = "select ID,API_PSPT_NO from T_DEP_CP_API where API_PSPT_NO is not null";
            String sql2 = "update T_DEP_CP_API set API_PSPT_NO=? where ID=?";
            PreparedStatement pstm1 = conn.prepareStatement(sql1);
            PreparedStatement pstm2 = conn.prepareStatement(sql2);
            ResultSet rs = pstm1.executeQuery();
            while(rs.next()){
                long id = rs.getLong("ID");
                String psptNo = StringUtil.trim(rs.getString("API_PSPT_NO"));
                
                String psptNo2 = "";
                if(StringUtil.isNotEmpty(psptNo)){
                    psptNo2 = RSAencryptUtil.EncRsa(psptNo);
                }
                
                pstm2.setString(1, psptNo2);
                pstm2.setLong(2, id);
                int i = pstm2.executeUpdate();
                System.out.println(id + ", " + i);
            }
            
            conn.commit();
            
            ConnectionUtil.close(rs);
            ConnectionUtil.close(pstm1);
            
            System.out.println("end");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ConnectionUtil.close(conn);
        }
    }
    
}
