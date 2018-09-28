package io.tradeledger.searchfacade.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.tradeledger.searchfacade.exception.InvalidParameterException;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LanguageFilter implements Filter {

    private String attribute;
    private String operator;
    private Object value; // Can be either a String or an Integer
    private Integer rangeFrom;
    private Integer rangeTo;

    private static final String EQUALS = "eq";
    private static final String GREATER_THAN_EQUALS = "gte";
    private static final String LOWER_THAN_EQUALS = "lte";
    private static final List<String> VALID_OPERATORS = Arrays.asList(EQUALS, GREATER_THAN_EQUALS, LOWER_THAN_EQUALS);

    public LanguageFilter(@JsonProperty(value= "attribute", required = true) String attribute,
                          @JsonProperty(value= "operator", required = true) String operator) {
        if (!VALID_OPERATORS.contains(operator)) {
            throw new InvalidParameterException("Invalid operator: " + operator);
        }

        this.attribute = attribute;
        this.operator = operator;
    }

    public LanguageFilter(String attribute, String operator, Object value,
                          Integer rangeFrom, Integer rangeTo) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }

    @JsonProperty("range")
    private void unpackRange(Map<String,Integer> range) {
        if (value != null) {
            throw new InvalidParameterException("Both value and range are not allowed.");
        }

        if (!operator.equals(EQUALS)) {
            throw new InvalidParameterException("Invalid operator for range.");
        }

        this.rangeFrom = range.get("from");
        this.rangeTo = range.get("to");
    }

    /**
     * Returns the Criteria query represented by this object.
     *
     * @return the Criteria query object
     */
    @Override
    public Criteria getCriteria() {
        if (value != null) {
            switch (operator) {
                case EQUALS:
                    return Criteria.where(attribute).is(value);
                case GREATER_THAN_EQUALS:
                    return Criteria.where(attribute).gte(value);
                case LOWER_THAN_EQUALS:
                    return Criteria.where(attribute).lte(value);
                default:
                    throw new InvalidParameterException("Invalid operator: " + operator);
            }
        }

        return Criteria.where(attribute).gte(rangeFrom).lte(rangeTo);
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(Integer rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public Integer getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(Integer rangeTo) {
        this.rangeTo = rangeTo;
    }

    @Override
    public String toString() {
        return "LanguageFilter{" +
                "attribute='" + attribute + '\'' +
                ", operator='" + operator + '\'' +
                ", value='" + value + '\'' +
                ", rangeFrom=" + rangeFrom +
                ", rangeTo=" + rangeTo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageFilter that = (LanguageFilter) o;
        return Objects.equals(attribute, that.attribute) &&
                Objects.equals(operator, that.operator) &&
                Objects.equals(value, that.value) &&
                Objects.equals(rangeFrom, that.rangeFrom) &&
                Objects.equals(rangeTo, that.rangeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, operator, value, rangeFrom, rangeTo);
    }

}
