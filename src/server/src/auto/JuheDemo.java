package auto;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JuheDemo {
	private Double lati;
	private Double longi;
	public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    //配置您申请的KEY
    public static final String APPKEY ="6b81b6411726ea49c2be2912ad9debec";
    
    public JuheDemo(String a,String b){
    	lati=Double.valueOf(a);
    	longi=Double.valueOf(b);
    }
    
    //2.检索周边加油站
    public String getRequest2(){
        String result =null;
        String url ="http://apis.juhe.cn/oil/local";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("lon",longi);//经纬(如:121.538123)
        params.put("lat",lati);//纬度(如：31.677132)
        params.put("r",3000);//搜索范围，单位M，默认3000，最大10000
        params.put("page",1);//页数,默认1
        params.put("format",2);//格式选择1或2，默认1
        params.put("key",APPKEY);//应用APPKEY(应用详细页查询)
        
        String ret="";
        try {
        	result =net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                //System.out.println(object.get("result"));
                JSONObject jsonObject = (JSONObject) object.get("result");
                JSONArray a1=jsonObject.getJSONArray("data");
                //System.out.println(a1);
                //System.out.println(a1.size());
                for(int i=0;i<a1.size();++i){
                    JSONObject o=a1.getJSONObject(i);
                    //System.out.println(o);
                    String position=o.getString("position");      
                    String[] pp=position.split(",");
                    Double f1=Double.valueOf(pp[0]);
                    Double f2=Double.valueOf(pp[1]);
                    //System.out.println(f1+","+f2);
                    if(Math.abs(f1-longi)<0.001 && Math.abs(f2-lati)<0.001){
                    	//System.out.println("yeye");
                    	String id=o.getString("id");
                    	String name=o.getString("name");
                    	JSONArray a2=o.getJSONArray("price");
                    	//System.out.println(a2);
                    	//System.out.println(a2.size());
                    	for(int j=0;j<a2.size();++j){
                    		//System.out.println(a2.getJSONObject(j).get("type"));
                    		ret+=a2.getJSONObject(j).get("type");
                    		ret+=',';
                    	}
                    	ret+=id;
                    	ret+=',';
                    	ret+=name;
                    	break;
                    }
                }
            }else{
                System.out.println(object.get("error_code")+":"+object.get("reason"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    private String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            /*if (params!= null && method.equals("POST")) {
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    out.writeBytes(urlencode(params));
                }
            }*/
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    private String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
