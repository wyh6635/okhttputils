package utils.okhttp.request;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import utils.okhttp.OkHttpUtils;

@SuppressWarnings("unused")
public class PostFileRequest extends OkHttpRequest {
    protected File file;
    protected MediaType mediaType;

    protected PostFileRequest(PostFileBuilder builder) {
        super(builder);
        this.file = builder.file;
        this.mediaType = builder.mediaType;
    }

    @Override
    public PostFileBuilder newBuilder() {
        return new PostFileBuilder(this);
    }

    @Override
    public String method() {
        return "POST";
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, file);
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody) {
        return new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                OkHttpUtils.getInstance().getThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten, contentLength);
                    }
                });
            }
        });
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    /**
     * 返回待提交的文件
     */
    public File file() {
        return file;
    }
}
