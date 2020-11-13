package com.saviynt.pam.validator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saviynt.pam.exception.BadRequestException;

public class CommonComparator<T> implements Comparator<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonComparator.class);

	private static final String DATATYPE_STRING = "java.lang.String";
	private static final String DATATYPE_DATE = "java.util.Date";
	private static final String DATATYPE_INTEGER = "java.lang.Integer";
	private static final String DATATYPE_LONG = "java.lang.Long";
	private static final String DATATYPE_FLOAT = "java.lang.Float";
	private static final String DATATYPE_DOUBLE = "java.lang.Double";
	private String fieldName;
	private boolean isAscendingOrder;

	public CommonComparator(final boolean sortAscending, String sortFieldNames) {
		this.isAscendingOrder = sortAscending;
		this.fieldName = sortFieldNames;
	}

	/**
	 * Override method for comparison of two elements(Objects).
	 */
	@Override
	public int compare(final T obj1, final T obj2) {
		int response = 1;
		try {
			Object fieldVal1 = invokeGetterValue(fieldName, obj1);
			Object fieldVal2 = invokeGetterValue(fieldName, obj2);

			if (fieldVal1 == null || fieldVal2 == null) {
				response = compareNulls(fieldVal1, fieldVal2);
			} else {
				response = compareValue(fieldVal1, fieldVal2);
			}

		} catch (SecurityException | IllegalArgumentException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new BadRequestException("Invalid sort column");
		}
		return response;
	}

	public Object invokeGetterValue(String fieldName,T obj) {
		Object value = null;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
			Method getter = pd.getReadMethod();
			value = getter.invoke(obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IntrospectionException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new BadRequestException("Invalid sort column");
		}
		return value;
	}

	/**
	 * Compare two actual values, according to different data types.
	 * 
	 * @param v1
	 * @param v2
	 * @return 1,-1, 0
	 */
	private int compareNulls(final Object v1, final Object v2) {
		if (v1 == v2)
			return 0;
		else if (v1 == null)
			return 1;
		else
			return -1;

	}

	/**
	 * Compare two actual values, according to different data types.
	 * 
	 * @param v1
	 * @param v2
	 * @return 1,-1, 0
	 */
	private int compareValue(final Object v1, final Object v2) {
		int acutal = 1;
		if (v1.getClass() == v2.getClass()) {
			String fieldType = v1.getClass().getName();
			if (fieldType.equals(DATATYPE_INTEGER)) {
				acutal = (((Integer) v1).compareTo((Integer) v2) * determineDirect());
			} else if (fieldType.equals(DATATYPE_LONG)) {
				acutal = (((Long) v1).compareTo((Long) v2) * determineDirect());
			} else if (fieldType.equals(DATATYPE_STRING)) {
				acutal = (((String) v1).compareTo((String) v2) * determineDirect());
			} else if (fieldType.equals(DATATYPE_DATE)) {
				acutal = (((Date) v1).compareTo((Date) v2) * determineDirect());
			} else if (fieldType.equals(DATATYPE_FLOAT)) {
				acutal = (((Float) v1).compareTo((Float) v2) * determineDirect());
			} else if (fieldType.equals(DATATYPE_DOUBLE)) {
				acutal = (((Double) v1).compareTo((Double) v2) * determineDirect());
			}
		} else {
			LOGGER.error("Objects are different classes");
		}
		return acutal;
	}

	private int determineDirect() {
		return isAscendingOrder ? 1 : -1;
	}

}
