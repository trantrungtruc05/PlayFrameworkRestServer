package controllers;

import play.mvc.Result;

public class MessageApiController extends BaseJsonWsController {
	
	public Result messageProcess() throws Exception {
		return doApiCall("messageProcess");
	}
}
