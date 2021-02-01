package com.saviynt.pam.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saviynt.pam.enums.SortOrder;
import com.saviynt.pam.exception.BadRequestException;

/**
 * The {@link CommonComparator} implements the generic comparator for 
 * all POJO classes using Reflection API  
 */
public class CommonComparator<T> implements Comparator<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonComparator.class);

	private static final String DATATYPE_STRING = "java.lang.String";
	private static final String DATATYPE_DATE = "java.util.Date";
	private static final String DATATYPE_INTEGER = "java.lang.Integer";
	private static final String DATATYPE_LONG = "java.lang.Long";
	private static final String DATATYPE_FLOAT = "java.lang.Float";
	private static final String DATATYPE_DOUBLE = "java.lang.Double";
	private static final String DATATYPE_SQL_TIMESTAMP = "java.sql.Timestamp";
	
	private String sortFieldName;
	private boolean isAscendingOrder;
	private boolean isNullFirst;

	public CommonComparator(final String sortFieldName,SortOrder sortOrder) {
		this.sortFieldName = sortFieldName;
		this.isAscendingOrder = SortOrder.asc == sortOrder;
	}

	public CommonComparator(final String sortFieldName,SortOrder sortOrder,boolean isNullFirst) {
        this(sortFieldName,sortOrder);
		this.isNullFirst = isNullFirst;
	}

	/**
	 * Override method for comparison of two elements(Objects).
	 * 
	 * @param t1
	 * @param t2
	 * @return value
	 * 
	 * @throws BadRequestException
	 */
	@Override
	public int compare(final T t1, final T t2) {
		int value = 1;
		try {
			Object fieldVal1 = invokeGetterValue(sortFieldName, t1);
			Object fieldVal2 = invokeGetterValue(sortFieldName, t2);

			if (fieldVal1 == null || fieldVal2 == null) {
				value = compareNull(fieldVal1, fieldVal2);
			} else {
				value = compareValue(fieldVal1, fieldVal2);
			}

		} catch (SecurityException | IllegalArgumentException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new BadRequestException("Invalid sort column");
		}
		return value * determineOrder();
	}

	/**
	 * Get actual value for the field
	 * 
	 * @param fieldName
	 * @param t
	 * @return value
	 * 
	 * @throws BadRequestException
	 */
	private Object invokeGetterValue(String fieldName,T t) {
		Object value = null;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(fieldName, t.getClass());
			Method getter = pd.getReadMethod();
			value = getter.invoke(t);
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
	 * @return -1,0,1
	 */
	private int compareNull(final Object v1, final Object v2) {
		int actual = -1;
		if (v1 == null && v2 == null)
			actual = 0;
		else if(v1 == null)
			actual = 1;
		return actual * determineNullOrder() * determineOrder();
	}

	/**
	 * Compare two actual values, according to different data types.
	 * 
	 * @param v1
	 * @param v2
	 * @return -1,0,1
	 */
	private int compareValue(final Object v1, final Object v2) {
		int actual = -1;
		if (v1.getClass() == v2.getClass()) {
			String fieldType = v1.getClass().getName();
			switch (fieldType) {
				case DATATYPE_INTEGER:
					actual = ((Integer) v1).compareTo((Integer) v2);
					break;
				case DATATYPE_LONG:
					actual = ((Long) v1).compareTo((Long) v2);
					break;
				case DATATYPE_STRING:
					actual = ((String) v1).compareTo((String) v2);
					break;
				case DATATYPE_DATE:
					actual = ((Date) v1).compareTo((Date) v2);
					break;
				case DATATYPE_FLOAT:
					actual = ((Float) v1).compareTo((Float) v2);
					break;
				case DATATYPE_DOUBLE:
					actual = ((Double) v1).compareTo((Double) v2);
					break;
				case DATATYPE_SQL_TIMESTAMP:
					actual = ((Timestamp) v1).compareTo((Timestamp) v2);
					break;
				default:
					LOGGER.error("Data type object is different class t1: {} and t2: {}",v1.getClass(),v2.getClass());
					throw new IllegalArgumentException("Data type object is different class");
			}
		} else {
			LOGGER.error("Objects are different classes");
		}
		return actual;
	}

	/**
	 * Get order type
	 */
	private int determineOrder() {
		return isAscendingOrder ? 1 : -1;
	}

	/**
	 * Get null order type
	 */
	private int determineNullOrder() {
		return isNullFirst ? -1 : 1;
	}
}
