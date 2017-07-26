package api.func;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.JacksonUtils;

import api.ApiParams;
import api.ApiResult;
import modules.registry.RegistryGlobal;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

public class ApiFuncMessageProcess {
	
	private static WSClient wsClient = RegistryGlobal.registry.getWsClient();
	private static String urlShippingOrder = "http://ghn-cdproxy.gpvcloud.com/api/getShippingOrders?code=1ID0UT2A";
	private static String urlCreateNewTicket = "http://staging-api-ticket.ghn.vn/api/tickets/create";
	
	public static ApiResult messageProcess(ApiParams params) throws InterruptedException, ExecutionException, ParseException{
		
		JsonNode request = params.getAllParams();
		
		Logger.info("----------Data Post " + params.getAllParams());
		JsonNode resultShippingOrder = callApiExternalByGet(urlShippingOrder, "");
		JsonNode getDataFromShippingOrder = resultShippingOrder.get("data");
		if (getDataFromShippingOrder.size() < 0) {
				return new ApiResult(404, "Khong tim thay don hang");
		} else {
			
			Logger.info("-------------- " + createRequestCreateNewTicket(request).toString());
			JsonNode resultCreateNewTicket = callApiExternalByPost(urlCreateNewTicket, createRequestCreateNewTicket(request).toString());
			if(JacksonUtils.getValue(resultCreateNewTicket, "success", Boolean.class)){
				return ApiResult.resultOk("Thanh cong");
			}else{
				return new ApiResult(404, "Khong thanh cong");
			}
		}
	}
	

	public static JsonNode callApiExternalByGet(String url, String queryString) throws InterruptedException, ExecutionException{
		CompletionStage<WSResponse> responsePromise = wsClient.url(url)
                .setContentType("application/json").setFollowRedirects(false)
                .get();
    	 WSResponse wsResponse = responsePromise.toCompletableFuture().get();
    	 JsonNode result = wsResponse.asJson();
    	 
    	 return result;
	}
	
	public static JsonNode callApiExternalByPost(String url, String data) throws InterruptedException, ExecutionException{
		CompletionStage<WSResponse> responsePromise = wsClient.url(url)
                .setContentType("application/json").setFollowRedirects(false)
                .post(data);
    	 WSResponse wsResponse = responsePromise.toCompletableFuture().get();
    	 JsonNode result = wsResponse.asJson();
    	 
    	 return result;
	}
	
	public static JsonNode createRequestCreateNewTicket(JsonNode requestFromSMSGateWay){
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		Logger.info("------- Old Date " + new Date().toString());
		Logger.info("-------Date " + nowAsISO);  // 2010-01-01T18:00:00.000+07:00
		
		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, String> order = new HashMap<String, String>();
		order.put("id", "4534645646456");
		order.put("code", "4534645646456");

		List<Map<String, String>> listOrder = new ArrayList<>();
		listOrder.add(order);

		request.put("department_id", 666);
		request.put("type_id", 1);
		request.put("eta_duration", 1);
		request.put("title", "Ticket Title");
		request.put("content", "Demo content");
		request.put("order_ids", listOrder);

		JsonNode parseRequest = Json.toJson(request);
		
		return parseRequest;
	}

}
