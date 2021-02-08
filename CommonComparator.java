package com.saviynt.pam.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

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

	private static final Logger log = LoggerFactory.getLogger(CommonComparator.class);

	private static final String DATATYPE_STRING = "java.lang.String";
	private static final String DATATYPE_DATE = "java.util.Date";
	private static final String DATATYPE_INTEGER = "java.lang.Integer";
	private static final String DATATYPE_LONG = "java.lang.Long";
	private static final String DATATYPE_FLOAT = "java.lang.Float";
	private static final String DATATYPE_DOUBLE = "java.lang.Double";
	private static final String DATATYPE_SQL_TIMESTAMP = "java.sql.Timestamp";
	
	private final String fieldName;
	private final boolean isAscendingOrder;
	private boolean isNullFirst;

	public CommonComparator(final String fieldName, SortOrder sortOrder) {
		this.fieldName = fieldName;
		this.isAscendingOrder = SortOrder.asc == sortOrder;
	}

	public CommonComparator(final String fieldName, SortOrder sortOrder, boolean isNullFirst) {
        this(fieldName,sortOrder);
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
		try {
			int value;
			Object fieldVal1 = invokeGetterValue(t1);
			Object fieldVal2 = invokeGetterValue(t2);

			if (fieldVal1 == null || fieldVal2 == null) {
				value = compareNull(fieldVal1, fieldVal2);
			} else {
				value = compareValue(fieldVal1, fieldVal2);
			}
			return value * determineOrder();
		} catch (SecurityException | IllegalArgumentException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new BadRequestException("Invalid sort column");
		}
	}

	/**
	 * Get actual value for the field
	 *
	 * @param t
	 * @return value
	 * 
	 * @throws BadRequestException
	 */
	private Object invokeGetterValue(T t) {
		Objects.requireNonNull(fieldName);
		try {
			PropertyDescriptor pd = new PropertyDescriptor(fieldName, t.getClass());
			Method getter = pd.getReadMethod();
			return getter.invoke(t);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| IntrospectionException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new IllegalArgumentException("Invalid property");
		}
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
		return actual * determineNullPos() * determineOrder();
	}

	/**
	 * Compare two actual values, according to different data types.
	 * 
	 * @param v1
	 * @param v2
	 * @return -1,0,1
	 */
	private int compareValue(final Object v1, final Object v2) {
		int actual;
		switch (v1.getClass().getName()) {
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
				log.error("Unsupported data type {}",v1.getClass().getName());
				throw new IllegalArgumentException("Unsupported data type");
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
	private int determineNullPos() {
		return isNullFirst ? -1 : 1;
	}

}
