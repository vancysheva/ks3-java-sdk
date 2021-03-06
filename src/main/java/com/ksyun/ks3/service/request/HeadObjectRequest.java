package com.ksyun.ks3.service.request;

import java.util.ArrayList;

import static com.ksyun.ks3.exception.client.ClientIllegalArgumentExceptionGenerator.notNull;
import static com.ksyun.ks3.exception.client.ClientIllegalArgumentExceptionGenerator.notCorrect;

import java.util.Date;
import java.util.List;

import com.ksyun.ks3.dto.ResponseHeaderOverrides;
import com.ksyun.ks3.dto.SSECustomerKey;
import com.ksyun.ks3.http.HttpHeaders;
import com.ksyun.ks3.http.HttpMethod;
import com.ksyun.ks3.http.Request;
import com.ksyun.ks3.utils.HttpUtils;
import com.ksyun.ks3.utils.StringUtils;

/**
 * @author lijunwei[lijunwei@kingsoft.com]  
 * 
 * @date 2014年10月22日 下午7:40:04
 * 
 * @description Head请求一个object,可以用来判断一个object是否存在或者是用来获取object的元数据
 **/
public class HeadObjectRequest extends Ks3WebServiceRequest {
	private String bucket;
	private String key;
	private String range = null;
	/**
	 * object的etag能匹配到则返回，否则返回结果的ifPreconditionSuccess为false，metadata为空
	 */
	private List<String> matchingETagConstraints = new ArrayList<String>();
	/**
	 * object的etag不同于其中的任何一个，否则返回结果的ifModified为false,metadata为空
	 */
	private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
	/**
	 * 在此时间之后没有被修改过，否则返回结果的ifPreconditionSuccess为false，metadata为空
	 */
	private Date unmodifiedSinceConstraint;
	/**
	 * 在此时间之后被修改过，否则返回结果的ifModified为false,metadata为空
	 */
	private Date modifiedSinceConstraint;
	/**
	 * 指定服务端加密使用的算法及key
	 */
	private SSECustomerKey sseCustomerKey;
	/**
	 * 修改返回的response的headers
	 */
	private ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
	public HeadObjectRequest(String bucketname,String objectkey)
	{
		this.bucket = bucketname;
		this.key = objectkey;
	}

	@Override
	public void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.bucket))
			throw notNull("bucketname");
		if(StringUtils.isBlank(this.key))
			throw notNull("objectkey");
		if(!StringUtils.isBlank(range))
		{
			if(!range.startsWith("bytes="))
				throw notCorrect("Range",range," bytes=x-y,y>=x");
		}
	}
	
	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRange() {
		return range;
	}
	public void setRange(long start,long end) {
		this.range = "bytes="+start+"-"+end;
	}
	/**
	 * object的etag能匹配到则返回，否则返回结果的ifPreconditionSuccess为false，object为空
	 */
	public List<String> getMatchingETagConstraints() {
		return matchingETagConstraints;
	}
	/**
	 * object的etag能匹配到则返回，否则返回结果的ifPreconditionSuccess为false，object为空
	 */
	public void setMatchingETagConstraints(List<String> matchingETagConstraints) {
		this.matchingETagConstraints = matchingETagConstraints;
	}
	/**
	 * object的etag不同于其中的任何一个，否则返回结果的ifModified为false,object为空
	 */
	public List<String> getNonmatchingEtagConstraints() {
		return nonmatchingEtagConstraints;
	}
	/**
	 * object的etag不同于其中的任何一个，否则返回结果的ifModified为false,object为空
	 */
	public void setNonmatchingEtagConstraints(
			List<String> nonmatchingEtagConstraints) {
		this.nonmatchingEtagConstraints = nonmatchingEtagConstraints;
	}
	/**
	 * 在此时间之后没有被修改过，否则返回结果的ifPreconditionSuccess为false，object为空
	 */
	public Date getUnmodifiedSinceConstraint() {
		return unmodifiedSinceConstraint;
	}
	/**
	 * 在此时间之后没有被修改过，否则返回结果的ifPreconditionSuccess为false，object为空
	 */
	public void setUnmodifiedSinceConstraint(Date unmodifiedSinceConstraint) {
		this.unmodifiedSinceConstraint = unmodifiedSinceConstraint;
	}
	/**
	 * 在此时间之后被修改过，否则返回结果的ifModified为false,object为空
	 */
	public Date getModifiedSinceConstraint() {
		return modifiedSinceConstraint;
	}
	/**
	 * 在此时间之后被修改过，否则返回结果的ifModified为false,object为空
	 */
	public void setModifiedSinceConstraint(Date modifiedSinceConstraint) {
		this.modifiedSinceConstraint = modifiedSinceConstraint;
	}
	public ResponseHeaderOverrides getOverrides() {
		return overrides;
	}
	public void setOverrides(ResponseHeaderOverrides overrides) {
		this.overrides = overrides;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public SSECustomerKey getSseCustomerKey() {
		return sseCustomerKey;
	}
	public void setSseCustomerKey(SSECustomerKey sseCustomerKey) {
		this.sseCustomerKey = sseCustomerKey;
	}

	@Override
	public void buildRequest(Request request) {
		request.setMethod(HttpMethod.HEAD);
		request.setBucket(bucket);
		request.setKey(key);
		if(!StringUtils.isBlank(range))
			request.addHeader(HttpHeaders.Range,range);
		if(matchingETagConstraints.size()>0)
			request.addHeader(HttpHeaders.IfMatch, StringUtils.join(matchingETagConstraints, ","));
		if(nonmatchingEtagConstraints.size()>0)
			request.addHeader(HttpHeaders.IfNoneMatch, StringUtils.join(nonmatchingEtagConstraints, ","));
		if(this.unmodifiedSinceConstraint !=null)
			request.addHeader(HttpHeaders.IfUnmodifiedSince, this.unmodifiedSinceConstraint.toGMTString());
		if(this.modifiedSinceConstraint !=null)
			request.addHeader(HttpHeaders.IfModifiedSince, this.modifiedSinceConstraint.toGMTString());
		request.getQueryParams().putAll(this.overrides.getOverrides());
		//添加服务端加密相关
		request.getHeaders().putAll(HttpUtils.convertSSECustomerKey2Headers(sseCustomerKey));
	}
	
}
