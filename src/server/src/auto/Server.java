package auto;

import java.util.*;

import com.mysql.jdbc.PreparedStatement;

import java.sql.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

class MyThread extends Thread{
	private ServerSocket ss;
	private Socket s;
	private BufferedReader buffer;
	private PrintWriter os;
	
	public MyThread(ServerSocket ss){
		this.ss=ss;
	}
	public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            //out = new PrintWriter(conn.getOutputStream());
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"),true);
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
	public void run(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn=DriverManager.getConnection("jdbc:mysql://localhost/auto?user=root&password=root&characterEncoding=UTF-8");
			if(!conn.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			
			while(true){
				s=ss.accept();
				buffer=new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
				os=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
				
				String line=buffer.readLine();
				System.out.println(line);
				//List<String> infos = new ArrayList<String>();
            	String[] infos = line.split(",");
            	String request=infos[0];
            	if(request.equals("login")){
            		String username=infos[1];
            		String pw=infos[2];
            		Statement stment=conn.createStatement();
            		String sql="select * from users where Username=" + "'" + username + "'";
        			try{
        				ResultSet result=stment.executeQuery(sql);
            			if(result.next()){
            				if(pw.equals(result.getString("Password")))
            				{
            					String ans="ok"+","+result.getString("Userid")+","+result.getString("Username")+","+result.getString("MusicSetting")+","+result.getString("ErrorSetting");
            					System.out.println(ans);
            					os.println(ans);
            				}
            					
            				else
            					os.println("fail,the password is wrong");
            			}
            			else{
            				os.println("fail,the user does not exist");
            			}
            			stment.close();
        			}catch(SQLException e){
        				e.printStackTrace();
        				os.println("fail,something goes wrong");
        			}
            		
            	}
            	else if(request.equals("register")){
            		String username=infos[1];
            		String pw=infos[2];
            		String email=infos[3];
            		PreparedStatement pstmt;
            		//Statement stment=conn.createStatement();
            		try{
            			String sql="insert into users(Username,Password,Email) values(?,?,?)";
                		pstmt = (PreparedStatement) conn.prepareStatement(sql);
                		pstmt.setString(1, username);
                		pstmt.setString(2, pw);
                		pstmt.setString(3, email);
                		int i=pstmt.executeUpdate();
                		pstmt.close();
                		//conn.close();
                		if(i>0)
                			os.println("ok,register successfully");
                		else
                			os.println("fail,email is wrong");
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("fail,something goes wrong");
            		}           		
            	}else if(request.equals("book")){
            		JuheDemo demo=new JuheDemo(infos[1],infos[2]);
            		String result=demo.getRequest2();
            		System.out.println(result);
            		
            		int user_id = Integer.valueOf(infos[3]);
            		String cars="";
            		try{
            			String sql = "select * from owning where Userid=?";
                		PreparedStatement st = (PreparedStatement) conn.prepareStatement(sql);
                		st.setInt(1, user_id);
                		ResultSet rs = st.executeQuery();
                		int number=0;
                		while(rs.next()){
                			++number;
                			cars+=rs.getString(2)+",";
                		}
                		cars+=number;
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("fail");
            		}
            		
            		//demo.getRequest2();
            		if(result==null)
            			os.println("fail");
            		else{
            			String x="ok"+","+result+","+cars;
            			System.out.println(x);
                		os.println(x);
            		}
            		
            	}    	
            	else if(request.equals("appointment")){
            		int id=Integer.valueOf(infos[1]);
            		System.out.println(id);
            		//String id=infos[1];
            		String name=infos[2];
            		String time=infos[3];
            		String stationid=infos[4];
            		String stationname=infos[5];
            		String type=infos[6];
            		int number=Integer.valueOf(infos[7]);
            		String carid=infos[8];
            		PreparedStatement st;
            		try{
            			String sql="insert into orders(Userid,Carid,Name,Time,Stationid,Stationname,Type,number) values(?,?,?,?,?,?,?,?)";
                		st = (PreparedStatement) conn.prepareStatement(sql);
                		st.setInt(1, id);
                		st.setString(2, carid);
                		st.setString(3, name);
                		st.setString(4, time);
                		st.setString(5, stationid);
                		st.setString(6, stationname);
                		st.setString(7, type);
                		st.setInt(8, number);
                		int i=st.executeUpdate();
                		st.close();
                		//conn.close();
                		if(i>0)
                			os.println("ok");
                		else
                			os.println("fail");
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("fail");
            		}
            	}else if(request.equals("carinfo")){
            		int id=Integer.valueOf(infos[1]);
            		String carid=infos[2];
            		String brand=infos[3];
            		String version=infos[4];
            		String engineid=infos[5];
            		float bodylevel=Float.valueOf(infos[6]);
            		int miles=Integer.valueOf(infos[7]);
            		int oilmass=Integer.valueOf(infos[8]);
            		int enginestate=Integer.valueOf(infos[9]);
            		int transmissionstate=Integer.valueOf(infos[10]);
            		int lightstate=Integer.valueOf(infos[11]);
            		conn.setAutoCommit(false);
            		PreparedStatement st1,st2;
            		try{
            			String sql1="insert into cars(Carid,Brand,Version,Engineid,Bodylevel,Miles,Oilmass,Enginestate,Transmissionstate,Lightstate,vin,cityName)"
            					+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            			String sql2="insert into owning(Userid,Carid) values(?,?)";
            			
            			st1=(PreparedStatement)conn.prepareStatement(sql1);
            			//st2=conn.prepareStatement(sql2)
            			st1.setString(1, carid);
            			st1.setString(2, brand);
            			st1.setString(3, version);
            			st1.setString(4, engineid);
            			st1.setFloat(5, bodylevel);
            			st1.setInt(6,miles);
            			st1.setInt(7, oilmass);
            			st1.setInt(8, enginestate);
            			st1.setInt(9, transmissionstate);
            			st1.setInt(10, lightstate);
            			st1.setString(11, infos[12]);
            			st1.setString(12, infos[13]);
            			st1.executeUpdate();
            			
            			st2=(PreparedStatement)conn.prepareStatement(sql2);
            			st2.setInt(1, id);
            			st2.setString(2, carid);
            			st2.executeUpdate();
            			           		
            			conn.commit();
            			os.println("ok");
            		}catch(SQLException e){
            			try{
            				conn.rollback();
            				os.println("fail");
            			}catch(Exception e1){
            				e1.printStackTrace();
            			}
            			e.printStackTrace();
            			os.println("fail");
            		}finally{
            			try {
                            conn.setAutoCommit(true);
                         } catch (SQLException e) {
                            e.printStackTrace();
                         }
            		}
            	}else if(request.equals("getinfos")){
            		int id=Integer.valueOf(infos[1]);
            		Statement st=conn.createStatement();
            		try{
            			String a="select Carid from owning where Userid="+id+"";
            			ResultSet rs=st.executeQuery(a);
            			int number=0;
            			String tt="";
            			while(rs.next()){
            				System.out.println(rs.getString(1));
            				
            				String sql="select * from cars where Carid=?";
            				PreparedStatement st1=(PreparedStatement)conn.prepareStatement(sql);
            				st1.setString(1, rs.getString(1));
            				ResultSet rs1=st1.executeQuery();
            				while(rs1.next()){
            					++number;            					
            					tt+=rs1.getString(1)+","+rs1.getString(2)+","+rs1.getString(3)+","+rs1.getString(4)+","+rs1.getFloat(5)+","
            							+rs1.getInt(6)+","+rs1.getInt(7)+","+rs1.getInt(8)+","+rs1.getInt(9)+","+rs1.getInt(10)+",";            					
            				}
            			}
            			System.out.println(tt+number);
            			os.println(tt+number);
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("0");
            		}
            	}else if(request.equals("check")){
            		int id=Integer.valueOf(infos[1]);
            		Statement st=conn.createStatement();
            		try{
            			String a="select Carid from owning where Userid="+id+"";
            			ResultSet rs=st.executeQuery(a);
            			int number=0;
            			String respond="";
            			while(rs.next()){
            				//System.out.println(rs.getString(1));			
            				String sql="select * from cars where Carid=?";
            				String carid=rs.getString(1);
            				PreparedStatement st1=(PreparedStatement)conn.prepareStatement(sql);
            				st1.setString(1, carid);
            				ResultSet rs1=st1.executeQuery();
            				while(rs1.next()){          					
            					int total=0;
            					int ms=0,os=0,es=0,ts=0,ls=0;
            					int miles=Integer.valueOf(rs1.getString(6));
            					int passmiles=Integer.valueOf(rs1.getString(11));
            					if(miles-passmiles>15000){
            						ms=1;
            						++total;
            						String updat = "update cars set Passmiles=Passmiles+15000 where Carid=?";
            						PreparedStatement up=(PreparedStatement)conn.prepareStatement(updat);
            						up.setInt(1, Integer.valueOf(carid));
            						up.executeUpdate();
                            			
            					}
            					int oil=Integer.valueOf(rs1.getString(7));
            					if(oil<20){
            						os=1;
            						++total;
            					}            						
            					String engine=rs1.getString(8);
            					if(engine.equals("0")){
            						es=1;
            						++total;
            					}
            					String transmission=rs1.getString(9);
            					if(transmission.equals("0")){
            						ts=1;
            						++total;
            					}
            					String light=rs1.getString(10);
            					if(light.equals("0")){
            						ls=1;
            						++total;
            					}
            					if(total!=0){
            						++number;
            						respond+=carid+","+ms+","+os+","+es+","+ts+","+ls+",";
            					}
            				}
            			}
            			System.out.println(respond);
            			if(number==0)
            				os.println("ok");
            			else{
            				os.println("fail"+","+number+","+respond);
            			}
            			
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("ok");
            		}	
            	}else if(request.equals("getorders")){
            		int id=Integer.valueOf(infos[1]);
            		PreparedStatement st1;
            		try{
            			String sql="select * from orders where Userid=?";
            			st1=(PreparedStatement)conn.prepareStatement(sql);
            			st1.setInt(1, id);
            			ResultSet rs1=st1.executeQuery();
            			int num=0;
            			String respond="";
            			while(rs1.next()){
            				++num;
            				respond+=rs1.getString(3)+","+rs1.getString(4)+","+rs1.getString(6)+","+rs1.getString(7)+","+rs1.getString(8)+","+rs1.getString(2)+",";
            			}
            			System.out.println(respond);
            			if(num==0)
            				os.println("0");
            			else
            				os.println(num+","+respond);
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("0");
            		}
            	}else if(request.equals("weizhang")){
            		String res="";
            		String key="b8200e44add54adfb5bfdeda3da0daf8";
            		String engineNumber="";
            		String vin="";
            		String cityName="";
            		try{
            			String sql="select * from cars where Carid=?";
            			PreparedStatement pst=(PreparedStatement)conn.prepareStatement(sql);
            			pst.setString(1, infos[1]);
            			ResultSet rs=pst.executeQuery();
            			while(rs.next()){
            				engineNumber=rs.getString(4);
            				vin=rs.getString(12);
            				cityName=rs.getString(13);
            				break;
            			}
            		}catch(SQLException e){
            			e.printStackTrace();
            		}
            		String carInfo="plateNumber="+infos[1]+"&engineNumber="+engineNumber+"&vehicleIdNumber="+vin+"&cityName="+cityName+"&hpzl=02&key="+key;
            		String url="http://apis.haoservice.com/weizhang/EasyQuery";
            		String result=sendPost(url,carInfo);
            		//System.out.println(result);
            		JSONObject jo=JSONObject.fromObject(result);
            		String totalScore="";
            		String totalMoney="";
            		String count="";
            		if(jo.getInt("error_code")==0){
            			JSONObject jsonject = jo.getJSONObject("result");
            			String list=jsonject.getString("lists");
            			//System.out.println(list);
            			if(list.equals("null"))
            			{
            				count="0";
            				totalScore="0";
            				totalMoney="0";
            				res+=totalScore+","+totalMoney+","+count+",";
            				//break;
            			}
            			else{
            				JSONArray jar = jsonject.getJSONArray("lists");
            				count=String.valueOf(jar.size());
            				int money=0;
            				int score=0;
            				for(int i=0;i<jar.size();++i){
            					JSONObject oo=jar.getJSONObject(i);
            					money+=Integer.valueOf(oo.getString("money"));
            					score+=Integer.valueOf(oo.getString("fen"));
            					res+=oo.getString("date")+","+oo.getString("money")+","+oo.getString("area")+","+oo.getString("act")+","+oo.getString("fen")+",";
            				}
            				totalScore=String.valueOf(score);
            				totalMoney=String.valueOf(money);
            				res=totalScore+","+totalMoney+","+count+","+res;
            			}
            		}
            		
            		System.out.println(res);
            		os.println(res);
            	}else if(request.equals("onMusic")){
            		int id = Integer.valueOf(infos[1]);
            		int flag = Integer.valueOf(infos[2]);
            		PreparedStatement pstmt;
            		try{
            			String sql="update users set MusicSetting=? where Userid=?";
            			//String sql="insert into users(Username,Password,Email) values(?,?,?)";
                		pstmt = (PreparedStatement) conn.prepareStatement(sql);
                		pstmt.setInt(1, flag);
                		pstmt.setInt(2, id);
                		int i=pstmt.executeUpdate();
                		if(i>0){
                			os.println("ok");
                		}else{
                			os.println("fail");
                		}
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("fail");
            		}
            	}else if(request.equals("onError")){
            		int id = Integer.valueOf(infos[1]);
            		int type = Integer.valueOf(infos[2]);
            		PreparedStatement pstmt;
            		try{
            			String sql="update users set ErrorSetting=? where Userid=?";
            			//String sql="insert into users(Username,Password,Email) values(?,?,?)";
                		pstmt = (PreparedStatement) conn.prepareStatement(sql);
                		pstmt.setInt(1, type);
                		pstmt.setInt(2, id);
                		int i=pstmt.executeUpdate();
                		if(i>0){
                			os.println("ok");
                		}else{
                			os.println("fail");
                		}
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("fail");
            		}
            	}else if(request.equals("delcar")){
            		String car_id = infos[1];
            		//System.out.println(car_id);
            		PreparedStatement ps=null;
            		PreparedStatement ps1=null;
            		try{
            			String sql="delete from cars where Carid=?";
            			ps = (PreparedStatement) conn.prepareStatement(sql);
            			ps.setString(1, car_id);
            			int i=ps.executeUpdate();
            			String sss="delete from owning where Carid=?";
            			ps1 = (PreparedStatement) conn.prepareStatement(sss);
            			ps1.setString(1, car_id);
            			int j=ps1.executeUpdate();
            			//System.out.println(i);
            			//System.out.println(j);
            			if(i>0 && j>0)
            				os.println("ok");
            			else
            				os.println("fail");
            		}catch(SQLException e){
            			e.printStackTrace();
            			os.println("fail");
            		}
            	}else{
            		continue;
            	}
            	
				os.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
}

public class Server {
	public static void main(String[] args){
		try{			
			ServerSocket ss=new ServerSocket(4800);
			//for (int i=0; i<10; i++){
				MyThread mt = new MyThread(ss);
				mt.start();
			//}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Server(){
		
	}
}
