package com.app.wedding.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.CharsetUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.app.wedding.Constants.C;
public class Upload extends Request<String> {

	MultipartEntityBuilder entity = MultipartEntityBuilder.create();
	HttpEntity httpentity;
	private String FILE_PART_NAME = "files";

	private final Response.Listener<String> mListener;
	private final File mFilePart;
	private Map<String, String> headerParams;
	private long fileLength = 0L;

	public Upload(String url, Response.ErrorListener errorListener,Response.Listener<String> listener, File file) {
		super(Method.POST, url, errorListener);

		this.mListener = listener;
		this.mFilePart = file;
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			entity.setCharset(CharsetUtils.get("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		buildMultipartEntity();
		httpentity = entity.build();
	}

	// public void addStringBody(String param, String value) {
	// if (mStringPart != null) {
	// mStringPart.put(param, value);
	// }
	// }

	private void buildMultipartEntity() {
		entity.addPart("file", new FileBody(mFilePart, ContentType.create("image/gif"), mFilePart.getName()));
		entity.addTextBody("message", "demo");

	}

	@Override
	public String getBodyContentType() {
		return httpentity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			httpentity.writeTo(new CountingOutputStream(bos, fileLength));
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
	
		try {
//			System.out.println("Network Response "+ new String(response.data, "UTF-8"));
			return Response.success(new String(response.data, "UTF-8"),
					getCacheEntry());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// fuck it, it should never happen though
			return Response.success(new String(response.data), getCacheEntry());
		}
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		params.put("Authorization", "Bearer "+ C.GET_TOKEN);
		return params;
	}

	public static interface MultipartProgressListener {
		void transferred(long transfered, int progress);
	}

	public static class CountingOutputStream extends FilterOutputStream {
		private long transferred;
		private long fileLength;

		public CountingOutputStream(final OutputStream out, long fileLength) {
			super(out);
			this.fileLength = fileLength;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);

				this.transferred += len;
			//	int prog = (int) (transferred * 100 / fileLength);

		}

		public void write(int b) throws IOException {
			out.write(b);
				this.transferred++;
				//int prog = (int) (transferred * 100 / fileLength);

		}

	}
}