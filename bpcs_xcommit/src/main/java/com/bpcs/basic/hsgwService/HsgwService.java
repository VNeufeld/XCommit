/**
 * 
 */
package com.bpcs.basic.hsgwService;

import com.bpcs.framework.exception.MarshallingException;


/**
 * this class retrieve the Cars information from hsgw
 * @author neu
 *
 */
public interface HsgwService {
	
	public String getHsgwConnection();

	public String executeToString( Object legacyRequest) throws MarshallingException;
}
