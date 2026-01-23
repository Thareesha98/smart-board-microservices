package com.sbms.boarding_service.repository.spec;


import org.springframework.data.jpa.domain.Specification;

import com.sbms.boarding_service.model.Boarding;
import com.sbms.boarding_service.model.enums.Status;
import com.sbms.boarding_service.model.enums.BoardingType;
import com.sbms.boarding_service.model.enums.Gender;

import java.math.BigDecimal;

public class BoardingSpecifications {

    public static Specification<Boarding> statusIs(Status status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Boarding> ownerIs(Long ownerId) {
        return (root, query, cb) ->
                cb.equal(root.get("ownerId"), ownerId);
    }

    public static Specification<Boarding> genderIs(Gender gender) {
        return (root, query, cb) ->
                cb.equal(root.get("genderType"), gender);
    }

    public static Specification<Boarding> boardingTypeIs(BoardingType type) {
        return (root, query, cb) ->
                cb.equal(root.get("boardingType"), type);
    }

    public static Specification<Boarding> minPrice(BigDecimal min) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("pricePerMonth"), min);
    }

    public static Specification<Boarding> maxPrice(BigDecimal max) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("pricePerMonth"), max);
    }

    public static Specification<Boarding> minKeyMoney(BigDecimal min) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("keyMoney"), min);
    }

    public static Specification<Boarding> maxKeyMoney(BigDecimal max) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("keyMoney"), max);
    }

    public static Specification<Boarding> addressOrTitleLike(String keyword) {
        return (root, query, cb) -> {
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("address")), like),
                    cb.like(cb.lower(root.get("title")), like)
            );
        };
    }
}
