package com.saviynt.pam.util;

import com.saviynt.pam.enums.SortOrder;
import com.saviynt.pam.exception.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CommonComparatorTest {

    private static final Logger log = LoggerFactory.getLogger(CommonComparatorTest.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Session> list;

    @BeforeEach
    void setUp() throws Exception {
        list = List.of(
                new Session(1,"one",dateFormat.parse("2020-01-01 21:12:22"),new Timestamp(dateFormat.parse("2020-01-02 21:12:22").getTime()),100l,2.3d,1.0f),
                new Session(4,"",dateFormat.parse("2020-01-04 21:12:22"),null,2300l,8.9d,67.6f),
                new Session(5,"five",dateFormat.parse("2020-01-05 21:12:22"),new Timestamp(dateFormat.parse("2020-01-06 21:12:22").getTime()),null,4d,null),
                new Session(3,"three",dateFormat.parse("2020-01-03 02:22:00"),new Timestamp(dateFormat.parse("2020-01-04 01:21:30").getTime()),300l,null,null),
                new Session(6,null,null,new Timestamp(dateFormat.parse("2020-01-02 21:12:22").getTime()),600l,7d,9f),
                new Session(null,"seven",dateFormat.parse("2020-01-07 21:12:22"),new Timestamp(dateFormat.parse("2020-01-08 21:12:22").getTime()),700l,34d,89f),
                new Session(2,"two",dateFormat.parse("2020-01-02 09:12:22"),new Timestamp(dateFormat.parse("2020-01-03 12:09:11").getTime()),200l,3.4d,5.6f)
        ).stream().collect(Collectors.toList());
    }

    @Test
    @DisplayName("Positive: String sorting ascending order")
    void testStringOrderAsc() throws Exception{
       //Expected
       List<String> expected = list.stream()
                                   .sorted(Comparator.comparing(Session::getName,Comparator.nullsLast(Comparator.naturalOrder())))
                                   .map(Session::getName)
                                   .collect(Collectors.toList());
       //Sort
       Collections.sort(list,new CommonComparator<>("name", SortOrder.asc));
       //Actual
       List<String> actual = list.stream()
                                 .map(Session::getName)
                                 .collect(Collectors.toCollection(ArrayList::new));
       log.debug("Expected : {}",expected);
       log.debug("Actual   : {}",actual);

       //assert
        Assertions.assertIterableEquals(expected,actual);

    }

    @Test
    @DisplayName("Positive: String sorting descending order")
    void testStringOrderDesc() throws Exception{
        //Expected
        List<String> expected = list.stream()
                .sorted(Comparator.comparing(Session::getName,Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Session::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("name", SortOrder.desc));
        //Actual
        List<String> actual = list.stream()
                .map(Session::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);

    }

    @Test
    @DisplayName("Positive: Integer sorting ascending order")
    void testIntegerOrderAsc() throws Exception{
        //Expected
        List<Integer> expected = list.stream()
                .sorted(Comparator.comparing(Session::getId,Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Session::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("id", SortOrder.asc));
        //Actual
        List<Integer> actual = list.stream()
                .map(Session::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);

    }

    @Test
    @DisplayName("Positive: Integer sorting descending order")
    void testIntegerOrderDesc() throws Exception{
        //Expected
        List<Integer> expected = list.stream()
                .sorted(Comparator.comparing(Session::getId,Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Session::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("id", SortOrder.desc));
        //Actual
        List<Integer> actual = list.stream()
                .map(Session::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Date sorting ascending order")
    void testDateOrderAsc() throws Exception{
        //Expected
        List<Date> expected = list.stream()
                .sorted(Comparator.comparing(Session::getStartDate,Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Session::getStartDate)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("startDate", SortOrder.asc));
        //Actual
        List<Date> actual = list.stream()
                .map(Session::getStartDate)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);

    }

    @Test
    @DisplayName("Positive: Date sorting descending order with null first")
    void testDateOrderDesc() throws Exception{
        //Expected
        List<Date> expected = list.stream()
                .sorted(Comparator.comparing(Session::getStartDate,Comparator.nullsFirst(Comparator.reverseOrder())))
                .map(Session::getStartDate)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("startDate", SortOrder.desc,true));
        //Actual
        List<Date> actual = list.stream()
                .map(Session::getStartDate)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Timestamp sorting ascending order with null last")
    void testTimestampOrderAsc() throws Exception{
        //Expected
        List<Timestamp> expected = list.stream()
                .sorted(Comparator.comparing(Session::getEndDate,Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Session::getEndDate)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("endDate", SortOrder.asc));
        //Actual
        List<Timestamp> actual = list.stream()
                .map(Session::getEndDate)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Timestamp sorting descending order with null first")
    void testTimestampOrderDesc() throws Exception{
        //Expected
        List<Timestamp> expected = list.stream()
                .sorted(Comparator.comparing(Session::getEndDate,Comparator.nullsFirst(Comparator.reverseOrder())))
                .map(Session::getEndDate)
                .collect(Collectors.toCollection(ArrayList::new));
        //Sort
        Collections.sort(list,new CommonComparator<>("endDate", SortOrder.desc,true));
        //Actual
        List<Timestamp> actual = list.stream()
                .map(Session::getEndDate)
                .collect(Collectors.toCollection(ArrayList::new));
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Long sorting ascending order with null last")
    void testLongOrderAsc() throws Exception{
        //Expected
        List<Long> expected = list.stream()
                .sorted(Comparator.comparing(Session::getDuration,Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(Session::getDuration)
                .collect(Collectors.toList());
        //Sort
        Collections.sort(list,new CommonComparator<>("duration", SortOrder.asc,true));
        //Actual
        List<Long> actual = list.stream()
                .map(Session::getDuration)
                .collect(Collectors.toList());
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Timestamp sorting descending order with null last")
    void testLongOrderDesc() throws Exception{
        //Expected
        List<Long> expected = list.stream()
                .sorted(Comparator.comparing(Session::getDuration,Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Session::getDuration)
                .collect(Collectors.toList());
        //Sort
        Collections.sort(list,new CommonComparator<>("duration", SortOrder.desc));
        //Actual
        List<Long> actual = list.stream()
                .map(Session::getDuration)
                .collect(Collectors.toList());
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Double sorting ascending order with null last")
    void testDoubleOrderAsc() throws Exception{
        //Expected
        List<Double> expected = list.stream()
                .sorted(Comparator.comparing(Session::getTimeOut,Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Session::getTimeOut)
                .collect(Collectors.toList());
        //Sort
        Collections.sort(list,new CommonComparator<>("timeOut", SortOrder.asc));
        //Actual
        List<Double> actual = list.stream()
                .map(Session::getTimeOut)
                .collect(Collectors.toList());
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Double sorting descending order with null last")
    void testDoubleOrderDesc() throws Exception{
        //Expected
        List<Double> expected = list.stream()
                .sorted(Comparator.comparing(Session::getTimeOut,Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Session::getTimeOut)
                .collect(Collectors.toList());
        //Sort
        Collections.sort(list,new CommonComparator<>("timeOut", SortOrder.desc));
        //Actual
        List<Double> actual = list.stream()
                .map(Session::getTimeOut)
                .collect(Collectors.toList());
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Float sorting descending order with null last")
    void testFloatOrderAsc() throws Exception{
        //Expected
        List<Float> expected = list.stream()
                .sorted(Comparator.comparing(Session::getCost,Comparator.nullsLast(Comparator.naturalOrder())))
                .map(Session::getCost)
                .collect(Collectors.toList());
        //Sort
        Collections.sort(list,new CommonComparator<>("cost", SortOrder.asc));
        //Actual
        List<Float> actual = list.stream()
                .map(Session::getCost)
                .collect(Collectors.toList());
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Positive: Float sorting descending order with null first")
    void testFloatOrderDesc() throws Exception{
        //Expected
        List<Float> expected = list.stream()
                .sorted(Comparator.comparing(Session::getCost,Comparator.nullsFirst(Comparator.reverseOrder())))
                .map(Session::getCost)
                .collect(Collectors.toList());
        //Sort
        Collections.sort(list,new CommonComparator<>("cost", SortOrder.desc,true));
        //Actual
        List<Float> actual = list.stream()
                .map(Session::getCost)
                .collect(Collectors.toList());
        log.debug("Expected : {}",expected);
        log.debug("Actual   : {}",actual);

        //assert
        Assertions.assertIterableEquals(expected,actual);
    }

    @Test
    @DisplayName("Negative: Field name with null")
    void testFieldNameWithNull() throws Exception{

        //assert
        Assertions.assertThrows(NullPointerException.class,()->{
            Collections.sort(list,new CommonComparator<>(null, SortOrder.desc,true));
        });
    }

    @Test
    @DisplayName("Negative: Invalid field name")
    void testInvalidFieldName() throws Exception{

        //assert
        Assertions.assertThrows(BadRequestException.class,()->{
            Collections.sort(list,new CommonComparator<>("timeout", SortOrder.desc,true));
        });
    }

    @Test
    @DisplayName("Negative: Sort element by unsupported data type")
    void testUnsupportedDatatype() throws Exception{
      //assert
        Assertions.assertThrows(BadRequestException.class,()->{
            Collections.sort(list,new CommonComparator<>("status", SortOrder.desc));
        });
    }

    @Test
    @DisplayName("Negative: CommonComparator is not supporting primitive data type collections")
    void testPrimitiveDataTypeList() throws Exception{

        List<String> status = List.of("NEW","PENDING","APPROVED","REJECTED");
        //assert
        Assertions.assertThrows(UnsupportedOperationException.class,()->{
            Collections.sort(status,new CommonComparator<>(null, SortOrder.desc,true));
        });
    }

}

class Session {

    private Integer id;
    private String name;
    private Date startDate;
    private Timestamp endDate;
    private Long duration;
    private Double timeOut;
    private Float cost;
    private boolean status;

    public Session(Integer id, String name, Date startDate, Timestamp endDate, Long duration, Double timeOut, Float cost) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.timeOut = timeOut;
        this.cost = cost;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Double getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Double timeOut) {
        this.timeOut = timeOut;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
