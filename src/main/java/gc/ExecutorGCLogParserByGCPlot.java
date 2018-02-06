package gc;

import com.sun.deploy.net.HttpRequest;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by xulijie on 17-9-1.
 */
public class ExecutorGCLogParserByGCPlot {


    // from http://www.cnblogs.com/zhuawang/archive/2012/12/08/2809380.html
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
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
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

    public static void main(String[] args) {

        String executorFile = "/Users/xulijie/Documents/GCResearch/Experiments/profiles/GroupByRDD-0.5-2/GroupByRDD-Parallel-2-14G-0.5_app-20170721101243-0006/executors";

        String gcLogFile = executorFile + File.separatorChar + "0" + File.separatorChar + "stdout";

        System.out.println(gcLogFile);

        String sr = sendPost("http://localhost:8080/analyzeGC?apiKey=e094a34e-c3eb-4c9a-8254-f0dd107245cc",
                "token=ad7a623b201a6c839cd29b25467ac4cd794947fad97956ff0ce496b7c42ea66f&upload=@" + gcLogFile);
        System.out.println(sr);

        // curl  -include --form upload=@/Users/xulijie/Documents/GCResearch/Experiments/profiles/GroupByRDD-0.5-2/GroupByRDD-Parallel-2-14G-0.5_app-20170721101243-0006/executors/0/stdout https://gs.gcplot.com/gc/jvm/log/process?token=ad7a623b201a6c839cd29b25467ac4cd794947fad97956ff0ce496b7c42ea66f


        // curl -X POST --data-binary @/Users/xulijie/Documents/GCResearch/Experiments/profiles/GroupByRDD-0.5-2/GroupByRDD-Parallel-2-14G-0.5_app-20170721101243-0006/executors/12/stdout http://localhost:8080/analyzeGC?apiKey=e094a34e-c3eb-4c9a-8254-f0dd107245cc --header "Content-Type:text"


        //curl -include --form upload=@/Users/xulijie/Documents/GCResearch/Experiments/profiles/GroupByRDD-0.5-2/GroupByRDD-Parallel-2-14G-0.5_app-20170721101243-0006/executors/12/stdout http://localhost:8080/analyzeGC?apiKey=e094a34e-c3eb-4c9a-8254-f0dd107245cc --header "Content-Type:text"

    }
}
