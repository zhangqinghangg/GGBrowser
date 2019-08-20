package Utlis;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 描述：Httppost请求类（json数据传输）
 */
public class PayHttpUtils {
	/**
	 * @param url
	 * 请求的网址
	 */
	public static String GetSingleCabCollect(String url) {
		HttpPost httpPost = new HttpPost(url);
		JSONObject jsonParam = new JSONObject();
		try {
			jsonParam.put("idLevel", "0001");
			jsonParam.put("idDev", "Dev1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 解决中文乱码问题
		StringEntity entity = null;
		try {
			entity = new StringEntity(jsonParam.toString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (entity != null) {
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
		}
		httpPost.setEntity(entity);
		HttpClient httpClient = new DefaultHttpClient();
		// 获取HttpResponse实例
		HttpResponse httpResp = null;
		try {
			httpResp = httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 判断是够请求成功
		if (httpResp != null) {
			if (httpResp.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				String result = null;
				try {
					result = EntityUtils
							.toString(httpResp.getEntity(), "UTF-8");
					Log.e("HttpPost方式请求成功，返回数据如下：", result);
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("打印数据", "HttpPost方式请求失败"
						+ httpResp.getStatusLine().getStatusCode());
			}
		}
		return null;
	}

}
