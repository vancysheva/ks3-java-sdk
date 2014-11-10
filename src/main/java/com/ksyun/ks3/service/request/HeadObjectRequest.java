package com.ksyun.ks3.service.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ksyun.ks3.dto.ResponseHeaderOverrides;
import com.ksyun.ks3.http.HttpHeaders;
import com.ksyun.ks3.http.HttpMethod;

/**
 * @author lijunwei[13810414122@163.com]  
 * 
 * @date 2014年10月22日 下午7:40:04
 * 
 * @description 
 **/
public class HeadObjectRequest extends Ks3WebServiceRequest {
	private String range = null;
	private List<String> matchingETagConstraints = new ArrayList<String>();
	private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
	private Date unmodifiedSinceConstraint;
	private Date modifiedSinceConstraint;
	private ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
	public HeadObjectRequest(String bucketname,String objectkey)
	{
		this.setBucketname(bucketname);
		this.setObjectkey(objectkey);
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void configHttpRequest() {
		this.setHttpMethod(HttpMethod.HEAD);
		if(!StringUtils.isBlank(range))
			this.addHeader(HttpHeaders.Range,range);
		if(matchingETagConstraints.size()>0)
			this.addHeader(HttpHeaders.IfMatch, StringUtils.join(matchingETagConstraints, ","));
		if(nonmatchingEtagConstraints.size()>0)
			this.addHeader(HttpHeaders.IfNoneMatch, StringUtils.join(nonmatchingEtagConstraints, ","));
		if(this.unmodifiedSinceConstraint !=null)
			this.addHeader(HttpHeaders.IfUnmodifiedSince, this.unmodifiedSinceConstraint.toGMTString());
		if(this.modifiedSinceConstraint !=null)
			this.addHeader(HttpHeaders.IfModifiedSince, this.modifiedSinceConstraint.toGMTString());
		this.getHeader().putAll(this.overrides.getOverrides());
	}

	@Override
	protected void validateParams() throws IllegalArgumentException {
		if(StringUtils.isBlank(this.getBucketname()))
			throw new IllegalArgumentException("bucket name can not be null");
		if(StringUtils.isBlank(this.getObjectkey()))
			throw new IllegalArgumentException("object key can not be null");
		if(!StringUtils.isBlank(range))
		{
			if(!range.startsWith("bytes="))
				throw new IllegalArgumentException("Range should be start with 'bytes='");
		}
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
}
