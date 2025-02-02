package botobo.core.ui.search;

import botobo.core.domain.workbook.Workbook;
import botobo.core.exception.search.InvalidPageSizeException;
import botobo.core.exception.search.InvalidPageStartException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkbookSearchParameter {

    private static final int MINIMUM_START_PAGE = 0;
    private static final int DEFAULT_START_PAGE = 0;
    private static final int MINIMUM_PAGE_SIZE = 1;
    private static final int MAXIMUM_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private SearchType searchType;
    private SearchKeyword searchKeyword;
    private SearchCriteria searchCriteria;
    private SearchOrder searchOrder;
    private int start;
    private int size;

    @Builder
    private WorkbookSearchParameter(String searchType, String searchCriteria, String searchOrder, String searchKeyword, String start, String size) {
        this.start = initializeStartValue(start);
        this.size = initializeSizeValue(size);
        this.searchType = SearchType.of(searchType);
        this.searchKeyword = SearchKeyword.of(searchKeyword);
        this.searchCriteria = SearchCriteria.of(searchCriteria);
        this.searchOrder = SearchOrder.of(searchOrder);
    }

    private int initializeStartValue(String start) {
        try {
            int value = Integer.parseInt(start);
            if (value < MINIMUM_START_PAGE) {
                throw new InvalidPageStartException();
            }
            return value;
        } catch (NumberFormatException e) {
            return DEFAULT_START_PAGE;
        }
    }

    private int initializeSizeValue(String size) {
        try {
            int value = Integer.parseInt(size);
            if (value < MINIMUM_PAGE_SIZE || value > MAXIMUM_PAGE_SIZE) {
                throw new InvalidPageSizeException();
            }
            return value;
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE_SIZE;
        }
    }

    public Specification<Workbook> toSpecification() {
        return Specification.where(matchesKeyword())
                .and(isOpened())
                .and(orderByCriteria())
                .and(containsAtLeastOneCard());
    }

    private Specification<Workbook> matchesKeyword() {
        return (root, query, builder) -> builder.like(
                expression(root, builder),
                pattern()
        );
    }

    private Specification<Workbook> isOpened() {
        return (root, query, builder) -> builder.isTrue(root.get("opened"));
    }

    private Specification<Workbook> orderByCriteria() {
        return (root, query, builder) -> {
            Order order = searchOrder.toOrder(builder, root, searchCriteria);
            query.orderBy(order);
            return null;
        };
    }

    private Expression<String> expression(Root<Workbook> root, CriteriaBuilder builder) {
        return searchType.toExpression(root, builder);
    }

    private String pattern() {
        return searchType.toPattern(searchKeyword.getValue());
    }

    private Specification<Workbook> containsAtLeastOneCard() {
        return (root, query, builder) -> builder
                .greaterThan(
                        builder.size(root.get("cards").get("cards")),
                        0
                );
    }

    public PageRequest toPageRequest() {
        return PageRequest.of(start, size);
    }
}
