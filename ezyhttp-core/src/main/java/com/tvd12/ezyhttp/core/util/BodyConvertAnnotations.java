package com.tvd12.ezyhttp.core.util;

import com.tvd12.ezyhttp.core.annotation.BodyConvert;

public final class BodyConvertAnnotations {
	
	private BodyConvertAnnotations() {}
	
	public static String getContentType(BodyConvert annotation) {
		return annotation.value();
	}
	
	public static String getContentType(Object converter) {
		BodyConvert annotation = converter.getClass().getAnnotation(BodyConvert.class);
		return getContentType(annotation);
	}
	
}
