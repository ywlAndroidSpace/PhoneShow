
package com.likebamboo.phoneshow.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.likebamboo.phoneshow.R;
import com.likebamboo.phoneshow.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * @author likebamboo
 */
public class GetInfoTask extends AsyncTask<Void, Void, Boolean> {

    /**
     * 上下文对象
     */
    private Context mContext = null;

    /**
     * 错误信息
     */
    private String mError = "";

    /**
     * 返回的数据
     */
    private String retJson = "";

    /**
     * 是否取消运行
     */
    private Status mStatus = Status.PENDING;

    /**
     * 返回结果监听器
     */
    private IOnResultListener mListener = null;

    public interface IOnResultListener {
        void onResult(final boolean success, final String error, final String retJson);
    }

    /**
     * 取消
     */
    public void cancel() {
        mStatus = Status.FINISHED;
    }

    /**
     * 进程是否在运行
     * 
     * @return
     */
    public boolean isRunning() {
        return mStatus != Status.FINISHED;
    }

    public GetInfoTask(Context mContext, IOnResultListener listener) {
        super();
        this.mContext = mContext;
        this.mListener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO Auto-generated method stub
        // 正在运行
        mStatus = Status.RUNNING;

        // 没网络
        if (!Utils.CheckNetworkConnection(mContext) && !Utils.IsWifiEnable(mContext)) {
            mError = mContext.getString(R.string.network_disable);
            return false;
        }

        HttpClient httpClient = new DefaultHttpClient();
        // 设置超时时间
        HttpGet post = new HttpGet("http://www.google.com/ig/cities?hl=zh-cn&country=cn");
        try {
            HttpResponse response = httpClient.execute(post);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == HttpStatus.SC_OK) {
                HttpEntity retEntity = response.getEntity();
                String ret = EntityUtils.toString(retEntity);
                JSONObject json = new JSONObject(ret);
                if (json.has("cities")) {
                    retJson = json.get("cities").toString();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mError = mContext.getString(R.string.unknow_error);
            return false;
        }

        if (!TextUtils.isEmpty(retJson)) {
            return true;
        }

        mError = mContext.getString(R.string.time_out);
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        if (mListener != null && mStatus != Status.FINISHED) {// 如果操作没有被取消
            System.out.println("return:" + retJson);
            mListener.onResult(result, mError, retJson);
        }
        mStatus = Status.FINISHED;
        super.onPostExecute(result);
    }

}
