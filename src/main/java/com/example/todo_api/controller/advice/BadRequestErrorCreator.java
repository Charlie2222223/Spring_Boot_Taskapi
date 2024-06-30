package com.example.todo_api.controller.advice;

import com.example.todo_api.model.BadRequestError;
import com.example.todo_api.model.InvalidParam;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BadRequestErrorCreator {
    public static BadRequestError from(MethodArgumentNotValidException ex) {
        var invalitParamList = createInvalidParamList(ex);
        var error = new BadRequestError();
        error.setInvalidParams(invalitParamList);
        return error;
    }

    private static List<InvalidParam> createInvalidParamList(MethodArgumentNotValidException ex) {
        return ex.getFieldErrors()
                .stream()
                .map(BadRequestErrorCreator::createInvalidParam)
                .collect(Collectors.toList());
    }

    private static InvalidParam createInvalidParam(FieldError fieldError) {
        var invalidParam = new InvalidParam();
        invalidParam.setName(fieldError.getField());
        invalidParam.setReason(fieldError.getDefaultMessage());
        return invalidParam;
    }

    public static BadRequestError from(ConstraintViolationException ex) {
        var invalidList = createInvalidParamList(ex);
        var error = new BadRequestError();
        error.setInvalidParams(invalidList);
        return error;
    }

    private static List<InvalidParam> createInvalidParamList(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(BadRequestErrorCreator::createInvalidParam)
                .collect(Collectors.toList());
    }

    private static InvalidParam createInvalidParam(ConstraintViolation<?> violation) {
        var parameterOpt =StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                .filter(node -> node.getKind().equals(ElementKind.PARAMETER))
                        .findFirst();
        var invalidParam = new InvalidParam();
        parameterOpt.ifPresent(p -> invalidParam.setName(p.getName()));
        invalidParam.setReason(violation.getMessage());

        return invalidParam;
    }
}