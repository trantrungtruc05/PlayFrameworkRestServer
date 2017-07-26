package api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.ddth.commons.utils.MapUtils;

import utils.IdUtils;

/**
 * API's running context.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiContext {

    public static ApiContext newContext(String apiName) {
        return new ApiContext(apiName);
    }

    public static ApiContext newContext(String gateway, String apiName) {
        ApiContext context = newContext(apiName);
        return context.setContextField(CTX_GATEWAY, gateway);
    }

    public final String id = IdUtils.nextId();
    public final long timestamp = System.currentTimeMillis();
    public final static String CTX_API_NAME = "api_name";
    public final static String CTX_GATEWAY = "gateway";

    private Map<String, Object> context = new ConcurrentHashMap<>();

    public ApiContext(String apiName) {
        this(apiName, null);
    }

    public ApiContext(String apiName, Map<String, Object> contextData) {
        if (contextData != null) {
            this.context.putAll(contextData);
        }
        setContextField(CTX_API_NAME, apiName);
    }

    /**
     * Set a context value.
     * 
     * @param name
     * @param value
     * @return
     */
    public ApiContext setContextField(String name, Object value) {
        context.put(name, value);
        return this;
    }

    /**
     * Get a context value
     * 
     * @param name
     * @param clazz
     * @return
     */
    public <T> T getContextField(String name, Class<T> clazz) {
        return MapUtils.getValue(context, name, clazz);
    }

    /**
     * Get context value: API name.
     * 
     * @return
     */
    public String getApiName() {
        return getContextField(CTX_API_NAME, String.class);
    }

    /**
     * Get context value: API gateway (e.g. "web" or "thrift").
     * 
     * @return
     */
    public String getGateway() {
        return getContextField(CTX_GATEWAY, String.class);
    }

}
